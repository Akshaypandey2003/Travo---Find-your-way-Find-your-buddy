import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  Heart,
  MessageCircle,
  Share2,
  UserPlus,
  MoreHorizontal,
  Camera,
  RefreshCw,
  TrendingUp,
} from "lucide-react";
import { useEffect, useRef, useCallback } from "react";
import useBlog from "../CustomHooks/useBlog";
import { useSelector, shallowEqual } from "react-redux";
import { useDispatch } from "react-redux";
import { setBlogsNextPageToken, addBlog } from "../Redux/Slices/blogsSlice";
import useNotificationsData from "../CustomHooks/useNotificationsData";
import BlogCard from "./BlogComponents/BlogCard";

const Dashboard = () => {
  const navigate = useNavigate();
  const [postText, setPostText] = useState("");
  const [feedPosts, setFeedPosts] = useState([]);
  const { getUserFeed } = useBlog();
  const dispatch = useDispatch();
  const scrollRef = useRef(null);
  const loadMoreRef = useRef(null);
  const { getAllNotifications } = useNotificationsData();
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(false);

  const currentBlogs = useSelector((store) => store.blog.blogs, shallowEqual);
  const blogNextPageToken = useSelector(
    (store) => store.blog.nextPageToken,
    shallowEqual,
  );

  console.log("All blogs received inside dashoard: ", currentBlogs);

  const loadMoreBlogs = useCallback(
    async (force = false) => {
      console.log("Loading is - ", loading);
      console.log("NExt page token is : ", blogNextPageToken);
      if (loading || (!blogNextPageToken && !force)) return;

      if (force && !blogNextPageToken) {
        dispatch(setBlogsNextPageToken(true));
      }

      setLoading(true);

      try {
        const data = await getUserFeed(page, 20);

        console.log("Blogs received in dashboard:", data);

        if (!data || data.length === 0) {
          dispatch(setBlogsNextPageToken(false));
          return;
        }

        dispatch(addBlog(data));
        setPage((prev) => prev + 1);
      } finally {
        setLoading(false);
      }
    },
    [page, loading, blogNextPageToken, getUserFeed, dispatch],
  );

  // ✅ Infinite scroll observer (ONLY this observer in page)
  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting) {
          loadMoreBlogs();
        }
      },
      {
        root: scrollRef.current?.querySelector(
          "[data-radix-scroll-area-viewport]",
        ),
        threshold: 1.0,
      },
    );

    if (loadMoreRef.current) {
      observer.observe(loadMoreRef.current);
    }

    return () => observer.disconnect();
  }, [loadMoreBlogs]);

  // ✅ First load
  useEffect(() => {
    if (currentBlogs.length === 0) {
      loadMoreBlogs(true);
    }
  }, []);

  const posts = currentBlogs.map((item) => ({
    id: item.resourceId || item.eventId,
    user: {
      userId: item.authorId,
      name: item.authorName || "TRAVO traveler",
      avatar:
        item.authorProfilePic ||
        "https://picsum.photos/seed/travo-user/100/100",
    },
    time: item.createdAt
      ? new Date(item.createdAt).toLocaleString()
      : "Recently",
    content: item.caption || "Shared a travel update.",
    images: item.images || item.thumbnailUrl,
    likes: 0,
    comments: 0,
  }));

  return (
    <div className="flex flex-col xl:flex-row gap-8 max-w-7xl mx-auto">
      {/* Main Feed */}
      <div className="flex-1 space-y-6">
        {/* Post List */}
        {loading ? (
          <div className="bg-white dark:bg-surface-dark rounded-2xl p-8 text-center text-sm text-slate-500">
            Loading your feed...
          </div>
        ) : posts.length === 0 ? (
          <div className="bg-white dark:bg-surface-dark rounded-2xl p-8 text-center text-sm text-slate-500">
            No feed posts yet. Follow travelers or publish a blog to get
            started.
          </div>
        ) : (
          posts.map((post) => (
         <BlogCard key={post?.eventId} post={post} />
          ))
        )}
      </div>

      {/* Right Column Widgets */}
      <div className="xl:w-85 space-y-6 shrink-0">
        {/* Upcoming Trips */}
        <div className="bg-white dark:bg-surface-dark rounded-2xl shadow-sm border border-slate-100 dark:border-gray-900 p-6">
          <div className="flex items-center justify-between mb-6">
            <h3 className="font-extrabold text-xs uppercase tracking-widest text-slate-900 dark:text-white">
              Upcoming Trips
            </h3>
            <button
              className="text-primary text-[10px] font-bold uppercase tracking-widest hover:underline"
              onClick={() => navigate("/trips")}
            >
              See All
            </button>
          </div>
          <div className="space-y-5">
            {[
              {
                month: "Oct",
                day: "15",
                title: "Bali Yoga Retreat",
                loc: "UBUD, INDONESIA • 7 DAYS",
              },
              {
                month: "Dec",
                day: "02",
                title: "Swiss Alps Skiing",
                loc: "ZERMATT, SWITZERLAND • 5 DAYS",
              },
            ].map((trip, idx) => (
              <div
                key={idx}
                className="flex gap-4 group cursor-pointer"
                onClick={() => navigate("/trips/1")}
              >
                <div className="w-12 h-12 bg-primary/10 dark:bg-primary/5 rounded-xl flex flex-col items-center justify-center border border-primary/10 shrink-0 group-hover:bg-primary group-hover:border-primary transition-all duration-300">
                  <span className="text-[8px] text-primary group-hover:text-white font-extrabold uppercase tracking-widest">
                    {trip.month}
                  </span>
                  <span className="text-xl font-extrabold text-primary group-hover:text-white leading-none">
                    {trip.day}
                  </span>
                </div>
                <div className="min-w-0">
                  <h4 className="text-sm font-bold truncate group-hover:text-primary transition-colors tracking-tight">
                    {trip.title}
                  </h4>
                  <p className="text-[9px] text-slate-500 font-bold uppercase tracking-wider mt-0.5 truncate">
                    {trip.loc}
                  </p>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Suggested Travelers */}
        <div className="bg-white dark:bg-surface-dark rounded-2xl shadow-sm border border-slate-100 dark:border-gray-900 p-6">
          <div className="flex items-center justify-between mb-6">
            <h3 className="font-extrabold text-xs uppercase tracking-widest text-slate-900 dark:text-white">
              Discover
            </h3>
            <button className="text-slate-400 hover:text-primary transition-colors">
              <RefreshCw size={16} />
            </button>
          </div>
          <div className="space-y-5">
            {[
              {
                name: "Mike T.",
                role: "Digital Nomad",
                avatar: "https://picsum.photos/seed/mike/100/100",
              },
              {
                name: "Elena R.",
                role: "Backpacker",
                avatar: "https://picsum.photos/seed/elena/100/100",
              },
              {
                name: "David K.",
                role: "Photographer",
                avatar: "https://picsum.photos/seed/david/100/100",
              },
            ].map((user, idx) => (
              <div key={idx} className="flex items-center justify-between">
                <div
                  className="flex items-center gap-3 cursor-pointer"
                  onClick={() => navigate(`/profile/${user.userId}`)}
                >
                  <img
                    src={user.avatar}
                    className="w-10 h-10 rounded-full object-cover border-2 border-primary/5"
                    alt={user.name}
                  />
                  <div>
                    <h4 className="text-sm font-bold leading-tight tracking-tight">
                      {user.name}
                    </h4>
                    <p className="text-[9px] text-slate-500 uppercase tracking-[0.15em] font-extrabold mt-0.5">
                      {user.role}
                    </p>
                  </div>
                </div>
                <button className="w-8 h-8 rounded-full bg-primary/10 hover:bg-primary text-primary hover:text-white flex items-center justify-center transition-all">
                  <UserPlus size={16} />
                </button>
              </div>
            ))}
          </div>
        </div>

        {/* Trending Now */}
        <div className="bg-gradient-to-br from-primary to-orange-600 rounded-2xl shadow-2xl shadow-primary/20 p-6 text-white relative overflow-hidden">
          <div className="relative z-10">
            <div className="flex items-center gap-2 mb-3 opacity-90">
              <TrendingUp size={14} />
              <span className="text-[9px] font-extrabold uppercase tracking-[0.25em]">
                Trending Now
              </span>
            </div>
            <h3 className="text-2xl font-extrabold mb-2 tracking-tight">
              Explore Patagonia
            </h3>
            <p className="text-sm text-orange-50 mb-5 leading-relaxed font-medium">
              Join 45 other travelers heading south this winter season.
            </p>
            <button className="bg-white text-primary px-6 py-2.5 rounded-lg text-xs font-bold uppercase tracking-widest hover:bg-orange-50 transition-colors shadow-lg">
              View Guide
            </button>
          </div>
          <div className="absolute -right-6 -bottom-10 w-36 h-36 bg-white/10 rounded-full blur-2xl" />
          <div className="absolute top-0 right-0 w-24 h-24 bg-orange-400/20 rounded-full blur-xl" />
        </div>
      </div>
    </div>
  );
};
export default Dashboard;
