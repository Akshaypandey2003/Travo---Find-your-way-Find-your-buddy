
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import {
  addUserTrips,
  removeTripMembers,
  removeTripRequest,
  removeUserTrips,
  updateTripMembers,
  updateTripRequest,
} from "../Redux/Slices/authSlice";
import { filterNotifications } from "../Redux/Slices/notificationSlice";
import { addTrips, filterTrips, updateTripFeedbacks } from "../Redux/Slices/tripSlice";
import { apiRequest, getApiErrorMessage } from "../lib/api";
import { notifyApiError, notifyApiSuccess } from "../lib/apiNotifications";

const useTrip = () => {
  const loggedInUser = useSelector((state) => state.auth.user);
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const sendTripRequest = async ({ notificationId, tripId, requestTo }) => {

    const token = localStorage.getItem("token");

    if (!loggedInUser) {
      navigate("/login");
      return;
    }
    console.log(
      loggedInUser?.userId +
        " has requested to join trip with id: " +
        tripId +
        " to user with id: " +
        requestTo
    );
    try {
      const { data, message } = await apiRequest(
        `http://localhost:8085/trip/send-trip-request/${tripId}/${loggedInUser?.userId}/${requestTo}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
          },
        }
      );

      {
        console.log("Trip request sent:", data);
        dispatch(
          updateTripRequest({
            requestTo: requestTo,
            requestFrom: loggedInUser?.userId,
            tripId: tripId,
          })
        );
        if (notificationId) dispatch(filterNotifications(notificationId));
        notifyApiSuccess(dispatch, message || "Trip request sent successfully.");
      }
    } catch (error) {
      notifyApiError(dispatch, getApiErrorMessage(error));
      console.error("Network error:", error.message);
    }
  };


  const getTrip = async (tripId) => {
    console.log("Fetching trip with id: ", tripId);
    const token = localStorage.getItem("token");
    try {
      const { data } = await apiRequest(
        `http://localhost:8085/trip/get-trip/${tripId}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
          },
        }
      );

      {
        console.log("Trip fetched successfully:", data);
        dispatch(addTrips(data));
      }
    } catch (error) {
      notifyApiError(dispatch, getApiErrorMessage(error));
      console.error("Network error:", error.message);
    }
  };


  const acceptTripRequest = async (
    notificationId,
    tripId,
    notificationFrom
  ) => {
    const token = localStorage.getItem("token");
    //We need to add trip details of trip with tripID to the user with id notificationFrom and delete the notification with id notification id
    console.log("Trip id inside accept trip custom hook: ", tripId);
    try {
      const { data, message } = await apiRequest(
        `http://localhost:8085/trip/accept-trip-request/${notificationId}/${tripId}/${notificationFrom}/${loggedInUser?.userId}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
          },
        }
      );
      {
        console.log("Trip request accepted:", data);
        dispatch(
          updateTripMembers({
            tripMember: notificationFrom,
            tripId: tripId,
          })
        );

        !notificationId
          ? dispatch(
              removeTripRequest({
                tripMember: notificationFrom,
                tripId: tripId,
              })
            )
          : dispatch(filterNotifications(notificationId));
        notifyApiSuccess(dispatch, message || "Trip request accepted successfully.");
      }
    } catch (error) {
      notifyApiError(dispatch, getApiErrorMessage(error));
      console.error("Network error:", error.message);
    }
  };


  const removeTripMember = async (tripId, userId) => {

    const token = localStorage.getItem("token");
    try {
      const { data } = await apiRequest(
        `http://localhost:8085/trip/remove-trip-member/${tripId}/${userId}`,
        {
          method: "DELETE",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
          },
        }
      );
      {
        console.log("Trip member removed successfully:", data);
        dispatch(removeTripMembers({ tripId: tripId, memberId: userId }));
      }
    } catch (error) {
      notifyApiError(dispatch, getApiErrorMessage(error));
      console.error("Network error:", error.message);
    }
  };


  // const deleteTripRequest = async (tripId, userId) => {
  //   console.log("Trip id inside delete trip  request custom hook: ", tripId);
  //   try {
  //     const response = await fetch(
  //       `http://localhost:8085/trip/delete-trip-request/${tripId}/${userId}`,
  //       {
  //         method: "DELETE",
  //         headers: {
  //           "Content-Type": "application/json",
  //         },
  //       }
  //     );
  //     if (response.ok) {
  //       const contentType = response.headers.get("content-type");
  //       let data;

  //       if (contentType && contentType.includes("application/json")) {
  //         data = await response.json();
  //       } else {
  //         data = await response.text(); // fallback to plain text
  //       }
  //       console.log("Trip removed successfully:", data);
  //       dispatch(
  //         removeTripRequest({
  //           tripMember: userId,
  //           tripId: tripId,
  //         })
  //       );
  //     } else {
  //       const err = await response.json();
  //       console.error("Error removing trip request:", err.message);
  //     }
  //   } catch (error) {
  //     console.error("Network error:", error.message);
  //   }
  // };
  
  
  const createTrip = async (tripData) => {

    const token = localStorage.getItem("token");
    if (!loggedInUser) {
      navigate("/login");
    }
    console.log(
      loggedInUser?.userId + " has created a trip with data: " + tripData
    );

    try {
      const { data } = await apiRequest(
        `http://localhost:8085/trip/create-trip`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
          },
          body: JSON.stringify(tripData),
        }
      );

      console.log("Trip created:", data);
      dispatch(addTrips(data));
      dispatch(addUserTrips(data));
      notifyApiSuccess(dispatch, "Trip created successfully.");
    } catch (error) {
      notifyApiError(dispatch, getApiErrorMessage(error));
      console.error("Network error while creating trip:", error.message);
    }
  };


  const updateTrip = async (tripData) => {

    const token = localStorage.getItem("token");
    if (!loggedInUser) {
      navigate("/login");
    }
    console.log(
      `${loggedInUser?.userId} has Updated a trip with data:`,
      tripData
    );
    try {
      const { data } = await apiRequest(
        `http://localhost:8085/trip/update-trip`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
          },
          body: JSON.stringify(tripData),
        }
      );

      dispatch(addUserTrips(data));
      dispatch(addTrips(data));
      console.log("Trip updated Successfully:", data);
      notifyApiSuccess(dispatch, "Trip updated successfully.");
    } catch (error) {
      notifyApiError(dispatch, getApiErrorMessage(error));
      console.error("Network error while updating trip:", error.message);
    }
  };

  const deleteTrip = async (tripId) => {

    const token = localStorage.getItem("token");
    if (!loggedInUser) {
      navigate("/login");
    }
    console.log(
      loggedInUser?.userId + " has deleted a trip with id: " + tripId
    );

    try {
      const { data } = await apiRequest(
        `http://localhost:8085/trip/delete-trip/${tripId}`,
        {
          method: "DELETE",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
          },
        }
      );

      dispatch(filterTrips(tripId));
      dispatch(removeUserTrips(tripId));
      console.log("Trip deleted:", data);
      notifyApiSuccess(dispatch, data?.message || "Trip deleted successfully.");
    } catch (error) {
      notifyApiError(dispatch, getApiErrorMessage(error));
      console.error("Network error while creating trip:", error.message);
    }
  };

  const fetchTripFeedbacks = async (tripId) => {

    const token = localStorage.getItem("token");
    console.log("Fetching feedbacks for the trip with id: ", tripId);
    try {
      const { data } = await apiRequest(
        `http://localhost:8085/feedback/get-trip-feedback/${tripId}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
          },
        }
      );

      {
        console.log("Trip feedbacks fetched successfully:", data);
        dispatch(
          updateTripFeedbacks({
            tripId: tripId,
            feedbacks: data,
          })
        );
      }
    } catch (error) {
      notifyApiError(dispatch, getApiErrorMessage(error));
      console.error("Network error:", error.message);
    }
  };


  const postTripFeedback = async (feedback) => {
    const token = localStorage.getItem("token");
    console.log("Posting feedback: ", feedback);
    try {
      const { data } = await apiRequest(
        `http://localhost:8085/feedback/trip/post-trip-feedback`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
          },
          body: JSON.stringify(feedback),
        }
      );

      {
        data.author = feedback.author;
        console.log("Trip feedback submitted successfully", data);
        dispatch(
          updateTripFeedbacks({ tripId: data?.tripId, feedbacks: data })
        );
        notifyApiSuccess(dispatch, "Trip feedback submitted successfully.");
      }
    } catch (error) {
      notifyApiError(dispatch, getApiErrorMessage(error));
      console.error("Network error:", error.message);
    }
  };
  
  return {
    sendTripRequest,
    createTrip,
    deleteTrip,
    acceptTripRequest,
    // deleteTripRequest,
    removeTripMember,
    getTrip,
    updateTrip,
    fetchTripFeedbacks,
    postTripFeedback,
  };
};
export default useTrip;
