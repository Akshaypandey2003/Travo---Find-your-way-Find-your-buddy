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
      // const existing = Array.isArray(state.notifications)
      //   ? state.notifications
      //   : [];
    
      // const incoming = Array.isArray(action.payload)
      //   ? action.payload
      //   : [action.payload]; // wrap single object in an array
    
      // state.notifications = [...existing, ...incoming];
      state.notifications = action.payload;
    },
    
    addNewNotification: (state, action) => {
      state.newNotification= action.payload;
      // if(state.notifications.length>0 ){
      //   state.notifications = state.notifications.filter((notification) => {
      //     return notification.notificationFrom !== action.payload.notificationFrom || notification.type !== action.payload.type;
      //   });
      // }
      state.notifications.push(action.payload);
      // if (state.notifications.length > 5) {
      //   state.notifications.pop(); // remove the oldest
      // }
      state.notificationStatus = true;
    },
    clearNotifications: (state) => {
      state.newNotification=[];
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
