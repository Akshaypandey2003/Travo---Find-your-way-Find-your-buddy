/* eslint-disable no-unused-vars */
import React from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";

const Footer = () => {
  return (
    <footer className="  mt-20">
      <div className="w-[90vw] border-t-2 m-auto py-5 flex flex-col gap-4">
        {/* Brand + About */}
        <div className="flex gap-4 items-center mt-4">
         
          {/* <h1 className="text-3xl font-bold">Travo</h1> */}
        </div>
        <div className="flex flex-col items-center gap-3 text-center">
           <img
            src="https://res.cloudinary.com/dwg7vniow/image/upload/v1757867374/Travel_Logo_third_gge6hk.png"
            alt="Travo Logo"
            className="w-48 max-w-48 object-contain"
          />
          <p className=" max-w-md font-light">
            Your trusted travel companion. Discover, plan, and experience
            unforgettable journeys.
          </p>
        </div>

        {/* Newsletter / Query */}
        {/* <div className="flex justify-center items-center gap-3">
          <Input
            className="w-64 border-gray-300"
            placeholder="Your Query"
          />
          <Button className="bg-orange-600 hover:bg-orange-500">
            <img src="../sendBtn.png" alt="Send" className="w-5 h-5" />
          </Button>
        </div> */}

        {/* Quick Links */}
        <div className="flex gap-8 justify-center text-gray-600 text-sm">
          <p className="cursor-pointer hover:text-orange-600">About Us</p>
          <p className="cursor-pointer hover:text-orange-600">Blog</p>
          <p className="cursor-pointer hover:text-orange-600">Contact</p>
          <p className="cursor-pointer hover:text-orange-600">FAQ</p>
        </div>

        {/* Social Links */}
        <div className="flex justify-center gap-5">
          <img
            src="../instagram-1.png"
            alt="Instagram"
            className="w-6 h-6 cursor-pointer hover:opacity-80"
          />
          <img
            src="../facebook-1.png"
            alt="Facebook"
            className="w-6 h-6 cursor-pointer hover:opacity-80"
          />
          <img
            src="../twitter-1.png"
            alt="Twitter"
            className="w-6 h-6 cursor-pointer hover:opacity-80"
          />
        </div>

        {/* Copyright */}
        <div className="text-center text-gray-500 text-sm mt-4">
          &copy; {new Date().getFullYear()} Travo. All rights reserved.
        </div>
      </div>
    </footer>
  );
};

export default Footer;
