/* eslint-disable no-unused-vars */
import React, { useState } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faArrowLeft, faArrowRight } from "@fortawesome/free-solid-svg-icons";
import { Button } from "@/components/ui/button";

const slidesData = [
  { id: 1, name: "Switzerland", img: "https://i.ibb.co/qCkd9jS/img1.jpg" },
  { id: 2, name: "Finland", img: "https://i.ibb.co/jrRb11q/img2.jpg" },
  { id: 3, name: "Iceland", img: "https://i.ibb.co/NSwVv8D/img3.jpg" },
  { id: 4, name: "Australia", img: "https://i.ibb.co/Bq4Q0M8/img4.jpg" },
  { id: 5, name: "Netherlands", img: "https://i.ibb.co/jTQfmTq/img5.jpg" },
  { id: 6, name: "Ireland", img: "https://i.ibb.co/RNkk6L0/img6.jpg" },
];

const DestinationDetailsImgCarousel = () => {
  const [slides, setSlides] = useState(slidesData);

  const moveNext = () => {
    setSlides((prevSlides) => [...prevSlides.slice(1), prevSlides[0]]);
  };

  const movePrev = () => {
    setSlides((prevSlides) => [prevSlides[prevSlides.length - 1], ...prevSlides.slice(0, -1)]);
  };

  return (
    <div className="container border border-black">
      <div className="slide ">
        {slides.map((slide, index) => (
          <div
            key={slide.id}
            className={`item item-${index} border border-yellow-300`}
            style={{ backgroundImage: `url(${slide.img})` }}
          >
            <div className="content">
              <div className="name">{slide.name}</div>
              <div className="des">
                Lorem ipsum dolor sit amet, consectetur adipisicing elit.
              </div>
              <button>See More</button>
            </div>
          </div>
        ))}
      </div>

      <div className="button">
        <Button className="prev border-none bg-orange-500" onClick={movePrev} >
          <FontAwesomeIcon icon={faArrowLeft}  className="text-black " />
        </Button>
        <Button className="next border-none bg-orange-500" onClick={moveNext} >
          <FontAwesomeIcon icon={faArrowRight} className="text-black " />
        </Button>
      </div>
    </div>
  );
};

export default DestinationDetailsImgCarousel;
