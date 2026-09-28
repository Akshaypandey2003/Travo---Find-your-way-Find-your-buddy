import { createSlice } from "@reduxjs/toolkit";

const initialState = {
  status: "idle",
  message: "",
  error: null,
  statusCode: null,
  requestKey: null,
};

const apiStatusSlice = createSlice({
  name: "apiStatus",
  initialState,
  reducers: {
    apiStarted: (state, action) => {
      state.status = "loading";
      state.message = "";
      state.error = null;
      state.statusCode = null;
      state.requestKey = action.payload || null;
    },
    apiSucceeded: (state, action) => {
      state.status = "succeeded";
      state.message = action.payload?.message || "";
      state.error = null;
      state.statusCode = action.payload?.statusCode || null;
      state.requestKey = action.payload?.requestKey || null;
    },
    apiFailed: (state, action) => {
      state.status = "failed";
      state.message = action.payload?.message || "Request failed.";
      state.error = action.payload?.error || state.message;
      state.statusCode = action.payload?.statusCode || null;
      state.requestKey = action.payload?.requestKey || null;
    },
    clearApiStatus: () => initialState,
  },
});

export const { apiStarted, apiSucceeded, apiFailed, clearApiStatus } =
  apiStatusSlice.actions;
export default apiStatusSlice.reducer;