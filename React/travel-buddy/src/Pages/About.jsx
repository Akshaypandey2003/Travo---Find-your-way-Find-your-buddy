/* eslint-disable no-unused-vars */
import * as React from "react";
import { Button } from "@/components/ui/button";
import { ScrollArea, ScrollBar } from "@/components/ui/scroll-area";
import { Card } from "@/components/ui/card";
import {
  faComments,
  faExclamationTriangle,
  faIdCard,
  faShieldAlt,
} from "@fortawesome/free-solid-svg-icons";
import ImgCarousel from "./PageComponents/ImgCarousel";
import HeroSectionImageCarousel from "./HeroSectionImageCarousel";
import Footer from "./Footer Section/Footer";
import CustomerCard from "./PageComponents/CustomerCard";
import { SafetyCard } from "./PageComponents/SafetyCard";
import { useSelector } from "react-redux";
import FeedbackCard from "./PageComponents/FeedbackCard";


const feedbacksInfo = [
  {
    name: "Riya Sharma",
    gender: "Female",
    userImg: "",
    comment:
      "This platform made it super easy for me to find a travel buddy. The whole trip was fun and stress-free!",
    rating: 5,
    createdAt: "2025-08-10T10:30:00Z",
  },
  {
    name: "Arjun Mehta",
    gender: "Male",
    userImg: "",
    comment:
      "I loved how simple it was to connect with like-minded travelers. Definitely planning my next trip here!",
    rating: 4,
    createdAt: "2025-08-15T14:45:00Z",
  },
  {
    name: "Priya Verma",
    gender: "Female",
    userImg: "",
    comment:
      "Great community and smooth experience. Found amazing companions for my Goa trip. Highly recommend!",
    rating: 5,
    createdAt: "2025-09-01T09:15:00Z",
  },
  {
    name: "Rohan Gupta",
    gender: "Male",
    userImg: "",
    comment:
      "The idea is fantastic! A little more filtering on trip types would make it perfect. Still, a great start.",
    rating: 3,
    createdAt: "2025-09-05T19:00:00Z",
  },
  {
    name: "Sneha Patel",
    gender: "Female",
    userImg: "",
    comment:
      "I not only found travel buddies but also made lifelong friends. This app really changed how I travel.",
    rating: 5,
    createdAt: "2025-09-08T12:20:00Z",
  },
];



const safetyCardInfo = [
  {
    title: "Every profile manually reviewed",
    icon: faIdCard,
    className: "text-9xl text-blue-500",
    desc:
      "Our team manually verifies every new workawayer and host profile" +
      "before they are listed on the site. To help keep our community" +
      "safe and fair, and to allow our members to make informed" +
      "decisions when arranging potential exchanges, we also review" +
      "every new and renewing workawayer, as well as every new and" +
      "updated host listing.",
  },
  {
    title: "Every profile manually reviewed",
    icon: faShieldAlt,
    className: "text-9xl text-green-700",
    desc:
      "Our team manually verifies every new workawayer and host profile" +
      "before they are listed on the site. To help keep our community" +
      "safe and fair, and to allow our members to make informed" +
      "decisions when arranging potential exchanges, we also review" +
      "every new and renewing workawayer, as well as every new and" +
      "updated host listing.",
  },
  {
    title: "Every profile manually reviewed",
    icon: faComments,
    className: "text-9xl text-orange-800",
    desc:
      "Our team manually verifies every new workawayer and host profile" +
      "before they are listed on the site. To help keep our community" +
      "safe and fair, and to allow our members to make informed" +
      "decisions when arranging potential exchanges, we also review" +
      "every new and renewing workawayer, as well as every new and" +
      "updated host listing.",
  },
  {
    title: "Every profile manually reviewed",
    icon: faExclamationTriangle,
    className: "text-9xl text-red-600",
    desc:
      "Our team manually verifies every new workawayer and host profile" +
      "before they are listed on the site. To help keep our community" +
      "safe and fair, and to allow our members to make informed" +
      "decisions when arranging potential exchanges, we also review" +
      "every new and renewing workawayer, as well as every new and" +
      "updated host listing.",
  },
];
export const About = () => {
  const theme = useSelector((store) => store.auth.theme);
  const feedbacks = useSelector((store) => store.trip.tripFeedbacks);
  console.log("Feedbacks from About page:", feedbacks);
  return (
    <div className="mt-16">
      <div className="p-10  m-auto">
        <ImgCarousel />
      </div>
      {/* --------- Second container ---------------------- */}
      <div className="flex items-center justify-center ">
        <div className="w-[40%] h-[75vh]">
          <Card className="w-[16rem] h-[19rem] rounded-3xl  object-contain overflow-hidden relative top-[5rem] left-[5rem]">
            <img
              src="https://res.cloudinary.com/dwg7vniow/image/upload/v1751808658/palace-1_pxsg8t.jpg"
              alt=""
              loading="lazy"
              className="w-full h-full"
            />
          </Card>
          <Card className="w-[16rem] h-[13rem] rounded-3xl  object-contain overflow-hidden relative bottom-14 left-[12rem]">
            <img
              src="https://res.cloudinary.com/dwg7vniow/image/upload/v1751808707/resort-1_ldhbwe.jpg"
              alt=""
              loading="lazy"
              className="w-full h-full"
            />
          </Card>
          <Card className="w-[10rem] h-[9rem] rounded-3xl object-contain overflow-hidden relative bottom-[24rem] left-[18rem]">
            <img
              src="https://res.cloudinary.com/dwg7vniow/image/upload/v1751808545/monument-1_googoq.jpg"
              alt=""
              loading="lazy"
              className="w-full h-full"
            />
          </Card>
        </div>
        <div className="w-[50%] px-20 py-10   ">
          <div className="flex items-center gap-2 mb-5">
            <div className="w-8 h-8 ">
              <img src="../plane.png" alt="" />
            </div>
            <h1 className="text-orange-500 font-semibold ">We Recommend</h1>
          </div>
          <p className="text-4xl mb-5">
            {" "}
            <strong className="text-5xl">We Recommend</strong> <br></br>Best
            Destinations Every Month
          </p>
          <p className="text-lg text-gray-500">
            Don't miss the chance! Find your desired destinations here and put
            another mark on your bucket list.
          </p>
          <div className="py-4 flex gap-5">
            <Card className="w-[8rem] h-[5rem] text-center py-3">
              <h2 className="font-bold text-lg">2000+</h2>
              <h3 className="text-gray-500">Our Explorers</h3>
            </Card>
            <Card className="w-[8rem] h-[5rem] text-center py-3">
              <h2 className="font-bold text-lg">50+</h2>
              <h3 className="text-gray-500">Hosts</h3>
            </Card>
            <Card className=" h-[5rem] text-center py-3 px-6">
              <h2 className="font-bold text-lg">5+</h2>
              <h3 className="text-gray-500">Years of Experience</h3>
            </Card>
          </div>
        </div>
      </div>

      {/* --------- Third container ---------------------- */}
      <div className="flex items-center justify-center ">
        <div className="w-[50%] px-20 py-10 ">
          <div className="flex items-center gap-2 mb-5">
            <div className="w-8 h-8 ">
              <img src="../plane.png" alt="" />
            </div>
            <h1 className="text-orange-500 font-semibold ">What We Give</h1>
          </div>
          <p className="text-4xl mb-5">
            {" "}
            <strong className="text-5xl">Best Features</strong> <br></br>For You
          </p>
          <p className="text-lg text-gray-500">
            We provide best possible features for our customers to make their
            journey more comfortable and memorable.
          </p>
        </div>
        <div className="w-[50%] px-20 py-10">
          <div className="py-4 flex gap-5">
            <Card className=" p-3">
              <div className="w-11 h-11  rounded-md p-2 bg-orange-200  ">
                <img src="../directions-1.png" alt="" />
              </div>
              <h2 className="font-semibold text-lg">Lots of Choices</h2>
              <h3 className="text-gray-500">
                We provide several choices of destinations and affordable
                traveling packages.
              </h3>
            </Card>
            <Card className=" p-3">
              <div className="w-11 h-11  rounded-md p-2 bg-orange-200  ">
                <img src="../guide-1.png" alt="" />
              </div>
              <h2 className="font-semibold text-lg">Best Tour Guide</h2>
              <h3 className="text-gray-500">
                We provide professional tour guide and people who understand the
                place.
              </h3>
            </Card>
            <Card className="p-3">
              <div className="w-11 h-11  rounded-md p-2 bg-orange-200  ">
                <img src="../calendar.png" alt="" />
              </div>
              <h2 className="font-semibold text-lg">Easy Bookings</h2>
              <h3 className="text-gray-500">
                We make it easier of our customers to do bookings to the places
                they want.
              </h3>
            </Card>
          </div>
        </div>
      </div>

      {/* ---------------------------- Fourth Container---------------------- */}
      <div
        className={`p-10  rounded-3xl my-12 w-[85rem] m-auto ${
          theme === "dark" ? "bg-black border-gray-800" : "bg-gray-100"
        }`}
      >
        <div className=" w-[40rem] m-auto text-center">
          <h1 className="text-orange-500 font-semibold ">Top Destinations</h1>
          <p className="text-4xl mb-5">
            {" "}
            <strong className="text-5xl">Let's Explore</strong> <br></br>Our Top
            Destinations For You
          </p>
          <p className="text-lg text-gray-500">
            We provide best possible features for our customers to make their
            journey more comfortable and memorable.
          </p>
        </div>
        <Card className="flex  mt-10 mx-auto w-[62rem]">
          <div className="border-r-2  flex p-2 px-8 gap-2 items-center">
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
          <div className="border-r-2  flex px-8 gap-2 items-center ">
            <div className="w-9 h-9  rounded-full p-2 bg-orange-200  ">
              <img src="../place.png" alt="" />
            </div>
            <div>
              <h2 className="font-bold">Person</h2>
              <p className="text-xs font-semibold text-gray-500">
                How many people?
              </p>
            </div>
          </div>
          <div className="border-r-2  flex px-8 gap-2 items-center ">
            <div className="w-9 h-9  rounded-full p-2 bg-orange-200  ">
              <img src="../place.png" alt="" />
            </div>
            <div>
              <h2 className="font-bold">Check In</h2>
              <p className="text-xs font-semibold text-gray-500">
                26th Jan 2025
              </p>
            </div>
          </div>
          <div className="border-r-2  flex px-8 gap-2 items-center ">
            <div className="w-9 h-9  rounded-full p-2 bg-orange-200  ">
              <img src="../calendar.png" alt="" />
            </div>
            <div>
              <h2 className="font-bold">Check Out</h2>
              <p className="text-xs font-semibold text-gray-500">
                1st Feb 2025
              </p>
            </div>
          </div>
          <div className="flex justify-center items-center px-8">
            <Button className="bg-orange-600 hover:bg-orange-500 border-none font-semibold">
              Get Started
            </Button>
          </div>
        </Card>
      </div>

      {/* ---------------- Fifth Container ------------------------- */}
      <div className="flex items-center justify-between">
        <div className="w-[40%]   flex flex-wrap">
          <Card className="w-[16rem] h-[22rem] rounded-3xl overflow-hidden relative top-[10rem] left-[5rem] border-none">
            <div className="w-full h-full shadow-inner-custom">
              <img
                src="https://res.cloudinary.com/dwg7vniow/image/upload/v1751808468/Friends-1_kstpqr.jpg"
                alt=""
                className="w-full h-full object-cover"
              />
            </div>
          </Card>
          <Card className="w-[16rem] h-[13rem] rounded-3xl overflow-hidden relative top-[7rem] left-[2rem] border-none">
            <div className="w-full h-full shadow-inner-custom">
              <img
                src="https://res.cloudinary.com/dwg7vniow/image/upload/v1751808485/friends-2_r4uowc.jpg"
                alt=""
                className="w-full h-full object-cover"
              />
            </div>
          </Card>
          <div className="w-[5rem] h-[5rem] rounded-3xl overflow-hidden relative top-[15rem] left-[3rem] border-none">
            <img
              src="https://res.cloudinary.com/dwg7vniow/image/upload/v1751808721/Star_Frame_bozrmn.png"
              alt=""
              className="w-full h-full object-cover"
            />
          </div>
          <Card className="w-[13rem] h-[9rem] rounded-3xl overflow-hidden relative bottom-[18rem] left-[5rem] border-none ">
            <div className="w-full h-full shadow-inner-custom">
              <img
                src="https://res.cloudinary.com/dwg7vniow/image/upload/v1751808507/friends-3_j0edsz.jpg"
                alt=""
                className="w-full h-full object-cover"
              />
            </div>
          </Card>
          <Card className="w-[22rem] h-[13rem] rounded-3xl overflow-hidden relative bottom-[2rem] left-[5rem] border-none">
            <div className="w-full h-full shadow-inner-custom">
              <img
                src="https://res.cloudinary.com/dwg7vniow/image/upload/v1751808526/friends-4_llyrfh.jpg"
                alt=""
                className="w-full h-full object-cover blur-[0.2rem]"
              />
            </div>
          </Card>
        </div>
        <div className="w-[50%] px-20 py-10 ">
          <div className="flex items-center gap-2 mb-5">
            <div className="w-8 h-8 ">
              <img src="../plane.png" alt="" />
            </div>
            <h1 className="text-orange-500 font-semibold ">
              What Actually We Do
            </h1>
          </div>
          <p className="text-4xl mb-5">
            {" "}
            <strong className="text-5xl">We Help Our Cutomers</strong> <br></br>
            To Find Someone At Ease And Make The Trip Memorable One
          </p>
          <p className="text-lg text-gray-500">
            Find people around the world to accompany you on your trip, make new
            friends and grow your network world wide.
          </p>
          <HeroSectionImageCarousel />
        </div>
      </div>
      {/* -------------- Sixth Container --------------------- */}
      <div className=" px-20 py-10">
        <div className="w-[50rem] ">
          <div className="flex items-center gap-2 mb-5">
            <div className="w-8 h-8 ">
              <img src="../plane.png" alt="" />
            </div>
            <h1 className="text-orange-500 font-semibold ">What They Say</h1>
          </div>
          <p className="text-4xl mb-5">
            {" "}
            <strong className="text-5xl">What Our Customers</strong> <br></br>
            Say About Us
          </p>
          <p className="text-lg text-gray-500">
            Find people around the world to accompany you on your trip, make new
            friends and grow your network world wide.
          </p>
        </div>
        <div className=" gap-[2rem] mt-10 w-[80rem] p-4">
          <ScrollArea className="whitespace-nowrap">
            <div className="flex w-max space-x-4 pb-4 scroll-smooth snap-x py-4">
              {feedbacksInfo.map((item, index) => (
                <FeedbackCard key={index} feedback={item} />
              ))}
            </div>
            <ScrollBar orientation="horizontal" />
          </ScrollArea>
        </div>
      </div>
      {/* -------------- Seventh Container --------------------- */}
      <div className=" px-20 py-10 ">
        <div className="w-[50rem]  m-auto text-center">
          <div className="flex items-center justify-center gap-2 mb-5  ">
            <div className="w-8 h-8 ">
              <img src="../plane.png" alt="" />
            </div>
            <h1 className="text-orange-500 font-semibold ">
              What We Do To Ensure Safety
            </h1>
          </div>
          <p className="text-4xl mb-5">
            {" "}
            <strong className="text-5xl">
              Your Safety, Our Priority!
            </strong>{" "}
            <br></br>
            Explore with Confidence
          </p>
          <p className="text-lg text-gray-500">
            From secure logins to encrypted communications, we prioritize your
            safety so you can focus on your adventures. We ensure authenticity
            by verifying profiles, so you can confidently connect with genuine
            travel companions.
          </p>
        </div>
        <div className=" flex justify-between gap-6 mt-10 w-[80rem]  flex-wrap  m-auto">
          {safetyCardInfo.map((item, index) => (
            <SafetyCard key={index} item={item} />
          ))}
        </div>
      </div>
      {/* ---------------------------- Eighth Container---------------------- */}
      <div
        className={`p-10  rounded-3xl w-[85rem] m-auto ${
          theme === "dark" ? "bg-black" : "bg-gray-100"
        }`}
      >
        <div className=" w-[40rem] m-auto text-center">
          <p className="text-4xl mb-5">
            {" "}
            <strong className="text-5xl">Let's Get Started</strong> <br></br>
            Choose Your Vibe And Let Us Guide You Way
          </p>
          <p className="text-lg text-gray-500">
            Embark on an adventurous journey to the most beautiful places in the
            world and make memories that will last a lifetime.
          </p>
        </div>
        <div className="flex justify-center items-center px-8 mt-5">
          <Button className="bg-orange-600 hover:bg-orange-500 border-none font-semibold">
            Get Started
          </Button>
        </div>
      </div>
      {/* ---------------------------- Footer Section (Eighth Container)---------------------- */}
      <Footer />
    </div>
  );
};
export default About;
