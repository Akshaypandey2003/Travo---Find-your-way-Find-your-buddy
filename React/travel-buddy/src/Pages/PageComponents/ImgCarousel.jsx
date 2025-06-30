/* eslint-disable no-unused-vars */
import React, { useState } from "react";

import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import {
  Carousel,
  CarouselContent,
  CarouselItem,
  CarouselNext,
  CarouselPrevious,
} from "@/components/ui/carousel";
import Autoplay from "embla-carousel-autoplay";
import HeroSectionImageCarousel from "../HeroSectionImageCarousel";

export function CarouselPlugin() {
  const plugin = React.useRef(
    Autoplay({ delay: 2000, stopOnInteraction: true })
  );
}
const imgs = ["/Beach1.jpg", "/Beach2.jpg", "/mountains1.jpg", "/mountains2.jpg", "/palace-1.jpg", "/resort-2.jpg"];
export const ImgCarousel = () => {

const [imgSrc,setImgSrc] = useState("");

  return (
    <Carousel
      opts={{
        align: "start",
        loop: true,
      }}
      plugins={[
        Autoplay({
          delay: 2000,
        }),
      ]}
      className="w-[90rem] m-auto carousel-wrapper"
    >
      <CarouselContent>
        {imgs.map((item) => (
          <CarouselItem key={item} className="w-full h-[30rem] ">
            <img
              src= {item}
              alt=""
              className="w-full h-full rounded-3xl "
            />
          </CarouselItem>
        ))}
      </CarouselContent>
      <CarouselPrevious />
      <CarouselNext />
      <div className="absolute top-0 left-0 w-full h-full bg-black bg-opacity-50 flex items-center justify-center">
        <div className="text-center px-20 py-10 w-[70rem] ">
         
          <p className="text-4xl mb-5 text-orange-500">
            {" "}
            <strong className="text-5xl">Empowering Innovation, Driving Success</strong>{" "}
            <br></br>Bringing the World Closer to You!
          </p>
          <p className="text-lg text-white">
          We are committed to delivering cutting-edge solutions that transform businesses. With a passion for excellence and a vision for the future, we help you achieve more.
          </p>
          {/* <div className=" flex justify-center">
            <Card className="flex px-5 mt-10 w-[70%]">
              <div className="border-r-2  flex p-2 gap-2 items-center ">
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
              <div className="border-r-2  flex px-8 gap-2 items-center  mr-4 ">
                <div className="w-9 h-9  rounded-full p-2 bg-orange-200  ">
                  <img src="../calendar.png" alt="" />
                </div>
                <div>
                  <h2 className="font-bold">Select Date</h2>
                  <p className="text-xs font-semibold text-gray-500">
                    1st Feb 2025
                  </p>
                </div>
              </div>
              <div className="flex justify-center items-center pl-6">
                <Button className="bg-orange-600 hover:bg-orange-500 border-none font-semibold">
                  Get Started
                </Button>
              </div>
            </Card>
          </div> */}
          <HeroSectionImageCarousel />
        </div>
      </div>
    </Carousel>
  );
};
export default ImgCarousel;
