/* eslint-disable no-unused-vars */
"use client";

import {
  IconBrandFacebook,
  IconBrandInstagram,
  IconBrandInstagramFilled,
  IconBrandTwitter,
} from "@tabler/icons-react";
import { Facebook, MailCheck, PlaneTakeoff } from "lucide-react";

export function Footer() {
  return (
    <div>
      <footer className="bg-background-light dark:bg-background-dark border-t border-gray-800 pt-16 pb-8">
        <div className="max-w-[1140px] mx-auto px-6">
          <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-5 gap-8 mb-12">
            <div className="col-span-2 lg:col-span-2">
              <a className="flex items-center space-x-2 mb-4" href="#">
                <div className="bg-primary p-1.5 rounded-lg text-white">
                  <PlaneTakeoff size={24} />
                </div>
                <span className="text-xl font-bold tracking-tight">
                  Travo
                </span>
              </a>
              <p className="text-slate-500 dark:text-slate-400 font-medium text-sm leading-relaxed mb-6 max-w-xs">
                Travo is the ultimate social platform for travelers. Connect,
                plan, and share your adventures with a global community.
              </p>
              <div className="flex space-x-4">
                {/* Facebook */}
                <a
                  href="#"
                  className="group p-2 rounded-full bg-[#1877F2]/10 hover:bg-[#1877F2] transition-all duration-300"
                >
                  <IconBrandFacebook
                    size={20}
                    className="text-[#1877F2] group-hover:text-white transition-colors"
                  />
                </a>

                {/* Instagram */}
                <a
                  href="#"
                  className="group p-2 rounded-full bg-gradient-to-tr from-[#f58529]/20 via-[#dd2a7b]/20 to-[#8134af]/20 hover:from-[#f58529] hover:via-[#dd2a7b] hover:to-[#8134af] transition-all duration-300"
                >
                  <IconBrandInstagram
                    size={20}
                    className="text-[#E1306C] group-hover:text-white transition-colors"
                  />
                </a>

                {/* Twitter */}
                <a
                  href="#"
                  className="group p-2 rounded-full bg-[#1DA1F2]/10 hover:bg-[#1DA1F2] transition-all duration-300"
                >
                  <IconBrandTwitter
                    size={20}
                    className="text-[#1DA1F2] group-hover:text-white transition-colors"
                  />
                </a>

                {/* Email */}
                <a
                  href="#"
                  className="group p-2 rounded-full bg-primary/10 hover:bg-primary transition-all duration-300"
                >
                  <MailCheck
                    size={20}
                    className="text-primary group-hover:text-white transition-colors"
                  />
                </a>
              </div>
            </div>
            <div className="">
              <h4 className="text-slate-500 dark:text-slate-400 font-bold mb-4">Company</h4>
              <ul className="space-y-2 text-sm text-gray-400">
                <li>
                  <a className="hover:text-primary transition-colors text-slate-500 dark:text-slate-400 font-medium" href="#">
                    About Us
                  </a>
                </li>
                <li>
                  <a className="hover:text-primary transition-colors text-slate-500 dark:text-slate-400 font-medium" href="#">
                    Careers
                  </a>
                </li>
                <li>
                  <a className="hover:text-primary transition-colors text-slate-500 dark:text-slate-400 font-medium" href="#">
                    Press
                  </a>
                </li>
              </ul>
            </div>
            <div>
              <h4 className=" font-bold mb-4 text-slate-500 dark:text-slate-400">Community</h4>
              <ul className="space-y-2 text-sm text-gray-400">
                <li>
                  <a className="hover:text-primary transition-colors text-slate-500 dark:text-slate-400 font-medium" href="#">
                    Safety Center
                  </a>
                </li>
                <li>
                  <a class="hover:text-primary transition-colors text-slate-500 dark:text-slate-400 font-medium" href="#">
                    Community Guidelines
                  </a>
                </li>
                <li>
                  <a className="hover:text-primary transition-colors text-slate-500 dark:text-slate-400 font-medium" href="#">
                    Success Stories
                  </a>
                </li>
                <li>
                  <a className="hover:text-primary transition-colors text-slate-500 dark:text-slate-400 font-medium" href="#">
                    Ambassadors
                  </a>
                </li>
              </ul>
            </div>
            <div>
              <h4 className=" font-bold mb-4 text-slate-500 dark:text-slate-400">Legal</h4>
              <ul className="space-y-2 text-sm text-gray-400">
                <li>
                  <a className="hover:text-primary transition-colors text-slate-500 dark:text-slate-400 font-medium" href="#">
                    Privacy Policy
                  </a>
                </li>
                <li>
                  <a className="hover:text-primary transition-colors text-slate-500 dark:text-slate-400 font-medium" href="#">
                    Terms of Service
                  </a>
                </li>
                <li>
                  <a className="hover:text-primary transition-colors text-slate-500 dark:text-slate-400 font-medium" href="#">
                    Cookie Policy
                  </a>
                </li>
                <li>
                  <a className="hover:text-primary transition-colors text-slate-500 dark:text-slate-400 font-medium" href="#">
                    Contact Us
                  </a>
                </li>
              </ul>
            </div>
          </div>
          <div className="border-t-2 border-gray-800 pt-8 flex flex-col md:flex-row justify-between items-center text-sm  text-slate-500 dark:text-slate-400 font-medium">
            <p>© 2026 Travo Inc. All rights reserved.</p>
            <div className="flex space-x-6 mt-4 md:mt-0">
              <span className="flex items-center gap-1 text-slate-500 dark:text-slate-400 font-medium">
                <span className="w-2 h-2 rounded-full bg-green-500"></span> Systems
                Operational
              </span>
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
}
