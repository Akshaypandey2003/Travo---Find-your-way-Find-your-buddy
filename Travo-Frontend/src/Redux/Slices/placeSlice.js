/* eslint-disable no-unused-vars */
import { createSlice } from "@reduxjs/toolkit";

const placeSlice = createSlice({
    name: "places",
    initialState: {
        placesData: [],
        descriptions: {},
        weatherData: {},
        placesDetails: {},
        hotelsData: {},
        nextPageToken: "",
        hotelsNextPageToken: "",
    },
    reducers: {
        setPlacesData: (state, action) => {
            const { placesData, desc, weatherInfo,token } = action.payload;

            // Prevent duplicates before merging
            const existingPlaceIds = new Set(state.placesData.map(place => place.place_id));
            const filteredPlaces = placesData.filter(place => !existingPlaceIds.has(place.place_id));

            // Merge the data at once
            state.placesData = [...state.placesData, ...filteredPlaces];
            state.descriptions = { ...state.descriptions, ...desc };
            state.weatherData = { ...state.weatherData, ...weatherInfo };
            state.nextPageToken = token
        },
        resetData: (state)=>{
            state.placesData = [];
            state.descriptions = {};
            state.nextPageToken = "";
            state.weatherData = {};
        },
        setPlacesDetails:(state,action)=>{

            state.placesDetails = {...state.placesDetails, ...action.payload};
        },
        setHotelsData:(state,action)=>{
            
            const {hotelsData,nextPageToken} = action.payload; 
            state.hotelsData = {...state.hotelsData, ...hotelsData};
            state.hotelsNextPageToken = nextPageToken;
        }
    },
});

export default placeSlice.reducer;
export const { setPlacesData,resetData,setPlacesDetails,setHotelsData } = placeSlice.actions;
