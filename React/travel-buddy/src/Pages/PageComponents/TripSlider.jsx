/* eslint-disable no-unused-vars */
/* eslint-disable react/prop-types */
import React, { useRef, useState, useMemo, useEffect } from "react";
import { ScrollArea, ScrollBar } from "@/components/ui/scroll-area";
import { TripDescCard } from "./TripDescCard";
import TripDescCardSkeleton from "./TripDescCardSkeleton";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

const TripSlider = ({ trips, tripType }) => {
  const scrollRef = useRef(null);

  const [selectedType, setSelectedType] = useState("ALL");
  const [selectedTheme, setSelectedTheme] = useState("ALL");
  const [selectedState, setSelectedState] = useState("ALL");
  const [showNoTripsMsg, setShowNoTripsMsg] = useState(false);

  const availableStates = useMemo(() => {
    const states = trips?.map((trip) => trip?.tripState).filter(Boolean);
    return [...new Set(states)];
  }, [trips]);

  const tripThemes = [
    "Mountains",
    "Beaches",
    "Adventure",
    "Historical",
    "Nature",
  ];
  const tripTypes = ["GROUP", "SOLO"];

  const filteredTrips = useMemo(() => {
    return trips?.filter((trip) => {
      const matchesType =
        selectedType !== "ALL" ? trip.tripType === selectedType : true;
      const matchesTheme =
        selectedTheme !== "ALL"
          ? trip.tripTags?.some(
              (tag) => tag.toLowerCase() === selectedTheme.toLowerCase()
            )
          : true;
      const matchesState =
        selectedState !== "ALL" ? trip.tripState === selectedState : true;

      return matchesType && matchesTheme && matchesState;
    });
  }, [trips, selectedType, selectedTheme, selectedState]);

  // Handle skeleton-to-message transition
  useEffect(() => {
    if (filteredTrips.length === 0) {
      setShowNoTripsMsg(false);
      const timer = setTimeout(() => {
        setShowNoTripsMsg(true);
      }, 2000); // 2 seconds delay

      return () => clearTimeout(timer);
    } else {
      setShowNoTripsMsg(false); // reset when trips exist
    }
  }, [filteredTrips]);

  return (
    <div className="relative w-[85rem]">
      <div className="flex items-center justify-between">
        <h1 className="text-3xl font-semibold mb-4">
          {tripType === "UPCOMING" ? "Upcoming Trips" : "Ongoing Trips"}
        </h1>
        <div className="flex items-center gap-6 mb-6">
          {/* Trip Theme Filter */}
          <div>
            <Select value={selectedTheme} onValueChange={setSelectedTheme}>
              <SelectTrigger className="w-[150px] border-orange-300 h-8">
                <SelectValue placeholder="Select Theme" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="ALL">All</SelectItem>
                {tripThemes.map((theme) => (
                  <SelectItem key={theme} value={theme}>
                    {theme}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          {/* State Filter */}
          <div>
            <Select value={selectedState} onValueChange={setSelectedState}>
              <SelectTrigger className="w-[150px] border-orange-300 h-8">
                <SelectValue placeholder="Select State" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="ALL">All</SelectItem>
                {availableStates.map((state, i) => (
                  <SelectItem key={i} value={state}>
                    {state}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
        </div>
      </div>

      {/* Trip Slider */}
      <ScrollArea className="whitespace-nowrap">
        {filteredTrips.length > 0 ? (
          <div
            className="flex w-max space-x-4 pb-4 scroll-smooth snap-x py-4"
            ref={scrollRef}
          >
            {filteredTrips.map((trip) => (
              <div key={trip?.tripId} className="min-w-[300px] snap-start">
                <TripDescCard trip={trip} />
              </div>
            ))}
          </div>
        ) : !showNoTripsMsg ? (
          // Show skeleton during initial 2 seconds
          <div className="flex w-max space-x-4 pb-4 scroll-smooth snap-x">
            {[1, 2, 3, 4].map((_, index) => (
              <div key={index} className="mx-4 py-4">
                <TripDescCardSkeleton />
              </div>
            ))}
          </div>
        ) : (
          // Show "No trips found" after delay
          <div className="w-full text-center py-10 text-gray-500 font-medium">
            No trips found with selected filters.
          </div>
        )}
        <ScrollBar orientation="horizontal" />
      </ScrollArea>
    </div>
  );
};

export default TripSlider;
