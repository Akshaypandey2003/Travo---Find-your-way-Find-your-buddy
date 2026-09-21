import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  Map as MapIcon,
  Plus,
  Filter,
  X,
  CheckCircle2,
  MapPin,
} from "lucide-react";
import useUserData from "../CustomHooks/useUserData";
import { useEffect } from "react";
import { useSelector, shallowEqual } from "react-redux";
import useFriendRequest from "../CustomHooks/useFriendRequest";
const Explore = () => {
  const navigate = useNavigate();

  const { getAllUsers } = useUserData();

  const usersList = useSelector((store) => store.auth?.usersList, shallowEqual);

  const {sendFriendRequest} = useFriendRequest();

  const usersNextPageToken = useSelector(
    (store) => store.auth?.nextPageToken,
    shallowEqual,
  );

  console.log("All users list inside explore page: ", usersList);
  console.log("User next page token: ", usersNextPageToken);

  useEffect(() => {
    getAllUsers();
  }, []);

  const travelers = [
    {
      id: "1",
      name: "Sarah Jenkins",
      loc: "Bali, Indonesia",
      bio: "Digital nomad exploring SE Asia. Looking for hiking buddies for Mount Batur!",
      tags: ["#SoloTravel", "#Hiking"],
      verified: true,
      avatar: "https://picsum.photos/seed/sarah/200/200",
      gradient: "from-primary/20",
    },
    {
      id: "2",
      name: "Marcus Chen",
      loc: "Kyoto, Japan",
      bio: "Foodie & Architect. I organize architecture tours in Kyoto. Let's grab sushi! 🍣",
      tags: ["#Foodie", "#Architecture"],
      verified: false,
      avatar: "https://picsum.photos/seed/marcus/200/200",
      gradient: "from-orange-500/10",
    },
    {
      id: "3",
      name: "Elena Rodriguez",
      loc: "Barcelona, Spain",
      bio: "Traveling through Europe this summer. Planning a trip to Rome next month.",
      tags: ["#EuropeTrip", "#Summer"],
      verified: false,
      avatar: "https://picsum.photos/seed/elena2/200/200",
      gradient: "from-amber-500/10",
    },
    {
      id: "4",
      name: "Jessica Lee",
      loc: "New York, USA",
      bio: "Photographer chasing sunsets. Currently planning a road trip across the west coast!",
      tags: ["#RoadTrip", "#WestCoast"],
      verified: true,
      avatar: "https://picsum.photos/seed/jess/200/200",
      gradient: "from-primary/10",
    },
  ];
  const trips = [
    {
      id: "t1",
      title: "Bali Backpacking Loop",
      loc: "Indonesia",
      desc: "7-day hostel and volcano plan for budget travelers.",
      tags: ["#Budget", "#Backpacking"],
    },
    {
      id: "t2",
      title: "Kyoto Culture Trail",
      loc: "Japan",
      desc: "Temples, tea houses, and hidden alley food spots.",
      tags: ["#Culture", "#Foodie"],
    },
    {
      id: "t3",
      title: "Iceland Ring Road",
      loc: "Iceland",
      desc: "Self-drive plan with waterfalls, hot springs, and aurora stops.",
      tags: ["#RoadTrip", "#Nature"],
    },
    {
      id: "t4",
      title: "Andes Adventure",
      loc: "Peru",
      desc: "Group plan for hiking routes and scenic mountain camps.",
      tags: ["#Adventure", "#Hiking"],
    },
  ];
  const destinations = [
    {
      id: "d1",
      title: "Santorini",
      loc: "Greece",
      desc: "Best sunsets, cliff stays, and island hopping routes.",
      tags: ["#Island", "#Sunset"],
    },
    {
      id: "d2",
      title: "Queenstown",
      loc: "New Zealand",
      desc: "Adrenaline capital for bungee, hikes, and alpine views.",
      tags: ["#Adventure", "#Mountains"],
    },
    {
      id: "d3",
      title: "Chefchaouen",
      loc: "Morocco",
      desc: "Blue streets, local art markets, and cozy riads.",
      tags: ["#CityWalk", "#Photography"],
    },
    {
      id: "d4",
      title: "Banff",
      loc: "Canada",
      desc: "Lakeside trails, glacier drives, and wildlife sightings.",
      tags: ["#Lakes", "#Trails"],
    },
  ];
  const blogs = [
    {
      id: "b1",
      title: "How I Traveled 5 Countries in 30 Days",
      loc: "Europe",
      desc: "Route strategy, budget split, and mistakes to avoid.",
      tags: ["#Guide", "#Budget"],
    },
    {
      id: "b2",
      title: "Remote Work Setup from Bali Cafes",
      loc: "Bali",
      desc: "Wi-Fi spots, quiet corners, and weekly routine tips.",
      tags: ["#DigitalNomad", "#Productivity"],
    },
    {
      id: "b3",
      title: "Japan Rail Pass: Worth It or Not?",
      loc: "Japan",
      desc: "A real cost comparison by route and season.",
      tags: ["#Transport", "#Tips"],
    },
    {
      id: "b4",
      title: "Solo Travel Safety Checklist",
      loc: "Global",
      desc: "Practical prep list used before every trip.",
      tags: ["#SoloTravel", "#Safety"],
    },
  ];
  const exploreTabs = [
    {
      value: "people",
      label: "People",
      cta: "Load More Travelers",
      disabled: !usersNextPageToken,
      onClick: () => {
        if (usersNextPageToken) {
          handleLoadMoreTravelers();
        }
      },
      content: (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-8">
          {usersList?.map((user) => (
            <div
              key={user?.userId}
              className="bg-white dark:bg-surface-dark rounded-3xl p-8 border border-slate-100 dark:border-gray-900 hover:-translate-y-2 hover:shadow-2xl hover:shadow-primary/10 transition-all duration-500 group flex flex-col items-center text-center relative overflow-hidden"
            >
              <div
                className={`absolute top-0 left-0 w-full h-32 bg-gradient-to-b to-transparent opacity-40`}
              />
              <div
                className="relative mt-2"
                onClick={() => navigate(`/profile/${user?.userId}`)}
              >
                <div className="w-28 h-28 rounded-full p-1.5 bg-gradient-to-tr from-primary to-orange-300 shadow-2xl cursor-pointer">
                  <img
                    src={user?.profilePic}
                    className="w-full h-full rounded-full object-cover border-4 border-white dark:border-surface-dark"
                    alt={user?.name}
                  />
                </div>
                <div className="absolute bottom-2 right-2 w-6 h-6 bg-emerald-500 border-4 border-white dark:border-surface-dark rounded-full shadow-lg" />
              </div>
              <div className="mt-6 mb-2">
                <h3 className="text-xl font-extrabold tracking-tight flex items-center justify-center gap-2 group-hover:text-primary transition-colors">
                  {user?.name}
                  {user?.verified && (
                    <CheckCircle2 size={18} className="text-primary" />
                  )}
                </h3>
                <div className="flex items-center justify-center gap-1.5 text-slate-500 dark:text-slate-400 text-[10px] font-bold uppercase tracking-widest mt-1.5">
                  <MapPin size={12} className="text-primary" />{" "}
                  {user?.city + ", " + user?.country}
                </div>
              </div>
              <p className="text-slate-600 dark:text-slate-400 text-sm leading-relaxed mb-6 line-clamp-2 px-4 font-medium italic">
                "{user?.bio}"
              </p>
              <div className="flex flex-wrap justify-center gap-2 mb-8">
                {user?.preferences.map((tag) => (
                  <span
                    key={tag}
                    className="px-3 py-1.5 rounded-lg bg-slate-100 dark:bg-surface-lighter text-[9px] text-slate-500 dark:text-slate-400 font-extrabold uppercase tracking-widest"
                  >
                    {tag}
                  </span>
                ))}
              </div>
              <div className="w-full grid grid-cols-2 gap-4 mt-auto">
                <button className="py-3 rounded-xl bg-primary text-white text-[10px] font-bold uppercase tracking-widest shadow-lg shadow-primary/20 hover:bg-primary-hover transition-all"
                onClick={()=>sendFriendRequest(user?.userId)}
                >
                  Follow
                </button>
                <button
                  className="py-3 rounded-xl bg-slate-50 dark:bg-surface-lighter text-slate-900 dark:text-white text-[10px] font-bold uppercase tracking-widest border border-slate-100 dark:border-gray-800 hover:border-primary transition-all"
                  onClick={() => navigate("/messages")}
                >
                  Message
                </button>
              </div>
            </div>
          ))}
        </div>
      ),
    },
    {
      value: "trips",
      label: "Trips",
      cta: "Load More Trips",
      content: (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-8">
          {trips.map((trip) => (
            <div
              key={trip.id}
              className="bg-white dark:bg-surface-dark rounded-3xl p-8 border border-slate-100 dark:border-gray-900 hover:-translate-y-2 hover:shadow-2xl hover:shadow-primary/10 transition-all duration-500"
            >
              <h3 className="text-xl font-extrabold tracking-tight text-slate-900 dark:text-white mb-2">
                {trip.title}
              </h3>
              <p className="flex items-center gap-1.5 text-slate-500 dark:text-slate-400 text-[10px] font-bold uppercase tracking-widest mb-4">
                <MapPin size={12} className="text-primary" />
                {trip.loc}
              </p>
              <p className="text-slate-600 dark:text-slate-400 text-sm leading-relaxed mb-6 font-medium italic">
                "{trip.desc}"
              </p>
              <div className="flex flex-wrap gap-2">
                {trip.tags.map((tag) => (
                  <span
                    key={tag}
                    className="px-3 py-1.5 rounded-lg bg-slate-100 dark:bg-surface-lighter text-[9px] text-slate-500 dark:text-slate-400 font-extrabold uppercase tracking-widest"
                  >
                    {tag}
                  </span>
                ))}
              </div>
            </div>
          ))}
        </div>
      ),
    },
    {
      value: "destinations",
      label: "Destinations",
      cta: "Load More Destinations",
      content: (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-8">
          {destinations.map((destination) => (
            <div
              key={destination.id}
              className="bg-white dark:bg-surface-dark rounded-3xl p-8 border border-slate-100 dark:border-gray-900 hover:-translate-y-2 hover:shadow-2xl hover:shadow-primary/10 transition-all duration-500"
            >
              <h3 className="text-xl font-extrabold tracking-tight text-slate-900 dark:text-white mb-2">
                {destination.title}
              </h3>
              <p className="flex items-center gap-1.5 text-slate-500 dark:text-slate-400 text-[10px] font-bold uppercase tracking-widest mb-4">
                <MapPin size={12} className="text-primary" />
                {destination.loc}
              </p>
              <p className="text-slate-600 dark:text-slate-400 text-sm leading-relaxed mb-6 font-medium italic">
                "{destination.desc}"
              </p>
              <div className="flex flex-wrap gap-2">
                {destination.tags.map((tag) => (
                  <span
                    key={tag}
                    className="px-3 py-1.5 rounded-lg bg-slate-100 dark:bg-surface-lighter text-[9px] text-slate-500 dark:text-slate-400 font-extrabold uppercase tracking-widest"
                  >
                    {tag}
                  </span>
                ))}
              </div>
            </div>
          ))}
        </div>
      ),
    },
    {
      value: "blogs",
      label: "Blogs",
      cta: "Load More Blogs",
      content: (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-8">
          {blogs.map((blog) => (
            <div
              key={blog.id}
              className="bg-white dark:bg-surface-dark rounded-3xl p-8 border border-slate-100 dark:border-gray-900 hover:-translate-y-2 hover:shadow-2xl hover:shadow-primary/10 transition-all duration-500"
            >
              <h3 className="text-xl font-extrabold tracking-tight text-slate-900 dark:text-white mb-2">
                {blog.title}
              </h3>
              <p className="flex items-center gap-1.5 text-slate-500 dark:text-slate-400 text-[10px] font-bold uppercase tracking-widest mb-4">
                <MapPin size={12} className="text-primary" />
                {blog.loc}
              </p>
              <p className="text-slate-600 dark:text-slate-400 text-sm leading-relaxed mb-6 font-medium italic">
                "{blog.desc}"
              </p>
              <div className="flex flex-wrap gap-2">
                {blog.tags.map((tag) => (
                  <span
                    key={tag}
                    className="px-3 py-1.5 rounded-lg bg-slate-100 dark:bg-surface-lighter text-[9px] text-slate-500 dark:text-slate-400 font-extrabold uppercase tracking-widest"
                  >
                    {tag}
                  </span>
                ))}
              </div>
            </div>
          ))}
        </div>
      ),
    },
  ];

  return (
    <div className="max-w-7xl mx-auto">
      {/* Header */}
      <div className="mb-12 mt-4 relative">
        <div className="flex flex-col md:flex-row md:items-end justify-between gap-8">
          <div>
            <h5 className="text-primary font-bold tracking-[0.2em] uppercase text-[10px] mb-3">
              Discover Community
            </h5>
            <h1 className="text-4xl md:text-5xl font-extrabold text-slate-900 dark:text-white mb-4 tracking-tight">
              Explore{" "}
              <span className="text-transparent bg-clip-text bg-gradient-to-r from-primary to-orange-500">
                Connections
              </span>
            </h1>
            <p className="text-slate-500 dark:text-slate-400 text-lg max-w-2xl font-medium leading-relaxed">
              Find your next travel buddy, get inspired by local guides, or
              simply make friends across the globe.
            </p>
          </div>
          <div className="flex items-center gap-4">
            <button className="bg-white dark:bg-surface-dark border border-slate-100 dark:border-gray-900 px-6 py-3 rounded-xl text-slate-700 dark:text-white text-xs font-bold uppercase tracking-widest hover:border-primary transition-all flex items-center gap-3">
              <MapIcon size={18} className="text-primary" />
              <span>Map View</span>
            </button>
            <button
              className="bg-primary hover:bg-primary-hover text-white px-6 py-3 rounded-xl text-xs font-bold uppercase tracking-widest transition-all shadow-xl shadow-primary/25 flex items-center gap-3"
              onClick={() => navigate("/trips/create")}
            >
              <Plus size={18} />
              <span>Post Trip</span>
            </button>
          </div>
        </div>
      </div>

      <TabsComponent exploreTabs={exploreTabs} />
    </div>
  );
};
const TabsComponent = ({ exploreTabs }) => {
  const [activeTab, setActiveTab] = useState(exploreTabs[0].value);
  return (
    <>
      {/* Tabs & Filters */}
      <div className="sticky top-20 z-30 bg-background-light/95 dark:bg-background-dark/95 backdrop-blur-md pt-2 pb-6 mb-10 border-b border-slate-100 dark:border-gray-900">
        <div className="flex flex-col gap-6">
          <div className="flex space-x-1 bg-slate-100 dark:bg-surface-dark p-1.5 rounded-2xl w-fit h-auto">
            {exploreTabs.map((tab) => (
              <button
                key={tab.value}
                onClick={() => setActiveTab(tab.value)}
                className={`px-8 py-2.5 rounded-xl text-xs font-bold uppercase tracking-widest transition-all ${
                  activeTab === tab.value
                    ? "bg-primary text-white shadow-lg"
                    : "text-slate-500 dark:text-slate-400 hover:text-primary"
                }`}
              >
                {tab.label}
              </button>
            ))}
          </div>
          <div className="flex flex-wrap items-center gap-4">
            <div className="relative group">
              <select className="appearance-none bg-white dark:bg-surface-dark border border-slate-100 dark:border-gray-900 text-slate-700 dark:text-slate-300 pl-5 pr-12 py-2.5 rounded-xl text-xs font-bold uppercase tracking-widest cursor-pointer outline-none focus:ring-1 focus:ring-primary transition-all">
                <option>Recommended</option>
                <option>Nearby</option>
                <option>Newest</option>
              </select>
              <div className="absolute right-4 top-1/2 -translate-y-1/2 pointer-events-none text-slate-400 group-hover:text-primary">
                <Filter size={14} />
              </div>
            </div>
            <div className="h-6 w-px bg-slate-200 dark:bg-gray-800 mx-2 hidden sm:block" />
            <button className="px-4 py-2.5 rounded-full bg-primary/10 border border-primary/20 text-primary text-[10px] font-bold uppercase tracking-widest flex items-center gap-2">
              Traveling Now
              <X size={12} />
            </button>
            {["Solo Travelers", "Adventure", "Foodie"].map((chip) => (
              <button
                key={chip}
                className="px-4 py-2.5 rounded-full bg-slate-50 dark:bg-surface-dark border border-slate-100 dark:border-gray-900 text-slate-500 dark:text-slate-400 text-[10px] font-bold uppercase tracking-widest hover:text-primary transition-all"
              >
                {chip}
              </button>
            ))}
          </div>
        </div>
      </div>

      {exploreTabs.map((tab) => (
        <div
          key={tab.value}
          style={{ display: activeTab === tab.value ? "block" : "none" }}
        >
          {tab.content}
          <div className="mt-20 text-center pb-12">
            <button
              onClick={tab.onClick}
              disabled={tab.disabled}
              className={`px-12 py-4 rounded-2xl text-xs font-bold uppercase tracking-widest transition-all duration-300 border shadow-xl ${
                tab.disabled
                  ? "bg-slate-200 dark:bg-surface-dark text-slate-400 cursor-not-allowed opacity-60"
                  : "bg-slate-100 dark:bg-surface-dark text-slate-900 dark:text-white hover:bg-primary hover:text-white border-slate-200 dark:border-gray-800"
              }`}
            >
              {tab.cta}
            </button>
          </div>
        </div>
      ))}
    </>
  );
};
export default Explore;
