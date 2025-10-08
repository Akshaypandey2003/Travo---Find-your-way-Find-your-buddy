/* eslint-disable no-unused-vars */
import { useEffect, useState } from "react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import HeroSectionImageCarousel from "./HeroSectionImageCarousel";
import Footer from "./Footer Section/Footer";
import destinationsData from "./MockData/destinationsData";
import usersData from "./MockData/usersData";
import usePlacesData from "../CustomHooks/usePlacesData";
import { useSelector, shallowEqual, useDispatch } from "react-redux";
import useUserData from "../CustomHooks/useUserData";
import { clearNotifications } from "../Redux/Slices/notificationSlice";
import useBlog from "../CustomHooks/useBlog";
import SmoothAutoScroller from "./PageComponents/SmoothAutoScroller";
import TripSlider from "./PageComponents/TripSlider";
import { PeopleSection } from "./PageComponents/PeopleSection";

const customerCardInfo = [
  {
    name: "Atul Garg",
    userImg: "../Shivam.JPG",
  },
  {
    name: "Anjali Kumari",
    userImg: "../Anjali.jpg",
  },
  {
    name: "Akshay Pandey",
    userImg: "../AkshayImg.JPG",
  },
  {
    name: "Khushi",
    userImg: "../Khushi.jpg",
  },
];
const randomData = [
  "Adventure",
  "Backpacking",
  "Beach",
  "Camping",
  "Cruise",
  "Expedition",
  "Explore",
  "Glamping",
  "Hiking",
  "Island",
  "Itinerary",
  "Journey",
  "Kayaking",
  "Landmark",
  "Luggage",
  "Mountain",
  "Nomad",
  "Passport",
  "Resort",
  "Roadtrip",
  "Safari",
  "Sightseeing",
  "Souvenir",
  "Tourist",
  "Trekking",
  "Vacation",
  "Wanderlust",
  "Wildlife",
  "WorldTour",
  "Yacht",
];

const destData = destinationsData;
const userData = usersData;

export const Home = () => {
  const { getPlaces } = usePlacesData();
  const { getAllUsers, getAllNotifications } = useUserData();
  const { getAllBlogs } = useBlog();
  const places = useSelector((store) => store.places.placesData, shallowEqual);
  const dispatch = useDispatch();
  const [loading, setLoading] = useState(false);
  const usersList = useSelector((store) => store.auth.usersList, shallowEqual);
  const loggedInUser = useSelector((store) => store.auth.user, shallowEqual);
  const [page, setPage] = useState(0);
  const trips = useSelector((store) => store.trip.trips);
  const upcomingTrips = trips.filter((trip) => trip?.tripStatus === "UPCOMING");
  const onGoingTrips = trips.filter((trip) => trip?.tripStatus === "ONGOING");

  const userNextPageToken = useSelector(
    (store) => store.auth.nextPageToken,
    shallowEqual
  );

  const weatherData = useSelector(
    (store) => store.places.weatherData,
    shallowEqual
  );
  const notifications = useSelector(
    (store) => store.notifications,
    shallowEqual
  );

  useEffect(() => {
    const timer = setTimeout(() => {
      dispatch(clearNotifications());
    }, 5000);
    return () => clearTimeout(timer);
  }, [notifications?.notificationStatus]);

  useEffect(() => {
    // getPlaces();
    if (!usersList || usersList?.length === 0) {
      getAllUsers(page);
    }
    if (
      loggedInUser &&
      (!notifications?.notifications ||
        notifications?.notifications?.length === 0)
    ) {
      getAllNotifications(loggedInUser?.userId);
    }
    getAllBlogs();
  }, [loggedInUser]);

  return (
    <div className="mt-36">
      {/* <div className="message-area absolute left-1/3  m-auto min-w-96 px-2">
        <AnimatePresence>
          {notifications?.notificationStatus && (
            <motion.div
              initial={{ opacity: 0, y: -20 }} // fadeIn + slide down
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -20 }} // fadeOut + slide up
              transition={{ duration: 0.5 }}
            >
              <Alert variant="" className="bg-green-200 text-lg font-semibold">
                <AlertDescription className="message-box ">
                  <div className="flex items-center gap-2">
                    <Avatar className="border border-black">
                      <AvatarImage src={notifications?.newNotification?.senderProfilePic || DEFAULT_MALE_PIC} alt="sender" />
                      <AvatarFallback>
                      </AvatarFallback>
                    </Avatar>
                    <h1>{notifications?.newNotification?.senderName}</h1>
                    <h1>{notifications?.newNotification?.message}</h1>
                  </div>
                </AlertDescription>
              </Alert>
            </motion.div>
          )}
        </AnimatePresence>
      </div> */}

      <div className="flex items-center justify-between">
        <div className="w-[50%] px-20 py-10">
          <div className="flex items-center gap-2 mb-5">
            <div className="w-8 h-8 ">
              <img src="../plane.png" alt="" />
            </div>
            <h1 className="text-orange-500 font-semibold ">
              Explore The World
            </h1>
          </div>
          <p className="text-4xl mb-5">
            {" "}
            <strong className="text-5xl">Plan less, explore more!</strong>{" "}
            <br></br>Your dream destinations
          </p>
          <p className="text-lg text-gray-500">
            Why travel alone when you can share the adventure? Find your ideal
            travel buddy and make every trip a memorable one!
          </p>
          {/* <Card className="flex px-5 mt-10">
            <div className="border-r-2  flex p-2 gap-2 items-center ">
              <div className="w-9 h-9  rounded-full p-2 bg-orange-200  ">
                <img src="../place.png" alt="" />
              </div>
              <div>
                <h2 className="font-bold">Location</h2>
                <p className="text-xs font-semibold text-gray-500">
                  Where are you going
                </p>
              </div>
            </div>
            <div className="border-r-2  flex px-8 gap-2 items-center  mr-4 ">
              <div className="w-9 h-9  rounded-full p-2 bg-orange-200  ">
                <img src="../calendar.png" alt="" />
              </div>
              <div>
                <h2 className="font-bold">Select Date</h2>
                <p className="text-xs font-semibold text-gray-500">
                  1st Feb 2025
                </p>
              </div>
            </div>
            <div className="flex justify-center items-center pl-6">
              <Button className="bg-orange-600 hover:bg-orange-500 border-none font-semibold">
                Get Started
              </Button>
            </div>
          </Card> */}
          <HeroSectionImageCarousel />
        </div>
        <div className="w-[50%] ">
          <Card className="w-[15rem] h-[8rem] absolute top-[12rem] right-[33rem] z-10 flex flex-col justify-center items-center text-center">
            <h1 className="text-2xl font-semibold ">100+ Destinations</h1>
            <span className="text-gray-400">
              More than 100 trips have been completed
            </span>
          </Card>
          <Card className="w-[6rem] h-[6rem] absolute top-[8rem] right-[9rem] z-10 flex flex-col justify-center items-center">
            <h1 className="text-2xl font-semibold ">100%</h1>
            <span className="text-gray-400">Verified</span>
          </Card>
          <Card className="w-[12rem] h-[15rem] object-contain overflow-hidden absolute top-[9rem] right-[23rem] transform transition-transform duration-500 ease-in-out hover:scale-105">
            <img
              src="https://res.cloudinary.com/dwg7vniow/image/upload/v1751808427/Beach1_ru7xus.jpg"
              alt=""
              className="w-full h-full"
            />
          </Card>

          <Card className="w-[20rem] h-[15rem]  object-contain overflow-hidden absolute top-[12rem] right-[2rem]">
            <img
              src="https://res.cloudinary.com/dwg7vniow/image/upload/v1751808450/Beach2_aofhe0.jpg"
              alt=""
              className="w-full h-full"
            />
          </Card>
          <Card className="w-[17rem] h-[15rem]  object-contain overflow-hidden absolute top-[25rem]  right-[23rem]">
            <img
              src="https://res.cloudinary.com/dwg7vniow/image/upload/v1751808585/mountains1_bnfhbr.jpg"
              alt=""
              className="w-full h-full"
            />
          </Card>
          <Card className="w-[17rem] h-[15rem] object-contain overflow-hidden absolute top-[28rem]  right-[5rem]">
            <img
              src="https://res.cloudinary.com/dwg7vniow/image/upload/v1751808631/mountains2_hpdkds.jpg"
              alt=""
              className="w-full h-full"
            />
          </Card>
        </div>
      </div>
      <div className="px-10  mt-20">
        {/* <SmoothAutoScroller data={randomData} reverse={false} speed={0.5} /> */}
        <SmoothAutoScroller data={randomData} reverse={false} speed={0.5} />

        <div className="my-5">
          {upcomingTrips && upcomingTrips.length > 0 && (
            <TripSlider trips={upcomingTrips} tripType={"UPCOMING"} />
          )}
          {onGoingTrips && onGoingTrips.length > 0 && (
            <TripSlider trips={onGoingTrips} tripType={"ONGOING"} />
          )}
          <PeopleSection />
        </div>
      </div>
      <Footer />
    </div>
  );
};
export default Home;
