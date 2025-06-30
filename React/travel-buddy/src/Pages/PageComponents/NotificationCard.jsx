/* eslint-disable react/prop-types */
/* eslint-disable no-unused-vars */
import { Avatar, AvatarImage, AvatarFallback } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { DEFAULT_MALE_PIC } from "../../Constants/constants";
import useUserData from "../../CustomHooks/useUserData";
import { shallowEqual, useDispatch, useSelector } from "react-redux";
import { useEffect } from "react";
import { clearNotifications } from "../../Redux/Slices/notificationSlice";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faXmark } from "@fortawesome/free-solid-svg-icons";
import useTrip from "../../CustomHooks/useTrip";

export const NotificationCard = ({ notification }) => {
  const { acceptFriendRequest, deleteNotification } = useUserData();
  const { acceptTripRequest, sendTripRequest } = useTrip();
  const dispatch = useDispatch();

  const notifications = useSelector(
    (store) => store.notifications,
    shallowEqual
  );
  console.log("Current notification: ", notification);
  useEffect(() => {
    const timer = setTimeout(() => {
      dispatch(clearNotifications()); // Clear the success message after showing it
      // Clear the message after showing it
    }, 5000);
    return () => clearTimeout(timer); // cleanup
  }, [notifications?.notificationStatus]);

  return (
    <>
      <div className="notification-card flex items-center justify-between bg-orange-100 my-2 p-2 rounded-lg shadow-md gap-2 ">
        <div className="flex items-center gap-2">
          <Avatar className="border border-black">
            <AvatarImage
              src={notification?.senderProfilePic || DEFAULT_MALE_PIC}
              alt="sender"
            />
            <AvatarFallback></AvatarFallback>
          </Avatar>
          <h1 className="font-semibold">
            {notification?.senderName}{" "}
            {notification?.message
              ? notification?.message
              : "wants to follow you"}
          </h1>
        </div>

        {notification?.type != "LIKE" &&
        notification?.type != "COMMENT" &&
        notification?.type != "NEW_TRIP" &&
        notification?.type != "ACCEPTED"? (
          <div>
            <Badge
              variant="outline"
              className="bg-orange-400  hover:cursor-pointer hover:bg-orange-300 hover:border hover:border-orange-600"
              onClick={() =>
                notification?.type === "FRIEND_REQUEST"
                  ? acceptFriendRequest(
                      notification?.notificationId,
                      notification?.notificationFrom,
                      notification?.notificationTo
                    )
                  : acceptTripRequest(
                      notification?.notificationId,
                      notification?.tripId,
                      notification?.notificationFrom
                    )
              }
            >
              Accept
            </Badge>

            <Badge
              variant="outline"
              className=" border-orange-400  hover:cursor-pointer hover:border-orange-800 hover:bg-orange-200"
              onClick={() => deleteNotification(notification?.notificationId)}
            >
              Delete
            </Badge>
          </div>
        ) : notification?.type == "NEW_TRIP" ? (
          <div className="flex">
            <Badge
              variant="outline"
              className="bg-orange-400  hover:cursor-pointer hover:bg-orange-300 hover:border hover:border-orange-600"
              onClick={() =>
                sendTripRequest({
                  notificationId: notification?.notificationId,
                  tripId: notification?.tripId,
                  requestTo: notification?.notificationFrom,
                })
              }
            >
              Join
            </Badge>

            <Badge
              variant="outline"
              className=" border-orange-400  hover:cursor-pointer hover:border-orange-800 hover:bg-orange-200"
              onClick={() => deleteNotification(notification?.notificationId)}
            >
              Delete
            </Badge>
          </div>
        ) : (
          <FontAwesomeIcon
            icon={faXmark}
            className="hover:cursor-pointer"
            onClick={() => deleteNotification(notification?.notificationId)}
          />
        )}
      </div>
    </>
  );
};
export default NotificationCard;
