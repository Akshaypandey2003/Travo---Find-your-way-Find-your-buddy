/* eslint-disable no-unused-vars */

import { useSelector } from "react-redux";
import { HeroSection } from "./HeroSection";
import { TimelineDemo } from "./TimeLineDemo";
import { Button } from "@/components/ui/button";
import Footer from "../Footer Section/Footer";
import { ScrollArea, ScrollBar } from "@/components/ui/scroll-area";
import FeedbackCard from "../PageComponents/FeedbackCard";
import TripCard from "./TripCard";
import { AnimatedTooltipPreview } from "./AnimatedToolTipPreview";
import { AnimatedTestimonials } from "../../components/ui/animated-testimonials";
import Masonry from "../../components/Masonry";

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

const tripCategoryInfo = [
  {
    title: "Solo Adventures",
    description:
      "Find companions for your independent journeys. Perfect for digital nomads and solo explorers who want flexibility with occasional company.",
    imgUrl:
      "https://res.cloudinary.com/dwg7vniow/image/upload/v1769105512/Solo-Trip_hesze7.jpg",
  },
  {
    title: "Family Getaways",
    description:
      "Connect with other families traveling to the same destination Share tips, coordinate activities, and let kids make new friends on vacation.",
    imgUrl:
      "https://res.cloudinary.com/dwg7vniow/image/upload/v1769105301/Screenshot_2026-01-22_233710_ng5ajz.png",
  },
  {
    title: "Group Expeditions",
    description:
      "Organize multi-person adventures with built-in participant management. Ideal for treks, road trips, and international excursions with new friends.",
    imgUrl:
      "https://res.cloudinary.com/dwg7vniow/image/upload/v1769105641/Group-Trip_e5skv7.png",
  },
];

const testimonials = [
  {
    quote:
      "The attention to detail and innovative features have completely transformed our workflow. This is exactly what we've been looking for.",
    name: "Sarah Chen",
    designation: "Product Manager at TechFlow",
    src: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?q=80&w=3560&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
  },
  {
    quote:
      "Implementation was seamless and the results exceeded our expectations. The platform's flexibility is remarkable.",
    name: "Michael Rodriguez",
    designation: "CTO at InnovateSphere",
    src: "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?q=80&w=3540&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
  },
  {
    quote:
      "This solution has significantly improved our team's productivity. The intuitive interface makes complex tasks simple.",
    name: "Emily Watson",
    designation: "Operations Director at CloudScale",
    src: "https://images.unsplash.com/photo-1623582854588-d60de57fa33f?q=80&w=3540&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
  },
  {
    quote:
      "Outstanding support and robust features. It's rare to find a product that delivers on all its promises.",
    name: "James Kim",
    designation: "Engineering Lead at DataPro",
    src: "https://images.unsplash.com/photo-1636041293178-808a6762ab39?q=80&w=3464&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
  },
  {
    quote:
      "The scalability and performance have been game-changing for our organization. Highly recommend to any growing business.",
    name: "Lisa Thompson",
    designation: "VP of Technology at FutureNet",
    src: "https://images.unsplash.com/photo-1624561172888-ac93c696e10c?q=80&w=2592&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
  },
];

const items = [
  {
    id: "1",
    img: "https://res.cloudinary.com/dwg7vniow/image/upload/v1751808526/friends-4_llyrfh.jpg",
    url: "https://example.com/one",
    height: 400,
  },
  {
    id: "2",
    img: "https://res.cloudinary.com/dwg7vniow/image/upload/v1751808450/Beach2_aofhe0.jpg",
    url: "https://example.com/two",
    height: 250,
  },
  {
    id: "3",
    img: "https://res.cloudinary.com/dwg7vniow/image/upload/v1751808427/Beach1_ru7xus.jpg",
    url: "https://example.com/three",
    height: 600,
  },
  {
    id: "4",
    img: "https://res.cloudinary.com/dwg7vniow/image/upload/v1769105512/Solo-Trip_hesze7.jpg",
    url: "https://example.com/three",
    height: 300,
  },
  {
    id: "5",
    img: "https://res.cloudinary.com/dwg7vniow/image/upload/v1732449538/samples/woman-on-a-football-field.jpg",
    url: "https://example.com/three",
    height: 200,
  },
  {
    id: "6",
    img: "https://res.cloudinary.com/dwg7vniow/image/upload/v1751808545/monument-1_googoq.jpg",
    url: "https://example.com/three",
    height: 350,
  },
  {
    id: "7",
    img: "https://res.cloudinary.com/dwg7vniow/image/upload/v1753052816/ppptliadie1xgncv8bzv.jpg",
    url: "https://example.com/three",
    height: 400,
  },
  {
    id: "8",
    img: "https://res.cloudinary.com/dwg7vniow/image/upload/v1751808707/resort-1_ldhbwe.jpg",
    url: "https://example.com/three",
    height: 350,
  },
  // ... more items
];

export const LandingPageHome = () => {
  const theme = useSelector((store) => store.auth.theme);

  return (
    <div>
      <HeroSection />
      <TimelineDemo />
      <div className="px-32 py-10">
        <div className="w-[50rem] ">
          <div className="flex items-center gap-2 mb-5">
            <div className="w-8 h-8 ">
              <img src="../plane.png" alt="" />
            </div>
            <h1 className="text-orange-500 font-semibold ">
              Choose the Journey That Fits You
            </h1>
          </div>
          <p className="text-4xl mb-5">
            <strong className="text-5xl">Every Journey is Different</strong>{" "}
            <br />
            Choose Yours
          </p>
          <p className="text-lg text-gray-500 md:text-base max-w-sm">
            Pick the travel style that suits you and start planning your next
            journey with the right people.
          </p>
        </div>
        <div className=" gap-[2rem] mt-10 w-[80rem] flex justify-around p-4">
          {tripCategoryInfo.map((item, index) => (
            <TripCard key={index} trip={item} />
          ))}
        </div>
      </div>

      {/* Customers review container */}
      <div className=" px-32 py-10">
        <div className="w-[50rem]">
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
          <p className="text-lg text-gray-500 md:text-base max-w-sm">
            Find people around the world to accompany you on your trip, make new
            friends and grow your network world wide.
          </p>
        </div>
        <div className=" gap-[2rem] mt-10 p-4 flex  ">
          {/* <ScrollArea className="whitespace-nowrap">
            <div className="flex w-max space-x-4 pb-4 scroll-smooth snap-x py-4">
              {feedbacksInfo.map((item, index) => (
                <FeedbackCard key={index} feedback={item} />
              ))}
            </div>
            <ScrollBar orientation="horizontal" />
          </ScrollArea> */}
          <div >
               <AnimatedTestimonials testimonials={testimonials} />
          </div>
          <div className=" w-[30rem] pt-10">
              <Masonry
            items={items}
            ease="power3.out"
            duration={0.6}
            stagger={0.05}
            animateFrom="bottom"
            scaleOnHover
            hoverScale={0.95}
            blurToFocus
            colorShiftOnHover={false}
          />
          </div>
         
        </div>
      </div>

      {/* Get Started here */}
      <div
        className={`p-10  rounded-3xl w-[85rem] m-auto ${
          theme === "dark" && "bg-black"
        }`}
      >
        <div className=" w-[40rem] m-auto text-center">
          <p className="text-4xl mb-5">
            {" "}
            <strong className="text-5xl">Let&#39;s Get Started</strong>{" "}
            <br></br>
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

      {/* Footer Section */}
      <Footer />
    </div>
  );
};
export default LandingPageHome;
