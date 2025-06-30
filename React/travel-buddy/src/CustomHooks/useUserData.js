/* eslint-disable no-unused-vars */

import { shallowEqual, useDispatch, useSelector } from "react-redux";
import {
  setUsersData,
  updateCloseFriends,
  updateLike,
} from "../Redux/Slices/authSlice";
import {
  addNotification,
  clearNotificationsData,
  filterNotifications,
  setMessage,
} from "../Redux/Slices/notificationSlice";
import store from "../Redux/Store";
import { useNavigate } from "react-router-dom";
import { useCallback } from "react";

const useUserData = () => {
  const dispatch = useDispatch();
  const notifications = useSelector(
    (store) => store.notifications,
    shallowEqual
  );
  const loggedInUser = useSelector((state) => state.auth.user);
  const navigate = useNavigate();
  const usersList = useSelector((store) => store.auth.usersList); // existing cached users

  const getAllUsers = async (page) => {
    try {
      const response = await fetch(
        `http://localhost:8085/user/getAllUsers?page=${page}&size=10`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );
      const data = await response.json();
      console.log("All Users data received:", data); // Debugging line

      dispatch(setUsersData(data));

      // Assuming you have a Redux action to set user data
      return data;
    } catch (error) {
      console.error("Error fetching users:", error);
      return [];
    }
  };
  const getAllNotifications = async (id) => {
    try {
      const response = await fetch(
        `http://localhost:8085/user/get-notifications/${id}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
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

  const acceptFriendRequest = async (notificationId, senderId, receiverId) => {
    try {
      const response = await fetch(
        `http://localhost:8085/user/accept-friend-request/${notificationId}/${senderId}/${receiverId}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
        }
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

      return data;
    } catch (error) {
      console.error("Error accepting notification:", error);
      return null;
    }
  };
  const deleteNotification = async (notificationId) => {
    try {
      const response = await fetch(
        `http://localhost:8085/user/delete-notification/${notificationId}`,
        {
          method: "DELETE",
          headers: {
            "Content-Type": "application/json",
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

  const likeUser = async (userId) => {
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
          },
        }
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
  const getUser = async (userId) => {
    try {
      const response = await fetch(
        `http://localhost:8085/user/get-user/${userId}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
        }
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
  };

  const addCloseFriend = async (userId) => {
    try {
      const response = await fetch(
        `http://localhost:8085/user/addCloseFriend/${loggedInUser?.userId}/${userId}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );
      const data = await response.json();
      console.log("Close friend addedd successfully", data);
      dispatch(updateCloseFriends({ friendId: userId, type: "add" }));
    } catch (error) {
      console.log("Some error occures while adding close friend");
    }
  };
  const removeCloseFriend = async (userId) => {
    try {
      const response = await fetch(
        `http://localhost:8085/user/removeCloseFriend/${loggedInUser?.userId}/${userId}`,
        {
          method: "DELETE",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );
      const data = await response.json();
      console.log("Close friend removed successfully", data);
      dispatch(updateCloseFriends({ friendId: userId, type: "remove" }));
    } catch (error) {
      console.log("Some error occures while removing close friend");
    }
  };

  const getGroupParticipantsData = async (participantIds = []) => {
    const existingUsersMap = new Map(
      usersList.map((user) => [user.userId, user])
    );

    // Separate existing users and missing userIds
    const existingUsers = [];
    const missingUserIds = [];

    for (const id of participantIds) {
      if (existingUsersMap.has(id)) {
        existingUsers.push(existingUsersMap.get(id));
      } else {
        missingUserIds.push(id);
      }
    }

    // Fetch missing users
    const fetchedUsers = await Promise.all(
      missingUserIds.map(async (id) => {
        try {
          const user = await getUser(id);
          return user;
        } catch (err) {
          console.error("Error fetching user:", id, err);
          return null;
        }
      })
    );

    // Filter out failed fetches (null)
    const finalFetchedUsers = fetchedUsers.filter(Boolean);

    // Merge and return
    return [...existingUsers, ...finalFetchedUsers];
  };

 const extractAllFriends = useCallback(async () => {
  console.log("inside extractFriends");
  if (!loggedInUser) return [];

  const allFriendIds = [...new Set([
    ...(loggedInUser.followers || []),
    ...(loggedInUser.followings || []),
  ])];
 console.log("All friend IDs:", allFriendIds);

  const friendsData = await Promise.all(
    allFriendIds.map(async (id) => {
      let user = usersList.find((u) => u.userId === id);
      if (!user) {
        try {
          user = await getUser(id);
          console.log("Fetched friend: ",user);
        } catch (err) {
          console.error(`Failed to fetch user ${id}:`, err);
          user = null;
        }
      }
      console.log("Fetched friend: ",user);
      return user;
    })
  ).catch((err) => {
  console.error("Promise.all error:", err);
});
  console.log("All fetched friends data inside custom hook: ", friendsData);
  return friendsData.filter(Boolean);
}, [loggedInUser, usersList, getUser]); // ✅ Add these


  return {
    getAllUsers,
    getAllNotifications,
    acceptFriendRequest,
    deleteNotification,
    likeUser,
    getUser,
    addCloseFriend,
    removeCloseFriend,
    getGroupParticipantsData,
    extractAllFriends
  };
};
export default useUserData;
