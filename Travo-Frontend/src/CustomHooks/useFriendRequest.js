
import { useSelector } from "react-redux";
import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import { apiRequest, getApiErrorMessage } from "../lib/api";
import { notifyApiError, notifyApiSuccess } from "../lib/apiNotifications";

const useFriendRequest = () => {
  const loggedInUser = useSelector((state) => state.auth.user);
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const sendFriendRequest = async (userId, userName) => {
    
    const token = localStorage.getItem("token");
    if (!loggedInUser) {
      navigate("/login");
      return;
    }

    console.log("Received user id, ", userId);
    try {
      const { data } = await apiRequest(
        `http://localhost:8085/api/v1/connections/follow/${userId}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
          },
        }
      );

      const message = data?.status === "FOLLOWING"
        ? `You started following ${userName || "this user"}.`
        : `Follow request sent to ${userName || "this user"}.`;
      notifyApiSuccess(dispatch, message);
    } catch (error) {
      const message = getApiErrorMessage(error);
      notifyApiError(dispatch, message);
      console.error("Friend request failed:", message);
    }
  };

  const acceptFriendRequest = async ({userId}) => {
    
    const token = localStorage.getItem("token");
    if (!loggedInUser) {
      navigate("/login");
      return;
    }
    try {
      const { message } = await apiRequest(
        `http://localhost:8085/api/v1/connections/accept/${userId}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
          },
        }
      );

      notifyApiSuccess(dispatch, message || "Friend request accepted successfully.");
    } catch (error) {
      notifyApiError(dispatch, getApiErrorMessage(error));
    }
  };

  const unFollowUser = async ({userId}) => {
    
    const token = localStorage.getItem("token");
    if (!loggedInUser) {
      navigate("/login");
      return;
    }
    try {
      const { message } = await apiRequest(
        `http://localhost:8085/api/v1/connections/unfollow/${userId}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
          },
        }
      );

      notifyApiSuccess(dispatch, message || "User unfollowed successfully.");
    } catch (error) {
      notifyApiError(dispatch, getApiErrorMessage(error));
    }
  };
  const rejectFriendRequest = async ({userId}) => {
    
    const token = localStorage.getItem("token");
    if (!loggedInUser) {
      navigate("/login");
      return;
    }
    try {
      const { message } = await apiRequest(
        `http://localhost:8085/api/v1/connections/reject/${userId}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
          },
        }
      );

      notifyApiSuccess(dispatch, message || "Friend request rejected successfully.");
    } catch (error) {
      notifyApiError(dispatch, getApiErrorMessage(error));
    }
  };


  const getReceivedFriendRequest = async () => {

    const token = localStorage.getItem("token");
    if (!loggedInUser) {
      navigate("/login");
      return;
    }
    try {
      const { data } = await apiRequest(
        `http://localhost:8085/connection/received/${loggedInUser?.userId}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
          },
        }
      );

      console.log("Received friend requests:", data);
    } catch (error) {
      notifyApiError(dispatch, getApiErrorMessage(error));
    }
  };

  
  return {sendFriendRequest,getReceivedFriendRequest,acceptFriendRequest,rejectFriendRequest};
};
export default useFriendRequest;
