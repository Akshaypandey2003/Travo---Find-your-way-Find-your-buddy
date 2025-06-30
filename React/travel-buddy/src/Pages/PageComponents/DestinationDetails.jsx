/* eslint-disable no-unused-vars */
import React, { useEffect } from "react";
import ImgCarousel from "./ImgCarousel";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Card } from "@/components/ui/card";
import ReactCardSlider from "react-card-slider-component";
import Footer from "../Footer Section/Footer";
import CustomerCard from "./CustomerCard";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faSun, faStar } from "@fortawesome/free-solid-svg-icons";
import usersData from "../MockData/usersData";
import { Badge } from "@/components/ui/badge";
import {
  HoverCard,
  HoverCardContent,
  HoverCardTrigger,
} from "@/components/ui/hover-card";
import usePlaceDetails from "../../CustomHooks/usePlaceDetails";
import { useParams } from "react-router-dom";
import { useDispatch, useSelector, shallowEqual } from "react-redux";
import { ScrollArea, ScrollBar } from "@/components/ui/scroll-area";
import { Separator } from "@/components/ui/separator";
import ReviewCards from "./ReviewCards";
import DestinationCard from "./DestinationCard";
import useHotelsData from "../../CustomHooks/useHotelsData";
import HotelsCard from "./HotelsCard";
import useHotelImages from "../../CustomHooks/useHotelImages";

const tags = Array.from({ length: 50 }).map(
  (_, i, a) => `v1.2.0-beta.${a.length - i}`
);

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

export const DestinationDetails = () => {
  const placeId = useParams();
  const descriptions = useSelector((store) => store.places.descriptions);
  const places = useSelector((store) => store.places.placesData, shallowEqual);

  const hotels = useSelector((store) => store.hotels.hotelsData[placeId.placeId], shallowEqual);
  const hotelImages = useSelector((store)=>store.hotels.hotelImages,shallowEqual);

  console.log("Hotel images inside destination details page: ",hotelImages);

  const hotelsNextPageToken = useSelector(
    (store) => store.places.hotelsNextPageToken,
    shallowEqual
  );

  const { getPlaceDetails } = usePlaceDetails();
  const { getHotelsData } = useHotelsData();
  const {fetchImages} = useHotelImages();
  

  const dispatch = useDispatch();

  const weatherData = useSelector(
    (store) => store.places.weatherData,
    shallowEqual
  );
  const weather = weatherData[placeId.placeId];

  const placesDetails = useSelector(
    (store) => store.places.placesDetails[placeId.placeId]);
    console.log("All destination details: ",placesDetails);

  const filteredSimilarPlaces = places.filter((item) => {
    return (
      item?.types.includes(placesDetails?.types[0]) &&
      item?.types.includes(placesDetails?.types[1]) &&
      item?.types.includes(placesDetails?.types[2]) &&
      item?.place_id != placeId.placeId
    );
  });

  useEffect(() => {
    if(!placesDetails)
    {
      getPlaceDetails(placeId).then((details) => {
        console.log("Details inside useEffect: ", details);
        if (details?.geometry?.location) {
          getHotelsData(
            placeId,
            details.geometry.location.lat,
            details.geometry.location.lng
          ).then((data)=>{
            fetchImages(data.length);
          });
        }
      });
    }
    else{
      console.log("Destination Details alreday available.");
    }
    
  }, [placeId, getHotelsData]);

  console.log(
    "Place details associated with placeId: ",
    placeId,
    " is: ",
    placesDetails
  );
 console.log("hotels inside destination details with placeId : ",placeId," ,",hotels);
  return (
    <div className="">
      <div className="">
        <ImgCarousel key={placeId} />
      </div>
      <div className=" flex justify-center p-5 ">
        <Tabs defaultValue="hotel&restro">
          <TabsList className=" gap-2">
            <TabsTrigger value="activities" className="border-none text-black">
              Activites To Do
            </TabsTrigger>
            <TabsTrigger value="overview" className="border-none text-black">
              Overview
            </TabsTrigger>
            <TabsTrigger value="suggestions" className="border-none text-black">
              Suggestions
            </TabsTrigger>
            <TabsTrigger
              value="hotel&restro"
              className="border-none text-black"
            >
              Hotel & Restaurants
            </TabsTrigger>
          </TabsList>
          <TabsContent value="activities">
            <div className="w-[90rem] py-5  flex ">
              <ReactCardSlider slides={slides} />
            </div>
          </TabsContent>
          <TabsContent value="overview">
            <div className="w-[90rem] py-5 ">
              <div className="flex justify-between gap-10 items-center">
                <div className="w-[50rem]  p-5">
                  <h1 className="text-2xl font-bold">Overview</h1>
                  <div className="mt-4">
                    <h1 className="text-xl font-semibold">{placesDetails?.name}</h1>
                    <p>
                      {placesDetails?.editorial_summary?.overview}{" "}
                      {descriptions[placeId.placeId]}
                    </p>
                    <div className=" flex items-center py-2 gap-8">
                      <div>
                        <Badge
                          variant="outline"
                          className="border border-orange-500 px-3"
                        >
                          <FontAwesomeIcon
                            icon={faStar}
                            className="text-orange-400"
                            size="xs"
                          />
                          <span className="mx-1 text-xs">
                            {placesDetails?.rating}
                          </span>
                        </Badge>
                      </div>
                      <div>
                        {placesDetails?.current_opening_hours?.open_now && (
                          <Badge
                            variant="outline"
                            className="border border-orange-500 px-4"
                          >
                            <span className="text-xs">
                              {placesDetails?.current_opening_hours?.open_now
                                ? "Open"
                                : "Closed"}
                            </span>
                          </Badge>
                        )}
                      </div>
                    </div>
                  </div>
                </div>
                <div className=" w-[30rem] p-5 flex justify-between">
                  {placesDetails?.current_opening_hours?.weekday_text && (
                    <div>
                      <h1 className="text-xl font-bold">Timings</h1>
                      {placesDetails?.current_opening_hours?.weekday_text?.map(
                        (item, index) => (
                          <h1 key={index} className="my-2 text-xs">
                            {item}
                          </h1>
                        )
                      )}
                    </div>
                  )}
                  <div>
                    <h1 className="text-xl font-bold">Weather</h1>
                    <div className="flex  items-center ">
                      <h1 className="text-lg font-semibold">
                        {weather?.condition?.text}
                      </h1>
                      <div className="w-[5rem] h-[5rem] flex items-center">
                        <img src={weather?.condition?.icon} alt="" />
                      </div>
                    </div>
                    <h1 className="mt-2">
                      <strong>Temp - </strong>
                      {weather?.temp_c + "\u00B0C, " + weather?.temp_f + "F"}
                    </h1>
                    <h1 className="mt-2">
                      <strong>Humidity - </strong> {weather?.humidity}
                    </h1>
                    {/* <h1 className="mt-2">
                    <strong>AQI - </strong> 100 Good
                  </h1> */}
                  </div>
                </div>
              </div>
              <div className="Visitors-review p-5">
                <h1 className="text-2xl font-bold">Reviews</h1>
                <ScrollArea className="h-72 w-[70%]  py-4 scrollbar-hide">
                  {placesDetails?.reviews?.map((item, index) => (
                    <ReviewCards key={index} item={item} />
                  ))}
                </ScrollArea>
              </div>
            </div>
          </TabsContent>
          <TabsContent value="suggestions">
            <div className="w-[90rem] py-5 ">
              <div className="flex gap-4 py-10 flex-wrap">
                {userData.slice(0, 4).map((item, index) => (
                  <CustomerCard key={index} item={item} />
                ))}
              </div>
              <div className=" flex px-5 justify-around ">
                <div className="  w-[50rem] px-2">
                  <h1 className="font-bold text-2xl">Best Time To Visit</h1>
                  <div className="flex flex-wrap  gap-4 p-2 justify-center">
                    {suggestedVisitTime.map((item) => (
                      <Card key={item} className="px-4 py-2 w-[15rem]">
                        <h1 className="font-semibold text-xl">
                          {item.interval}
                        </h1>
                        <p>{item.desc}</p>
                      </Card>
                    ))}
                  </div>
                </div>
                <div className=" w-[30rem] px-2">
                  <h1 className="font-bold text-2xl">Special Events</h1>
                  <div className="flex flex-wrap gap-4 p-2">
                    {specialEvents.map((item) => (
                      <HoverCard key={item}>
                        <HoverCardTrigger className="cursor-pointer px-4 border border-orange-500 rounded-full hover:text-black">
                          {item.event}
                        </HoverCardTrigger>
                        <HoverCardContent>{item.desc}</HoverCardContent>
                      </HoverCard>
                    ))}
                  </div>
                </div>
                <div></div>
              </div>
            </div>
          </TabsContent>
          <TabsContent value="hotel&restro">
            <div className=" flex justify-center">
              <div className=" w-[90rem] pt-10">
                <div className="">
                  <ScrollArea className="overflow-x-auto ">
                    <div className="flex gap-4  items-center justify-start flex-nowrap">
                      {hotels?.map((item, index) => (
                        <HotelsCard
                          key={index}
                          item={item}
                          count={index}
                          imgUrl = {hotelImages[index%hotelImages.length]?.urls?.regular}
                        />
                      ))}
                    </div>
                    <ScrollBar orientation="horizontal" />
                  </ScrollArea>
                </div>
              </div>
            </div>
          </TabsContent>
        </Tabs>
      </div>
      <div className=" flex justify-center p-6">
        <div className=" w-[90rem] ">
          <h1 className="text-3xl font-bold">On Going Trips</h1>
          <div className="">
            <ScrollArea className="overflow-x-auto ">
              <div className="flex gap-4  items-center justify-start flex-nowrap">
                {filteredSimilarPlaces.map((item, index) => (
                  <DestinationCard key={index} item={item} count={index} />
                ))}
              </div>
              <ScrollBar orientation="horizontal" />
            </ScrollArea>
          </div>
        </div>
      </div>
      <div className=" flex justify-center p-6">
        <div className=" w-[90rem] ">
          <h1 className="text-3xl font-bold">Similar Places</h1>
          <div className="">
            <ScrollArea className="overflow-x-auto ">
              <div className="flex gap-4  items-center justify-start flex-nowrap">
                {filteredSimilarPlaces.map((item, index) => (
                  <DestinationCard key={index} item={item} count={index} />
                ))}
              </div>
              <ScrollBar orientation="horizontal" />
            </ScrollArea>
          </div>
        </div>
      </div>
      <div className=" flex justify-center p-6">
        <div className=" w-[90rem] ">
          <h1 className="text-3xl font-bold">Find Your Dream Destination</h1>
          <div className="">
            <ScrollArea className="overflow-x-auto ">
              <div className="flex gap-4  items-center justify-start flex-nowrap">
                {places.map((item, index) => (
                  <DestinationCard key={index} item={item} count={index} />
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
export default DestinationDetails;
