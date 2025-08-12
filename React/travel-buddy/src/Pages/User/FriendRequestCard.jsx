/* eslint-disable no-unused-vars */
/* eslint-disable react/prop-types */
import React from "react";
import { Card } from "@/components/ui/card";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faLocationDot,
  faUser,
  faHeart,
  faUserGroup,
  faBell,
} from "@fortawesome/free-solid-svg-icons";
import { Link, useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { useSelector } from "react-redux";
import {
  DEFAULT_FEMALE_PIC,
  DEFAULT_MALE_PIC,
} from "../../Constants/constants";
import { Badge } from "@/components/ui/badge";
import useFriendRequest from "../../CustomHooks/useFriendRequest";
import useUserData from "../../CustomHooks/useUserData";
import useTrip from "../../CustomHooks/useTrip";

export const FriendRequestCard = ({ user }) => {
  const navigate = useNavigate();
  const loggedInUser = useSelector((state) => state.auth.user);
  const theme = useSelector((state) => state.auth.theme);

  const {acceptConnectionRequest,deleteConnectionRequest} = useUserData();

  return (
    <div className="relative group w-[20rem]">
      <Card
        className={`relative z-10 p-4 flex flex-col justify-between rounded-2xl border transform transition duration-300 ease-in-out group-hover:scale-105 group-hover:shadow-md group-hover:shadow-orange-200 ${
          theme === "dark"
            ? "bg-gray-950 border-gray-800"
            : "bg-white border-gray-100"
        }`}>
        {/* Header */}
        <div
          className={`flex justify-between items-start border-b pb-3 gap-4 ${
            theme === "dark" ? "border-gray-700" : "border-orange-100"
          }`}>      
          <div className="flex gap-4">
            <div className="w-16 h-16 rounded-full overflow-hidden border-2 border-orange-300 shadow-sm">
              <img
                src={
                  user?.profilePic
                    ? user?.senderDetails?.profilePic
                    : user?.senderDetails?.gender?.toLowerCase() === "male"
                    ? DEFAULT_MALE_PIC
                    : DEFAULT_FEMALE_PIC
                }
                alt="profile"
                className="object-cover w-full h-full"
              />
            </div>
            <div>
              <Link
                to={loggedInUser ? `/user_profile/${user?.senderDetails?.userId}` : `/login`}
                state={{ redirectTo: `/user_profile/${user?.senderDetails?.userId}` }}
                className={`text-lg font-bold no-underline hover:text-inherit ${
                  theme === "dark" ? "text-orange-400" : "text-orange-700"
                }`}
              >
                {user?.senderDetails?.name}
              </Link>
              <div
                className={`text-sm mt-1 ${
                  theme === "dark" ? "text-gray-400" : "text-gray-600"
                }`}
              >
                <FontAwesomeIcon
                  icon={faLocationDot}
                  className="text-orange-500 mr-1"
                />
                {user?.senderDetails?.country}
              </div>
            </div>
          </div>
        </div>

        {/* Middle Content: Bio or Upcoming Trip */}
        <div
          className={`mt-4 text-sm flex-1 ${
            theme === "dark" ? "text-gray-300" : "text-gray-700"
          }`}
        >
            <p className={`font-medium ${theme === "dark" ? "text-gray-300" : ""}`}>
              {user?.senderDetails?.bio?.substring(0,38)+"..." || "No bio available."}
            </p>
        </div>

        {/* Footer */}
        <div className="flex justify-between items-center pt-2 border-orange-100">
            <Badge
              variant="outline"
              className="bg-orange-400  hover:cursor-pointer hover:bg-orange-300 hover:border hover:border-orange-600"
              onClick={() =>
               acceptConnectionRequest(
                     user?.request?.connectionId,
                     user?.request?.requestFrom,
                     user?.request?.requestTo
                    )
              }
            >
              Accept
            </Badge>

            <Badge
              variant="outline"
              className=" border-orange-400  hover:cursor-pointer hover:border-orange-800 hover:bg-orange-200"
              onClick={() => deleteConnectionRequest(user?.request?.connectionId)}
            >
              Delete
            </Badge>
          </div>
      </Card>
    </div>
  );
};

export default FriendRequestCard;
