/* eslint-disable no-unused-vars */
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { ScrollArea } from "@/components/ui/scroll-area";
import NotificationCard from "./NotificationCard";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { motion, AnimatePresence } from "framer-motion";
import { Avatar, AvatarImage, AvatarFallback } from "@/components/ui/avatar";
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
  SheetFooter,
} from "@/components/ui/sheet";
import {
  DEFAULT_FEMALE_PIC,
  DEFAULT_MALE_PIC,
} from "../../Constants/constants";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faBell,
  faChartLine,
  faClockRotateLeft,
  faComment,
  faKey,
  faMessage,
  faMoon,
  faSun,
  faUserGroup,
} from "@fortawesome/free-solid-svg-icons";
import { shallowEqual, useDispatch, useSelector } from "react-redux";
import { Link, useNavigate, useParams } from "react-router-dom";
import { changeTheme } from "../../Redux/Slices/authSlice";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import useAuth from "../../CustomHooks/useAuth";
import { persistor } from "../../Redux/Store";
import NotificationAccordian from "./NotificationAccordian";
import useUserData from "../../CustomHooks/useUserData";
import ConnectionCard from "./ConnectionCard";

export const SideSheet = () => {
  const dispatch = useDispatch();
  const userData = useSelector((store) => store.auth, shallowEqual);
  const profileStatus = useSelector(
    (store) => store.auth.profileStatus,
    shallowEqual
  );
  const notifications = useSelector(
    (store) => store.notifications,
    shallowEqual
  );
  // console.log("Notifications", notifications?.notifications);
  const friendRequests = Array.isArray(notifications?.notifications)
    ? notifications.notifications.filter(
        (item) => item.type === "FRIEND_REQUEST"
      )
    : [];

  const likes = Array.isArray(notifications?.notifications)
    ? notifications.notifications.filter((item) => item.type === "LIKE")
    : [];
  const comments = Array.isArray(notifications?.notifications)
    ? notifications.notifications.filter((item) => item.type === "COMMENT")
    : [];

  const tripRequests = Array.isArray(notifications?.notifications)
    ? notifications.notifications.filter((item) => item.type === "TRIP_REQUEST")
    : [];

  const newTrips = Array.isArray(notifications?.notifications)
    ? notifications.notifications.filter((item) => item.type === "NEW_TRIP")
    : [];

  const acceptedTripRequest = Array.isArray(notifications?.notifications)
    ? notifications.notifications.filter((item) => item.type === "ACCEPTED")
    : [];

  let currentUser = useSelector((store) => store.auth.user, shallowEqual); // Get the current user's data
  const messages = useSelector((store) => store.chat.messages);
  // console.log("Current user is: ", currentUser);
  // console.log("Current users following is: ", currentUser?.following);
  const navigate = useNavigate();
  const { logoutUser, loading } = useAuth();

  const handleLogout = () => {
    persistor.purge();
    logoutUser();
    window.location.reload();
  };

  //---------------------Counting number of unread messages-------------------------
  const unreadMessages = [];

  for (const chatId in messages) {
    const chatMessages = messages[chatId];

    const unread = chatMessages.filter((msg) => msg.read === false);

    unreadMessages.push(...unread);
  }

  return (
    <Sheet>
      <SheetTrigger className="border border-orange-700 rounded-full bg-orange-200">
        <Avatar>
          <AvatarImage
            src={
              userData?.user?.profilePic
                ? userData?.user?.profilePic
                : userData?.user?.gender?.toLowerCase() === "male"
                ? DEFAULT_MALE_PIC
                : DEFAULT_FEMALE_PIC
            }
            alt="PROFILE"
          />
          <AvatarFallback>
            {userData.user.name.charAt(0).toUpperCase()}
          </AvatarFallback>
        </Avatar>
      </SheetTrigger>
      <SheetContent className="">
        <SheetHeader>
          <SheetTitle>Welcome Back, {userData.user.name}</SheetTitle>
          <SheetDescription>
            {profileStatus < 100 ? (
              <p>
                Looks like you haven't yet completed your profile. Please click
                the button below to complete your profile and get the most out
                of our services
              </p>
            ) : (
              <p>
                Lorem ipsum dolor sit amet consectetur adipisicing elit. Non
                velit dolores est similique magnam possimus itaque voluptatibus
                suscipit exercitationem voluptas, ipsum repudiandae nobis quam
                explicabo, sit iusto! Ipsam, quas enim.
              </p>
            )}
          </SheetDescription>
          {profileStatus < 100 && (
            <Link to={`/user_profile/${userData?.user?.userId}`}>
              Complete your profile
            </Link>
          )}

          <div className="flex items-center justify-between">
            <h2 className="text-lg font-semibold">User Profile</h2>
            <p
              onClick={() => dispatch(changeTheme())}
              className="hover:cursor-pointer"
            >
              <FontAwesomeIcon
                icon={userData.theme === "dark" ? faSun : faMoon}
                className={`text-${
                  userData.theme === "dark" ? "orange-500" : "black"
                } text-2xl`}
              />
            </p>
          </div>

          <hr />
          <Dialog>
            <DialogTrigger
              aschild="true"
              className="py-1 px-2 hover:bg-orange-200 border-none font-semibold outline-none focus:outline-none focus:ring-0 focus:ring-offset-0"
            >
              <div className="flex items-center gap-2">
                <FontAwesomeIcon icon={faBell} />
                <div className="flex items-center justify-between w-full ">
                  <h1>Notifications</h1>
                  {notifications?.notifications?.length > 0 && (
                    <Badge
                      variant="outline"
                      className="bg-orange-300 border-none px-2 rounded-2xl"
                    >
                      {notifications?.notifications?.length}
                    </Badge>
                  )}
                </div>
              </div>
            </DialogTrigger>
            <DialogContent className="sm:max-w-[550px]">
              <ScrollArea className="h-96 rounded-md">
                <DialogHeader>
                  <DialogTitle>
                    <h1>Notifications</h1>
                  </DialogTitle>
                  <DialogDescription>
                    See how people are interacting with your profile & get latest updates.
                  </DialogDescription>
                </DialogHeader>
                {notifications?.notifications?.length == 0 && (
                  <div className="border border-black flex items-center">
                    <h1 className=" mx-auto">Notifications not available!!</h1>
                  </div>
                )}
                {friendRequests?.length > 0 && (
                  <NotificationAccordian
                    notifications={friendRequests}
                    type={"Follow Requests"}
                  />
                  // <div className="m-4 border-b-2 border-orange-200">
                  //   <h1>Follow Requests</h1>
                  //   <div className="message-area absolute left-14  m-auto min-w-96 px-2">
                  //     <AnimatePresence>
                  //       {notifications?.notificationStatus && (
                  //         <motion.div
                  //           initial={{ opacity: 0, y: -20 }} // fadeIn + slide down
                  //           animate={{ opacity: 1, y: 0 }}
                  //           exit={{ opacity: 0, y: -20 }} // fadeOut + slide up
                  //           transition={{ duration: 0.5 }}
                  //         >
                  //           <Alert
                  //             variant=""
                  //             className={`${
                  //               notifications?.success
                  //                 ? "bg-green-200"
                  //                 : "bg-red-500"
                  //             } text-lg font-semibold`}
                  //           >
                  //             <AlertDescription className="message-box ">
                  //               <h1>{notifications?.message}</h1>
                  //             </AlertDescription>
                  //           </Alert>
                  //         </motion.div>
                  //       )}
                  //     </AnimatePresence>
                  //   </div>
                  //   {friendRequests?.length > 0 &&
                  //     friendRequests?.map((item, index) => (
                  //       <NotificationCard
                  //         key={index}
                  //         notification={item}
                  //       />
                  //     ))}
                  // </div>
                )}
                {likes?.length > 0 && (
                  <NotificationAccordian notifications={likes} type={"Likes"} />
                  // <div className="m-4 border-b-2 border-orange-200">
                  //   <h1>Likes</h1>
                  //   {/* <div className="message-area absolute left-14  m-auto min-w-96 px-2">
                  //     <AnimatePresence>
                  //       {notifications?.notificationStatus && (
                  //         <motion.div
                  //           initial={{ opacity: 0, y: -20 }} // fadeIn + slide down
                  //           animate={{ opacity: 1, y: 0 }}
                  //           exit={{ opacity: 0, y: -20 }} // fadeOut + slide up
                  //           transition={{ duration: 0.5 }}
                  //         >
                  //           <Alert
                  //             variant=""
                  //             className={`${
                  //               notifications?.success
                  //                 ? "bg-green-200"
                  //                 : "bg-red-500"
                  //             } text-lg font-semibold`}
                  //           >
                  //             <AlertDescription className="message-box ">
                  //               <h1>{notifications?.message}</h1>
                  //             </AlertDescription>
                  //           </Alert>
                  //         </motion.div>
                  //       )}
                  //     </AnimatePresence>
                  //   </div> */}
                  //   {likes?.length > 0 &&
                  //     likes?.map((item, index) => (
                  //       <NotificationCard
                  //         key={index}
                  //         notification={item}
                  //       />
                  //     ))}
                  // </div>
                )}
                {comments?.length > 0 && (
                  <NotificationAccordian
                    notifications={comments}
                    type={"Comments"}
                  />
                )}
                {newTrips?.length > 0 && (
                  <NotificationAccordian
                    notifications={newTrips}
                    type={"New Trips"}
                  />
                )}
                {tripRequests?.length > 0 && (
                  <NotificationAccordian
                    notifications={tripRequests}
                    type={"Trip Requests"}
                  />
                )}
                {acceptedTripRequest?.length > 0 && (
                  <NotificationAccordian
                    notifications={acceptedTripRequest}
                    type={"Accepted Requests"}
                  />
                )}
              </ScrollArea>
            </DialogContent>
          </Dialog>

          <Link
            to={`/inbox/${userData?.user?.userId}`}
            className=" hover:text-black px-2 py-1 hover:bg-orange-200 rounded-lg flex items-center justify-between"
          >
            <div>
                <FontAwesomeIcon icon={faComment} /> <span>Messages</span>
            </div>
           
            {unreadMessages.length > 0 && (
              <Badge
                variant="outline"
                className="bg-orange-300 border-none px-2 rounded-2xl"
              >
                {unreadMessages.length}
              </Badge>
            )}
          </Link>

          <Dialog>
            <DialogTrigger
              aschild="true"
              className="py-1 px-2 hover:bg-orange-200 border-none font-semibold outline-none focus:outline-none focus:ring-0 focus:ring-offset-0"
            >
              <div className="flex items-center gap-2">
                <FontAwesomeIcon icon={faUserGroup} />
                <div className="flex items-center justify-between w-full ">
                  <h1>Close Friends</h1>
                  {currentUser?.closeFriends?.length > 0 && (
                    <Badge
                      variant="outline"
                      className="bg-orange-300 border-none px-2 rounded-2xl"
                    >
                      {currentUser?.closeFriends?.length}
                    </Badge>
                  )}
                </div>
              </div>
            </DialogTrigger>
            <DialogContent className="sm:max-w-[550px]">
              <ScrollArea className="h-96 rounded-md">
                <DialogHeader>
                  <DialogTitle>
                    <h1>Close Friends</h1>
                  </DialogTitle>
                  <DialogDescription>
                    Select your close friends to share your private plans and
                    trips with them.
                  </DialogDescription>
                </DialogHeader>

                {currentUser?.following?.length == 0 && (
                  <div className="border border-black flex items-center">
                    <h1 className=" mx-auto">
                      No connection available, Please make connections !
                    </h1>
                  </div>
                )}
                {currentUser?.following?.length > 0 && (
                  <div className="p-4 ">
                    {currentUser?.following?.map((item, index) => (
                      <ConnectionCard key={index} userId={item} />
                    ))}
                  </div>
                )}
              </ScrollArea>
            </DialogContent>
          </Dialog>
          <div className="px-2 py-1 hover:bg-orange-200 rounded-lg">
            <Link to={`/user_profile/${userData?.user?.userId}`}>
              <FontAwesomeIcon icon={faKey} /> <span>Change Password</span>
            </Link>
          </div>
        </SheetHeader>
        <SheetFooter className="mt-2">
          <Button
            onClick={handleLogout}
            className="bg-orange-600 hover:bg-orange-500 border-none font-semibold"
          >
            Logout
          </Button>
        </SheetFooter>
      </SheetContent>
    </Sheet>
  );
};
export default SideSheet;
