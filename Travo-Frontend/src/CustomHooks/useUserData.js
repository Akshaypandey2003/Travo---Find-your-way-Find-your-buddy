/* eslint-disable no-unused-vars */

import { useCallback } from "react";
import { shallowEqual, useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import {
  setNextPageToken,
  setUsersData,
  updateCloseFriends,
  updateFollowings,
  updateFriendRequests,
  updateLike
} from "../Redux/Slices/authSlice";
import {
  filterNotifications,
  setMessage
} from "../Redux/Slices/notificationSlice";

const useUserData = () => {
  const dispatch = useDispatch();
  const notifications = useSelector(
    (store) => store.notifications,
    shallowEqual,
  );
  const loggedInUser = useSelector((state) => state.auth.user);
  const navigate = useNavigate();
  const usersList = useSelector((store) => store.auth.usersList); // existing cached users

  //  User specific APIs
  const getAllUsers = async (page) => {
    const token = localStorage.getItem("token");

    try {
      const response = await fetch(
        `http://localhost:8085/api/v1/user/all?page=${page}&size=10`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        },
      );
      const data = await response.json();
      console.log("All Users data received:", data); // Debugging line

      dispatch(setUsersData(data?.content));
      dispatch(setNextPageToken(!data?.last));

      // Assuming you have a Redux action to set user data
      return data;
    } catch (error) {
      console.error("Error fetching users:", error);
      return [];
    }
  };

  // eslint-disable-next-line react-hooks/exhaustive-deps
  const getUser = useCallback(async (userId) => {
    const token = localStorage.getItem("token");
    try {
      const response = await fetch(
        `http://localhost:8085/user/get-user/${userId}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        },
      );
      const data = await response.json();
      console.log("Feched user:", data); // Debugging line
      dispatch(setUsersData(data));

      // Assuming you have a Redux action to set user data
      return data;
    } catch (error) {
      console.error("Error fetching users:", error);
      return [];
    }
  });

  const likeUser = async (userId) => {
    const token = localStorage.getItem("token");
    try {
      if (!loggedInUser) {
        navigate("/login");
        return;
      }
      const response = await fetch(
        `http://localhost:8085/user/updateLike/${userId}/${loggedInUser?.userId}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        },
      );
      const contentType = response.headers.get("content-type");
      let data;

      if (contentType && contentType.includes("application/json")) {
        data = await response.json();
      } else {
        data = await response.text(); // fallback to plain text
      }
      console.log("User liked successfully:", data); // Debugging line
      dispatch(updateLike({ userId: userId, senderId: loggedInUser?.userId }));

      return data;
    } catch (error) {
      console.error("Error accepting notification:", error);
      return null;
    }
  };

  const addCloseFriend = async (userId) => {
    const token = localStorage.getItem("token");
    try {
      const response = await fetch(
        `http://localhost:8085/user/close-friend/add/${loggedInUser?.userId}/${userId}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        },
      );
      const data = await response.json();
      console.log("Close friend addedd successfully", data);
      dispatch(updateCloseFriends({ friendId: userId, type: "add" }));
    } catch (error) {
      console.log("Some error occures while adding close friend");
    }
  };

  const removeCloseFriend = async (userId) => {
    const token = localStorage.getItem("token");
    try {
      const response = await fetch(
        `http://localhost:8085/user/close-friend/remove/${loggedInUser?.userId}/${userId}`,
        {
          method: "DELETE",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        },
      );
      const data = await response.json();
      console.log("Close friend removed successfully", data);
      dispatch(updateCloseFriends({ friendId: userId, type: "remove" }));
    } catch (error) {
      console.log("Some error occures while removing close friend");
    }
  };

  // Connection related APIs
  const acceptFriendRequest = async (notificationId, senderId, receiverId) => {
    const token = localStorage.getItem("token");
    try {
      const response = await fetch(
        `http://localhost:8085/connection/accept/${notificationId}/${senderId}/${receiverId}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        },
      );
      const data = await response.json();

      console.log("Notification accepted:", data); // Debugging line
      const payload = {
        message: data?.messageResponse?.message,
        success: data?.messageResponse?.status == "success" ? true : false,
        error: data?.messageResponse?.status == "error" ? true : false,
      };
      dispatch(setMessage(payload));
      dispatch(filterNotifications(notificationId));
      dispatch(updateFollowings(senderId));

      return data;
    } catch (error) {
      console.error("Error accepting notification:", error);
      return null;
    }
  };

  const fetchFriendRequests = async () => {
    const token = localStorage.getItem("token");

    try {
      const response = await fetch(
        `http://localhost:8085/connection/get-all/${loggedInUser?.userId}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        },
      );
      const data = await response.json();
      console.log("Fetched Friend Requests are:", data); // Debugging line

      const detailedRequests = await Promise.all(
        data.map(async (request) => {
          const senderId = request.requestFrom;

          // First, try to find the user in usersList
          let user = usersList.find((u) => u.userId === senderId);

          // If not found, fetch from backend
          if (!user) {
            try {
              user = await getUser(senderId);
            } catch (err) {
              console.error(`Error fetching user ${senderId}:`, err);
              user = null;
            }
          }

          return {
            request: request,
            senderDetails: user, // Will be null if failed
          };
        }),
      );
      console.log("Detailed Friend Requests:", detailedRequests);
      dispatch(updateFriendRequests(detailedRequests));
      // Assuming you have a Redux action to set user data
      return detailedRequests;
    } catch (error) {
      console.error("Error fetching users:", error);
      return [];
    }
  };

  return {
    getAllUsers,
    acceptFriendRequest,
    likeUser,
    getUser,
    addCloseFriend,
    removeCloseFriend,
    fetchFriendRequests,
  };
};
export default useUserData;
