/* eslint-disable no-unused-vars */
/* eslint-disable react/prop-types */
/* ImageCarousel.jsx */
/* eslint-disable react/prop-types */
import React, { useEffect, useRef, useState } from "react";

/**
 * ImageCarousel
 * - images: array of image URLs
 * - height: px height of the carousel (optional)
 *
 * Note: keep the "scrollbar-hide" / hidden-scrollbar CSS in your global styles:
 * .scrollbar-hide::-webkit-scrollbar { display: none; }
 * .scrollbar-hide { -ms-overflow-style: none; scrollbar-width: none; }
 */
const ImageCarousel = ({ images = [], height = 256 }) => {
  const containerRef = useRef(null);
  const rafRef = useRef(null);
  const [current, setCurrent] = useState(0);

  // Go to slide index (use container.clientWidth * index for reliable alignment)
  const goTo = (index) => {
    const container = containerRef.current;
    if (!container) return;

    const w = Math.max(container.clientWidth, 1);
    const left = index * w;
    container.scrollTo({ left, behavior: "smooth" });

    // optimistic update so dot updates immediately
    setCurrent(index);
  };

  // onScroll: rAF-throttled update of current index
  useEffect(() => {
    const container = containerRef.current;
    if (!container) return;

    const onScroll = () => {
      if (rafRef.current) cancelAnimationFrame(rafRef.current);
      rafRef.current = requestAnimationFrame(() => {
        const w = Math.max(container.clientWidth, 1);
        const idx = Math.round(container.scrollLeft / w);
        setCurrent(idx);
      });
    };

    container.addEventListener("scroll", onScroll, { passive: true });
    return () => {
      container.removeEventListener("scroll", onScroll);
      if (rafRef.current) cancelAnimationFrame(rafRef.current);
    };
  }, []);

  // on resize -> re-align current slide (keeps index correct when width changes)
  useEffect(() => {
    const handleResize = () => {
      const container = containerRef.current;
      if (!container) return;
      const w = Math.max(container.clientWidth, 1);
      container.scrollTo({ left: current * w, behavior: "auto" });
    };
    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, [current]);

  if (!images || images.length === 0) return null;

  return (
    <div className="flex flex-col items-center w-full">
      {/* Scrollable container */}
      <div
        ref={containerRef}
        className="w-96 h-96 overflow-x-auto snap-x snap-mandatory scroll-smooth scrollbar-hide"
        style={{ WebkitOverflowScrolling: "touch" }}
      >
        <div className="flex w-full h-full">
          {images.map((src, i) => (
            <div
              key={i}
              className="snap-start flex-shrink-0 w-full flex items-center justify-center"
              style={{ height: `${height}px` }}
            >
              <img
                src={src}
                alt={`slide-${i}`}
                className="w-full object-cover"
                draggable={false}
              />
            </div>
          ))}
        </div>
      </div>

      {/* Dots */}
      <div className="flex gap-2 mt-3">
        {images.map((_, idx) => (
          <button
            key={idx}
            onClick={() => goTo(idx)}
            aria-label={`Go to image ${idx + 1}`}
            className={`w-3 h-3 rounded-full transition-transform focus:outline-none ${
              idx === current ? "bg-orange-500 scale-110" : "bg-gray-300"
            }`}
          />
        ))}
      </div>
    </div>
  );
};

export default ImageCarousel;



