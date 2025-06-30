/* eslint-disable no-unused-vars */
import { createSlice } from "@reduxjs/toolkit";

const authSlice = createSlice({
  name: "auth",
  initialState: {
    user: null,
    usersList: [],
    profileStatus: 0,
    theme: "light",
    loading: false,
    error: null,
    success: null,
    nextPageToken: true,
  },
  reducers: {
    registerSuccess: (state, action) => {
      state.user = action.payload;
      state.error = null;
    },
    loginSuccess: (state, action) => {
      state.user = action.payload;
      state.error = null;
      state.usersList = state.usersList?.filter(
        (user) => user?.userId != action.payload?.userId
      );
    },
    authFailure: (state, action) => {
      state.error = action.payload;
    },
    authSucess: (state, action) => {
      state.success = action.payload;
    },
    addTrips: (state, action) => {
      state.user.trips.push(action.payload);
    },
    removeTrips: (state, action) => {
      state.user.trips = state.user.trips?.filter(
        (trip) => trip?.tripId !== action.payload
      );
    },
    logout: (state) => {
      state.user = null;
      state.usersList = null;
      state.profileStatus = 0;
      state.loading = false;
      state.error = null;
      state.success = null;
      state.nextPageToken = true;
    },
    updateUser: (state, action) => {
      state.user = action.payload;
      state.error = null;
    },
    updateUserSuccess: (state, action) => {
      state.user = action.payload;
      state.error = null;
    },
    profileCompletion: (state, action) => {
      state.profileStatus = action.payload;
    },
    setUsersData: (state, action) => {
      const incomingUsers = Array.isArray(action.payload)
        ? action.payload
        : [action.payload];

      const filteredUsers = incomingUsers.filter(
        (user) =>
          user?.userId != null &&
          user?.userId !== state?.user?.userId &&
          !state.usersList?.some(
            (existingUser) => existingUser.userId === user.userId
          )
      );

      // const newUser = action.payload;

      state.usersList = Array.isArray(state.usersList)
        ? [...state.usersList, ...filteredUsers]
        : [...filteredUsers];
    },
    updateLike: (state, action) => {
      const { userId, senderId } = action.payload;
      const userIndex = state.usersList.findIndex(
        (user) => user.userId === userId
      );
      if (userIndex !== -1) {
        const likesArray = state.usersList[userIndex].likes;

        if (!Array.isArray(likesArray)) {
          state.usersList[userIndex].likes = [senderId];
          return;
        } else if (likesArray.includes(senderId)) {
          // Remove like
          state.usersList[userIndex].likes = likesArray.filter(
            (id) => id !== senderId
          );
        } else {
          // Add like
          likesArray.push(senderId);
        }
      }
    },
    setNextPageToken: (state, action) => {
      state.nextPageToken = action.payload;
    },
    changeTheme: (state) => {
      state.theme = state.theme === "light" ? "dark" : "light";
    },
    updateCloseFriends: (state, action) => {
      const { friendId, type } = action.payload;
      if (type === "add") {
        if (!state.user.closeFriends.includes(friendId)) {
          state.user.closeFriends.push(friendId);
        }
      } else if (type === "remove") {
        state.user.closeFriends = state.user.closeFriends.filter(
          (id) => id !== friendId
        );
      }
    },
    updateTripRequest: (state, action) => {
      const { requestTo, requestFrom, tripId } = action.payload;
      const userIndex = state.usersList?.findIndex(
        (user) => user.userId === requestTo
      );
      if (userIndex != -1) {
        const trips = state.usersList[userIndex]?.trips;
        const tripIndex = trips?.findIndex((trip) => trip.tripId === tripId);
        if (tripIndex != -1) {
          const trip = trips[tripIndex];
          // if (!trip.tripRequests.includes(requestFrom)) {
          trip.tripRequests.push(requestFrom);
          // }
        }
      }
    },
    removeTripRequest: (state, action) => {
      const { tripId, tripMember } = action.payload;
      const tripIndex = state.user?.trips?.findIndex(
        (trip) => trip.tripId === tripId
      );

      if (tripIndex !== -1) {
        // Properly update the tripRequests array
        state.user.trips[tripIndex].tripRequests = state.user.trips[
          tripIndex
        ].tripRequests.filter((item) => item !== tripMember);
      }
    },
    removeTripMembers: (state,action)=>{
      const {tripId, memberId} = action.payload;
        const tripIndex = state.user?.trips?.findIndex(
        (trip) => trip.tripId === tripId
      );

      if (tripIndex !== -1) {
        // Properly update the tripRequests array
        state.user.trips[tripIndex].tripMembers = state.user.trips[
          tripIndex
        ].tripMembers.filter((item) => item !== memberId);
      }
    },
    updateTripMembers: (state, action) => {
      const { tripId, tripMember } = action.payload;
      const tripIndex = state.user.trips?.findIndex(
        (trip) => trip.tripId == tripId
      );
      if (tripIndex != -1) {
        state.user.trips[tripIndex].tripMembers.push(tripMember);
      }
    },
  },
});
export const {
  registerSuccess,
  loginSuccess,
  authFailure,
  authSucess,
  setUsersData,
  logout,
  updateUser,
  updateUserSuccess,
  profileCompletion,
  changeTheme,
  setNextPageToken,
  updateLike,
  addTrips,
  updateCloseFriends,
  updateTripRequest,
  removeTrips,
  updateTripMembers,
  removeTripRequest,
  removeTripMembers,
} = authSlice.actions;
export default authSlice.reducer;
