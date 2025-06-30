/* eslint-disable no-unused-vars */

import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";

const useFriendRequest = () => {
  const loggedInUser = useSelector((state) => state.auth.user);
  const navigate = useNavigate();

//   if (!loggedInUser) {
//     navigate("/login");
//     return {};
//   }

  const sendFriendRequest = async ({userId}) => {
    
    if (!loggedInUser) {
      navigate("/login");
      return;
    }
    try {
      const response = await fetch(
        `http://localhost:8088/connection/send/${loggedInUser?.userId}/${userId}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            // Authorization: `Bearer ${loggedInUser?.token}`, // assuming you store token in auth slice
          },
          body: JSON.stringify({
            senderId: loggedInUser.userId,
            receiverId: userId,
          }),
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
  return {sendFriendRequest};
};
export default useFriendRequest;
