import React from "react";
import { useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";
import { useParams } from "react-router-dom";
import EditProfile from "./EditProfile";
import { Badge } from "@/components/ui/badge";
import {
  MapPin,
  Star,
  PlaneTakeoff,
  Camera,
  MoreHorizontal,
  MessageSquare,
  UserPlus,
  Backpack,
  Heart,
  Share2,
  Bookmark,
} from "lucide-react";
const Profile = () => {
  const navigate = useNavigate();

  const auth = useSelector((store) => store.auth);
  const { userId } = useParams();

  const stats = [
    { label: "Countries", value: 28 },
    { label: "Cities", value: 142 },
    { label: "Miles", value: "42k", icon: true },
  ];
  
  return (
    <div className="max-w-7xl mx-auto -mt-8 -mx-4 md:-mx-8">
      {/* Cover Header */}
      <div className="h-64 md:h-80 w-full relative overflow-hidden">
        <img
          src="https://lh3.googleusercontent.com/aida-public/AB6AXuAkCbBFkBQ-_jTAH9PpV_gRb4sgR0c3ppvQgilIKZb74CTBZVc6SfBs4YearIdpy-8UNFTLcO0BNlyz5Bys7i5heiRLIOFuLuuAazCvUkuew0IOsdpsKLsgUquTYIflsY1a8cnGlX5DNklqs8_SfsQQx91KV-T-Im7M-d-Pbs0GhUcmeVENYSkfuJsmp7AnMUrnU2C102zRfsCs7EB97kWL9-7k337O5AJnSsdPoGpKmphpDOZ8-h6z2TeBTmgeSfvSgt-5limD8j8K"
          className="w-full h-full object-cover"
          alt="Cover"
        />
        <div className="absolute inset-0 bg-gradient-to-t from-background-dark via-background-dark/30 to-transparent" />
        <button className="absolute top-4 right-4 bg-black/40 backdrop-blur-md text-white px-4 py-2 rounded-xl text-xs font-bold flex items-center gap-2 border border-white/10 hover:bg-black/60">
          <Camera size={16} /> Change Cover
        </button>
      </div>

      {/* Profile Info Overlay */}
      <div className="px-4 md:px-12 relative -mt-20">
        <div className="flex flex-col lg:flex-row items-end gap-6 pb-6 border-b dark:border-slate-800">
          <div className="relative">
            <img
              src={auth?.user?.profilePic}
              className="w-32 h-32 md:w-40 md:h-40 rounded-full border-4 border-background-light dark:border-background-dark object-cover shadow-2xl"
              alt="Alex"
            />
            <div className="absolute bottom-2 right-2 w-8 h-8 bg-green-500 border-4 border-background-light dark:border-background-dark rounded-full" />
          </div>

          <div className="flex-1 text-center lg:text-left pt-4 lg:pt-0">
            <div className="flex flex-col lg:flex-row lg:items-center gap-2 lg:gap-4 mb-3">
              <h1 className="text-3xl font-extrabold text-orange-500">
                {auth?.user?.name}
              </h1>
              <span className="bg-green-500/15 text-green-400 px-3 py-1 rounded-full text-[10px] font-bold uppercase tracking-widest flex items-center gap-1 self-center lg:self-auto border border-green-500">
                Verified
              </span>
            </div>
            <div className="flex flex-wrap justify-center lg:justify-start gap-5 text-slate-500 text-sm mb-4">
              <div className="flex items-center gap-1.5">
                <MapPin size={16} className="text-primary" />
                {auth?.user?.city}
                {", "}
                {auth?.user?.country}
              </div>
              <div className="flex items-center gap-1.5">
                <Star size={16} className="text-yellow-500 fill-current" />{" "}
                <span className="font-bold text-slate-900 dark:text-white">
                  4.9
                </span>{" "}
                (124 Reviews)
              </div>
              <div className="flex items-center gap-1.5">
                <PlaneTakeoff size={16} className="text-primary" />{" "}
                {auth?.user?.tripsCount} Trips
              </div>
            </div>
            <p className="text-slate-500 max-w-2xl text-sm leading-relaxed mx-auto lg:mx-0">
              {auth?.user?.bio ||
                "Travel enthusiast exploring the world one adventure at a time. Sharing my experiences and tips for fellow wanderers."}
            </p>
            <p className="text-slate-500 max-w-2xl text-sm leading-relaxed mx-auto lg:mx-0">
              {/* {auth?.user?.preferences?.map((pref, index) => (
                // <span key={index} className="mr-2">
                //   {pref}
                // </span>
                <Badge
                  key={index}
                  variant="outline"
                  className="border-orange-500 rounded-full mr-2"
                >
                  {pref}
                </Badge>
              ))} */}
            </p>
          </div>

          {auth?.user?.userId != userId && (
            <div className="flex gap-3 w-full lg:w-auto mt-4">
              <button className="flex-1 lg:flex-none bg-primary hover:bg-primary-hover text-white px-8 py-3 rounded-xl font-bold shadow-xl shadow-primary/20 transition-all flex items-center justify-center gap-2">
                <UserPlus size={20} /> Connect
              </button>

              <button
                className="px-6 py-3 bg-slate-100 dark:bg-slate-800 rounded-xl font-bold hover:bg-slate-200 transition-all"
                onClick={() => navigate("/messages")}
              >
                Message
              </button>
            </div>
          )}
          {auth?.user?.userId == userId && <EditProfile />}
        </div>

        {/* Tabs */}
        <div className="flex items-center gap-8 overflow-x-auto pt-2 pb-4 border-b dark:border-slate-800/50 mb-8 scrollbar-hide">
          {["Upcoming Trips", "Posts", "Photos", "Reviews", "Connections"].map(
            (tab, i) => (
              <button
                key={tab}
                className={`pb-3 text-sm font-bold whitespace-nowrap transition-all border-b-2 ${i === 0 ? "border-primary text-primary" : "border-transparent text-slate-500 hover:text-slate-300"}`}
              >
                {tab} {i === 0 && <span className="ml-1 opacity-50">2</span>}
              </button>
            ),
          )}
        </div>

        {/* Layout */}
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
          {/* Main Content */}
          <div className="lg:col-span-8 space-y-8">
            <div className="bg-white dark:bg-surface-darker rounded-2xl overflow-hidden border dark:border-slate-800 shadow-sm">
              <div className="p-5 border-b dark:border-slate-800 flex justify-between items-center bg-slate-50/50 dark:bg-slate-900/50">
                <h2 className="text-lg font-bold flex items-center gap-2">
                  <Backpack size={20} className="text-primary" /> Next Adventure
                </h2>
                <span className="text-[10px] font-bold text-slate-500 uppercase tracking-widest">
                  Created 2 days ago
                </span>
              </div>
              <div className="flex flex-col sm:flex-row">
                <div className="sm:w-2/5 relative h-52 sm:h-auto">
                  <img
                    src="https://picsum.photos/seed/bali-trip/400/500"
                    className="w-full h-full object-cover"
                    alt="Bali"
                  />
                  <div className="absolute top-3 left-3 bg-black/60 backdrop-blur-md text-white px-3 py-1 rounded-lg text-[10px] font-bold">
                    OCT 12 - OCT 25
                  </div>
                </div>
                <div className="sm:w-3/5 p-6 flex flex-col justify-between">
                  <div>
                    <div className="flex justify-between items-start mb-3">
                      <h3 className="text-2xl font-bold">
                        Backpacking in Bali
                      </h3>
                      <span className="bg-green-500/10 text-green-500 px-2 py-0.5 rounded text-[10px] font-bold uppercase tracking-widest">
                        Planning
                      </span>
                    </div>
                    <p className="text-sm text-slate-500 mb-6 leading-relaxed">
                      Planning a 2-week trip exploring Ubud, Canggu, and the
                      Nusa islands. Looking for 2-3 travel buddies who enjoy
                      hiking, yoga, and street food!
                    </p>
                    <div className="flex items-center gap-3 mb-6">
                      <div className="flex -space-x-3">
                        {[1, 2].map((i) => (
                          <img
                            key={i}
                            src={`https://picsum.photos/seed/u${i}/100/100`}
                            className="w-8 h-8 rounded-full border-2 border-white dark:border-slate-900"
                            alt="p"
                          />
                        ))}
                      </div>
                      <span className="text-[10px] font-bold text-slate-500 uppercase">
                        +1 other interested
                      </span>
                    </div>
                  </div>
                  <div className="flex gap-3">
                    <button className="flex-1 bg-primary hover:bg-primary-hover text-white py-3 rounded-xl font-bold shadow-xl shadow-primary/20 transition-all">
                      Request to Join
                    </button>
                    <button className="px-4 py-3 bg-slate-100 dark:bg-slate-800 rounded-xl hover:bg-slate-200 transition-all">
                      <Bookmark size={20} />
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <div className="bg-white dark:bg-surface-darker rounded-2xl p-6 border dark:border-slate-800 shadow-sm">
              <div className="flex items-center justify-between mb-6">
                <div className="flex items-center gap-3">
                  <img
                    src="https://picsum.photos/seed/me/100/100"
                    className="w-10 h-10 rounded-full"
                    alt="avatar"
                  />
                  <div>
                    <h4 className="font-bold text-sm">Alex Roamer</h4>
                    <p className="text-[10px] text-slate-500 font-bold uppercase tracking-widest">
                      5 hours ago • Kyoto, Japan
                    </p>
                  </div>
                </div>
                <MoreHorizontal size={20} className="text-slate-400" />
              </div>
              <p className="text-sm leading-relaxed mb-6">
                The hidden gems of Kyoto were unbelievable. Stumbled upon this
                quiet shrine just off the main path in Arashiyama. The bamboo
                forest at sunrise is something everyone needs to experience at
                least once! 🎋⛩️
              </p>
              <div className="grid grid-cols-2 gap-2 mb-6 rounded-2xl overflow-hidden">
                <img
                  src="https://picsum.photos/seed/kyoto1/600/400"
                  className="w-full h-56 object-cover"
                  alt="k"
                />
                <div className="grid grid-rows-2 gap-2">
                  <img
                    src="https://picsum.photos/seed/kyoto2/400/300"
                    className="w-full h-full object-cover"
                    alt="k"
                  />
                  <div className="relative">
                    <img
                      src="https://picsum.photos/seed/kyoto3/400/300"
                      className="w-full h-full object-cover"
                      alt="k"
                    />
                    <div className="absolute inset-0 bg-black/60 flex items-center justify-center text-white text-2xl font-bold">
                      +5
                    </div>
                  </div>
                </div>
              </div>
              <div className="flex items-center justify-between pt-6 border-t dark:border-slate-800">
                <div className="flex gap-8">
                  <button className="flex items-center gap-2 text-slate-500 hover:text-red-500 transition-colors group">
                    <Heart size={20} className="group-hover:fill-current" />
                    <span className="text-xs font-bold">243</span>
                  </button>
                  <button className="flex items-center gap-2 text-slate-500 hover:text-primary transition-colors">
                    <MessageSquare size={20} />
                    <span className="text-xs font-bold">18</span>
                  </button>
                  <button className="flex items-center gap-2 text-slate-500 hover:text-primary transition-colors">
                    <Share2 size={20} />
                  </button>
                </div>
                <Bookmark size={20} className="text-slate-400" />
              </div>
            </div>
          </div>

          {/* Sidebar */}
          <div className="lg:col-span-4 space-y-8">
            <div className="bg-white dark:bg-surface-darker rounded-2xl p-6 border dark:border-slate-800 shadow-sm">
              <h3 className="text-xs font-bold text-slate-500 uppercase tracking-widest mb-6">
                Travel Stats
              </h3>
              <div className="grid grid-cols-2 gap-4">
                {stats.slice(0, 2).map((s) => (
                  <div
                    key={s.label}
                    className="bg-slate-50 dark:bg-slate-900/50 p-4 rounded-xl text-center border dark:border-slate-800"
                  >
                    <span className="block text-2xl font-extrabold text-primary mb-1">
                      {s.value}
                    </span>
                    <span className="text-[10px] font-bold text-slate-500 uppercase tracking-widest">
                      {s.label}
                    </span>
                  </div>
                ))}
                <div className="col-span-2 bg-slate-50 dark:bg-slate-900/50 p-4 rounded-xl flex items-center justify-between px-8 border dark:border-slate-800">
                  <div>
                    <span className="block text-xl font-extrabold mb-1">
                      42k
                    </span>
                    <span className="text-[10px] font-bold text-slate-500 uppercase tracking-widest">
                      Miles
                    </span>
                  </div>
                  <PlaneTakeoff
                    size={32}
                    className="text-slate-300 dark:text-slate-700"
                  />
                </div>
              </div>
            </div>

            <div className="bg-white dark:bg-surface-darker rounded-2xl p-6 border dark:border-slate-800 shadow-sm">
              <div className="flex justify-between items-center mb-6">
                <h3 className="text-xs font-bold text-slate-500 uppercase tracking-widest">
                  Interests
                </h3>
                <button className="text-[10px] font-bold text-primary hover:underline uppercase tracking-widest">
                  View All
                </button>
              </div>
              <div className="flex flex-wrap gap-2">
                {auth.user?.preferences.map((i) => (
                  <span
                    key={i}
                    className="px-3 py-1.5 bg-primary/10 text-primary border border-primary/20 rounded-full text-[10px] font-bold uppercase tracking-widest"
                  >
                    {i}
                  </span>
                ))}
              </div>
            </div>

            <div className="bg-white dark:bg-surface-darker rounded-2xl p-6 border dark:border-slate-800 shadow-sm">
              <div className="flex justify-between items-center mb-6">
                <h3 className="text-xs font-bold text-slate-500 uppercase tracking-widest">
                  Recent Photos
                </h3>
                <button className="text-[10px] font-bold text-primary hover:underline uppercase tracking-widest">
                  See All
                </button>
              </div>
              <div className="grid grid-cols-3 gap-2">
                {[1, 2, 3, 4, 5].map((i) => (
                  <img
                    key={i}
                    src={`https://picsum.photos/seed/photo${i}/200/200`}
                    className="aspect-square rounded-lg object-cover hover:opacity-80 transition-opacity cursor-pointer"
                    alt="p"
                  />
                ))}
                <div className="aspect-square bg-slate-100 dark:bg-slate-800 rounded-lg flex items-center justify-center text-[10px] font-bold text-slate-500 cursor-pointer">
                  +124
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
export default Profile;
