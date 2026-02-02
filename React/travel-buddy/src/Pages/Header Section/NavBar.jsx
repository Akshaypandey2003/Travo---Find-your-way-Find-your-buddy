/* eslint-disable no-undef */
/* eslint-disable no-unused-vars */
"use client";

import { Button } from "@/components/ui/button";
import { Link, useNavigate } from "react-router-dom";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faMoon, faSun } from "@fortawesome/free-solid-svg-icons";
import { persistor } from "../../Redux/Store";
import { shallowEqual, useDispatch, useSelector } from "react-redux";
import useAuth from "../../CustomHooks/useAuth";
import { changeTheme } from "../../Redux/Slices/authSlice";
import SideSheet from "../PageComponents/SideSheet";
import { useEffect, useState } from "react";

export const NavBar = () => {
  const dispatch = useDispatch();
  const userData = useSelector((store) => store.auth, shallowEqual);
  
  const profileStatus = useSelector(
    (store) => store.auth.profileStatus,
    shallowEqual,
  );
  const notifications = useSelector(
    (store) => store.notifications,
    shallowEqual,
  );
  const friendRequests = Array.isArray(notifications?.notifications)
    ? notifications.notifications.filter(
        (item) => item.type === "FRIEND_REQUEST",
      )
    : [];

  const likes = Array.isArray(notifications?.notifications)
    ? notifications.notifications.filter((item) => item.type === "LIKE")
    : [];

  const navigate = useNavigate();
  const [isVisible, setIsVisible] = useState(true);
  const [lastScrollY, setLastScrollY] = useState(0);
  useEffect(() => {
    const handleScroll = () => {
      const currentScrollY = window.scrollY;
      if (currentScrollY > lastScrollY && currentScrollY > 100) {
        setIsVisible(false); // Scroll down => hide
      } else {
        setIsVisible(true); // Scroll up => show
      }
      setLastScrollY(currentScrollY);
    };

    window.addEventListener("scroll", handleScroll);
    return () => window.removeEventListener("scroll", handleScroll);
  }, [lastScrollY]);
  return (
    <div
      className={`w-[100vw] flex justify-between items-center h-20 py-2 px-20 fixed top-0 left-0 right-0 z-50 transition-transform duration-300 ${
        isVisible ? "translate-y-0" : "-translate-y-full"
      } backdrop-blur-md dark:bg-orange-600/10`}
    >
      <div className="flex gap-4 items-center mt-4">
        <img
          src="https://res.cloudinary.com/dwg7vniow/image/upload/v1757867374/Travel_Logo_third_gge6hk.png"
          alt="Travo Logo"
          className="w-48 max-w-48 object-contain"
        />
        {/* <h1 className="text-3xl font-bold">Travo</h1> */}
      </div>
      <div className="navbar-menu">
        <ul className="flex gap-20">
          <li className="cursor-pointer font-bold">
            <Link
              to="/"
              className="text-inherit no-underline hover:text-inherit hover:no-underline"
            >
              Home
            </Link>
          </li>
          {/* <li className="cursor-pointer font-bold">
            <Link to="/services" className="text-inherit no-underline hover:text-inherit hover:no-underline">Services</Link>
          </li> */}
          <li className="cursor-pointer font-bold">
            <Link
              to="/about"
              className="text-inherit no-underline hover:text-inherit hover:no-underline"
            >
              About Us
            </Link>
          </li>
          {/* <li className="cursor-pointer font-bold">
            <Link to="/destinations" className="text-inherit no-underline hover:text-inherit hover:no-underline">Destinations</Link>
          </li> */}

          {userData?.user && (
            <li className="cursor-pointer font-bold">
              <Link
                to="/blogs/:id"
                className="text-inherit no-underline hover:text-inherit hover:no-underline"
              >
                Blogs
              </Link>
            </li>
          )}
        </ul>
      </div>
      {userData.user == null ? (
        <div className="flex gap-4">
          <Button
            onClick={() => navigate("/login")}
            className="bg-orange-600 hover:bg-orange-500 border-none font-semibold"
          >
            Login
          </Button>
          <Button
            onClick={() => navigate("/register")}
            variant="outline"
            className="border text-orange-400 font-semibold border-orange-400 hover:border-orange-600 hover:text-orange-600"
          >
            SignUp
          </Button>
          <p
            onClick={() => dispatch(changeTheme())}
            className="hover:cursor-pointer"
          >
            <FontAwesomeIcon
              icon={userData.theme === "dark" ? faSun : faMoon}
              className={`text-${
                userData.theme === "dark" ? "orange-500" : "blue-400"
              } text-2xl`}
            />
          </p>
        </div>
      ) : (
        <div className="flex gap-8 items-center">
          <SideSheet />
          <p
            onClick={() => dispatch(changeTheme())}
            className="hover:cursor-pointer"
          >
            <FontAwesomeIcon
              icon={userData.theme === "dark" ? faSun : faMoon}
              className={`text-${
                userData.theme === "dark" ? "orange-500" : "blue-400"
              } text-2xl`}
            />
          </p>
        </div>
      )}
    </div>
  );
};
export default NavBar;
