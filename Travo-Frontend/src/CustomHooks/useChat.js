import { useDispatch, useSelector } from "react-redux";
import {
  addChats,
  addMessageToChat,
  setActiveChat,
  setMessagesForChat,
  updateChat,
  updateMessage,
} from "../Redux/Slices/chatSlice";
import useAuth from "./useAuth";
import { useCallback } from "react";
import useUserData from "./useUserData";
import { apiRequest, getApiErrorMessage } from "../lib/api";
import { notifyApiError } from "../lib/apiNotifications";

/* eslint-disable no-unused-vars */
const useChat = () => {
  const dispatch = useDispatch();
  const loggedInUser = useSelector((store) => store.auth.user);
  const { uploadImageToCloudinary } = useAuth();
  const usersList = useSelector((store) => store.auth.usersList);
  const {getUser} = useUserData();

  const createGroup = async (data) => {
    const token = localStorage.getItem("token");
    console.log("Data inside createGroup custom hook: ", data);
    try {
       
      const groupImage = data?.groupImageUrl;
      data.groupImageUrl = "";
      const { data: createdGroup } = await apiRequest(`http://localhost:8085/chat/create`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify(data),
      });
      console.log("Group Created Successfully: ", createdGroup);

      dispatch(addChats(createdGroup));
      dispatch(setActiveChat(createdGroup));

       if (groupImage) {
        const { url, public_id } = await uploadImageToCloudinary(
          groupImage
        );
        createdGroup.groupImageUrl = url;
      }
      console.log("Updated group with image URL: ", createdGroup);
      updateGroupChat(createdGroup.chatId, createdGroup);
      return createdGroup;
    } catch (error) {
      notifyApiError(dispatch, getApiErrorMessage(error));
      console.error("Error while creating new group chat:", error.message);
    }
  };

  const fetchChats = async (userId) => {
    const token = localStorage.getItem("token");
    console.log("Fetching chats for user:", userId);
    try {
      const { data } = await apiRequest(
        `http://localhost:8085/chat/get-chat/${userId}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
          },
        }
      );
      console.log("All chats fetched successfully: ", data);
      dispatch(addChats(data));

      return data;
    } catch (error) {
      notifyApiError(dispatch, getApiErrorMessage(error));
      console.error("Error fetching chats:", error.message);
    }
  };

  const sendMessage = async ({ message }) => {
    const token = localStorage.getItem("token");
    console.log("Received message to send is: ", message);
    try {
      const { data } = await apiRequest(
        `http://localhost:8085/chat/message/send`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
          },
          body: JSON.stringify(message),
        }
      );
      console.log("Message Sent Successfully: ", data);
      dispatch(addMessageToChat({ chatId: data?.chatId, message: data }));
      dispatch(
        updateChat({
          chatId: data.chatId,
          updatedData: {
            recentConversationAt: new Date().toISOString(),
          },
        })
      );

      return data;
    } catch (error) {
      notifyApiError(dispatch, getApiErrorMessage(error));
      console.error("Error sending message:", error.message);
    }
  };

  const startChat = async ({ message, receiver }) => {

    const token = localStorage.getItem("token");
    const payload = {
      participants: [loggedInUser?.userId, receiver],
    };
    console.log("Starting chat with: ", payload.participants);
    try {
      const { data } = await apiRequest(`http://localhost:8085/chat/create`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify(payload),
      });
      console.log("Chat Created Successfully: ", data);

      const messageData = {
        chatId: data?.chatId,
        senderId: message?.senderId,
        messageType: message?.messageType,
        messageContent: message?.messageContent,
      };
      console.log("MEssage data to be sent is : ", messageData);
      const sentMsg = await sendMessage({ message: messageData });
      console.log("Sent msg is: ", sentMsg);
      dispatch(addChats(data));
      dispatch(setActiveChat(data));
      return data;
    } catch (error) {
      notifyApiError(dispatch, getApiErrorMessage(error));
      console.error("Error while creating new chat:", error.message);
    }
  };

  const fetchMessages = async (chatId) => {

    const token = localStorage.getItem("token");
    try {
      const { data } = await apiRequest(
        `http://localhost:8085/chat/message/get-messages/${chatId}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
          },
        }
      );
      console.log("Messages fetched successfully: ", data);
      dispatch(setMessagesForChat({ chatId, messages: data }));
      return data;
    } catch (error) {
      notifyApiError(dispatch, getApiErrorMessage(error));
      console.error("Error fetching messages:", error.message);
    }
  };

  const updateReadStatus = async ({ messageId, chatId }) => {

    const token = localStorage.getItem("token");
    console.log("Updating read status for messageId:", messageId);
    try {
      const { data } = await apiRequest(
        `http://localhost:8085/chat/message/read/${messageId}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
          },
        }
      );

      console.log("Message read status updated successfully: ", data);
      dispatch(
        updateMessage({ messageId: messageId, chatId: chatId, message: data })
      );
      return data;
    } catch (error) {
      notifyApiError(dispatch, getApiErrorMessage(error));
      console.error("Error updating read status:", error.message);
    }
  };

  
  const updateChatMessage = async (messageId, chatId, messageData) => {
    const token = localStorage.getItem("token");
    console.log("Updating message for messageId:", messageId);
    try {
      const { data } = await apiRequest(
        `http://localhost:8085/chat/message/update/${messageId}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
          },
          body: JSON.stringify(messageData),
        }
      );
      console.log("Message updated successfully: ", data);
      dispatch(
        updateMessage({ messageId: messageId, chatId: chatId, message: data })
      );
      return data;
    } catch (error) {
      notifyApiError(dispatch, getApiErrorMessage(error));
      console.error("Error updating message:", error.message);
    }
  };

  const updateFavorite = async (chatId, userId) => {

    const token = localStorage.getItem("token");
    console.log("Updating favorite status for chatId:", chatId);
    try {
      const { data } = await apiRequest(
        `http://localhost:8085/chat/update-favorite/${chatId}/${userId}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
          },
        }
      );
      console.log("Favorite status updated successfully: ", data);
      dispatch(updateChat({ chatId, updatedData: data }));
      dispatch(setActiveChat(data));
      return data;
    } catch (error) {
      notifyApiError(dispatch, getApiErrorMessage(error));
      console.error("Error updating favorite status:", error.message);
    }
  };

  const updateGroupMembers = async (chatId, members) => {

    const token = localStorage.getItem("token");
    let finalMembers;
    if (Array.isArray(members)) {
      finalMembers = members.map((member) =>
        typeof member === "string" ? member : member.userId
      );
    } else {
      finalMembers = [loggedInUser?.userId];
    }
    console.log(
      "Updating group members for chatId:",
      chatId,
      " with members:",
      finalMembers
    );
    try {
      const { data } = await apiRequest(
        `http://localhost:8085/chat/update-group-members/${chatId}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
          },
          body: JSON.stringify(finalMembers),
        }
      );
      console.log("Group members updated successfully: ", data);
      dispatch(updateChat({ chatId, updatedData: data }));
      dispatch(setActiveChat(data));
      return data;
    } catch (error) {
      notifyApiError(dispatch, getApiErrorMessage(error));
      console.error("Error updating group members:", error.message);
    }
  };
  
  const updateGroupChat = async (chatId, groupData) => {

    const token = localStorage.getItem("token");
    console.log("Updating group for chatId:", chatId);
    try {
      const { data } = await apiRequest(
        `http://localhost:8085/chat/update/${chatId}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
          },
          body: JSON.stringify(groupData),
        }
      );
      console.log("Group updated successfully: ", data);
      dispatch(updateChat({ chatId, updatedData: data }));
      dispatch(setActiveChat(data));
      return data;
    } catch (error) {
      notifyApiError(dispatch, getApiErrorMessage(error));
      console.error("Error updating group:", error.message);
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
  
      const allFriendIds = [
        ...new Set([
          ...(loggedInUser.followers || []),
          ...(loggedInUser.followings || []),
        ]),
      ];
      console.log("All friend IDs:", allFriendIds);
  
      const friendsData = await Promise.all(
        allFriendIds.map(async (id) => {
          let user = usersList.find((u) => u.userId === id);
          if (!user) {
            try {
              user = await getUser(id);
              console.log("Fetched friend: ", user);
            } catch (err) {
              console.error(`Failed to fetch user ${id}:`, err);
              user = null;
            }
          }
          console.log("Fetched friend: ", user);
          return user;
        })
      ).catch((err) => {
        console.error("Promise.all error:", err);
      });
      console.log("All fetched friends data inside custom hook: ", friendsData);
      return friendsData.filter(Boolean);
    }, [loggedInUser, usersList, getUser]); // ✅ Add these
  
  return {
    fetchChats,
    sendMessage,
    startChat,
    fetchMessages,
    updateChatMessage,
    updateReadStatus,
    updateFavorite,
    createGroup,
    updateGroupMembers,
    updateGroupChat,
    getGroupParticipantsData,
    extractAllFriends
  };
};
export default useChat;
