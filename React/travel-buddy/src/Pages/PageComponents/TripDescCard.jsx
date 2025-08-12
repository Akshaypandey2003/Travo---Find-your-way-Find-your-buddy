/* eslint-disable no-unused-vars */
/* eslint-disable react/prop-types */
import { Card } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faPaperPlane,
} from "@fortawesome/free-solid-svg-icons";
import {
  MapPin,
  CalendarDays,
  Users,
  DollarSign,
  Tag,
} from "lucide-react";
import { format } from "date-fns";
import useTrip from "../../CustomHooks/useTrip";
import { useSelector } from "react-redux";
import { useEffect } from "react";

export const TripDescCard = ({ trip }) => {
  const { sendTripRequest,fetchTripFeedbacks } = useTrip();
  const theme = useSelector((state) => state.auth.theme); // 🌙 get theme from Redux

  const {
    createdBy,
    tripName,
    tripCity,
    tripState,
    tripCountry,
    tripDate,
    tripBudget,
    memberSize,
    tripDescription,
    tripTags = [],
    tripType,
    tripId,
  } = trip;

  const formattedDate = tripDate
    ? format(new Date(tripDate), "dd MMM yyyy")
    : "N/A";
   
   


  return (
    <div className="relative group">
      {/* Glow border */}
      <div className="absolute inset-0 rounded-2xl p-[2px] bg-gradient-to-r from-orange-400 via-red-400 to-yellow-400 opacity-0 group-hover:opacity-100 transition-opacity duration-300 blur-md" />

      <Card
        className={`relative z-10 p-4 shadow-lg rounded-2xl border transition duration-300 ease-in-out transform group-hover:scale-105 group-hover:shadow-md group-hover:shadow-orange-200 
        ${theme === "dark" ? "bg-gray-950 border-gray-600" : "bg-white border-gray-200"}`}
      >
        <div className="space-y-2">
          {/* Trip Name */}
          <div className="flex justify-between">
            <h2 className="text-xl font-bold text-orange-500">{tripName}</h2>
          </div>

          {/* Location */}
          <div className={`text-sm flex items-center gap-1 ${theme === "dark" ? "text-gray-400" : "text-gray-600"}`}>
            <MapPin className="w-4 h-4 text-orange-400" />
            {tripCity}, {tripState}, {tripCountry}
          </div>

          {/* Trip Info */}
          <div className={`flex flex-wrap gap-4 mt-2 text-sm ${theme === "dark" ? "text-gray-300" : "text-gray-700"}`}>
            <div className="flex items-center gap-1">
              <CalendarDays className="w-4 h-4 text-orange-400" />
              <span className="font-medium">{formattedDate}</span>
            </div>
            <div className="flex items-center gap-1">
              <Users className="w-4 h-4 text-orange-400" />
              <span>{memberSize} Members</span>
            </div>
            <div className="flex items-center gap-1">
              <DollarSign className="w-4 h-4 text-orange-400" />
              <span>₹ {tripBudget || 0}</span>
            </div>
            <div className="flex items-center gap-1">
              <Tag className="w-4 h-4 text-orange-400" />
              <span>{tripType} Trip</span>
            </div>
          </div>

          {/* Description */}
          <div className="w-96 text-wrap">
            <p className={`mt-2 line-clamp-3 text-xs ${theme === "dark" ? "text-gray-400" : "text-gray-700"}`}>
              {tripDescription}
            </p>
          </div>

          {/* Tags + Action */}
          <div className="flex justify-between items-center">
            {tripTags.length > 0 && (
              <div className="flex flex-wrap gap-2 mt-3 items-center">
                {tripTags.slice(0, 3).map((tag, idx) => (
                  <Badge
                    key={idx}
                    variant="outline"
                    className={`text-xs ${theme==="dark" ?"bg-gray-900 border-gray-400 text-inherit" :"bg-orange-50 border-orange-500 text-orange-600"} `}
                  >
                    #{tag}
                  </Badge>
                ))}
              </div>
            )}

            <FontAwesomeIcon
              icon={faPaperPlane}
              className="text-orange-500 cursor-pointer hover:scale-125 transition-transform"
              onClick={() =>
                sendTripRequest({
                  tripId: trip?.tripId,
                  requestTo: createdBy,
                })
              }
            />
          </div>
        </div>
      </Card>
    </div>
  );
};
export default TripDescCard;
