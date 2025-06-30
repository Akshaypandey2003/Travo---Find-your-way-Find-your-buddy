/* eslint-disable react/prop-types */
/* eslint-disable no-unused-vars */
"use client";

import React from "react";
import { Check, ChevronsUpDown } from "lucide-react";

import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from "@/components/ui/command";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";

const stateNames = [
  "Andhra Pradesh",
  "Arunachal Pradesh",
  "Assam",
  "Bihar",
  "Chhattisgarh",
  "Goa",
  "Gujarat",
  "Haryana",
  "Himachal Pradesh",
  "Jharkhand",
  "Karnataka",
  "Kerala",
  "Madhya Pradesh",
  "Maharashtra",
  "Manipur",
  "Meghalaya",
  "Mizoram",
  "Nagaland",
  "Odisha",
  "Punjab",
  "Rajasthan",
  "Sikkim",
  "Tamil Nadu",
  "Telangana",
  "Tripura",
  "Uttar Pradesh",
  "Uttarakhand",
  "West Bengal",
];

const travelLocations = [
  "Beaches",
  "Islands",
  "Mountains",
  "Hill Stations",
  "Deserts",
  "Forests",
  "Lakes",
  "Rivers",
  "Waterfalls",
  "Caves",
  "Wildlife Sanctuaries",
  "National Parks",
  "Temples",
  "Churches",
  "Mosques",
  "Monasteries",
  "Monuments",
  "Historical Sites",
  "Heritage Villages",
  "Adventure Parks",
  "Ski Resorts",
  "Hot Springs",
  "Backwaters",
  "Coral Reefs",
  "Dams",
  "Botanical Gardens",
  "Zoos",
  "Rock Formations",
  "Tea Plantations",
  "Vineyards",
  "Fortresses",
  "Palaces",
  "Bridges",
  "Cultural Festivals",
  "Museums",
  "Street Markets",
  "Floating Markets",
  "Hiking Trails",
  "Cycling Routes",
  "Safari Parks",
  "Diving Spots",
  "Rafting Locations",
  "Spiritual Retreats",
  "Camping Sites",
  "Glamping Resorts",
  "Luxury Resorts",
  "Eco-Tourism Spots",
  "Underground Cities",
  "Urban Landmarks",
  "Sunset Points",
  "Stargazing Spots",
];

export const SelectFilterOptions = ({ item }) => {
  const [open, setOpen] = React.useState(false);
  const [value, setValue] = React.useState("");
  const targetData = item.type==="state" ? stateNames : travelLocations;
  return (
    <Popover open={open} onOpenChange={setOpen}>
      <PopoverTrigger asChild>
        <Button
          variant="outline"
          role="combobox"
          aria-expanded={open}
          className="w-[200px] justify-between"
        >
          {value ||
            (item?.type === "state" ? "Select State" : "Select Destination")}
          <ChevronsUpDown className="opacity-50" />
        </Button>
      </PopoverTrigger>
      <PopoverContent className="w-[200px] p-0">
        <Command>
          <CommandInput placeholder="Search state..." />
          <CommandList>
            <CommandEmpty>No State found.</CommandEmpty>
            <CommandGroup>
              {
                targetData.map((state) => (
                <CommandItem
                  key={state}
                  value={state}
                  onSelect={() => {
                    setValue(state); // ✅ Correctly updating value
                    setOpen(false);
                  }}
                >
                  {state}
                  <Check
                    className={cn(
                      "ml-auto",
                      value === state ? "opacity-100" : "opacity-0"
                    )}
                  />
                </CommandItem>
              ))}
            </CommandGroup>
          </CommandList>
        </Command>
      </PopoverContent>
    </Popover>
  );
};
