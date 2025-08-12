/* eslint-disable react/prop-types */
/* eslint-disable no-unused-vars */
import React, { useRef, useEffect, useState } from "react";
import { ScrollArea, ScrollBar } from "@/components/ui/scroll-area";

const AutoScrollContainer = ({ children, speed = 0.5 }) => {
  const scrollRef = useRef(null);
  const animationRef = useRef(null);
  const [isPaused, setIsPaused] = useState(false);

  useEffect(() => {
    const scrollElement = scrollRef.current;

    const scroll = () => {
      if (!isPaused && scrollElement) {
        scrollElement.scrollLeft += speed;
        if (
          scrollElement.scrollLeft + scrollElement.clientWidth >=
          scrollElement.scrollWidth
        ) {
          scrollElement.scrollLeft = 0; // loop back
        }
      }
      animationRef.current = requestAnimationFrame(scroll);
    };

    animationRef.current = requestAnimationFrame(scroll);

    return () => cancelAnimationFrame(animationRef.current);
  }, [isPaused, speed]);

  return (
    <ScrollArea
      className="w-[87rem] rounded-md border-none shadow-none overflow-hidden"
      onMouseEnter={() => setIsPaused(true)}
      onMouseLeave={() => setIsPaused(false)}
    >
      <div
        ref={scrollRef}
        className="flex gap-4 px-4 py-4 w-max overflow-x-auto scroll-smooth scrollbar-hide"
      >
        {children}
      </div>
      <ScrollBar orientation="horizontal" />
    </ScrollArea>
  );
};

export default AutoScrollContainer;
