/* eslint-disable no-unused-vars */
import { createSlice } from "@reduxjs/toolkit";

const tripSlice = createSlice({
  name: "trip",
  initialState: {
    trips: [],
    tripFeedbacks: {},
  },
  reducers: {
    addTrips: (state, action) => {
      const existingTripIds = new Set(state.trips.map((trip) => trip?.tripId));

      if (Array.isArray(action.payload)) {
        const newTrips = action.payload.filter(
          (trip) => !existingTripIds.has(trip.tripId)
        );
        state.trips = [...state.trips, ...newTrips];
      } else if (
        action.payload &&
        typeof action.payload === "object" &&
        action.payload.tripId
      ) {
        if (!existingTripIds.has(action.payload.tripId)) {
          state.trips.push(action.payload);
        } else {
          const index = state.trips.findIndex(
            (trip) => trip.tripId === action.payload.tripId
          );
          if (index !== -1) {
            state.trips[index] = action.payload;
            console.log("Trip updated:", state.trips[index]);
          }
        }
      } else {
      }
    },

    updateTripFeedbacks: (state, action) => {
      const { tripId, feedbacks } = action.payload;

      if (!tripId) {
        return;
      }

      // Normalize single feedback to array
      const normalizedFeedbacks = Array.isArray(feedbacks)
        ? feedbacks
        : [feedbacks];

      // If feedbacks already exist for this trip
      if (state.tripFeedbacks[tripId]) {
        const existingFeedbacks = state.tripFeedbacks[tripId];

        // Replace feedbacks from same authorId if they exist, otherwise append
        const updatedFeedbacks = [...existingFeedbacks];

        normalizedFeedbacks.forEach((newFeedback) => {
          const index = updatedFeedbacks.findIndex(
            (f) => f.authorId === newFeedback.authorId
          );

          if (index !== -1) {
            // Replace old feedback from the same author
            updatedFeedbacks[index] = newFeedback;
          } else {
            // Add new feedback
            updatedFeedbacks.push(newFeedback);
          }
        });

        state.tripFeedbacks[tripId] = updatedFeedbacks;
      } else {
        // No existing feedbacks for this trip, just assign
        state.tripFeedbacks[tripId] = normalizedFeedbacks;
      }

    },
    filterTrips:(state,action)=>{
      state.trips = state.trips?.filter(
        (trip) => trip?.tripId !== action.payload
      );
    }
  },
});
export const { addTrips, updateTripFeedbacks,filterTrips } = tripSlice.actions;
export default tripSlice.reducer;
