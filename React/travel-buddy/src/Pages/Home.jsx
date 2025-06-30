/* eslint-disable no-unused-vars */
import React, { useEffect, useState } from "react";
import { Card } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import HeroSectionImageCarousel from "./HeroSectionImageCarousel";
import Footer from "./Footer Section/Footer";
import Marquee from "react-fast-marquee";
import CustomerCard from "./PageComponents/CustomerCard";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faFilter, faSearch } from "@fortawesome/free-solid-svg-icons";
import { SelectFilterOptions } from "./PageComponents/SelectFilterOptions";
import DestinationCard from "./PageComponents/DestinationCard";
import destinationsData from "./MockData/destinationsData";
import usersData from "./MockData/usersData";
import usePlacesData from "../CustomHooks/usePlacesData";
import { useSelector, shallowEqual, useDispatch } from "react-redux";
import store from "../Redux/Store";
import useUserData from "../CustomHooks/useUserData";
import { setNextPageToken } from "../Redux/Slices/authSlice";
import { Skeleton } from "@/components/ui/skeleton";
import { ScrollArea, ScrollBar } from "@/components/ui/scroll-area";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { motion, AnimatePresence } from "framer-motion";
import { clearNotifications } from "../Redux/Slices/notificationSlice";
import { DEFAULT_MALE_PIC } from "../Constants/constants";
import { Avatar, AvatarImage, AvatarFallback } from "@/components/ui/avatar";
import useBlog from "../CustomHooks/useBlog";

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
  const { getAllUsers,getAllNotifications } = useUserData();
  const {getAllBlogs} = useBlog();

  const places = useSelector((store) => store.places.placesData, shallowEqual);
  const blog = useSelector((store)=>store.blog.blogs);
  console.log("All blogs are: ", blog);
  
  // const nextPageToken = useSelector(
  //   (store) => store.places.nextPageToken,
  //   shallowEqual
  // );
  // const descriptions = useSelector((store) => store.places.descriptions);

  // const user = useSelector((store) => store.auth.user, shallowEqual);
  const usersList = useSelector((store) => store.auth.usersList, shallowEqual);
  const loggedInUser = useSelector((store) => store.auth.user, shallowEqual);
  const chat = useSelector((store)=>store.chat);
  console.log("Chat: ", chat);
 
  const [page, setPage] = useState(0);
  const userNextPageToken = useSelector(
    (store) => store.auth.nextPageToken,
    shallowEqual
  );

  // console.log("next page token is: ", userNextPageToken);
  const [loading, setLoading] = useState(false);

  console.log("User list data in redux store: ", usersList);

  // console.log("User in redux store is: ",user);
  // console.log("DEscription data: ",descriptions);
  const weatherData = useSelector(
    (store) => store.places.weatherData,
    shallowEqual
  );

  //  console.log(places);
  // console.log(places.length);
  // console.log("Next page token: ",nextPageToken);
  // console.log("Weather data: ",weatherData);

  const dispatch = useDispatch();

  const notifications = useSelector(
    (store) => store.notifications,
    shallowEqual
  );
  // console.log("All notifications: ",notifications?.notifications);
  // console.log("new notifications received: ", notifications?.newNotification);
  // console.log("Notification status: ", notifications?.notificationStatus);

  const loadMoreUsers = async () => {
    if (!userNextPageToken || loading) return; // prevent multiple calls

    setLoading(true);
    setPage((page) => page + 1);
    const newUsers = await getAllUsers(page);
    const pageSize = 10 + Math.pow(page, 2);

    if (newUsers && newUsers.length < pageSize) {
      dispatch(setNextPageToken(false));
    }
    setLoading(false);
  };
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
    if( loggedInUser && (!notifications?.notifications || notifications?.notifications?.length === 0)) 
    {
      console.log("Fetching notifications for user: ", loggedInUser?.userId);
      getAllNotifications(loggedInUser?.userId);
    }
    getAllBlogs();
  }, [loggedInUser]);


  return (
    <div className="">
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
            travel buddy and make every trip a memorable one!.
          </p>
          <Card className="flex px-5 mt-10">
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
          </Card>
          <HeroSectionImageCarousel />
        </div>
        <div className="w-[50%] ">
          <Card className="w-[15rem] h-[8rem] absolute top-[12rem] right-[33rem] z-10 flex flex-col justify-center items-center text-center">
            <h1 className="text-2xl font-semibold ">
              100+ Destinations
            </h1>
            <span className="text-gray-400">
              More than 100 trips have been completed
            </span>
          </Card>
          <Card className="w-[6rem] h-[6rem] absolute top-[8rem] right-[9rem] z-10 flex flex-col justify-center items-center">
            <h1 className="text-2xl font-semibold ">100%</h1>
            <span className="text-gray-400">Verified</span>
          </Card>
          <Card className="w-[12rem] h-[15rem]  object-contain overflow-hidden absolute top-[9rem]  right-[23rem]">
            <img src="../Beach1.jpg" alt="" className="w-full h-full" />
          </Card>
          <Card className="w-[20rem] h-[15rem]  object-contain overflow-hidden absolute top-[12rem] right-[2rem]">
            <img src="../Beach2.jpg" alt="" className="w-full h-full" />
          </Card>
          <Card className="w-[17rem] h-[15rem]  object-contain overflow-hidden absolute top-[25rem]  right-[23rem]">
            <img src="../mountains1.jpg" alt="" className="w-full h-full" />
          </Card>
          <Card className="w-[17rem] h-[15rem] object-contain overflow-hidden absolute top-[28rem]  right-[5rem]">
            <img src="../mountains2.jpg" alt="" className="w-full h-full" />
          </Card>
        </div>
      </div>
      <div className="p-10  mt-10">
        <div className="border-none shadow-none">
          <div className="flex flex-col items-center w-[90rem]  my-7">
            <Marquee autoFill={true} speed={10} className="">
              {randomData.map((item, index) => (
                <Badge key={index} className="mx-4  border-none">
                  {item}
                </Badge>
              ))}
            </Marquee>
          </div>
          <div className="flex flex-col items-center w-[90rem] ">
            <Marquee autoFill={true} direction="right" speed={10} className="">
              {randomData.map((item, index) => (
                <Badge key={index} className="mx-4  border-none">
                  {item}
                </Badge>
              ))}
            </Marquee>
          </div>
        </div>
        <div className="  p-10 ">
          {/* [mask-image:linear-gradient(to_bottom,rgba(0,0,0,1)_75%,rgba(0,0,0,0)_100%)] */}
          <h1 className="text-3xl font-semibold">People</h1>
          <div className="py-4">
            <ScrollArea className=" w-full h-[40rem] rounded-md border-black shadow-none">
              <div className="flex gap-4 py-5 flex-wrap">
                {usersList && usersList?.length > 0
                  ? usersList.map((item, index) => (
                      <CustomerCard key={index} user={item} />
                    ))
                  : [1, 2, 3, 4, 5, 6, 7, 8, 9, 10].map((item, index) => (
                      <div key={index} className="flex flex-col space-y-3">
                        <Skeleton className="h-[200px] w-[320px] rounded-xl p-3">
                          <Skeleton className="h-11 w-11 rounded-full" />
                        </Skeleton>
                        <div className="space-y-2">
                          <Skeleton className="h-4 w-[250px]" />
                          <Skeleton className="h-4 w-[200px]" />
                        </div>
                      </div>
                    ))}
                {/*------------------------ Skeleton part on loading ----------------------- */}
                {loading &&
                  [1, 2, 3, 4, 5, 6, 7, 8, 9, 10].map((item, index) => (
                    <div key={index} className="flex flex-col space-y-3">
                      <Skeleton className="h-[200px] w-[320px] rounded-xl p-3">
                        <Skeleton className="h-11 w-11 rounded-full" />
                      </Skeleton>
                      <div className="space-y-2">
                        <Skeleton className="h-4 w-[250px]" />
                        <Skeleton className="h-4 w-[200px]" />
                      </div>
                    </div>
                  ))}
                {/* ------------------------------------------------------------------------- */}
              </div>
            </ScrollArea>
          </div>
          <div className="text-center">
            {userNextPageToken ? (
              <Button
                onClick={() => loadMoreUsers()}
                className="bg-orange-700 hover:bg-orange-600 border-none"
                // disabled={!userNextPageToken}
              >
                {loading ? "Loading..." : "View More"}
              </Button>
            ) : (
              <p className="text-gray-500 mt-4">No more users to load!</p>
            )}
          </div>
        </div>
        <div className="  p-10 ">
          <h1 className="text-3xl font-semibold">Destinations</h1>
          <div className=" py-4 flex">
            <div className="flex gap-4  items-center w-full">
              <FontAwesomeIcon
                icon={faFilter}
                size="xl"
                className="text-orange-600"
              />
              <SelectFilterOptions item={{ type: "state" }} />
              <SelectFilterOptions item={{ type: "destinations" }} />
              <Input className="w-[40rem]" placeholder="Search Location" />

              <Button className="bg-orange-700 hover:bg-orange-600">
                <FontAwesomeIcon icon={faSearch} className="text-black" />
              </Button>
            </div>
          </div>
          <div className="py-4">
            <ScrollArea className=" w-full h-[40rem] rounded-md border-black shadow-none">
              <div className="flex gap-4 py-10 flex-wrap  items-center justify-around">
                {places.map((item, index) => (
                  <DestinationCard key={index} item={item} count={index} />
                ))}
              </div>
            </ScrollArea>
          </div>
          <div className="text-center">
            <Button
              onClick={() => getPlaces(true)}
              className="bg-orange-700 hover:bg-orange-600 border-none "
              // disabled={!nextPageToken}
            >
              View More
            </Button>
          </div>
        </div>
      </div>
      <Footer />
    </div>
  );
};
export default Home;
