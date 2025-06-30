/* eslint-disable no-unused-vars */
import React from 'react'
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";

export const Footer = () => {
  return (
    <div className=" w-[90vw] mt-20 m-auto p-5">
           <div className="flex justify-between items-center border-b-2 border-orange-200 p-2">
             <div className="flex gap-4 items-center">
               <div className="w-20 h-20">
                 <img src="../AppLogo.png" alt="" className="w-52" />
               </div>
               <h1 className="text-3xl font-bold">Travo</h1>
             </div>
             <div className="flex gap-4  items-center">
               <div className=" w-[4rem]  object-cover overflow-hidden ">
                 <img src="../email-1.png" alt="" className=""/>
               </div>
              <Input className="border-2 border-black " placeholder="Your Query"></Input>
               <Button className="bg-orange-600 hover:bg-orange-500 border-none font-semibold 
               w-[3rem] h-[2rem] object-cover overflow-hidden p-2 ">
                 <img src="../sendBtn.png" alt="" className=""/>
               </Button>
             </div>
           </div>
           <div className="flex justify-between  p-10 ">
             <div className="">
               <h1 className="font-semibold text-lg">About</h1>
               <p className="text-gray-500 cursor-pointer">About Us</p>
               <p className="text-gray-500 cursor-pointer">Features</p>
               <p className="text-gray-500">Services</p>
             </div>
             <div className="">
               <h1 className="font-semibold text-lg">Company</h1>
               <p className="text-gray-500 cursor-pointer">Our Team</p>
               <p className="text-gray-500 cursor-pointer">Partner with us</p>
               <p className="text-gray-500 cursor-pointer">FAQ</p>
               <p className="text-gray-500 cursor-pointer">Blog</p>
             </div>
             <div className="">
               <h1 className="font-semibold text-lg">Support</h1>
               <p className="text-gray-500 cursor-pointer">Account</p>
               <p className="text-gray-500 cursor-pointer">Support Desk</p>
               <p className="text-gray-500 cursor-pointer">Feedback</p>
               <p className="text-gray-500 cursor-pointer">Contact Us</p>
             </div>
             <div className="">
               <h1 className="font-semibold text-lg">Social Links</h1>
               <div className="flex gap-5 mt-5">
                 <div className="w-[3rem] h-[3rem] cursor-pointer">
                   <img src="../instagram-1.png" alt="" />
                 </div>
                 <div className="w-[3rem] h-[3rem] cursor-pointer">
                   <img src="../facebook-1.png" alt="" />
                 </div>
                 <div className="w-[3rem] h-[3rem] cursor-pointer">
                   <img src="../twitter-1.png" alt="" />
                 </div>
               </div>
             </div>
           </div>
           <div className="mt-5 text-center">
             <h1 className="font-semibold text-gray-700">&copy;
             2025 Travo Copyright and All rights reserved</h1>
           </div>
         </div>
  )
}
export default Footer;
