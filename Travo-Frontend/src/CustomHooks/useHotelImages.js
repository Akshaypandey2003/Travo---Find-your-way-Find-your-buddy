/* eslint-disable no-unused-vars */
import { useEffect, useState } from "react";
import axios from "axios";
import { UNSPLASH_API_KEY, UNSPLASH_URL } from "../Constants/constants";
import { useDispatch } from "react-redux";
import { setHotelImages } from "../Redux/Slices/hotelSlice";

// const UNSPLASH_ACCESS_KEY = "UjpJYGVWuo2WnP8llshb-ELpDbOKSzXPp7wxCN2iY0s";
// const UNSPLASH_URL = `https://api.unsplash.com/search/photos?query=hotel&client_id=${UNSPLASH_ACCESS_KEY}`;

const useHotelImages = (hotelCount) => {

    const dispatch = useDispatch();


    const fetchImages = async (hotelCount) => {

      try {
        const url = UNSPLASH_URL+UNSPLASH_API_KEY;
        const response = await axios.get(url);
        
        const images = response.data.results;

        if (images.length > 0) {
          // ✅ Shuffle and assign one unique image per hotel
          const shuffledImages = images.sort(() => 0.5 - Math.random());
          console.log("Images response: ",shuffledImages.slice(0, hotelCount));
          
           dispatch(
            setHotelImages(shuffledImages.slice(0, hotelCount))
           )
        }
      } catch (error) {
        console.error("Error fetching hotel images:", error);
      }
    };

    return { fetchImages };

};
export default useHotelImages;
