/* eslint-disable no-undef */
/* eslint-disable no-unused-vars */
"use client";

import { Button } from "@/components/ui/button";
import { Link, useNavigate } from "react-router-dom";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faMoon,
  faSun
} from "@fortawesome/free-solid-svg-icons";
import { persistor } from "../../Redux/Store";
import { shallowEqual, useDispatch, useSelector } from "react-redux";
import useAuth from "../../CustomHooks/useAuth";
import { changeTheme } from "../../Redux/Slices/authSlice";
import SideSheet from "../PageComponents/SideSheet";

const components = [
  {
    title: "Alert Dialog",
    href: "/docs/primitives/alert-dialog",
    description:
      "A modal dialog that interrupts the user with important content and expects a response.",
  },
  {
    title: "Hover Card",
    href: "/docs/primitives/hover-card",
    description:
      "For sighted users to preview content available behind a link.",
  },
  {
    title: "Progress",
    href: "/docs/primitives/progress",
    description:
      "Displays an indicator showing the completion progress of a task, typically displayed as a progress bar.",
  },
  {
    title: "Scroll-area",
    href: "/docs/primitives/scroll-area",
    description: "Visually or semantically separates content.",
  },
  {
    title: "Tabs",
    href: "/docs/primitives/tabs",
    description:
      "A set of layered sections of content—known as tab panels—that are displayed one at a time.",
  },
  {
    title: "Tooltip",
    href: "/docs/primitives/tooltip",
    description:
      "A popup that displays information related to an element when the element receives keyboard focus or the mouse hovers over it.",
  },
];

export const NavBar = () => {
  const dispatch = useDispatch();
  const userData = useSelector((store) => store.auth, shallowEqual);
  const profileStatus = useSelector(
    (store) => store.auth.profileStatus,
    shallowEqual
  );
  const notifications = useSelector(
    (store) => store.notifications,
    shallowEqual
  );
  console.log("Notifications", notifications?.notifications);
  const friendRequests = Array.isArray(notifications?.notifications)
  ? notifications.notifications.filter(item => item.type === "FRIEND_REQUEST")
  : [];

  const likes = Array.isArray(notifications?.notifications)
  ? notifications.notifications.filter(item => item.type === "LIKE")
  : [];

  const navigate = useNavigate();
  
  return (
    <div className="w-[100vw]  flex justify-between items-center py-2 px-20">
      <div className="flex gap-4 items-center">
        <div className="w-20 h-20">
          <img src="../AppLogo.png" alt="" className="w-52" />
        </div>
        <h1 className="text-3xl font-bold">Travo</h1>
      </div>
      <div className="navbar-menu">
        <ul className="flex gap-20">
          <li className="cursor-pointer font-bold ">
            <Link to="/">Home</Link>
          </li>
          <li className="cursor-pointer font-bold">
            <Link to="/services">Services</Link>
          </li>
          <li className="cursor-pointer font-bold">
            <Link to="/about">About Us</Link>
          </li>
          <li className="cursor-pointer font-bold">
            <Link to="/destinations">Destinations</Link>
          </li>
          <li className="cursor-pointer font-bold">
            <Link to="/blogs/:id">Blogs</Link>
          </li>
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
          <SideSheet/>
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
