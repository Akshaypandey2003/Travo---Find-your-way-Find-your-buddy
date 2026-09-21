/* eslint-disable no-unused-vars */
import { createSlice } from "@reduxjs/toolkit";

const hotelsSlice = createSlice({
    name: "hotels",
    initialState: {
        hotelsData: {},
        hotelsDetails: {},
        hotelImages:[],
        hotelsNextPageToken: "",
    },
    reducers: {
        setHotelsData:(state,action)=>{
            
            const {hotelsData,nextPageToken} = action.payload; 
            state.hotelsData = {...state.hotelsData, ...hotelsData};
            state.hotelsNextPageToken = nextPageToken;
        },
        setHotelsDetails:(state,action)=>{
            state.hotelsDetails = {...state.hotelsDetails, ...action.payload};
        },
        setHotelImages: (state, action) => {
            // Ensure the payload is always an array
            // const newImages = Array.isArray(action.payload) ? action.payload : [];
          
            // Ensure state.hotelImages is an array and append the new images
            state.hotelImages = action.payload;
          }
          
    },
});

export default hotelsSlice.reducer;
export const {setHotelsData,setHotelsDetails,setHotelImages } = hotelsSlice.actions;
