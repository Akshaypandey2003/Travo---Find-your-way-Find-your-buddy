/* eslint-disable no-unused-vars */
"use client";

import React from "react";
import { Timeline } from "@/components/ui/timeline";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCheck } from "@fortawesome/free-solid-svg-icons";
import { useSelector } from "react-redux";
import { Badge } from "@/components/ui/badge";

export function TimelineDemo() {
  const theme = useSelector((store) => store.auth.theme);

 const data = [
  {
    title: "Create Your Profile",
    content: (
      <div>
        <p className={`mb-2 text-2xl font-normal  md:text-5xl ${theme === "dark" ? "dark:text-neutral-200" : "text-neutral-800"}`}>
          Let others know the kind of traveler you are
        </p>
        <p className="text-sm md:text-base text-neutral-700 dark:text-neutral-300">
          Set up your traveler profile
        </p>

        <p className={`mb-8 text-sm md:text-base ${theme === "dark" ? "text-neutral-300" : "text-neutral-700"}`}>
          Add your photo, interests, travel preferences, and past experiences so others can understand the kind of traveler you are.
        </p>

        <div className={`grid grid-cols-2 gap-4 ${theme === "dark" ? "dark:bg-black" : "bg-orange-50"} p-3 rounded-lg`}>
          <img src="https://res.cloudinary.com/dwg7vniow/image/upload/v1769115498/Screenshot_2026-01-23_022753_fjgqaj.png" alt="profile setup"
            className="h-20 w-full rounded-lg object-cover md:h-44 lg:h-60" />
          <img src="https://res.cloudinary.com/dwg7vniow/image/upload/v1769115498/Screenshot_2026-01-23_022753_fjgqaj.png" alt="profile details"
            className="h-20 w-full rounded-lg object-cover md:h-44 lg:h-60" />
        </div>
      </div>
    ),
  },
  {
    title: "Connect with Travelers",
    content: (
      <div>
        <p className={`mb-2 text-2xl font-normal text-neutral-800 md:text-5xl ${theme === "dark" ? "dark:text-neutral-200" : "text-neutral-800"}`}>
          Find people who share your travel vibe
        </p>
        <p className={`text-sm md:text-base ${theme === "dark" ? "text-neutral-300" : "text-neutral-700"}`}>
          Build your trusted travel network
        </p>

        <p className={`mb-8 text-sm md:text-base ${theme === "dark" ? "text-neutral-300" : "text-neutral-700"}`}>
          Discover people from different locations, explore their profiles, and send friend requests to like-minded adventurers.
        </p>

        <div className={`grid grid-cols-2 gap-4 ${theme === "dark" ? "dark:bg-black" : "bg-orange-50"} p-3 rounded-lg`}>
          <img src="https://res.cloudinary.com/dwg7vniow/image/upload/v1757975818/hahw3nqi5zpteqmxvf61.jpg" className="h-20 w-full rounded-lg object-cover md:h-44 lg:h-60" />
          <img src="https://res.cloudinary.com/dwg7vniow/image/upload/v1751808526/friends-4_llyrfh.jpg" className="h-20 w-full rounded-lg object-cover md:h-44 lg:h-60" />
        </div>
      </div>
    ),
  },
  {
    title: "Plan Your Trip",
    content: (
      <div>
        <p className={`mb-2 text-2xl font-normal text-neutral-800 md:text-5xl ${theme === "dark" ? "dark:text-neutral-200" : "text-neutral-800"}`}>
          Organize journeys exactly the way you want
        </p>
        <p className={`mb-4 text-lg md:text-base ${theme === "dark" ? "text-neutral-300" : "text-neutral-700"}`}>
          Organize journeys your way
        </p>

        <div className={`mb-8 space-y-2 text-sm md:text-base ${theme === "dark" ? "text-neutral-300" : "text-neutral-700"}`}>
          
          <div><FontAwesomeIcon icon={faCheck} className="text-orange-500 mr-2" /> Choose destination, dates, and trip type</div>
          <div><FontAwesomeIcon icon={faCheck} className="text-orange-500 mr-2" /> Set participant limits and visibility</div>
          <div><FontAwesomeIcon icon={faCheck} className="text-orange-500 mr-2" /> Accept or reject join requests</div>
          <div><FontAwesomeIcon icon={faCheck} className="text-orange-500 mr-2" /> Manage participants as trip admin</div>
        </div>

        <div className={`grid grid-cols-2 gap-4 ${theme === "dark" ? "dark:bg-black" : "bg-orange-50"} p-3 rounded-lg`}>
          <img src="https://res.cloudinary.com/dwg7vniow/image/upload/v1732449536/samples/balloons.jpg" className="h-20 w-full rounded-lg object-cover md:h-44 lg:h-60" />
          <img src="https://res.cloudinary.com/dwg7vniow/image/upload/v1732449531/samples/landscapes/nature-mountains.jpg" className="h-20 w-full rounded-lg object-cover md:h-44 lg:h-60" />
        </div>
      </div>
    ),
  },
  {
    title: "Chat and Coordinate",
    content: (
      <div>
        <p className={`mb-2 text-2xl font-normal text-neutral-800 md:text-5xl ${theme === "dark" ? "dark:text-neutral-200" : "text-neutral-800"}`}>
          Stay connected before and during the trip
        </p>
        <p className={`mb-4 text-sm md:text-base ${theme === "dark" ? "text-neutral-300" : "text-neutral-700"}`}>
          Communicate in real-time
        </p>

        <div className={`mb-8 space-y-2 text-sm md:text-base ${theme === "dark" ? "text-neutral-300" : "text-neutral-700"}`}>
          <div><FontAwesomeIcon icon={faCheck} className="text-orange-500 mr-2" />One-to-one and group conversations</div>
          <div><FontAwesomeIcon icon={faCheck} className="text-orange-500 mr-2" /> Message delivery and seen status</div>
          <div><FontAwesomeIcon icon={faCheck} className="text-orange-500 mr-2" /> Admin controls for group chats</div>
          <div><FontAwesomeIcon icon={faCheck} className="text-orange-500 mr-2" /> Seamless coordination before and during trips</div>
        </div>

        <div className={`grid grid-cols-2 gap-4 ${theme === "dark" ? "dark:bg-black" : "bg-orange-50"} p-3 rounded-lg`}>
          <img src="https://res.cloudinary.com/dwg7vniow/image/upload/v1751808507/friends-3_j0edsz.jpg" className="h-20 w-full rounded-lg object-cover md:h-44 lg:h-60" />
          <img src="https://res.cloudinary.com/dwg7vniow/image/upload/v1751808658/palace-1_pxsg8t.jpg" className="h-20 w-full rounded-lg object-cover md:h-44 lg:h-60" />
        </div>
      </div>
    ),
  },
  {
    title: "Travel and Share Memories",
    content: (
      <div>
        <p className={`mb-2 text-2xl font-normal text-neutral-800 md:text-5xl ${theme === "dark" ? "dark:text-neutral-200" : "text-neutral-800"}`}>
          Turn every journey into a story worth sharing
        </p>
        <p className={`mb-4 text-sm md:text-base ${theme === "dark" ? "text-neutral-300" : "text-neutral-700"}`}>
          Turn journeys into stories
        </p>

        <div className={`mb-8 space-y-2 text-sm md:text-base ${theme === "dark" ? "text-neutral-300" : "text-neutral-700"}`}>
          <div><FontAwesomeIcon icon={faCheck} className="text-orange-500 mr-2" /> Post blogs with photos and experiences</div>
          <div><FontAwesomeIcon icon={faCheck} className="text-orange-500 mr-2" /> Like, comment, and interact with blogs</div>
          <div><FontAwesomeIcon icon={faCheck} className="text-orange-500 mr-2" /> Rate and review your trip partners</div>
          <div><FontAwesomeIcon icon={faCheck} className="text-orange-500 mr-2" /> Build your travel history on your profile</div>
          
        </div>

        <div className={`grid grid-cols-2 gap-4 ${theme === "dark" ? "dark:bg-black" : "bg-orange-50"} p-3 rounded-lg`}>
          <img src="https://res.cloudinary.com/dwg7vniow/image/upload/v1751808485/friends-2_r4uowc.jpg" className="h-20 w-full rounded-lg object-cover md:h-44 lg:h-60" />
          <img src="https://res.cloudinary.com/dwg7vniow/image/upload/v1747595403/rroxsnzy5k2tlyf30vlk.jpg" className="h-20 w-full rounded-lg object-cover md:h-44 lg:h-60" />
        </div>
      </div>
    ),
  },
];


  return (
    <div className="relative overflow-clip">
      <Timeline data={data} />
    </div>
  );
}
