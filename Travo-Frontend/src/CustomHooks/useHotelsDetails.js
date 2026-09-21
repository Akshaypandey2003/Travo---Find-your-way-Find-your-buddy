/* eslint-disable no-unused-vars */
import axios from "axios";
import { useCallback, useState } from "react";
import { GOOGLE_API_KEY, PLACE_DETAILS_BASE_URL } from "../Constants/constants";
import { useDispatch } from "react-redux";
import { setHotelsDetails } from "../Redux/Slices/hotelSlice";


const useHotelsDetails = ()=>{
    
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const dispatch = useDispatch();

    const getHotelsDetails = useCallback(async (hotelId)=>{
        setLoading(true);
        setError(null);
        try {
            const response = await axios.get(`${PLACE_DETAILS_BASE_URL}&place_id=${hotelId}&key=${GOOGLE_API_KEY}`);
            
            // console.log("Place id in custom hook is: ",placeId)
            console.log("FEtched details: ",response.data.result);

            if (response.data.result) 
            {  
                // ✅ Ensure correct structure (single object)
                dispatch(setHotelsDetails({[hotelId]: response.data.result })); 
                console.log("Fetched hotels Details: ",response.data.result);
                return response.data.result;
            } else {
                setError("No Details Found.");
                return null;
            }
        } catch (error) {
            setError("No Details Found.");
            return null;
        }
        finally {
            setLoading(false);
        }
    },[dispatch])
    return { loading, error,  getHotelsDetails };
}
export default useHotelsDetails;