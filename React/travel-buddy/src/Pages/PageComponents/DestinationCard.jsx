/* eslint-disable react/prop-types */
/* eslint-disable no-unused-vars */
import React from "react";
import { Card, CardContent, CardFooter } from "@/components/ui/card";
import { Button} from "@/components/ui/button";
import {  Badge } from "@/components/ui/badge";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faSun,faStar,faAnglesRight } from "@fortawesome/free-solid-svg-icons";
import { Link } from "react-router-dom";
import { GOOGLE_API_KEY } from "../../Constants/constants";
import { useSelector, shallowEqual } from "react-redux";

export const DestinationCard = ({ item, count }) => {

  const descriptions = useSelector((store) => store.places.descriptions);
  const weatherData = useSelector(
    (store) => store.places.weatherData,
    shallowEqual
  );

  const weather = weatherData[item.place_id];
  const photoReference = item?.photos[0]?.photo_reference;

  const photoUrl = photoReference
    ? `https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=${photoReference}&key=${GOOGLE_API_KEY}`
    : null;
  return (
    <Card className="border-none shadow-xl p-4 bg-orange-50  w-[23rem]">
      <div className="relative border border-orange-200 gap-[1rem] w-[21rem] h-[15rem] overflow-hidden object-cover rounded-xl group">
        <div className="absolute inset-0 bg-black bg-opacity-50 rounded-xl   opacity-0 transition-opacity duration-300 group-hover:opacity-100">
          <div className=" float-end p-2 text-orange-500 font-semibold">
            <div className="flex items-center">
              <h1 className="">
                {weather?.condition?.text}
              </h1>
                {/* <FontAwesomeIcon
                  icon={faSun}
                  className="mx-2 text-orange-400"
                /> */}
                <div className="w-[3rem] h-[3rem] ">
                <img src={weather?.condition?.icon} alt="" />
                </div>
             
            </div>
            <h1>Temp - {weather?.temp_c + "C, " + weather?.temp_f + "F"}</h1>
            <h1>Humidity- {weather?.humidity}</h1>
            <h1>AQI- 100 Good</h1>
          </div>
        </div>
        <img
          src={photoUrl}
          alt="kitten"
          className="w-full h-full object-cover"
        />
      </div>
      <CardFooter className="px-1 py-0 flex flex-col ">
        <div className=" w-full">
          <h1 className="font-semibold text-lg">{item.name}</h1>
          <h1 className="text-sm text-gray-700">
            {descriptions[item?.place_id]?.substring(0, 100) + "..."}
          </h1>
        </div>
        <div className=" w-full flex items-center justify-between py-2">
          <div>
            <Badge variant="outline" className="border border-orange-500">
            <FontAwesomeIcon icon={faStar} className="text-orange-400" />
            <span className="mx-2">{item.rating}</span>
            </Badge>
          </div>
          {item?.opening_hours &&
          <div>
            <Badge variant="outline" className="border border-orange-500">
            <span className="mx-2">{item?.opening_hours?.open_now?"Open":"Closed"}</span>
            </Badge>
          </div>
          }
          <Link to={`/destination/details/${item.place_id}`}>
          <div className="flex items-center gap-1">
            <span className="block text-orange-600 float-right hover:cursor-pointer text-xs">
              Explore More
            </span>
            <span> <FontAwesomeIcon icon={faAnglesRight} className="text-orange-600 text-xs" /></span>
            </div>
          </Link>
        </div>
      </CardFooter>
    </Card>
  );
};
export default DestinationCard;
// /details/${item.place_id}
