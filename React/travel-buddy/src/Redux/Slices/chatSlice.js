/* eslint-disable no-unused-vars */
import { createSlice } from "@reduxjs/toolkit";

const chatSlice = createSlice({
  name: "chat",
  initialState: {
    chats: [],
    activeChat: {},
    nextPageTokens: {},
    success: false,
    failure: false,
    messages: {},
  },
  reducers: {
    addChats: (state, action) => {
      const incomingChats = Array.isArray(action.payload)
        ? [...action.payload]
        : [action.payload];

      const mergedChats = [
        ...state.chats.filter(
          (existing) =>
            existing?.chatId != null &&
            !incomingChats.some((newChat) => newChat.chatId === existing.chatId)
        ),
        ...incomingChats,
      ];
      state.chats = mergedChats.sort(
        (a, b) => new Date(b.createdAt) - new Date(a.createdAt)
      );
    },
    setActiveChat: (state, action) => {
      state.activeChat = action.payload;
    },

    setMessagesForChat: (state, action) => {
      const { chatId, messages } = action.payload;

      if (!Array.isArray(messages)) {
        console.error("Payload messages is not an array:", messages);
        return;
      }

      if (!state.messages[chatId]) {
        state.messages[chatId] = [];
      }

      // Combine and filter out duplicates by messageId
      const existingMessageIds = new Set(
        state.messages[chatId].map((msg) => msg.messageId)
      );

      const uniqueMessages = messages.filter(
        (msg) => !existingMessageIds.has(msg.messageId)
      );

      state.messages[chatId] = [...state.messages[chatId], ...uniqueMessages];

      state.messages[chatId].sort(
        (a, b) => new Date(a.createdAt) - new Date(b.createdAt)
      );
    },

    addMessageToChat: (state, action) => {
      const { chatId, message } = action.payload;

      if (!chatId) {
        console.error("Invalid chatId in addMessageToChat");
        return;
      }

      if (!state.messages[chatId]) {
        state.messages[chatId] = [];
      }
      
      state.messages[chatId] = state.messages[chatId].filter((msg)=>msg.messageId!=message.messageId);
      state.messages[chatId].push(message);
      state.messages[chatId].sort(
          (a, b) => new Date(a.createdAt) - new Date(b.createdAt)
        );
        
      // const exists = state.messages[chatId].some(
      //   (msg) => msg.messageId === message.messageId
      // );

      // if (!exists) {
      //   state.messages[chatId].push(message);
      //   state.messages[chatId].sort(
      //     (a, b) => new Date(a.createdAt) - new Date(b.createdAt)
      //   );
      // }
    },

    updateMessage: (state, action) => {
      const { messageId, chatId, message } = action.payload;

      const index = state.messages[chatId].findIndex(
        (msg) => msg.messageId === messageId
      );
      if (index !== -1) {
        state.messages[chatId][index] = message;
      }
    },

    clearChats: (state) => {
      (state.chats = []),
        (state.messages = {}),
        (state.activeChat = {}),
        (state.nextPageTokens = {});
    },

    updateChat: (state, action) => {
      const { chatId, updatedData } = action.payload;
      console.log("Updating chat with chatId:", chatId, "and data:", updatedData);
      const chatIndex = state.chats.findIndex((chat) => chat.chatId === chatId);
      if (chatIndex !== -1) {
        state.chats[chatIndex] = { ...state.chats[chatIndex], ...updatedData };
      }
    }
  },
});
export const {
  addChats,
  setActiveChat,
  setMessagesForChat,
  addMessageToChat,
  updateMessage,
  clearChats,
  updateChat
} = chatSlice.actions;
export default chatSlice.reducer;
