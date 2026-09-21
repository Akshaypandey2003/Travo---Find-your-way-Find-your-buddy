/* eslint-disable no-unused-vars */
import { useState } from "react";
import { useDispatch } from "react-redux";
import {
  loginSuccess,
  registerSuccess,
  authFailure,
  logout,
  updateUserData,
  profileCompletion,
  authSucess,
  setLoading,
} from "../Redux/Slices/authSlice";
import { useLocation, useNavigate } from "react-router-dom";
import { clearNotifications } from "../Redux/Slices/notificationSlice";
import { clearBlogsData } from "../Redux/Slices/blogsSlice";
import { clearCommentsData } from "../Redux/Slices/commentSlice";
import { clearChats } from "../Redux/Slices/chatSlice";

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
      const response = await fetch(
        "http://localhost:8085/api/v1/auth/register",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(userData),
        }
      );

      const data = await response.json();
      console.log("Data received from register API: ", data);

      if (!response.ok) {
        throw new Error(data.message || "Registration failed");
      }

      localStorage.setItem("token", data.accessToken);

      dispatch(registerSuccess(data.user));

      dispatch(
        authSucess(
          "Registration successful! please complete your profile."
        )
      );

      const completion = getProfileCompletion(data.user);
      dispatch(profileCompletion(completion));

      navigate(`/profile/${data?.user?.userId}`);
    } catch (error) {
      console.error("Registration failed:", error);
      dispatch(authFailure(error.message));
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
      const response = await fetch(
        "http://localhost:8085/api/v1/auth/login",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(credentials),
        }
      );

      const data = await response.json();

      console.log("Data received from login API: ", data);

      if (!response.ok) {
        throw new Error(data.message || "Login failed");
      }

      localStorage.setItem("token", data.accessToken);

      dispatch(loginSuccess(data.user));

      const completion = getProfileCompletion(data.user);
      dispatch(profileCompletion(completion));

      const redirectTo =
        location?.state?.redirectTo || "/dashboard";

      navigate(redirectTo);
    } catch (error) {
      console.error("Login failed:", error);
      dispatch(authFailure(error.message));
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
      const response = await fetch(
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

      const data = await response.json();

      if (!response.ok) {
        throw new Error(
          data.message || "Unable to process request"
        );
      }

      const message =
        data.message ||
        "Password reset instructions have been sent to your email.";

      setForgotMessage(message);

      // Return success information to Login.jsx
      return {
        success: true,
        message,
        data,
      };
    } catch (error) {
      console.error("Forgot password failed:", error);

      setForgotError(error.message);

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

      const response = await fetch(
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

      console.log(
        "Content-Type:",
        response.headers.get("content-type")
      );

      const data = await response.json();

      if (!response.ok) {
        throw new Error(
          data.message || "Profile update failed"
        );
      }
     
      console.log("Data received from update API: ", data);
      dispatch(updateUserData(data));

      navigate(`/profile/${id}`);
    } catch (error) {
      dispatch(authFailure(error.message));
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

    const response = await fetch(
      `https://api.cloudinary.com/v1_1/dwg7vniow/image/upload`,
      {
        method: "POST",
        body: formData,
      }
    );

    if (!response.ok) {
      throw new Error("Image upload failed");
    }

    const data = await response.json();

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