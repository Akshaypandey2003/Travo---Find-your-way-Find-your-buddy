/* eslint-disable no-unused-vars */
import {
  BrowserRouter as Router,
  Route,
  Routes,
  useLocation,
} from "react-router-dom";
import Home from "./Pages/Home";
import NavBar from "./Pages/Header Section/NavBar";
import { About } from "./Pages/About";
import { DestinationDetails } from "./Pages/PageComponents/DestinationDetails";
import UserProfile from "./Pages/User/UserProfile";
import HotelsDetails from "./Pages/PageComponents/HotelsDetails";
import Register from "./Pages/Auth/Register";
import Auth from "./Pages/Auth/Auth";
import { Login } from "./Pages/Auth/Login";
import { shallowEqual, useDispatch, useSelector } from "react-redux";
import { profileCompletion } from "./Redux/Slices/authSlice";
import ProtectedRoute from "./Pages/Auth/ProtectedRoute";
import useNotificationSocket from "./CustomHooks/useNotificationSocket";
import { useEffect } from "react";
import BlogsPage from "./Pages/PageComponents/BlogComponents/BlogsPage";
import MessagePage from "./Pages/PageComponents/MessageComponents/MessagePage";
import { Avatar, AvatarImage, AvatarFallback } from "@/components/ui/avatar";
import { clearNotifications } from "./Redux/Slices/notificationSlice";
import { motion, AnimatePresence } from "framer-motion";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { DEFAULT_MALE_PIC } from "./Constants/constants";
import useChat from "./CustomHooks/useChat";

function App() {
  const loggedInUser = useSelector((store) => store.auth.user);
  const location = useLocation();
  const hideNavBarRoutes = ["/login", "/register"];
  const dispatch = useDispatch();
  const chats = useSelector((store) => store.chat.chats);
  const messages = useSelector((store) => store.chat.messages);
  const { fetchChats } = useChat();
  const { fetchMessages } = useChat();
  useNotificationSocket();
  const notifications = useSelector(
    (store) => store.notifications,
    shallowEqual
  );
  // useWebSocket();
  useEffect(() => {
    const timer = setTimeout(() => {
      dispatch(clearNotifications());
    }, 5000);
    return () => clearTimeout(timer);
  }, [notifications?.notificationStatus]);

  useEffect(() => {
    if (chats == null || chats.length === 0) fetchChats(loggedInUser?.userId);
  }, [loggedInUser]);

  useEffect(() => {
    chats?.map((chat) => fetchMessages(chat?.chatId));
  }, [loggedInUser]);

  // console.log("All fetched messages in app file are: ", messages);
  // console.log("All fetched chats in app file are: ", chats);
  return (
    <>
      {!hideNavBarRoutes.includes(location.pathname) && <NavBar />}

      <div className="message-area absolute w-full  py-0 z-50">
        <AnimatePresence>
          {notifications?.notificationStatus && (
            <motion.div
              initial={{ opacity: 0, y: -20 }} // fadeIn + slide down
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -20 }} // fadeOut + slide up
              transition={{ duration: 0.5 }}
            >
              <Alert
                variant=""
                className="bg-green-200 text-lg font-semibold max-w-96 min-w-64 m-auto"
              >
                <AlertDescription className="message-box ">
                  <div className="flex items-center gap-2">
                    <Avatar className="border border-black">
                      <AvatarImage
                        src={
                          notifications?.newNotification?.senderProfilePic ||
                          DEFAULT_MALE_PIC
                        }
                        alt="sender"
                      />
                      <AvatarFallback></AvatarFallback>
                    </Avatar>
                    <div className="flex items-center">
                      <h1>{notifications?.newNotification?.senderName+" "}</h1>
                      <h1>{notifications?.newNotification?.message}</h1>
                    </div>
                  </div>
                </AlertDescription>
              </Alert>
            </motion.div>
          )}
        </AnimatePresence>
      </div>

      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/about" element={<About />} />
        <Route
          path="/destination/details/:placeId"
          element={<DestinationDetails />}
        />
        <Route
          path="/:placeId/hotels/details/:hotelId"
          element={<HotelsDetails />}
        />
        <Route
          path="/user_profile/:userId"
          element={
            <ProtectedRoute>
              <UserProfile />
            </ProtectedRoute>
          }
        />
        <Route path="/register" element={<Register />} />
        <Route path="/inbox/:userId" element={<MessagePage />} />
        <Route path="/login" element={<Login />} />
        <Route path="/blogs/:id" element={<BlogsPage />} />
      </Routes>
    </>
    //  /details/:placeId
  );
}

export default App;
