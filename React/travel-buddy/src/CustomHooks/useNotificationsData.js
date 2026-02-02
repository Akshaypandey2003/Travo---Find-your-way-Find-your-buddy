/* eslint-disable no-unused-vars */

import { shallowEqual, useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import {
    addNotification,
    filterNotifications} from "../Redux/Slices/notificationSlice";

const useNotificationsData = () => {
  const dispatch = useDispatch();
  const notifications = useSelector(
    (store) => store.notifications,
    shallowEqual
  );
  const loggedInUser = useSelector((state) => state.auth.user);
  const navigate = useNavigate();
  const usersList = useSelector((store) => store.auth.usersList); // existing cached users


  const getAllNotifications = async (userId) => {
    const token = localStorage.getItem("token");
    try {
      const response = await fetch(
        `http://localhost:8085/notification/get-notification-by-user/${userId}?read=false`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
          },
        }
      );
      const data = await response.json();
      console.log("All Notifications received:", data); // Debugging line

      // if(data?.messageResponse?.status=="success")
      dispatch(addNotification(data));

      // Assuming you have a Redux action to set user data
      return data;
    } catch (error) {
      console.error("Error fetching users notifications:", error);
      return [];
    }
  };


   const deleteNotification = async (notificationId) => {

    const token = localStorage.getItem("token");
    try {
      const response = await fetch(
        `http://localhost:8085/notification/delete/${notificationId}`,
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

      console.log("Notification deleted:", data);
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
