/* eslint-disable no-unused-vars */
"use client";

import React from "react";
import { Timeline } from "../../components/ui/timeline";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCheck } from "@fortawesome/free-solid-svg-icons";

export function TimeLine() {

  const data = [
    {
      title: "Create Your Profile",
      content: (
        <div>
          <p className="mb-2 text-2xl font-normal md:text-4xl text-neutral-800 dark:text-neutral-200 tracking-tight">
            Let others know the kind of traveler you are
          </p>
          <p className="text-sm md:text-base text-slate-500 dark:text-slate-400 font-medium tracking-tight">
            Set up your traveler profile
          </p>

          <p className="mb-8 text-sm md:text-base text-slate-500 dark:text-slate-400 font-medium tracking-tight">
            Add your photo, interests, travel preferences, and past experiences so others can understand the kind of traveler you are.
          </p>

          <div className="grid grid-cols-2 gap-4 bg-orange-50 dark:bg-black p-3 rounded-lg">
            <img
              src="https://res.cloudinary.com/dwg7vniow/image/upload/v1772106551/Travo-login_sx2bry.png"
              alt="profile setup"
              className="h-20 w-full rounded-lg object-cover md:h-44 lg:h-48"
            />
            <img
              src="https://res.cloudinary.com/dwg7vniow/image/upload/v1772106549/Travo-signup_or2mml.png"
              alt="profile details"
              className="h-20 w-full rounded-lg object-cover md:h-44 lg:h-48"
            />
          </div>
        </div>
      ),
    },
    {
      title: "Connect with Travelers",
      content: (
        <div>
          <p className="mb-2 text-2xl font-normal md:text-4xl text-neutral-800 dark:text-neutral-200 tracking-tight">
            Find people who share your travel vibe
          </p>
          <p className="text-sm md:text-base text-slate-500 dark:text-slate-400 font-medium tracking-tight">
            Build your trusted travel network
          </p>

          <p className="mb-8 text-sm md:text-base text-slate-500 dark:text-slate-400 font-medium tracking-tight">
            Discover people from different locations, explore their profiles, and send friend requests to like-minded adventurers.
          </p>

          <div className="grid grid-cols-2 gap-4 bg-orange-50 dark:bg-black p-3 rounded-lg">
            <img
              src="https://res.cloudinary.com/dwg7vniow/image/upload/v1772106549/travo-connections-page_xm3pui.png"
              className="h-20 w-full rounded-lg object-cover md:h-44 lg:h-48"
            />
            <img
              src="https://res.cloudinary.com/dwg7vniow/image/upload/v1772106550/Travo-chats_axc6j5.png"
              className="h-20 w-full rounded-lg object-cover md:h-44 lg:h-48"
            />
          </div>
        </div>
      ),
    },
    {
      title: "Plan Your Trip",
      content: (
        <div>
          <p className="mb-2 text-2xl font-normal md:text-4xl text-neutral-800 dark:text-neutral-200 tracking-tight">
            Organize journeys exactly the way you want
          </p>
          <p className="mb-4 text-lg md:text-base text-slate-500 dark:text-slate-400 font-medium tracking-tight">
            Organize journeys your way
          </p>

          <div className="mb-8 space-y-2 text-sm md:text-base text-slate-500 dark:text-slate-400 font-medium tracking-tight">
            <div><FontAwesomeIcon icon={faCheck} className="text-orange-500 mr-2" /> Choose destination, dates, and trip type</div>
            <div><FontAwesomeIcon icon={faCheck} className="text-orange-500 mr-2" /> Set participant limits and visibility</div>
            <div><FontAwesomeIcon icon={faCheck} className="text-orange-500 mr-2" /> Accept or reject join requests</div>
            <div><FontAwesomeIcon icon={faCheck} className="text-orange-500 mr-2" /> Manage participants as trip admin</div>
          </div>

          <div className="grid grid-cols-2 gap-4 bg-orange-50 dark:bg-black p-3 rounded-lg">
            <img
              src="https://res.cloudinary.com/dwg7vniow/image/upload/v1772106551/Travo-trips_gawqj1.png"
              className="h-20 w-full rounded-lg object-cover md:h-44 lg:h-48"
            />
            <img
              src="https://res.cloudinary.com/dwg7vniow/image/upload/v1772106551/Travo-trips-2_yfde0d.png"
              className="h-20 w-full rounded-lg object-cover md:h-44 lg:h-48"
            />
          </div>
        </div>
      ),
    },
    {
      title: "Chat and Coordinate",
      content: (
        <div>
          <p className="mb-2 text-2xl font-normal md:text-4xl text-neutral-800 dark:text-neutral-200 tracking-tight">
            Stay connected before and during the trip
          </p>
          <p className="mb-4 text-sm md:text-base text-slate-500 dark:text-slate-400 font-medium tracking-tight">
            Communicate in real-time
          </p>

          <div className="mb-8 space-y-2 text-sm md:text-base text-slate-500 dark:text-slate-400 font-medium tracking-tight">
            <div><FontAwesomeIcon icon={faCheck} className="text-orange-500 mr-2" /> One-to-one and group conversations</div>
            <div><FontAwesomeIcon icon={faCheck} className="text-orange-500 mr-2" /> Message delivery and seen status</div>
            <div><FontAwesomeIcon icon={faCheck} className="text-orange-500 mr-2" /> Admin controls for group chats</div>
            <div><FontAwesomeIcon icon={faCheck} className="text-orange-500 mr-2" /> Seamless coordination before and during trips</div>
          </div>

          <div className="grid grid-cols-2 gap-4 bg-orange-50 dark:bg-black p-3 rounded-lg">
            <img
              src="https://res.cloudinary.com/dwg7vniow/image/upload/v1772106550/Travo-chats_axc6j5.png"
              className="h-20 w-full rounded-lg object-cover md:h-44 lg:h-48"
            />
          </div>
        </div>
      ),
    },
    {
      title: "Travel and Share Memories",
      content: (
        <div>
          <p className="mb-2 text-2xl font-normal md:text-5xl text-neutral-800 dark:text-neutral-200 tracking-tight">
            Turn every journey into a story worth sharing
          </p>
          <p className="mb-4 text-sm md:text-base text-slate-500 dark:text-slate-400 font-medium tracking-tight">
            Turn journeys into stories
          </p>

          <div className="mb-8 space-y-2 text-xs md:text-base  text-slate-500 dark:text-slate-400 font-medium tracking-tight">
            <div><FontAwesomeIcon icon={faCheck} className="text-orange-500 mr-2" /> Post blogs with photos and experiences</div>
            <div><FontAwesomeIcon icon={faCheck} className="text-orange-500 mr-2" /> Like, comment, and interact with blogs</div>
            <div><FontAwesomeIcon icon={faCheck} className="text-orange-500 mr-2" /> Rate and review your trip partners</div>
            <div><FontAwesomeIcon icon={faCheck} className="text-orange-500 mr-2" /> Build your travel history on your profile</div>
          </div>

          <div className="grid grid-cols-2 gap-4 bg-orange-50 dark:bg-black p-3 rounded-lg">
            <img
              src="https://res.cloudinary.com/dwg7vniow/image/upload/v1772106550/Travo-dashboard_qw4cag.png"
              className="h-20 w-full rounded-lg object-cover md:h-44 lg:h-48"
            />
            <img
              src="https://res.cloudinary.com/dwg7vniow/image/upload/v1772106551/Travo-user-profile_svcduw.png"
              className="h-20 w-full rounded-lg object-cover md:h-44 lg:h-48"
            />
          </div>
        </div>
      ),
    },
  ];

  return (
    <div className="relative overflow-clip bg-white dark:bg-black">
      <Timeline data={data} />
    </div>
  );
}
