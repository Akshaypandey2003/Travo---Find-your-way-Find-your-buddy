/* eslint-disable react/prop-types */
/* eslint-disable no-unused-vars */
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
// import { fa } from "@fortawesome/free-regular-svg-icons";

// library.add(fas, fa);

export const CustomerCard = ({ user }) => {
  const navigate = useNavigate();
  // const { loginUser, loading } = useAuth();

  const loggedInUser = useSelector((state) => state.auth.user);
  // console.log("Logged in user is: ", loggedInUser, user?.likes?.includes(loggedInUser?.userId));
  // console.log("User is: ", user);
  const upcomingTrip = user?.trips?.filter(
    (trip) => trip.tripStatus === "UPCOMING"
  );
  const { sendFriendRequest } = useFriendRequest();
  const { sendTripRequest } = useTrip();
  const { likeUser } = useUserData();
  // console.log("Upcoming trips are: ", upcomingTrip);

  return (
    <Card className="border-none shadow-xl p-2 bg-orange-50 w-[20rem]">
      <div className="flex justify-between p-2 border-b-2 border-orange-200 gap-[1rem]">
        <div className="flex users-center gap-[1rem] ">
          <div className="w-[5rem] h-[5rem] rounded-full object-cover overflow-hidden ">
            <img
              src={
                user?.profilePic
                  ? user?.profilePic
                  : user?.gender?.toLowerCase() === "male"
                  ? DEFAULT_MALE_PIC
                  : DEFAULT_FEMALE_PIC
              }
              alt="profile"
              className="shadow-2xl"
            />
          </div>

          <div className="">
            <Link
              to={loggedInUser ? `/user_profile/${user?.userId}` : `/login`}
              state={{ redirectTo: `/user_profile/${user?.userId}` }}
            >
              <h1 className="text-lg font-semibold">{user?.name}</h1>
            </Link>
            <span>
              <FontAwesomeIcon
                icon={faLocationDot}
                className="mr-2 text-orange-600"
              />
            </span>
            <span className="text-gray-500 text-sm">{user?.country}</span>
          </div>
        </div>
        <div className="">
          {/* {loggedInUser ? ( */}
          {/* <Popover>
            <PopoverTrigger asChild>
              <Button
                variant="outline"
                className="w-1 border-none bg-transparent hover:bg-orange-200"
              >
                <FontAwesomeIcon icon={faEllipsisVertical} />
              </Button>
            </PopoverTrigger>
            <PopoverContent className="w-28">
              <div className=" flex flex-col gap-2">
                <Button
                  className="border-none focus:border-none bg-transparent text-black hover:bg-orange-200"
                  onClick={() => {
                    if (!loggedInUser) {
                      navigate("/login", {
                        state: { redirectTo: `/user_profile/${user?.userId}` },
                      });
                      return;
                    }
                    navigate(`/user_profile/${user?.userId}`);
                  }}
                >
                  View Profile
                </Button>
                <Button
                  className="border-none focus:border-none bg-transparent text-black hover:bg-orange-200"
                  onClick={() => {
                    if (!loggedInUser) {
                      navigate("/login", {
                        state: { redirectTo: `/user_profile/${user?.userId}` },
                      });
                      return;
                    }
                    navigate(`/user_profile/${user?.userId}`);
                  }}
                >
                  Notify
                </Button>
              </div>
            </PopoverContent>
          </Popover> */}
          {/* ) : ( */}
          {/* <Dialog>
              <DialogTrigger
                aschild="true"
                className="w-7 border-none bg-transparent hover:bg-orange-200"
              >
                <FontAwesomeIcon icon={faEllipsisVertical} />
              </DialogTrigger>
              <DialogContent className="sm:max-w-[550px] bg-orange-100">
                <DialogHeader>
                  <DialogTitle className="m-auto"></DialogTitle>
                  <DialogDescription className="">
                    <div className="flex flex-col items-center">
                      <LoginForm data="bg-orange-100"/>
                    </div>
                  </DialogDescription>
                </DialogHeader>
                <div className="m-4"></div>
              </DialogContent>
            </Dialog> */}
          {/* )} */}
        </div>
      </div>
      <div className="p-2">
        {upcomingTrip?.length > 0 ? (
          <div>
            <h1 className="font-semibold">Upcoming Trips </h1>
            {upcomingTrip?.slice(0, 1).map((item, index) => (
              <p key={index} className="text-sm mt-1">
                <div className="flex justify-between items-center">
                  <div>
                    <span>
                      <FontAwesomeIcon
                        icon={faLocationDot}
                        className="mr-2 text-orange-600"
                      />
                    </span>
                    <span className="font-semibold">{item?.tripName} </span>
                    <br />
                    {item?.tripCity},{item?.tripState},{item?.tripCountry}
                  </div>
                  {!item?.tripRequests?.includes(loggedInUser?.userId) &&
                    !item?.tripMembers?.includes(loggedInUser?.userId) && 
                    item?.createdBy!=loggedInUser?.userId && (
                      <FontAwesomeIcon
                        icon={faBell}
                        className={`${
                          item?.tripRequests?.includes(loggedInUser?.userId) ||
                          item?.tripMembers?.includes(loggedInUser?.userId)
                            ? "text-orange-500"
                            : "text-orange-200"
                        } hover:cursor-pointer hover:scale-150 `}
                        onClick={() =>
                          sendTripRequest({
                            tripId: item?.tripId,
                            requestTo: user?.userId,
                          })
                        }
                      />
                    )}
                </div>

                {item?.tripTags?.slice(0, 3).map((item, index) => (
                  <Badge
                    key={index}
                    variant="outline"
                    className="h-4 border-orange-500 mx-1 font-thin"
                  >
                    {item}
                  </Badge>
                ))}
                <div className="p-2 flex justify-between items-center">
                  {item?.tripType === "GROUP" ? (
                    <span>
                      <FontAwesomeIcon icon={faUserGroup} />
                      <span className="mx-2 font-semibold">
                        {" "}
                        {item?.memberSize}
                      </span>
                    </span>
                  ) : (
                    <span>
                      <FontAwesomeIcon icon={faUserGroup} />
                      <span className="mx-2 font-semibold">Solo</span>
                    </span>
                  )}
                  <span>
                    <Link
                      to={
                        loggedInUser
                          ? `/user_profile/${user?.userId}`
                          : `/login`
                      }
                      state={{ redirectTo: `/user_profile/${user?.userId}` }}
                    >
                      <span>more...</span>
                    </Link>
                  </span>
                </div>
              </p>
            ))}
          </div>
        ) : (
          <p className="text-gray-800">
            <h1 className="text-md font-semibold"> {user?.bio}</h1>
            {user?.preferences && (
              <div className="flex items-center flex-wrap gap-1 p-2 mt-2">
                {user?.preferences?.map((item, index) => (
                  <Badge
                    className="text-xs font-thin border-orange-600 h-4"
                    variant="ghost"
                    key={index}
                  >
                    {item}
                  </Badge>
                ))}
              </div>
            )}
          </p>
        )}
      </div>
      <div className="flex justify-between items-center p-2">
        <Button
          variant="outline"
          className="border-none"
          onClick={() => sendFriendRequest({ userId: user?.userId })}
          disabled={
            loggedInUser?.followers?.includes(user?.userId) ||
            loggedInUser?.following?.includes(user?.userId)
          }
        >
          <FontAwesomeIcon icon={faUser} className="" />
          <span className="text-bold">+</span>
        </Button>
        {
          <div className="flex gap-4 items-center">
            {/* <Button className="w-0 h-2 rounded-full p-0"> */}
            <FontAwesomeIcon
              className={`${
                user?.likes?.includes(loggedInUser?.userId)
                  ? "text-orange-500"
                  : "text-orange-200"
              } hover:cursor-pointer hover:scale-150 `}
              icon={faHeart}
              onClick={() => likeUser(user?.userId)}
              size={`${
                user?.likes?.includes(loggedInUser?.userId) ? "xl" : "lg"
              }`}
            />
            {/* </Button> */}
            {user?.likes?.length > 0 && user?.likes?.length}
          </div>
        }
      </div>
    </Card>
  );
};
export default CustomerCard;
