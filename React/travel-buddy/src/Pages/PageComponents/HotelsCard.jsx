/* eslint-disable react/prop-types */
/* eslint-disable no-unused-vars */
import React from "react";
import { Card, CardContent, CardFooter } from "@/components/ui/card";
import { Button} from "@/components/ui/button";
import {  Badge } from "@/components/ui/badge";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faSun,faStar,faAnglesRight } from "@fortawesome/free-solid-svg-icons";
import { Link, useParams } from "react-router-dom";
import { GOOGLE_API_KEY } from "../../Constants/constants";
import { useSelector, shallowEqual } from "react-redux";

export const  HotelsCard = ({ item, count,imgUrl }) => {



  const photoReference = item?.photos?.[0]?.photo_reference;
  const placeId = useParams();
  // console.log("hotel: ",count," ",item);

  const photoUrl = photoReference
    ? `https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=${photoReference}&key=${GOOGLE_API_KEY}`
    : null;
  return (
    <Card className="border-2 border-orange-200  p-4  w-[23rem]">
      <div className="relative  gap-[1rem] w-[21rem] h-[15rem] overflow-hidden object-cover rounded-xl group">
        <div className="absolute inset-0 bg-black bg-opacity-50 rounded-xl   opacity-0 transition-opacity duration-300 group-hover:opacity-100">
          
        </div>
        <img
          src={imgUrl||"/resort-1.jpg"}
          alt="kitten"
          className="w-full h-full object-cover"
        />
      </div>
      <CardFooter className="px-1 py-0 flex flex-col ">
        <div className=" w-full">
          <h1 className="font-semibold text-lg">{item?.name?.length >35 ? item?.name?.substring(0,35)+"..." :item?.name}</h1>
        </div>
        {/* <div className=" w-full flex items-center justify-between py-2">
           
          <div className="flex items-center gap-2">
             <img src={item.icon} alt="" className="w-[1rem] h-[1rem] " />
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
          <Link to={`/${placeId.placeId}/hotels/details/${item.place_id}`}>
          <div className="flex items-center gap-1 ">
            <span className="block text-orange-600 float-right hover:cursor-pointer text-xs">
              Explore More
            </span>
            <span> <FontAwesomeIcon icon={faAnglesRight} className="text-orange-600 text-xs" /></span>
            </div>
          </Link>
        </div> */}
        <div className=" w-full">
        <Link to={`/${placeId.placeId}/hotels/details/${item.hotelId}`}>
          <div className="flex items-center gap-1 float-end">
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
export default HotelsCard;
// /details/${item.place_id}
