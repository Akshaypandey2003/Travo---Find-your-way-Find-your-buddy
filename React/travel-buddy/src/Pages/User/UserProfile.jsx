/* eslint-disable no-unused-vars */
import React, { useEffect, useState } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faHeart, faLocationDot } from "@fortawesome/free-solid-svg-icons";
import usersData from "../MockData/usersData";
import { Progress } from "@/components/ui/progress";
import { Badge } from "@/components/ui/badge";

import { shallowEqual, useDispatch, useSelector } from "react-redux";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { motion, AnimatePresence } from "framer-motion";
import { authSucess } from "../../Redux/Slices/authSlice";
import {
  DEFAULT_FEMALE_PIC,
  DEFAULT_MALE_PIC,
} from "../../Constants/constants";
import { useParams } from "react-router-dom";
import PlanTripComponent from "./PlanTripComponent";
import EditProfile from "./EditProfile";
import UserTabs from "./UserTabs";
import useUserData from "../../CustomHooks/useUserData";
import DatePicker from "../PageComponents/DatePicker";
import { updateSuccess } from "../../Redux/Slices/blogsSlice";

const userData = usersData;

const UserProfile = () => {


  // const user = useSelector((store) => store.auth.user, shallowEqual);
  const success = useSelector((store) => store.auth.success, shallowEqual);
  const {getUser} = useUserData();
  const [currentUser, setCurrentUser] = useState(null);
  const profileStatus = useSelector(
    (store) => store.auth.profileStatus,
    shallowEqual
  );
  const [showAlert, setShowAlert] = useState(false);
  const dispatch = useDispatch();
  const user = useSelector((store) => store.auth, shallowEqual); // Get the logged-in user
  const { userId } = useParams(); // The id from URL like /user_profile/:userId
  const loggedInUser = user?.user; // Find the user by ID
  const usersList = user?.usersList;
  let localUser = userId==loggedInUser?.userId ? loggedInUser :usersList?.find((user) => user.userId == userId); // Get the current user's data

  //Blog fetching
  const blog = useSelector((store)=>store.blog,shallowEqual);
  console.log("Blogs data inside userProfile: ",blog);
  useEffect(() => {
    if (localUser) {
      setCurrentUser(localUser);
    }
  }, [localUser]);

  useEffect(() => {
    const fetchUser = async () => {
      if (!localUser && !currentUser) {
        const fetchedUser = await getUser(userId);
        setCurrentUser(fetchedUser);
      }
    };
    fetchUser();
  }, [localUser, currentUser, userId]);


  const notifications = useSelector(
    (store) => store.notifications.notifications,
    shallowEqual
  );
  const newTrips = useSelector(store=> store.auth.trips);

  // console.log("New trips are: ", newTrips);
  // console.log("Current notifications: ", notifications);


  // console.log("Whole users list is: ", usersList);
  // console.log("current users data is: ", currentUser);
  // console.log("logged in user is: ", loggedInUser);
  // console.log("Current user's profile pic is: ",currentUser?.profilePic);

  const {likeUser} = useUserData();
  
  useEffect(() => {
    setShowAlert(true);

    const timer = setTimeout(() => {
      setShowAlert(false);
      dispatch(authSucess("")); // Clear the success message after showing it
      if (location?.state?.message) {
        location.state.message = "";
      }
      // Clear the message after showing it
    }, 2000);
    return () => clearTimeout(timer); // cleanup
  }, [success]);

  useEffect(() => {
    setShowAlert(true);

    const timer = setTimeout(() => {
      setShowAlert(false);
      dispatch(updateSuccess({success:false,message:""})); // Clear the success message after showing it
    
    }, 2000);
    return () => clearTimeout(timer); // cleanup
  }, [blog.success]);
  

  return (
    <>
      <div className="message-area absolute left-1/3 min-w-96 ">
        <AnimatePresence>
          {showAlert && success && (
            <motion.div
              initial={{ opacity: 0, y: -20 }} // fadeIn + slide down
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -20 }} // fadeOut + slide up
              transition={{ duration: 0.5 }}
            >
              <Alert variant="" className="bg-green-200 text-lg font-semibold">
                <AlertDescription className="message-box ">
                  {success}
                </AlertDescription>
              </Alert>
            </motion.div>
          )}
        </AnimatePresence>
        {/* ----------------------Popup for blog---------------- */}
        <AnimatePresence>
          {showAlert && blog?.success && (
            <motion.div
              initial={{ opacity: 0, y: -20 }} // fadeIn + slide down
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -20 }} // fadeOut + slide up
              transition={{ duration: 0.5 }}
            >
              <Alert variant="" className="bg-green-200 text-lg font-semibold">
                <AlertDescription className="message-box text-center">
                  {blog?.message}
                </AlertDescription>
              </Alert>
            </motion.div>
          )}
        </AnimatePresence>

      </div>
      <div className="p-10 flex flex-col items-center justify-center">
        <div className="rounded-xl w-[80rem] h-[25rem] object-contain overflow-hidden">
          <img src="../friends-2.jpg" alt="" className="w-full h-full" />
        </div>

        <div className=" relative  bottom-[4rem]">
          <div className=" flex items-center justify-between ">
            <div>
              <div
                className={
                  currentUser?.profilePic
                    ? "border-4 border-orange-600 h-[10rem] w-[10rem] rounded-full object-cover overflow-hidden"
                    : "border-4 border-orange-600 bg-orange-200 h-[10rem] w-[10rem] rounded-full object-cover overflow-hidden "
                }
              >
                <img
                  src={
                    currentUser?.profilePic
                      ? currentUser?.profilePic
                      : currentUser?.gender?.toLowerCase() === "male"
                      ? DEFAULT_MALE_PIC
                      : DEFAULT_FEMALE_PIC
                  }
                  alt="profile"
                />
              </div>

              <div className="">
                <span className="font-semibold text-2xl mr-2">
                  {currentUser?.name}
                </span>
                {currentUser?.country && (
                  <>
                    <span>
                      <FontAwesomeIcon
                        icon={faLocationDot}
                        className="mr-2 text-orange-600"
                      />
                    </span>
                    <span className="text-gray-500 text-sm">
                      {currentUser?.country}
                    </span>
                  </>
                )}
              </div>
              <span className="text-sm ">{currentUser?.bio}</span>
              <div className="flex gap-2   ">
                {currentUser?.preferences &&
                  currentUser?.preferences?.map((item, index) => (
                    <Badge
                      key="index"
                      variant="outline"
                      className="border-orange-600 h-4 font-thin"
                    >
                      {item}
                    </Badge>
                  ))}
              </div>
              {currentUser?.likes?.length > 0 && (
                <div className="flex gap-4 items-center">
                  {/* <Button className="w-0 h-2 rounded-full p-0"> */}
                  <FontAwesomeIcon
                    className={`${
                      currentUser?.likes?.includes(loggedInUser?.userId)
                        ? "text-orange-500"
                        : "text-orange-200"
                    } hover:cursor-pointer `}
                    icon={faHeart}
                    onClick={() => likeUser(currentUser?.userId)}
                    size={`${
                        currentUser?.likes?.includes(loggedInUser?.userId) ? "xl" : "lg"
                    }`}
                  />
                  {/* </Button> */}
                  <h1>{currentUser?.likes?.length > 0 && currentUser?.likes?.length}</h1>
                </div>
               
              )}
            </div>
            {userId == user?.user?.userId && (
              <div className=" flex gap-4 mr-[2rem] mt-[2rem]">
                 
                <PlanTripComponent />
                <EditProfile />
                {profileStatus < 100 && (
                  <div className="flex items-center gap-2">
                    <Progress
                      className="text-orange-400"
                      value={profileStatus}
                    ></Progress>
                    <span> {profileStatus}%</span>
                  </div>
                )}
              </div>
            )}
          </div>

          <div className="">
            <UserTabs />
          </div>
        </div>
      </div>
      {/* <DatePicker /> */}
    </>
  );
};
export default UserProfile;
