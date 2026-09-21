import { useDispatch } from "react-redux";
import {
    addNotification,
    filterNotifications} from "../Redux/Slices/notificationSlice";

const useNotificationsData = () => {
  const dispatch = useDispatch();

  const getAllNotifications = async () => {
    const token = localStorage.getItem("token");
    try {
      const response = await fetch(
        `http://localhost:8085/api/v1/notification?read=false`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
          },
        }
      );
      if (!response.ok) throw new Error(`Notification request failed: ${response.status}`);
      const data = await response.json();

      console.log("REceived notifications : ", data);
      dispatch(addNotification(Array.isArray(data) ? data : []));

      // Assuming you have a Redux action to set user data
      return data;
    } catch (error) {
      console.error("Error fetching notifications:", error);
      return [];
    }
  };


   const deleteNotification = async (notificationId) => {

    const token = localStorage.getItem("token");
    try {
      const response = await fetch(
        `http://localhost:8085/api/v1/notification/${notificationId}`,
        {
          method: "DELETE",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
          },
        }
      );
      // Check response type
      const contentType = response.headers.get("content-type");
      let data;

      if (contentType && contentType.includes("application/json")) {
        data = await response.json();
      } else {
        data = await response.text(); // fallback to plain text
      }

      dispatch(filterNotifications(notificationId));

      // Debugging line
      // const payload = {
      //   message: data?.messageResponse?.message,
      //   success: data?.messageResponse?.status=="success" ? true : false,
      //   error: data?.messageResponse?.status=="error" ? true : false,
      // };
      // dispatch(setMessage(payload));

      return data;
    } catch (error) {
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
