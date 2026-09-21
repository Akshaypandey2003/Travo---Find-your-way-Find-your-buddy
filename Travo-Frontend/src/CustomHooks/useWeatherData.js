/* eslint-disable no-unused-vars */
import { useState, useCallback } from "react";
import { useDispatch, useSelector } from "react-redux";
import axios from "axios";
import { WEATHER_API_KEY, WEATHER_BASE_URL } from "../Constants/constants";

const useWeatherData = () => {
    const dispatch = useDispatch();
    const weatherData = useSelector((state) => state.places.weatherData);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const getWeatherForPlaces = useCallback(async (places) => {
        if (!places || places.length === 0) return {};

        setLoading(true);
        setError(null);

        try {
            const weatherPromises = places.map(async (place) => {
                const response = await axios.get(`${WEATHER_BASE_URL}?key=${WEATHER_API_KEY}&q=${place.geometry.location.lat},${place.geometry.location.lng}&aqi=yes`);
                return { placeId: place.place_id, weather: response.data.current };
            });

            const weatherResults = await Promise.all(weatherPromises);

            const formattedWeatherData = {};
            weatherResults.forEach(({ placeId, weather }) => {
                formattedWeatherData[placeId] = weather;
            });

            return formattedWeatherData;
        } catch (error) {
            setError("Failed to fetch weather data.");
            return {};
        } finally {
            setLoading(false);
        }
    }, []);

    return { weatherData, getWeatherForPlaces, loading, error };
};

export default useWeatherData;
