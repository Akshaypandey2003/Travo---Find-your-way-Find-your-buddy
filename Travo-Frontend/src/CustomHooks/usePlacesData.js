/* eslint-disable no-unused-vars */
import { useState, useEffect, useCallback } from "react";
import { useDispatch, useSelector } from "react-redux";
import axios from "axios";
import { 
    setPlacesData, 
} from "../Redux/Slices/placeSlice";
import { GOOGLE_API_KEY, PLACES_BASE_URL } from "../Constants/constants";
import useWeatherData from "./useWeatherData";

const usePlacesData = () => {
    const dispatch = useDispatch();
    const places = useSelector((state) => state.places?.placesData || []);
    const nextPageToken = useSelector((state) => state.places?.nextPageToken || null);
    const descriptions = useSelector((state) => state.places?.descriptions || {});  
    const weatherData = useSelector((state) => state.places?.weatherData || {});

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const { getWeatherForPlaces } = useWeatherData();

    const getPlaces = useCallback(async (useNextPage = false) => {

        if (!useNextPage && places.length > 0) return;

        setLoading(true);
        setError(null);

        try {

            let url = `${PLACES_BASE_URL}&key=${GOOGLE_API_KEY}`;
            if (useNextPage && nextPageToken) {
                url += `&pagetoken=${nextPageToken}`;
            }

            const response = await axios.get(url);

            const pageToken =  response.data.next_page_token || null;

            if (response.data.results) {
                const newPlaces = response.data.results;

                // 🔹 Step 1: Fetch Weather Data in Parallel
                const weatherData = await getWeatherForPlaces(newPlaces);

                // 🔹 Step 2: Fetch Wikipedia Summaries in Parallel
                const descriptionPromises = newPlaces.map(place => getWikipediaSummary(place.name,place.place_id));
                const descriptions = await Promise.all(descriptionPromises);
                const formattedDescriptions = descriptions.reduce((acc, curr) => {
                    if (curr) acc[curr.placeId] = curr.description;
                    return acc;
                }, {});

                // 🔹 Step 3: Dispatch a single action for all data
                dispatch(setPlacesData({placesData:newPlaces,weatherInfo:weatherData,desc:formattedDescriptions,token:pageToken}));
                // dispatch(setWeatherData(weatherData));
                // dispatch(setPlaceDescription(formattedDescriptions));

            } else {
                setError("No tourist attractions found.");
            }
        } catch (error) {
            setError(error.response?.data?.message || "Failed to fetch data.");
        } finally {
            setLoading(false);
        }
    }, [dispatch, places, nextPageToken]);

    // 🔹 Fetch Wikipedia Summary
    const getWikipediaSummary = async (placeName,placeId) => {
        try {
            const searchResponse = await axios.get(
                `https://en.wikipedia.org/w/api.php?action=query&format=json&list=search&srsearch=${encodeURIComponent(placeName)}&origin=*`
            );

            if (searchResponse.data.query.search.length > 0) {
                const firstResultTitle = searchResponse.data.query.search[0].title;
                const summaryResponse = await axios.get(
                    `https://en.wikipedia.org/api/rest_v1/page/summary/${encodeURIComponent(firstResultTitle)}`
                );

                return {
                    placeId,
                    description: summaryResponse.data.extract || "No description available."
                };
            } else {
                return { placeId, description: "No Wikipedia page found." };
            }
        } catch (error) {
            return { placeId, description: "Error fetching Wikipedia data." };
        }
    };

    useEffect(() => {
        getPlaces();
    }, []);

    return { places, descriptions, loading, error, getPlaces };
};

export default usePlacesData;
