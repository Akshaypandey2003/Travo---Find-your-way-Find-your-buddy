import axios from "axios";
import { useCallback, useState } from "react";
import { useDispatch } from "react-redux";
import { setHotelsData } from "../Redux/Slices/hotelSlice";
import {  AMADEUS_API_KEY, AMADEUS_AUTH_URL, AMADEUS_HOTELS_GEO_URL, AMADEUS_SECRET_KEY } from "../Constants/constants";

const useHotelsData = () => {
  
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const dispatch = useDispatch();
  const [accessToken, setAccessToken] = useState(null);

  // Function to get Amadeus OAuth2 Access Token
  const fetchAccessToken = async () => {

    try {
      const response = await axios.post(
        AMADEUS_AUTH_URL,
        new URLSearchParams({
          grant_type: "client_credentials",
          client_id: AMADEUS_API_KEY,
          client_secret: AMADEUS_SECRET_KEY,
        }),
        { headers: { "Content-Type": "application/x-www-form-urlencoded" } }
      );
      setAccessToken(response.data.access_token);
      return response.data.access_token;
    } catch (error) {
      console.error("Error fetching Amadeus access token:", error);
      setError("Authentication failed");
      return null;
    }
  };

  // Function to get hotels from Amadeus API using latitude & longitude
  const getHotelsData = useCallback(
    async (placeId, latitude, longitude) => {
      console.log("Executing getHotelsData function");
      setLoading(true);
      setError(null);

      try {
        // Ensure we have a valid access token
        let token = accessToken;
        if (!token) {
          token = await fetchAccessToken();
          if (!token) throw new Error("Failed to fetch access token");
        }

        const url = `${AMADEUS_HOTELS_GEO_URL}?latitude=${latitude}&longitude=${longitude}`;
        console.log("Hotels URL: ", url);

        const response = await axios.get(url, {
          headers: { Authorization: `Bearer ${token}` },
        });

        console.log("Fetched Hotel details: ", response);
         
        if (response.data.data) {
          dispatch(
            setHotelsData({
              hotelsData: { [placeId.placeId]: response.data.data },
              // nextPageToken: response.data.next_page_token,
            })
          );
          return response.data.data;
        } else {
          setError("No Hotels Found.");
        }
      } catch (error) {
        setError("No Hotels Found.");
        console.error("Error fetching hotel data:", error);
      } finally {
        setLoading(false);
      }
    },
    [dispatch, accessToken]
  );

  return { loading, error, getHotelsData };
};

export default useHotelsData;

