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

/* eslint-disable no-unused-vars */
const useChat = () => {
  const dispatch = useDispatch();
  const loggedInUser = useSelector((store) => store.auth.user);
  const { uploadImageToCloudinary } = useAuth();

  const createGroup = async (data) => {
    console.log("Data inside createGroup custom hook: ", data);
    try {
      if (data?.groupImageUrl) {
        const { url, public_id } = await uploadImageToCloudinary(
          data?.groupImageUrl
        );
        data.groupImageUrl = url;
      }
      // else
      // data.groupImageUrl = GROUP_DEFAULT_PIC;

      const response = await fetch(`http://localhost:8085/auth/chats/create`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(data),
      });
      if (!response.ok) {
        throw new Error("Failed to create group");
      }
      const createdGroup = await response.json();
      console.log("Group Created Successfully: ", createdGroup);

      dispatch(addChats(createdGroup));
      dispatch(setActiveChat(createdGroup));

      return createdGroup;
    } catch (error) {
      console.error("Error while creating new group chat:", error.message);
    }
  };

  const fetchChats = async (userId) => {
    console.log("Fetching chats for user:", userId);
    try {
      const response = await fetch(
        `http://localhost:8085/auth/chats/get-chat/${userId}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );
      if (!response.ok) {
        throw new Error("Failed to fetch chats");
      }
      const data = await response.json();
      console.log("All chats fetched successfully: ", data);
      dispatch(addChats(data));

      return data;
    } catch (error) {
      console.error("Error fetching chats:", error.message);
    }
  };
  const sendMessage = async ({ message }) => {
    console.log("Received message to send is: ", message);
    try {
      const response = await fetch(
        `http://localhost:8085/auth/chats/message/send`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(message),
        }
      );
      if (!response.ok) {
        throw new Error("Failed to send message");
      }
      const data = await response.json();
      console.log("Message Sent Successfully: ", data);
      dispatch(addMessageToChat({ chatId: data?.chatId, message: data }));

      return data;
    } catch (error) {
      console.error("Error sending message:", error.message);
    }
  };

  const startChat = async ({ message, receiver }) => {
    const payload = {
      participants: [loggedInUser?.userId, receiver],
    };
    try {
      const response = await fetch(`http://localhost:8085/auth/chats/create`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(payload),
      });
      if (!response.ok) {
        throw new Error("Failed to create chat");
      }
      const data = await response.json();
      console.log("Chat Created Successfully: ", data);

      const messageData = {
        chatId: data?.chatId,
        senderId: message?.senderId,
        messageType: message?.messageType,
        messageContent: message?.messageContent,
      };
      const sentMsg = await sendMessage(messageData);
      dispatch(addChats(data));
      dispatch(setActiveChat(data));
      return data;
    } catch (error) {
      console.error("Error while creating new chat:", error.message);
    }
  };

  const fetchMessages = async (chatId) => {
    try {
      const response = await fetch(
        `http://localhost:8085/auth/chats/message/get-messages/${chatId}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );
      if (!response.ok) {
        throw new Error("Failed to fetch messages");
      }
      const data = await response.json();
      console.log("Messages fetched successfully: ", data);
      dispatch(setMessagesForChat({ chatId, messages: data }));
      return data;
    } catch (error) {
      console.error("Error fetching messages:", error.message);
    }
  };

  const updateReadStatus = async ({ messageId, chatId }) => {
    console.log("Updating read status for messageId:", messageId);
    try {
      const response = await fetch(
        `http://localhost:8085/auth/chats/message/read/${messageId}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );

      if (!response.ok) {
        throw new Error("Failed to update read status");
      }
      const data = await response.json();
      console.log("Message read status updated successfully: ", data);
      dispatch(
        updateMessage({ messageId: messageId, chatId: chatId, message: data })
      );
      return data;
    } catch (error) {
      console.error("Error updating read status:", error.message);
    }
  };
  const updateChatMessage = async (messageId, chatId, messageData) => {
    console.log("Updating message for messageId:", messageId);
    try {
      const response = await fetch(
        `http://localhost:8085/auth/chats/message/update/${messageId}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(messageData),
        }
      );
      if (!response.ok) {
        throw new Error("Failed to update message");
      }
      const data = await response.json();
      console.log("Message updated successfully: ", data);
      dispatch(
        updateMessage({ messageId: messageId, chatId: chatId, message: data })
      );
      return data;
    } catch (error) {
      console.error("Error updating message:", error.message);
    }
  };

  const updateFavorite = async (chatId, userId) => {
    console.log("Updating favorite status for chatId:", chatId);
    try {
      const response = await fetch(
        `http://localhost:8085/auth/chats/update-favorite/${chatId}/${userId}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );
      if (!response.ok) {
        throw new Error("Failed to update favorite status");
      }
      const data = await response.json();
      console.log("Favorite status updated successfully: ", data);
      dispatch(updateChat({ chatId, updatedData: data }));
      dispatch(setActiveChat(data));
      return data;
    } catch (error) {
      console.error("Error updating favorite status:", error.message);
    }
  };

  const updateGroupMembers = async (chatId, members) => {
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
      const response = await fetch(
        `http://localhost:8085/auth/chats/update-group-members/${chatId}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(finalMembers),
        }
      );
      if (!response.ok) {
        throw new Error("Failed to update group members");
      }
      const data = await response.json();
      console.log("Group members updated successfully: ", data);
      dispatch(updateChat({ chatId, updatedData: data }));
      dispatch(setActiveChat(data));
      return data;
    } catch (error) {
      console.error("Error updating group members:", error.message);
    }
  };

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
  };
};
export default useChat;
