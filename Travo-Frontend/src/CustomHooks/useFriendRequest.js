
import { useSelector } from "react-redux";
import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import { setMessage } from "../Redux/Slices/notificationSlice";

const useFriendRequest = () => {
  const loggedInUser = useSelector((state) => state.auth.user);
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const sendFriendRequest = async (userId) => {
    
    const token = localStorage.getItem("token");
    if (!loggedInUser) {
      navigate("/login");
      return;
    }

    console.log("Received user id, ", userId);
    try {
      const response = await fetch(
        `http://localhost:8085/api/v1/connections/follow/${userId}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
          },
        }
      );

      if (response.ok) {
        const result = await response.json();
        console.log("Friend request sent:", result);
         dispatch(setMessage({
          message: "Friend request sent successfully.",
          error: false,
          success: true,
          notificationStatus: true
        }));
        // Optionally, show a toast/alert
      } else {
        const err = await response.json();
        console.error("Error sending friend request:", err.message);
        dispatch(setMessage({
          message: err.message || "Unable to send friend request.",
          error: true,
          success: false,
          notificationStatus: true
        }));
      }
    } catch (error) {
      console.error("Network error:", error.message);
    }
  };

  const acceptFriendRequest = async ({userId}) => {
    
    const token = localStorage.getItem("token");
    if (!loggedInUser) {
      navigate("/login");
      return;
    }
    try {
      const response = await fetch(
        `http://localhost:8085/api/v1/connections/accept/${userId}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
          },
        }
      );

      if (response.ok) {
        const result = await response.json();
        console.log("Friend request sent:", result);
        // Optionally, show a toast/alert
      } else {
        const err = await response.json();
        console.error("Error sending friend request:", err.message);
      }
    } catch (error) {
      console.error("Network error:", error.message);
    }
  };

  const unFollowUser = async ({userId}) => {
    
    const token = localStorage.getItem("token");
    if (!loggedInUser) {
      navigate("/login");
      return;
    }
    try {
      const response = await fetch(
        `http://localhost:8085/api/v1/connections/unfollow/${userId}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
          },
        }
      );

      if (response.ok) {
        const result = await response.json();
        console.log("Friend request sent:", result);
        // Optionally, show a toast/alert
      } else {
        const err = await response.json();
        console.error("Error sending friend request:", err.message);
      }
    } catch (error) {
      console.error("Network error:", error.message);
    }
  };
  const rejectFriendRequest = async ({userId}) => {
    
    const token = localStorage.getItem("token");
    if (!loggedInUser) {
      navigate("/login");
      return;
    }
    try {
      const response = await fetch(
        `http://localhost:8085/api/v1/connections/reject/${userId}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
          },
        }
      );

      if (response.ok) {
        const result = await response.json();
        console.log("Friend request sent:", result);
        // Optionally, show a toast/alert
      } else {
        const err = await response.json();
        console.error("Error sending friend request:", err.message);
      }
    } catch (error) {
      console.error("Network error:", error.message);
    }
  };


  const getReceivedFriendRequest = async () => {

    const token = localStorage.getItem("token");
    if (!loggedInUser) {
      navigate("/login");
      return;
    }
    try {
      const response = await fetch(
        `http://localhost:8085/connection/received/${loggedInUser?.userId}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
          },
        }
      );

      if (response.ok) {
        const result = await response.json();
        console.log("Received friend requests:", result);
        // Optionally, show a toast/alert
      } else {
        const err = await response.json();
        console.error("Error fetching received friend request:", err.message);
      }
    } catch (error) {
      console.error("Network error:", error.message);
    }
  };

  
  return {sendFriendRequest,getReceivedFriendRequest,acceptFriendRequest,rejectFriendRequest};
};
export default useFriendRequest;
