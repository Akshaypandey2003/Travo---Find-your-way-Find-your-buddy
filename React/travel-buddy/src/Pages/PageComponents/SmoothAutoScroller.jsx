/* eslint-disable react/prop-types */
/* eslint-disable no-unused-vars */
import React, { useEffect, useRef, useState } from "react";
import { Badge } from "@/components/ui/badge";

const SmoothAutoScroller = ({ data = [], reverse = false, speed = 0.5 }) => {
  const containerRef = useRef(null);
  const contentRef = useRef(null);
  const [isPaused, setIsPaused] = useState(false);
  const [canScroll, setCanScroll] = useState(false);

  useEffect(() => {
    const container = containerRef.current;
    const content = contentRef.current;
    if (!container || !content) return;

    // Observe when content size is ready
    const resizeObserver = new ResizeObserver(() => {
      if (reverse) {
        container.scrollLeft = content.scrollWidth / 2;
      }
      setCanScroll(true); // start animation only after layout ready
    });

    resizeObserver.observe(content);

    let animationFrame;

    const scroll = () => {
      if (!isPaused && canScroll) {
        const direction = reverse ? -1 : 1;
        container.scrollLeft += speed * direction;

        if (!reverse && container.scrollLeft >= content.scrollWidth / 2) {
          container.scrollLeft = 0;
        }
        if (reverse && container.scrollLeft <= 0) {
          container.scrollLeft = content.scrollWidth / 2;
        }
      }

      animationFrame = requestAnimationFrame(scroll);
    };

    animationFrame = requestAnimationFrame(scroll);

    return () => {
      cancelAnimationFrame(animationFrame);
      resizeObserver.disconnect();
    };
  }, [isPaused, reverse, speed, canScroll]);

  return (
    <div
      className="w-full overflow-hidden"
      onMouseEnter={() => setIsPaused(true)}
      onMouseLeave={() => setIsPaused(false)}
    >
      <div
        ref={containerRef}
        className="whitespace-nowrap overflow-hidden scroll-smooth"
        style={{ width: "90rem" }}
      >
        <div ref={contentRef} className="inline-block">
          {[...data, ...data].map((item, idx) => (
            <Badge key={idx} className="mx-4 border-orange-600" variant="ghost">
              {item}
            </Badge>
          ))}
        </div>
      </div>
    </div>
  );
};

export default SmoothAutoScroller;
