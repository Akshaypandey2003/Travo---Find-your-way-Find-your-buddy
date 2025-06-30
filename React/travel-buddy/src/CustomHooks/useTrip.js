/* eslint-disable no-unused-vars */

import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import {
  addTrips,
  removeTripMembers,
  removeTripRequest,
  removeTrips,
  updateTripMembers,
  updateTripRequest,
} from "../Redux/Slices/authSlice";
import { filterNotifications } from "../Redux/Slices/notificationSlice";

const useTrip = () => {
  const loggedInUser = useSelector((state) => state.auth.user);
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const sendTripRequest = async ({ notificationId,tripId, requestTo }) => {
    if (!loggedInUser) {
      navigate("/login");
    }
    console.log(
      loggedInUser?.userId +
        " has requested to join trip with id: " +
        tripId +
        " to user with id: " +
        requestTo
    );
    try {
      const response = await fetch(
        `http://localhost:8085/auth/user/trip/send-trip-request/${tripId}/${loggedInUser?.userId}/${requestTo}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );

      if (response.ok) {
        const contentType = response.headers.get("content-type");
        let data;

        if (contentType && contentType.includes("application/json")) {
          data = await response.json();
        } else {
          data = await response.text(); // fallback to plain text
        }
        console.log("Trip request sent:", data);
        dispatch(
          updateTripRequest({
            requestTo: requestTo,
            requestFrom: loggedInUser?.userId,
            tripId: tripId,
          })
         
        );
        if(notificationId)
         dispatch(filterNotifications(notificationId))
      } else {
        const err = await response.json();
        console.error("Error sending trip request:", err.message);
      }
    } catch (error) {
      console.error("Network error:", error.message);
    }
  };
  const getTrip = async(tripId)=>
  {
    console.log("Fetching trip with id: ", tripId)
    try {
      const response = await fetch(
        `http://localhost:8085/auth/user/trip/get-trip-by-tripId/${tripId}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );

      if (response.ok) {
        const contentType = response.headers.get("content-type");
        let data;

        if (contentType && contentType.includes("application/json")) {
          data = await response.json();
        } else {
          data = await response.text(); // fallback to plain text
        }
        console.log("Trip fetched successfully:", data);
        dispatch(
         addTrips(data)   
        );
      
      } else {
        const err = await response.json();
        console.error("Error fetching trip:", err.message);
      }
    } catch (error) {
      console.error("Network error:", error.message);
    }
  }
  const acceptTripRequest = async (
    notificationId,
    tripId,
    notificationFrom,
  ) => {
    //We need to add trip details of trip with tripID to the user with id notificationFrom and delete the notification with id notification id
    console.log("Trip id inside accept trip custom hook: ",tripId);
    try {
      const response = await fetch(
        `http://localhost:8085/auth/user/trip/accept-trip-request/${notificationId}/${tripId}/${notificationFrom}/${loggedInUser?.userId}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );
       if (response.ok) {
        const contentType = response.headers.get("content-type");
        let data;

        if (contentType && contentType.includes("application/json")) {
          data = await response.json();
        } else {
          data = await response.text(); // fallback to plain text
        }
        console.log("Trip request accepted:", data);
        dispatch(
          updateTripMembers({
            tripMember: notificationFrom,
            tripId: tripId,
          })
        );
         
         !notificationId ?
           dispatch(removeTripRequest({
            tripMember: notificationFrom,
            tripId: tripId,
          })
        ):dispatch(filterNotifications(notificationId));

      } else {
        const err = await response.json();
        console.error("Error accepting trip request:", err.message);
      }
    } catch (error) {
      console.error("Network error:", error.message);
    }
  };
  const removeTripMember = async(tripId,userId)=>
  {
     try {
      const response = await fetch(
        `http://localhost:8085/auth/user/trip/remove-trip-member/${tripId}/${userId}`,
        {
          method: "DELETE",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );
       if (response.ok) {
        const contentType = response.headers.get("content-type");
        let data;

        if (contentType && contentType.includes("application/json")) {
          data = await response.json();
        } else {
          data = await response.text(); // fallback to plain text
        }
        console.log("Trip member removed successfully:", data);
        dispatch(removeTripMembers({tripId:tripId,memberId:userId}))

      } else {
        const err = await response.json();
        console.error("Error while removing member with id:",userId," ", err.message);
      }
    } catch (error) {
      console.error("Network error:", error.message);
    }
  }
  const deleteTripRequest = async(tripId,userId)=>
  {
     console.log("Trip id inside delete trip  request custom hook: ",tripId);
    try {
      const response = await fetch(
        `http://localhost:8085/auth/user/trip/delete-trip-request/${tripId}/${userId}`,
        {
          method: "DELETE",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );
       if (response.ok) {
        const contentType = response.headers.get("content-type");
        let data;

        if (contentType && contentType.includes("application/json")) {
          data = await response.json();
        } else {
          data = await response.text(); // fallback to plain text
        }
        console.log("Trip removed successfully:", data);
        dispatch(
          removeTripRequest({
            tripMember: userId,
            tripId: tripId,
          })
        );
      } else {
        const err = await response.json();
        console.error("Error removing trip request:", err.message);
      }
    } catch (error) {
      console.error("Network error:", error.message);
    }
  }
  const createTrip = async (tripData) => {
    if (!loggedInUser) {
      navigate("/login");
    }
    console.log(
      loggedInUser?.userId + " has created a trip with data: " + tripData
    );

    try {
      const response = await fetch(
        `http://localhost:8085/auth/user/trip/create-trip`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(tripData),
        }
      );

      if (response.ok) {
        const contentType = response.headers.get("content-type");
        let data;

        if (contentType && contentType.includes("application/json")) {
          data = await response.json();

          dispatch(addTrips(data)); // Dispatch the action to add the trip to the store
        } else {
          data = await response.text(); // fallback to plain text
        }
        console.log("Trip created:", data);
      } else {
        const err = await response.json();
        console.error("Error creating trip:", err.message);
      }
    } catch (error) {
      console.error("Network error while creating trip:", error.message);
    }
  };
  const deleteTrip = async (tripId) => {
    if (!loggedInUser) {
      navigate("/login");
    }
    console.log(
      loggedInUser?.userId + " has deleted a trip with id: " + tripId
    );

    try {
      const response = await fetch(
        `http://localhost:8085/auth/user/trip/delete-trip/${tripId}`,
        {
          method: "DELETE",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(tripId),
        }
      );

      if (response.ok) {
        const contentType = response.headers.get("content-type");
        let data;

        if (contentType && contentType.includes("application/json")) {
          data = await response.json();

          dispatch(removeTrips(tripId)); // Dispatch the action to remove the trip from the store
        } else {
          data = await response.text(); // fallback to plain text
        }
        console.log("Trip deleted:", data);
      } else {
        const err = await response.json();
        console.error("Error deleting trip:", err.message);
      }
    } catch (error) {
      console.error("Network error while creating trip:", error.message);
    }
  };
  return { sendTripRequest, createTrip, deleteTrip, acceptTripRequest,deleteTripRequest,removeTripMember,getTrip};
};
export default useTrip;
