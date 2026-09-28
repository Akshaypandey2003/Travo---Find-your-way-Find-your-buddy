import { useEffect } from "react";
import { Navigate, Route, Routes, useLocation } from "react-router-dom";
import { shallowEqual, useDispatch, useSelector } from "react-redux";
import { motion, AnimatePresence } from "framer-motion";
import Landing from "./pages/Landing";
import Login from "./pages/Login";
import Onboarding from "./pages/Onboarding";
import Dashboard from "./pages/Dashboard";
import Explore from "./pages/Explore";
import Messages from "./pages/Messages";
import MyTrips from "./pages/MyTrips";
import CreateTrip from "./pages/CreateTrip";
import CreateBlog from "./pages/CreateBlog";
import Profile from "./pages/Profile";
import TripDetails from "./pages/TripDetails";
import Settings from "./pages/Settings";
import Activity from "./pages/Activity";
import Review from "./pages/Review";
import Layout from "./components/Layout";
import useNotificationSocket from "./CustomHooks/useNotificationSocket";
import { Avatar, AvatarImage, AvatarFallback } from "./components/ui/avatar";
import { clearNotifications } from "./Redux/Slices/notificationSlice";
import { Alert, AlertDescription } from "./components/ui/alert";
import { DEFAULT_MALE_PIC } from "./Constants/constants";
import useChat from "./CustomHooks/useChat";
import { addTrips } from "./Redux/Slices/tripSlice";
import ProtectedRoute from "./components/ProtectedRoute";
import ResetPassword from "./pages/ResetPassword";

const ScrollToTop = () => {
  const { pathname } = useLocation();

  useEffect(() => {
    window.scrollTo(0, 0);
  }, [pathname]);

  return null;
};

const App = () => {
  const loggedInUser = useSelector((store) => store.auth.user);
  const dispatch = useDispatch();
  const chats = useSelector((store) => store.chat.chats);
  const allUsers = useSelector((store) => store.auth.usersList);
  const theme = useSelector((store) => store.auth.theme);
  const notifications = useSelector(
    (store) => store.notifications,
    shallowEqual,
  );
  const { fetchChats, fetchMessages } = useChat();

  useNotificationSocket();

  useEffect(() => {
    const timer = setTimeout(() => {
      dispatch(clearNotifications());
    }, 5000);

    return () => clearTimeout(timer);
  }, [dispatch, notifications?.notificationStatus]);

  // useEffect(() => {
  //   if (chats == null || chats.length === 0) {
  //     fetchChats(loggedInUser?.userId);
  //   }
  // }, [loggedInUser]);

  // useEffect(() => {
  //   chats?.forEach((chat) => fetchMessages(chat?.chatId));
  // }, [loggedInUser]);

  // useEffect(() => {
  //   if (allUsers && allUsers.length > 0) {
  //     const trips = allUsers.flatMap((user) => user.trips || []);
  //     dispatch(addTrips(trips));
  //   }
  // }, [allUsers, dispatch]);

  return (
    <div className="min-h-screen">
      <ScrollToTop />
      <div className="message-area absolute w-full py-0 z-50">
        <AnimatePresence>
          {notifications?.notificationStatus && (
            <motion.div
              initial={{ opacity: 0, y: -20 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -20 }}
              transition={{ duration: 0.5 }}
            >
              <Alert
                variant="default"
                className={`${
                  notifications?.error
                    ? "bg-red-500/15 border-red-400/30 text-red-100"
                    : "bg-emerald-500/15 border-emerald-400/30 text-emerald-50"
                } 
  backdrop-blur-xl
  shadow-2xl
  rounded-2xl
  border
  max-w-96
  min-w-64
  m-auto
  px-4
  py-3
  text-sm
  font-semibold
  transition-all
  duration-300`}
              >
                <AlertDescription className="message-box">
                  <div className="flex items-center gap-3">
                    {notifications?.newNotification?.message ? (
                      <>
                        <Avatar className="border border-white/30 shadow-md shrink-0">
                          <AvatarImage
                            src={
                              notifications.newNotification.senderProfilePic ||
                              DEFAULT_MALE_PIC
                            }
                            alt="sender"
                          />
                          <AvatarFallback />
                        </Avatar>

                        <h1 className="leading-relaxed">
                          {notifications.newNotification.message}
                        </h1>
                      </>
                    ) : (
                      <h1 className="leading-relaxed text-center w-full">
                        {notifications?.message}
                      </h1>
                    )}
                  </div>
                </AlertDescription>
              </Alert>
            </motion.div>
          )}
        </AnimatePresence>
      </div>

      <Routes>
        <Route path="/" element={<Landing />} />
        <Route path="/login" element={<Login />} />
        <Route path="/reset-password" element={<ResetPassword />} />
        <Route path="/onboarding" element={<Onboarding />} />

        <Route element={<ProtectedRoute />}>/
          <Route element={<Layout />}>
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/explore" element={<Explore />} />
            <Route path="/messages" element={<Messages />} />
            <Route path="/trips" element={<MyTrips />} />
            <Route path="/trips/create" element={<CreateTrip />} />
            <Route
              path="/blogs/create"
              element={<CreateBlog formType="create" />}
            />
            <Route path="/trips/:tripId" element={<TripDetails />} />
            <Route path="/profile/:userId" element={<Profile />} />
            <Route path="/settings" element={<Settings />} />
            <Route path="/activity" element={<Activity />} />
            <Route path="/review" element={<Review />} />
          </Route>
        </Route>

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </div>
  );
};

export default App;
