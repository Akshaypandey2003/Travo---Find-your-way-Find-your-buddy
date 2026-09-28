import { configureStore, combineReducers } from "@reduxjs/toolkit";
import { persistStore, persistReducer } from "redux-persist";
import storage from "redux-persist/lib/storage"; 
import authReducer from "./Slices/authSlice";
import blogReducer from "./Slices/blogsSlice";
import commentReducer from "./Slices/commentSlice";
import notificationReducer from "./Slices/notificationSlice";
import chatReducer from "./Slices/chatSlice";
import tripReducer from "./Slices/tripSlice";
import apiStatusReducer from "./Slices/apiStatusSlice";

// 🔹 Step 1: Configure persist settings
const persistConfig = {
  key: "root", // Key for localStorage
  storage, // Use localStorage to persist
  whitelist: ["auth","notifications","blog","comment","chat","trip"],
};

// 🔹 Step 2: Wrap root reducer with persistedReducer
const rootReducer = combineReducers({
  auth: authReducer, // Persist this slice
  blog: blogReducer,
  comment: commentReducer,
  notifications: notificationReducer,
  trip: tripReducer,
  chat: chatReducer, // Persist this slice
  apiStatus: apiStatusReducer,
});

const persistedReducer = persistReducer(persistConfig, rootReducer);

// 🔹 Step 3: Create Redux Store
const store = configureStore({
  reducer: persistedReducer,
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: false, // Ignore serialization warnings
    }),
});

// 🔹 Step 4: Create persistor
export const persistor = persistStore(store);
export default store;
