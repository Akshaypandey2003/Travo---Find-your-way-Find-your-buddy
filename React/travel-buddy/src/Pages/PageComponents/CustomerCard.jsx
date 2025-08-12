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

export const CustomerCard = ({ user }) => {
  const navigate = useNavigate();
  const loggedInUser = useSelector((state) => state.auth.user);
  const theme = useSelector((state) => state.auth.theme);
  const upcomingTrip = user?.trips?.filter(
    (trip) => trip.tripStatus === "UPCOMING"
  );
  const { sendFriendRequest } = useFriendRequest();
  const { sendTripRequest } = useTrip();
  const { likeUser } = useUserData();

  return (
    <div className="relative group w-[20rem]">
      <Card
        className={`relative z-10 p-4 h-[20rem] flex flex-col justify-between rounded-2xl border transform transition duration-300 ease-in-out group-hover:scale-105 group-hover:shadow-md group-hover:shadow-orange-200 ${
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
                    ? user?.profilePic
                    : user?.gender?.toLowerCase() === "male"
                    ? DEFAULT_MALE_PIC
                    : DEFAULT_FEMALE_PIC
                }
                alt="profile"
                className="object-cover w-full h-full"
              />
            </div>
            <div>
              <Link
                to={loggedInUser ? `/user_profile/${user?.userId}` : `/login`}
                state={{ redirectTo: `/user_profile/${user?.userId}` }}
                className={`text-lg font-bold no-underline hover:text-inherit ${
                  theme === "dark" ? "text-orange-400" : "text-orange-700"
                }`}
              >
                {user?.name}
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
                {user?.country}
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
          {upcomingTrip?.length > 0 ? (
            <div>
              <h2
                className={`font-semibold mb-2 ${
                  theme === "dark" ? "text-gray-100" : "text-gray-800"
                }`}
              >
                Upcoming Trip
              </h2>
              {upcomingTrip.slice(0, 1).map((trip, index) => (
                <div key={index}>
                  <div className="flex justify-between items-start">
                    <div>
                      <div className="font-semibold text-orange-700">
                        <FontAwesomeIcon
                          icon={faLocationDot}
                          className="mr-1"
                        />
                        {trip?.tripName}
                      </div>
                      <div
                        className={`text-xs ${
                          theme === "dark" ? "text-gray-400" : "text-gray-600"
                        }`}
                      >
                        {trip?.tripCity}, {trip?.tripState},{" "}
                        {trip?.tripCountry}
                      </div>
                    </div>
                    {!trip?.tripRequests?.includes(loggedInUser?.userId) &&
                      !trip?.tripMembers?.includes(loggedInUser?.userId) &&
                      trip?.createdBy !== loggedInUser?.userId && (
                        <FontAwesomeIcon
                          icon={faBell}
                          className="text-orange-500 cursor-pointer hover:scale-125 transition-transform"
                          onClick={() =>
                            sendTripRequest({
                              tripId: trip?.tripId,
                              requestTo: user?.userId,
                            })
                          }
                        />
                      )}
                  </div>

                  <div className="flex flex-wrap gap-2 mt-2">
                    {trip?.tripTags?.slice(0, 3).map((tag, i) => (
                      <Badge
                        key={i}
                        className={`text-xs ${
                          theme === "dark"
                            ? "bg-gray-900 border-gray-400 text-inherit"
                            : "bg-orange-50 border-orange-500 text-orange-600"
                        }`}
                      >
                        #{tag}
                      </Badge>
                    ))}
                  </div>

                  <div className="flex justify-between items-center mt-2">
                    <div>
                      <FontAwesomeIcon icon={faUserGroup} className="mr-1" />
                      {trip?.tripType === "GROUP"
                        ? `${trip?.memberSize} members`
                        : "Solo Trip"}
                    </div>
                    <Link
                      to={
                        loggedInUser
                          ? `/user_profile/${user?.userId}`
                          : `/login`
                      }
                      state={{ redirectTo: `/user_profile/${user?.userId}` }}
                      className="text-orange-600 font-semibold hover:underline text-xs"
                    >
                      more...
                    </Link>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <p className={`font-medium ${theme === "dark" ? "text-gray-300" : ""}`}>
              {user?.bio || "No bio available."}
            </p>
          )}
        </div>

        {/* Footer */}
        <div className="flex justify-between items-center mt-4 pt-2 border-t border-orange-100">
          <Button
            variant="outline"
            className={`border text-orange-700 hover:bg-orange-50 transition ${
              theme === "dark"
                ? "border-orange-600 hover:bg-orange-950 text-orange-400"
                : "border-orange-400"
            }`}
            onClick={() => sendFriendRequest({ userId: user?.userId })}
            disabled={
              loggedInUser?.followers?.includes(user?.userId) ||
              loggedInUser?.following?.includes(user?.userId)
            }
          >
            <FontAwesomeIcon icon={faUser} className="mr-1" /> Follow
          </Button>

          <div className="flex items-center gap-2">
            <FontAwesomeIcon
              icon={faHeart}
              className={`cursor-pointer transition-transform hover:scale-125 ${
                user?.likes?.includes(loggedInUser?.userId)
                  ? "text-orange-600"
                  : theme === "dark"
                  ? "text-orange-300"
                  : "text-orange-200"
              }`}
              onClick={() => likeUser(user?.userId)}
            />
            <span
              className={`text-sm font-medium ${
                theme === "dark" ? "text-gray-400" : "text-gray-700"
              }`}
            >
              {user?.likes?.length > 0 && user?.likes?.length}
            </span>
          </div>
        </div>
      </Card>
    </div>
  );
};

export default CustomerCard;
