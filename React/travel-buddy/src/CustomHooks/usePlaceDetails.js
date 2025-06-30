/* eslint-disable no-unused-vars */
import axios from "axios";
import { useCallback, useState } from "react";
import { GOOGLE_API_KEY, PLACE_DETAILS_BASE_URL } from "../Constants/constants";
import { useDispatch } from "react-redux";
import { setPlacesDetails } from "../Redux/Slices/placeSlice";


const usePlaceDetails = ()=>{
    
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const dispatch = useDispatch();

    const getPlaceDetails = useCallback(async (placeId)=>{

        setLoading(true);
        setError(null);
        try {
            const response = await axios.get(`${PLACE_DETAILS_BASE_URL}&place_id=${placeId.placeId}&key=${GOOGLE_API_KEY}`);
            
            // console.log("Place id in custom hook is: ",placeId)
            console.log("FEtched details: ",response.data.result);

            if (response.data.result) 
            {  
                // ✅ Ensure correct structure (single object)
                dispatch(setPlacesDetails({[placeId.placeId]: response.data.result })); 
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
    return { loading, error,  getPlaceDetails };
}
export default usePlaceDetails;