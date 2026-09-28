// redux/slices/notificationSlice.js
import { createSlice } from "@reduxjs/toolkit";

const notificationSlice = createSlice({
  name: "notifications",
  initialState: {
    notifications: [],
    newNotification: {},
    userName:"",
    success:false,
    error:false,
    message:"",
    notificationStatus:false,
  },
  reducers: {
    addNotification: (state, action) => {
      state.notifications = Array.isArray(action.payload) ? action.payload : [];
    },
    
    addNewNotification: (state, action) => {
      state.newNotification= action.payload;
      state.notifications.push(action.payload);
      state.notificationStatus = true;
    },
    clearNotifications: (state) => {
      state.newNotification={};
      state.success = false;
      state.error = false;
      state.message = "";
      state.notificationStatus = false;
    },
    setMessage: (state,action)=>{
      state.message = action.payload.message;
      state.success = action.payload.success;
      state.error = action.payload.error;
      state.notificationStatus = true;
    },
    // eslint-disable-next-line no-unused-vars
    filterNotifications: (state,action)=>{
      state.notifications = state.notifications.filter((notification) => {
        return notification.notificationId !== action.payload;
      });
    },
    clearNotificationsData: (state)=>{
      state.notifications = [];
      state.newNotification = {};
      state.success = false;
      state.error = false;
      state.message = "";
      state.notificationStatus = false;
    }
  },
});
export const { addNotification, clearNotifications,addNewNotification,setMessage,filterNotifications,clearNotificationsData } = notificationSlice.actions;
export default notificationSlice.reducer;
