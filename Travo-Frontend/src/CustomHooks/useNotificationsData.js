import { useDispatch } from "react-redux";
import {
    addNotification,
    filterNotifications} from "../Redux/Slices/notificationSlice";
import { apiRequest, getApiErrorMessage } from "../lib/api";
import { notifyApiError, notifyApiSuccess } from "../lib/apiNotifications";

const useNotificationsData = () => {
  const dispatch = useDispatch();

  const getAllNotifications = async () => {
    const token = localStorage.getItem("token");
    try {
      const { data } = await apiRequest(
        `http://localhost:8085/api/v1/notification?read=false`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
          },
        }
      );
      console.log("REceived notifications : ", data);
      dispatch(addNotification(Array.isArray(data) ? data : []));

      // Assuming you have a Redux action to set user data
      return data;
    } catch (error) {
      notifyApiError(dispatch, getApiErrorMessage(error));
      console.error("Error fetching notifications:", error);
      return [];
    }
  };


   const deleteNotification = async (notificationId) => {

    const token = localStorage.getItem("token");
    try {
      const { data } = await apiRequest(
        `http://localhost:8085/api/v1/notification/${notificationId}`,
        {
          method: "DELETE",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
          },
        }
      );
      dispatch(filterNotifications(notificationId));
      notifyApiSuccess(dispatch, typeof data === "string" ? data : data?.message);

      // Debugging line
      // const payload = {
      //   message: data?.messageResponse?.message,
      //   success: data?.messageResponse?.status=="success" ? true : false,
      //   error: data?.messageResponse?.status=="error" ? true : false,
      // };
      // dispatch(setMessage(payload));

      return data;
    } catch (error) {
      notifyApiError(dispatch, getApiErrorMessage(error));
      console.error("Error while deleting notification:", error);
      return null;
    }
  };


  return {
    getAllNotifications,
    deleteNotification,
  };
};
export default useNotificationsData;
