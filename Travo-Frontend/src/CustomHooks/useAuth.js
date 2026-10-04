/* eslint-disable no-unused-vars */
import { useState } from "react";
import { useDispatch } from "react-redux";
import {
  loginSuccess,
  registerSuccess,
  logout,
  updateUserData,
  profileCompletion,
  setLoading,
} from "../Redux/Slices/authSlice";
import { useLocation, useNavigate } from "react-router-dom";
import { clearNotifications } from "../Redux/Slices/notificationSlice";
import { clearBlogsData } from "../Redux/Slices/blogsSlice";
import { clearCommentsData } from "../Redux/Slices/commentSlice";
import { clearChats } from "../Redux/Slices/chatSlice";
import { apiRequest, getApiErrorMessage } from "../lib/api";
import { notifyApiError, notifyApiSuccess } from "../lib/apiNotifications";

const useAuth = () => {
  const dispatch = useDispatch();
  const location = useLocation();
  const navigate = useNavigate();

  // Forgot password states
  const [forgotLoading, setForgotLoading] = useState(false);
  const [forgotMessage, setForgotMessage] = useState("");
  const [forgotError, setForgotError] = useState("");

  const getProfileCompletion = (user) => {
    if (!user) return 0;

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

    let filledFields = fields.filter((field) => user[field]);
    let completionPercentage = (filledFields.length / fields.length) * 100;

    return completionPercentage.toFixed(0);
  };

  // =========================================================
  // REGISTER USER
  // =========================================================

  const registerUser = async (userData) => {
    dispatch(setLoading(true));
    console.log("Registering user with data: ", userData);

    try {
      const { data } = await apiRequest(
        "http://localhost:8085/api/v1/auth/register",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(userData),
        }
      );

      console.log("Data received from register API: ", data);

      localStorage.setItem("token", data.accessToken);

      dispatch(registerSuccess(data.user));
      notifyApiSuccess(
        dispatch,
        data?.messageReponse?.message || "User registered successfully.",
        "registerUser",
      );

      const completion = getProfileCompletion(data.user);
      dispatch(profileCompletion(completion));

      navigate(`/profile/${data?.user?.userId}`);
    } catch (error) {
      console.error("Registration failed:", error);
      const message = getApiErrorMessage(error);
      notifyApiError(dispatch, message, "registerUser");
    } finally {
      dispatch(setLoading(false));
    }
  };

  // =========================================================
  // LOGIN USER
  // =========================================================

  const loginUser = async (credentials) => {
    console.log("Login credentials received: ", credentials);

    dispatch(setLoading(true));

    try {
      const { data } = await apiRequest(
        "http://localhost:8085/api/v1/auth/login",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(credentials),
        }
      );

      console.log("Data received from login API: ", data);

      localStorage.setItem("token", data.accessToken);

      dispatch(loginSuccess(data.user));
      notifyApiSuccess(dispatch, data?.messageReponse?.message || "Something went wrong, Please try again !!");

      const completion = getProfileCompletion(data.user);
      dispatch(profileCompletion(completion));

      const redirectTo =
        location?.state?.redirectTo || "/dashboard";

      navigate(redirectTo);
    } catch (error) {
      console.error("Login failed:", error);
      const message = getApiErrorMessage(error);
      notifyApiError(dispatch, message);
    } finally {
      dispatch(setLoading(false));
    }
  };

  // =========================================================
  // FORGOT PASSWORD
  // =========================================================

  const forgotPassword = async (email) => {
    setForgotLoading(true);
    setForgotError("");
    setForgotMessage("");

    try {
      const { data } = await apiRequest(
        "http://localhost:8085/api/v1/auth/forget-password",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            email,
          }),
        }
      );

      const message =
        data.message ||
        "Password reset instructions have been sent to your email.";

      setForgotMessage(message);
      notifyApiSuccess(dispatch, message);

      // Return success information to Login.jsx
      return {
        success: true,
        message,
        data,
      };
    } catch (error) {
      console.error("Forgot password failed:", error);

      const message = getApiErrorMessage(error);
      setForgotError(message);
      notifyApiError(dispatch, message);

      // Important: allow Login.jsx to know that request failed
      return {
        success: false,
        message: error.message,
      };
    } finally {
      setForgotLoading(false);
    }
  };

  // =========================================================
  // LOGOUT
  // =========================================================

  const logoutUser = () => {
    localStorage.removeItem("token");

    dispatch(logout());
    dispatch(clearNotifications());
    dispatch(clearBlogsData());
    dispatch(clearCommentsData());
    dispatch(clearChats());

    navigate("/");
  };

  // =========================================================
  // UPDATE USER
  // =========================================================

  const updateUser = async (userData, id) => {
    console.log("Userdata received to update: ", userData);

    dispatch(setLoading(true));

    try {
      const token = localStorage.getItem("token");

      //------------- File upload logic (Frontend) -------------------

      if (userData.profilePic instanceof File) {
        const { url, public_id } =
          await uploadImageToCloudinary(userData.profilePic);

        userData.profilePic = url;

        userData = {
          ...userData,
          cloudinaryImagePublicId: public_id,
        };
      }

      //----------------- File upload logic (Backend)

      const { data, status, raw } = await apiRequest(
        `http://localhost:8085/api/v1/user`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify(userData),
        }
      );

      console.log("Update API status:", status);
      console.log("Data received from update API: ", data);
      dispatch(updateUserData(data));
      notifyApiSuccess(
        dispatch,
        raw?.message || data?.message || "Profile updated successfully",
        "updateUser",
      );
      

      navigate(`/profile/${id}`);
    } catch (error) {
      const message = getApiErrorMessage(error);
      notifyApiError(dispatch, message);
    } finally {
      dispatch(setLoading(false));
    }
  };

  // =========================================================
  // CLOUDINARY IMAGE UPLOAD
  // =========================================================

  const uploadImageToCloudinary = async (file) => {
    const formData = new FormData();

    formData.append("file", file);
    formData.append("upload_preset", "TravoApp");
    formData.append("cloud_name", "dwg7vniow");

    const { data } = await apiRequest(
      `https://api.cloudinary.com/v1_1/dwg7vniow/image/upload`,
      {
        method: "POST",
        body: formData,
      }
    );

    return {
      url: data.secure_url,
      public_id: data.public_id,
    };
  };

  // =========================================================
  // RETURN
  // =========================================================

  return {
    registerUser,
    loginUser,
    logoutUser,
    updateUser,
    uploadImageToCloudinary,

    // Forgot password
    forgotPassword,
    forgotLoading,
    forgotMessage,
    forgotError,
  };
};

export default useAuth;