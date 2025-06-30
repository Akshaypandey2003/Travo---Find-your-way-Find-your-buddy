import { configureStore, combineReducers } from "@reduxjs/toolkit";
import { persistStore, persistReducer } from "redux-persist";
import storage from "redux-persist/lib/storage"; // Uses localStorage
import placeReducer from "./Slices/placeSlice"; // Your existing place reducer
import hotelReducer from "./Slices/hotelSlice"; // Your existing place reducer
import authReducer from "./Slices/authSlice"; // Your existing place reducer
import blogReducer from "./Slices/blogsSlice"; // Your existing place reducer
import commentReducer from "./Slices/commentSlice"; // Your existing place reducer
import notificationReducer from "./Slices/notificationSlice"; // Your existing place reducer
import chatReducer from "./Slices/chatSlice"; // Your existing place reducer

// 🔹 Step 1: Configure persist settings
const persistConfig = {
  key: "root", // Key for localStorage
  storage, // Use localStorage to persist
  whitelist: ["places","hotels","auth","notifications","blog","comment","chat"], 
};

// 🔹 Step 2: Wrap root reducer with persistedReducer
const rootReducer = combineReducers({
  places: placeReducer, // Persist this slice
  hotels: hotelReducer,
  auth: authReducer, // Persist this slice
  blog: blogReducer,
  comment: commentReducer,
  notifications: notificationReducer,
  chat: chatReducer, // Persist this slice
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
