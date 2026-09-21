/* eslint-disable no-unused-vars */
"use client";

import { faStar } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { motion } from "framer-motion";
import { AnimatedTooltipPreview } from "./AnimatedToolTipPreview";


export function HeroSection() {
  return (
    <div className="relative my-20 mx-auto w-[100vw] flex  flex-col items-center justify-center">
     
      <div className="px-4 md:py-20">
        <h1 className="relative z-10 mx-auto max-w-4xl text-center text-2xl font-bold text-orange-600 md:text-4xl lg:text-7xl dark:text-orange-600">
          {"Find Your Way, Find Your Buddy".split(" ").map((word, index) => (
            <motion.span
              key={index}
              initial={{ opacity: 0, filter: "blur(4px)", y: 10 }}
              animate={{ opacity: 1, filter: "blur(0px)", y: 0 }}
              transition={{
                duration: 0.3,
                delay: index * 0.1,
                ease: "easeInOut",
              }}
              className="mr-2 inline-block"
            >
              {word}
            </motion.span>
          ))}
        </h1>
        <motion.p
          initial={{
            opacity: 0,
          }}
          animate={{
            opacity: 1,
          }}
          transition={{
            duration: 0.3,
            delay: 0.8,
          }}
          className="relative z-10 mx-auto max-w-xl py-4 text-center text-lg font-normal text-black dark:text-neutral-400"
        >
          Connect with like-minded adventurers, plan unforgettable trips, and
          turn every journey into a shared memory. Travo is your ultimate travel
          social network.
        </motion.p>
        <motion.div
          initial={{
            opacity: 0,
          }}
          animate={{
            opacity: 1,
          }}
          transition={{
            duration: 0.3,
            delay: 1,
          }}
          className="relative z-10 mt-8 flex flex-wrap items-center justify-center gap-4"
        >
            <AnimatedTooltipPreview />
            <span className="text-sm font-medium text-neutral-400">
              1000+ Customers Trusts us 
               </span>
             {[1,2,3,4,5].map((index) => (
                <FontAwesomeIcon key={index} icon={faStar} className="text-orange-400 mx-0" />
              ))}
           
        </motion.div>
      </div>
    </div>
  );
}