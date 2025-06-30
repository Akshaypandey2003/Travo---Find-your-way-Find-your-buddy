/* eslint-disable no-unused-vars */
import React, { useEffect } from "react";
import usersData from "../MockData/usersData";
import { Badge } from "@/components/ui/badge";
import { useParams } from "react-router-dom";
import { useDispatch, useSelector, shallowEqual } from "react-redux";
import useHotelsDetails from "../../CustomHooks/useHotelsDetails";
import Marquee from "react-fast-marquee";
import { GOOGLE_API_KEY } from "../../Constants/constants";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Card } from "@/components/ui/card";
import ReactCardSlider from "react-card-slider-component";
import CustomerCard from "./CustomerCard";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faLocationDot,
  faPhone,
  faStar,
} from "@fortawesome/free-solid-svg-icons";
import {
  HoverCard,
  HoverCardContent,
  HoverCardTrigger,
} from "@/components/ui/hover-card";
import { ScrollArea, ScrollBar } from "@/components/ui/scroll-area";
import ReviewCards from "./ReviewCards";
import HotelsCard from "./HotelsCard";
import Footer from "../Footer Section/Footer";

const userData = usersData;

const slides = [
  {
    image: "https://picsum.photos/200/300",
    title: "This is a title",
    description: "This is a description",
  },
  {
    image: "https://picsum.photos/600/500",
    title: "This is a second title",
    description: "This is a second description",
  },
  {
    image: "https://picsum.photos/700/600",
    title: "This is a third title",
    description: "This is a third description",
  },
  {
    image: "https://picsum.photos/500/400",
    title: "This is a fourth title",
    description: "This is a fourth description",
  },
  {
    image: "https://picsum.photos/200/300",
    title: "This is a fifth title",
    description: "This is a fifth description",
  },
  {
    image: "https://picsum.photos/800/700",
    title: "This is a sixth title",
    description: "This is a sixth description",
  },
  {
    image: "https://picsum.photos/300/400",
    title: "This is a seventh title",
    description: "This is a seventh description",
  },
];

const suggestedVisitTime = [
  {
    interval: "Jan - March",
    desc: "Some description here for specific time interval to visit a particular locations.",
  },
  {
    interval: "April - June",
    desc: "Some description here for specific time interval to visit a particular locations.",
  },
  {
    interval: "July - Sept",
    desc: "Some description here for specific time interval to visit a particular locations.",
  },
  {
    interval: "Oct - Dec",
    desc: "Some description here for specific time interval to visit a particular locations.",
  },
];

const specialEvents = [
  {
    event: "Maha Shivaratri",
    desc: "This is one of the best event to experience at this location.",
  },
  {
    event: "Ganesh Chaturthi",
    desc: "This is one of the best event to experience at this location.",
  },
  {
    event: "Holi",
    desc: "This is one of the best event to experience at this location.",
  },
  {
    event: "Diwali",
    desc: "This is one of the best event to experience at this location.",
  },
  {
    event: "Ramnavmi",
    desc: "This is one of the best event to experience at this location.",
  },
];

export const HotelsDetails = () => {
  const { hotelId, placeId } = useParams();

  const dispatch = useDispatch();
  const { getHotelsDetails } = useHotelsDetails();
  const hotels = useSelector((store) => store.hotels.hotelsData, shallowEqual);
  const hotelsDetails = useSelector(
    (store) => store.hotels.hotelsDetails[hotelId]
  );
  console.log("hotels details: ", hotelsDetails);

  useEffect(() => {
    if (!hotelsDetails) getHotelsDetails(hotelId);
    else console.log("Hotels details already available.");
  }, [hotelId]);

  const filteredHotels = hotels[placeId]?.filter((item) => {
    return item?.place_id != hotelId.hotelId;
  });
  return (
    <div className="">
      <div className=" h-[50vh] flex justify-center  ">
        <div className="flex  items-center justify-center w-[90rem] ">
          <Marquee autoFill={true} speed={25} className="rounded-xl -mx-2">
            {hotelsDetails?.photos?.map((item, index) => {
              let photoUrl = item?.photo_reference
                ? `https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=${item?.photo_reference}&key=${GOOGLE_API_KEY}`
                : null;
              return photoUrl ? ( // ✅ Ensure JSX is returned
                <div
                  key={index}
                  className=" w-[30rem] h-[20rem] py-10 mx-2 rounded-xl"
                >
                  <img
                    src={photoUrl}
                    alt="img"
                    className="w-full h-full object-cover"
                  />
                </div>
              ) : null;
            })}
          </Marquee>
        </div>
      </div>
      <div className=" flex justify-center p-5 ">
        <Tabs defaultValue="overview">
          <TabsList className=" gap-2">
            <TabsTrigger value="overview" className="border-none text-black">
              Overview
            </TabsTrigger>
            <TabsTrigger value="Photos" className="border-none text-black">
              Photos
            </TabsTrigger>
            <TabsTrigger value="Rooms" className="border-none text-black">
              Rooms
            </TabsTrigger>
          </TabsList>
          <TabsContent value="overview">
            <div className="w-[90rem] py-5 ">
              <div className="flex justify-between items-center">
                <div className="w-[50rem]  px-5">
                  <h1 className="text-2xl font-bold">Overview</h1>
                  <div className="mt-4 flex items-center gap-4">
                    <h1 className="text-lg font-semibold">
                      {hotelsDetails?.name}
                    </h1>
                    <img
                      src={hotelsDetails?.icon}
                      alt=""
                      className={
                        "w-[2rem] h-[2rem] " +
                        "bg-" +
                        hotelsDetails?.icon_background_color
                      }
                    />
                  </div>
                  <p>{hotelsDetails?.editorial_summary?.overview}</p>
                  <Badge
                    variant="outline"
                    className="border border-orange-500 mt-6"
                  >
                    <FontAwesomeIcon
                      icon={faStar}
                      className="text-orange-400"
                    />
                    <span className="mx-2">{hotelsDetails?.rating}</span>
                  </Badge>
                </div>
                <div className=" w-[30rem] px-5">
                  <div className="mb-4">
                    <span className="text-xl font-semibold mr-4">Address</span>
                    <span className="text-orange-500">
                      <FontAwesomeIcon icon={faLocationDot} />
                    </span>
                    <p>{hotelsDetails?.formatted_address}</p>
                  </div>
                  <div>
                    <span className="text-xl font-semibold mr-4">Contact</span>
                    <span className="text-orange-500">
                      <FontAwesomeIcon icon={faPhone} />
                    </span>
                    <p>{hotelsDetails?.international_phone_number}</p>
                  </div>
                </div>
              </div>
              <div className="Visitors-review p-5">
                <h1 className="text-2xl font-bold">Reviews</h1>
                <ScrollArea className="h-72 w-[70%]  py-4 scrollbar-hide">
                  {hotelsDetails?.reviews?.map((item, index) => (
                    <ReviewCards key={index} item={item} />
                  ))}
                </ScrollArea>
              </div>
            </div>
          </TabsContent>
          <TabsContent value="Photos">
            <div className="w-[90rem] py-5 ">
              <div className="flex justify-between gap-10 items-center">
                <div className="min-w-[50rem]  p-5">
                  <h1 className="text-2xl font-bold">Photos</h1>

                  <ScrollArea className="h-[25rem] py-4  flex flex-wrap items-center justify-center">
                    <div className="flex flex-wrap items-center gap-2">
                      {hotelsDetails?.photos?.map((item, index) => {
                        let photoUrl = item?.photo_reference
                          ? `https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=${item?.photo_reference}&key=${GOOGLE_API_KEY}`
                          : null;
                        return photoUrl ? ( // ✅ Ensure JSX is returned
                          <div
                            key={index}
                            className=" w-[25rem] h-[15rem]  mx-2 object-cover overflow-hidden hover:shadow-lg rounded-xl"
                          >
                            <img
                              src={photoUrl}
                              alt="img"
                              className="w-full h-full  transition-transform duration-300 ease-in-out hover:scale-105 "
                            />
                          </div>
                        ) : null;
                      })}
                    </div>
                  </ScrollArea>
                </div>
              </div>
            </div>
          </TabsContent>
          <TabsContent value="Rooms">
            <div className="w-[90rem] py-5 ">
              <div className="flex justify-between gap-10 items-center">
                <div className="min-w-[50rem]  p-5">
                  <h1 className="text-2xl font-bold">Rooms</h1>
                  <ScrollArea className="h-[25rem] py-4  flex flex-wrap items-center justify-center">
                    <div className="flex flex-wrap items-center gap-2">
                      {hotelsDetails?.photos?.map((item, index) => {
                        let photoUrl = item?.photo_reference
                          ? `https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=${item?.photo_reference}&key=${GOOGLE_API_KEY}`
                          : null;
                        return photoUrl ? ( // ✅ Ensure JSX is returned
                          <div
                            key={index}
                            className=" w-[25rem] h-[15rem]  mx-2 object-cover overflow-hidden hover:shadow-lg rounded-xl"
                          >
                            <img
                              src={photoUrl}
                              alt="img"
                              className="w-full h-full  transition-transform duration-300 ease-in-out hover:scale-105 "
                            />
                          </div>
                        ) : null;
                      })}
                    </div>
                  </ScrollArea>
                </div>
              </div>
            </div>
          </TabsContent>
        </Tabs>
      </div>
      <div className=" flex justify-center p-6">
        <div className=" w-[90rem] ">
          <h1 className="text-3xl font-bold">Nearby Hotels</h1>
          <div className="mt-4">
            <ScrollArea className="overflow-x-auto ">
              <div className="flex gap-4  items-center justify-start flex-nowrap">
                {filteredHotels?.map((item, index) => (
                  <HotelsCard key={index} item={item} count={index} />
                ))}
              </div>
              <ScrollBar orientation="horizontal" />
            </ScrollArea>
          </div>
        </div>
      </div>
      <Footer />
    </div>
  );
};
export default HotelsDetails;
