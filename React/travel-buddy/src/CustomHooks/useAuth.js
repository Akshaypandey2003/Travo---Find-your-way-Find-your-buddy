/* eslint-disable no-unused-vars */
import { useState } from "react";
import { useDispatch } from "react-redux";
import {
  loginSuccess,
  registerSuccess,
  authFailure,
  logout,
  updateUserSuccess,
  profileCompletion,
  authSucess,
} from "../Redux/Slices/authSlice";
import { useLocation, useNavigate } from "react-router-dom";
import { clearNotifications } from "../Redux/Slices/notificationSlice";
import { clearBlogsData } from "../Redux/Slices/blogsSlice";
import { clearCommentsData } from "../Redux/Slices/commentSlice";
import { clearChats } from "../Redux/Slices/chatSlice";

const useAuth = () => {
  const [loading, setLoading] = useState(false);
  // const user = useSelector((store) => store.auth.user, shallowEqual);

  const dispatch = useDispatch();
  const location = useLocation();
  const navigate = useNavigate(); // Assuming you are using react-router-dom for navigation


  const getProfileCompletion = (user) => {
    if (!user) return 0; // If no user is logged in, return 0% completion

    const fields = [
      "name",
      "email",
      "gender",
      "phone",
      "profilePic",
      "country",
      "state",
      "city",
      "bio",
      "preferences",
    ];
    let filledFields = fields.filter((field) => user[field]); // Count filled fields
    let completionPercentage = (filledFields.length / fields.length) * 100;

    return completionPercentage.toFixed(0); // Round to nearest whole number
  };
  // Calculate profile completion percentage
 

  const registerUser = async (userData) => {

    setLoading(true);
    try {
      const response = await fetch("http://localhost:8085/auth/register", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(userData),
      });

      const data = await response.json();

      if (!response.ok) throw new Error(data.messageReponse.message || "Registration failed");

      // Store token securely
      localStorage.setItem("token", data.accessToken);

      dispatch(registerSuccess(data.user)); // Update Redux state
      dispatch(authSucess("Registration successful! please complete your profile.")); // Update Redux state

      const completion = getProfileCompletion(data.user);
      dispatch(profileCompletion(completion));

      navigate("/user_profile");
    } catch (error) {
        dispatch(authFailure(error.message));
    } finally {
      setLoading(false);
    }
  };

  const loginUser = async (credentials) => 
    {
    setLoading(true);
    try {
      const response = await fetch("http://localhost:8085/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(credentials),
      });

      const data = await response.json();
      if (!response.ok) throw new Error(data.messageReponse.message || "Login failed");

      localStorage.setItem("token", data.accessToken);
      dispatch(loginSuccess(data.user));
  
      const completion = getProfileCompletion(data.user);
      dispatch(profileCompletion(completion));
     
      const redirectTo = location?.state?.redirectTo || "/";
      navigate(redirectTo);
    } catch (error) {
      dispatch(authFailure(error.message));
    } finally {
      setLoading(false);
    }
  };

  const logoutUser = () => {
    localStorage.removeItem("token");
    dispatch(logout());
    dispatch(clearNotifications())
    dispatch(clearBlogsData());
    dispatch(clearCommentsData())
    dispatch(clearChats());
    navigate("/");
  };

  const updateUser = async (userData, id) => {

    console.log("Userdata received to update: ",userData);
    setLoading(true);
    try {
      const token = localStorage.getItem("token");

     //------------- File upload logic (Frontend) -------------------

      // If user uploaded a new image file
      if (userData.profilePic instanceof File) 
     {
        const {url, public_id} = await uploadImageToCloudinary(userData.profilePic);
        userData.profilePic = url; // Update the userData with the new image URL

        userData = {
          ...userData,
          cloudinaryImagePublicId: public_id,
        }
      }      
      //----------------- File upload logic (Backend)
      // const formData = new FormData();
      // const userDataToSend = { ...userData };
      // delete userDataToSend.profilePic;

      // formData.append("userData", JSON.stringify(userDataToSend));
      // if (userData.profilePic) {
      //   formData.append("profilePic", userData.profilePic);
      // }

      // console.log("User data for prodile update after appending image:"); // Debugging line
      // for (var pair of formData.entries()) {
      //   console.log(pair[0] + ", " + pair[1]);
      // }
       
      const response = await fetch(
        `http://localhost:8085/user/update-user/${id}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          //   "Authorization": `Bearer ${token}`,
          },
          body: JSON.stringify(userData),
        }
      );
      const data = await response.json();
      if (!response.ok)
        throw new Error(data.message || "Profile update failed");

      // Store token securely

      dispatch(updateUserSuccess(data)); // Update Redux state
      navigate(`/user_profile/${id}`);

    } catch (error) {
      dispatch(authFailure(error.message));
    } finally {
      setLoading(false);
    }
  };

  //File upload function to Cloudinary
  const uploadImageToCloudinary = async (file) => {
    const formData = new FormData();
    formData.append("file", file);
    formData.append("upload_preset", "TravoApp");  // Replace with your preset
    formData.append("cloud_name", "dwg7vniow");          // Replace with your Cloudinary cloud name
  
    const response = await fetch(`https://api.cloudinary.com/v1_1/dwg7vniow/image/upload`, {
      method: "POST",
      body: formData,
    });
  
    if (!response.ok) {
      throw new Error("Image upload failed");
    }
  
    const data = await response.json();
    
    return  { 
      url: data.secure_url, 
      public_id: data.public_id 
    }; // This is the image URL & public id associated with that image
  };

  return { registerUser, loginUser, logoutUser, loading, updateUser,uploadImageToCloudinary };
};

export default useAuth;
