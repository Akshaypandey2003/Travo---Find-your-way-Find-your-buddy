/* eslint-disable no-unused-vars */

import { Image } from "lucide-react";
import React from "react";

const HeroSectionImageCarousel = () => {
  return (
    <div className="wrapper">
      <div className="item item1">
        <img src="https://res.cloudinary.com/dwg7vniow/image/upload/v1751808427/Beach1_ru7xus.jpg" loading="lazy" alt="" />
      </div>
      <div className="item item2">
        <img src="https://res.cloudinary.com/dwg7vniow/image/upload/v1751808450/Beach2_aofhe0.jpg" loading="lazy" alt="" />
      
      </div>
      <div className="item item3">
        <img src="https://res.cloudinary.com/dwg7vniow/image/upload/v1751808585/mountains1_bnfhbr.jpg" loading="lazy" alt="" />
      </div>
      <div className="item item4">
        <img src="https://res.cloudinary.com/dwg7vniow/image/upload/v1751808631/mountains2_hpdkds.jpg" loading="lazy" alt="" />
      </div>
      <div className="item item5">
        <img src="https://res.cloudinary.com/dwg7vniow/image/upload/v1751808658/palace-1_pxsg8t.jpg" loading="lazy" alt="" />
      </div>
      <div className="item item6">
        <img src="https://res.cloudinary.com/dwg7vniow/image/upload/v1751808697/resort-2_upapaz.jpg" loading="lazy" alt="" />
      </div>
    </div>
  );
};

export default HeroSectionImageCarousel;
