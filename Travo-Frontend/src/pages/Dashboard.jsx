import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Heart, MessageCircle, Share2, UserPlus, MoreHorizontal, Calendar, Camera, RefreshCw, TrendingUp } from 'lucide-react';
import { shallowEqual, useDispatch, useSelector } from "react-redux";
import { ScrollArea } from "@/components/ui/scroll-area";
import { useCallback, useEffect, useRef } from "react";
import useBlog from "../CustomHooks/useBlog";
import { addBlog, setBlogsNextPageToken } from "../Redux/Slices/blogsSlice";
import useNotificationsData from '../CustomHooks/useNotificationsData';

const Dashboard = () => {
    const navigate = useNavigate();
    const [postText, setPostText] = useState('');
    const posts = [
        {
            id: '1',
            user: { name: 'Marcus Chen', avatar: 'https://lh3.googleusercontent.com/aida-public/AB6AXuC46bdSJmeyhnGLQBrtyvgNnLwqeq7rFMhICgNhE08lGFCrLnfzl4yuCZgysAy9ilnTWgb3bsV7-n7SmS0A9c4OAP95iIecyDPhYdxHUxgz2c-wxV48CuR3MBJPrlJXVWQUDP0Y9sP_3o_eDQL2jnN1sq0sgJvxZvBY02qD7wvVUScbOrFCiVg-dPXCuT6XTMPw0ydeya7eObOTTZczfMthsNtDlAcetWxf9XeWm4ilkHBk7d0t7KNzRKobJuIs0BH7CktODB6iLZaY', location: 'Kyoto, Japan' },
            time: '2h ago',
            content: 'Just arrived in Kyoto! The autumn colors are starting to show and it\'s absolutely magical. Looking for someone to grab dinner with tonight around Gion. Any recommendations? 🍁🍜',
            tags: ['#KyotoTravel', '#Foodie'],
            specialTag: 'Looking for buddy',
            image: 'https://lh3.googleusercontent.com/aida-public/AB6AXuBaY9yCh_w2QDmo20_W8OSqoY3a2OZ7ikVBIlmC3mRpZzH9O0ldxWNRK2Tdc8pFcsqAwNMwm3ezr3GWIgi9kPQgDFNmmBvtOF7ac5gzabTi3mL_LqDDtViVWXig_U8AJXNJDoBiWXnMm4h8e45HN_7-F3NVDk460y53qaRbgD52IzBz3mlLUAh2B-HYKCAUugvXYHJs3bUfcjnrdYR5g4VDI5nFfpLAbA2J4R9cZmxOpIRmjEpOajG7eMRWlnRRGtK_sgOyGz9Dna04',
            likes: 142,
            comments: 24,
        },
        {
            id: '2',
            user: { name: 'Sarah Jenkins', avatar: 'https://lh3.googleusercontent.com/aida-public/AB6AXuBhU98cqIOodyTR0IGfcD1laB2Oim7KZ285ETY2fu9SFqbpyj3nzd3z0Dx7t3J-RzEcQGhaIpFB_E13wWQmdXXzFfMnobTfEOLut35WHvAkLLa32pjIBDYbm3ksek0wafxQS118KLW6V_u9oYqOYSOvzZV4uUqu12sg-qYLY-jgas7L2Va9h45fMAkbVAkjK19beOAYCE2iwZtWYJlToQGgJ7n5oas5vzMGNFgQvYNLJfU6OcQZNX_8VbfTE51r2n6_UFvudb9tEmsf', location: 'Reykjavik, Iceland' },
            time: '5h ago',
            content: 'Does anyone have a 5-day itinerary for Iceland\'s South Coast? Planning a trip for next month and want to hit all the major waterfalls and black sand beaches! 🏔️🌊',
            isTripCard: true,
            tripTitle: 'Iceland Explorer',
            tripDates: 'Oct 12 - Oct 17',
            image: 'https://lh3.googleusercontent.com/aida-public/AB6AXuDQT0vc0j5ld9q_or_8xw0rm73cepogtBxISWYmTQkC1wrM-dhgum5Ebmcbnci9IaXvadtzkldPRcJO_L-m_XaMRnjEmKX5ZH6b4pS3OK_Gg51E0QMJKEF80Hxa2XZ6S1lM0yOaROQZa6zSQdIpNE4rTWqJEssONJOzRekS9hErr2G8Kogvpob65N6rfZl_pi7_1ahAx3y7XJ_3ChQciQLHPyCzcSWAVEQGrh5Y-gNKaCeatXD40ymZfFA4EagbyLPPqbhkRFTzMHG1',
            likes: 89,
            comments: 56,
        }
    ];
    
  const dispatch = useDispatch();
  const scrollRef = useRef(null);
  const loadMoreRef = useRef(null);

  const { getAllBlogs } = useBlog();
  const {getAllNotifications} = useNotificationsData();

  const currentBlogs = useSelector((store) => store.blog.blogs, shallowEqual);
  const blogNextPageToken = useSelector(
    (store) => store.blog.nextPageToken,
    shallowEqual
  );


  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(false);

  // ✅ Load blogs function (correct pagination)
  const loadMoreBlogs = useCallback(async () => {
    if (loading || !blogNextPageToken) return;

    setLoading(true);

    const nextPage = page + 1;
    const data = await getAllBlogs(nextPage);

    if (!data || data.length === 0) {
      dispatch(setBlogsNextPageToken(false));
    } else {
      dispatch(addBlog(data));
      setPage(nextPage);
    }

    setLoading(false);
  }, [page, loading, blogNextPageToken]);

  

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
          "[data-radix-scroll-area-viewport]"
        ),
        threshold: 1.0,
      }
    );

    if (loadMoreRef.current) {
      observer.observe(loadMoreRef.current);
    }

    return () => observer.disconnect();
  }, [loadMoreBlogs]);

  // ✅ First load
  useEffect(() => {
    if (currentBlogs.length === 0) {

      console.log("Fetching initial blogs...");
      loadMoreBlogs();
    }
    getAllNotifications();
  }, []);

    return (
    <div className="flex flex-col xl:flex-row gap-8 max-w-7xl mx-auto">

      {/* Main Feed */}
      <div className="flex-1 space-y-6">
        {/* Create Post Widget */}
        {/* <div className="bg-white dark:bg-surface-dark rounded-2xl shadow-sm border border-slate-100 dark:border-gray-900 p-5">
          <div className="flex gap-4 mb-4">
            <img
              src="https://picsum.photos/seed/me123/100/100"
              className="w-12 h-12 rounded-full object-cover shrink-0 border-2 border-primary/20"
              alt="Me"
            />
            <div className="flex-1">
              <textarea
                value={postText}
                onChange={(e) => setPostText(e.target.value)}
                className="w-full bg-slate-50 dark:bg-surface-lighter border-none rounded-xl p-4 text-sm text-slate-900 dark:text-white placeholder-slate-500 focus:ring-0 resize-none min-h-[100px] font-medium"
                placeholder="Where are you heading next? Share your plans..."
              />
            </div>
          </div>
          <div className="flex items-center justify-between pt-4 border-t border-slate-100 dark:border-gray-900">
            <div className="flex gap-2">
              <button className="flex items-center gap-2 px-4 py-2 text-slate-500 dark:text-slate-400 hover:bg-slate-50 dark:hover:bg-surface-lighter rounded-lg text-xs font-bold uppercase tracking-wider transition-colors">
                <ImageIcon size={18} className="text-primary" />
                <span>Photo</span>
              </button>
              <button className="flex items-center gap-2 px-4 py-2 text-slate-500 dark:text-slate-400 hover:bg-slate-50 dark:hover:bg-surface-lighter rounded-lg text-xs font-bold uppercase tracking-wider transition-colors">
                <MapPin size={18} className="text-primary" />
                <span>Location</span>
              </button>
            </div>
            <button className="bg-primary hover:bg-primary-hover text-white px-8 py-2.5 rounded-lg text-xs font-bold uppercase tracking-widest shadow-lg shadow-primary/20 transition-all disabled:opacity-50">
              Post
            </button>
          </div>
        </div> */}

        {/* Post List */}
        {posts.map((post) => (<div key={post.id} className="bg-white dark:bg-surface-dark rounded-2xl shadow-sm border border-slate-100 dark:border-gray-900 overflow-hidden">
            <div className="p-4 flex items-center justify-between">
              <div className="flex items-center gap-3 cursor-pointer" onClick={() => navigate(`/profile/${post.user.userId}`)}>
                <div className="relative">
                  <img src={post.user.avatar} className="w-11 h-11 rounded-full object-cover border-2 border-primary/10" alt={post.user.name}/>
                  <div className="absolute -bottom-0.5 -right-0.5 w-3.5 h-3.5 bg-emerald-500 rounded-full border-2 border-white dark:border-surface-dark"/>
                </div>
                <div>
                  <h3 className="font-bold text-sm tracking-tight">{post.user.name}</h3>
                  <p className="text-[10px] text-slate-500 font-bold uppercase tracking-widest flex items-center gap-1.5 mt-0.5">
                    {post.user.location} <span className="w-1 h-1 bg-slate-300 dark:bg-slate-700 rounded-full"/> {post.time}
                  </p>
                </div>
              </div>
              <button className="text-slate-400 hover:text-primary transition-colors">
                <MoreHorizontal size={20}/>
              </button>
            </div>
            <div className="px-5 pb-4">
              <p className="text-sm leading-relaxed mb-4 text-slate-700 dark:text-slate-300 font-medium">{post.content}</p>
              <div className="flex flex-wrap gap-2">
                {post.tags?.map((tag) => (<span key={tag} className="px-2.5 py-1 rounded-md bg-primary/10 text-primary text-[10px] font-bold uppercase tracking-wider">{tag}</span>))}
                {post.specialTag && (<span className="px-2.5 py-1 rounded-md bg-orange-500/20 text-orange-400 text-[10px] font-bold uppercase tracking-wider">{post.specialTag}</span>)}
              </div>
            </div>
            
            {post.isTripCard ? (<div className="relative aspect-[2/1] cursor-pointer group" onClick={() => navigate('/trips/1')}>
                <img src={post.image} className="w-full h-full object-cover transition-transform group-hover:scale-105 duration-700" alt="Trip"/>
                <div className="absolute inset-0 bg-gradient-to-t from-black/80 to-transparent"/>
                <div className="absolute bottom-5 left-5 text-white">
                  <h4 className="font-extrabold text-xl tracking-tight">TRIP: {post.tripTitle}</h4>
                  <p className="text-[10px] font-bold uppercase tracking-[0.2em] flex items-center gap-2 mt-1.5 opacity-90"><Calendar size={12}/> {post.tripDates}</p>
                </div>
                <button className="absolute bottom-5 right-5 bg-primary text-white px-5 py-2 rounded-lg text-[10px] font-bold uppercase tracking-widest hover:bg-primary-hover transition-all shadow-xl shadow-primary/30">
                  View Itinerary
                </button>
              </div>) : (<div className="relative aspect-video">
                <img src={post.image} className="w-full h-full object-cover" alt="Post"/>
                <div className="absolute bottom-4 right-4 bg-black/50 backdrop-blur-md text-white text-[10px] font-bold px-3 py-1 rounded-full flex items-center gap-2 uppercase tracking-widest border border-white/10">
                  <Camera size={14}/> 1/4
                </div>
              </div>)}

            <div className="px-5 py-4 border-t border-slate-100 dark:border-gray-900 flex items-center justify-between">
              <div className="flex gap-8">
                <button className="flex items-center gap-2.5 text-slate-500 dark:text-slate-400 hover:text-primary transition-colors group">
                  <Heart size={20} className="group-hover:fill-current"/>
                  <span className="text-xs font-bold">{post.likes}</span>
                </button>
                <button className="flex items-center gap-2.5 text-slate-500 dark:text-slate-400 hover:text-primary transition-colors">
                  <MessageCircle size={20}/>
                  <span className="text-xs font-bold">{post.comments}</span>
                </button>
                <button className="flex items-center gap-2.5 text-slate-500 dark:text-slate-400 hover:text-primary transition-colors">
                  <Share2 size={20}/>
                </button>
              </div>
              <button className="flex items-center gap-2 text-primary hover:text-primary-hover text-[10px] font-bold uppercase tracking-widest transition-colors">
                <UserPlus size={18}/>
                Connect
              </button>
            </div>
          </div>))}
      </div>

      {/* Right Column Widgets */}
      <div className="xl:w-85 space-y-6 shrink-0">
        {/* Upcoming Trips */}
        <div className="bg-white dark:bg-surface-dark rounded-2xl shadow-sm border border-slate-100 dark:border-gray-900 p-6">
          <div className="flex items-center justify-between mb-6">
            <h3 className="font-extrabold text-xs uppercase tracking-widest text-slate-900 dark:text-white">Upcoming Trips</h3>
            <button className="text-primary text-[10px] font-bold uppercase tracking-widest hover:underline" onClick={() => navigate('/trips')}>See All</button>
          </div>
          <div className="space-y-5">
            {[
            { month: 'Oct', day: '15', title: 'Bali Yoga Retreat', loc: 'UBUD, INDONESIA • 7 DAYS' },
            { month: 'Dec', day: '02', title: 'Swiss Alps Skiing', loc: 'ZERMATT, SWITZERLAND • 5 DAYS' }
        ].map((trip, idx) => (<div key={idx} className="flex gap-4 group cursor-pointer" onClick={() => navigate('/trips/1')}>
                <div className="w-12 h-12 bg-primary/10 dark:bg-primary/5 rounded-xl flex flex-col items-center justify-center border border-primary/10 shrink-0 group-hover:bg-primary group-hover:border-primary transition-all duration-300">
                  <span className="text-[8px] text-primary group-hover:text-white font-extrabold uppercase tracking-widest">{trip.month}</span>
                  <span className="text-xl font-extrabold text-primary group-hover:text-white leading-none">{trip.day}</span>
                </div>
                <div className="min-w-0">
                  <h4 className="text-sm font-bold truncate group-hover:text-primary transition-colors tracking-tight">{trip.title}</h4>
                  <p className="text-[9px] text-slate-500 font-bold uppercase tracking-wider mt-0.5 truncate">{trip.loc}</p>
                </div>
              </div>))}
          </div>
        </div>

        {/* Suggested Travelers */}
        <div className="bg-white dark:bg-surface-dark rounded-2xl shadow-sm border border-slate-100 dark:border-gray-900 p-6">
          <div className="flex items-center justify-between mb-6">
            <h3 className="font-extrabold text-xs uppercase tracking-widest text-slate-900 dark:text-white">Discover</h3>
            <button className="text-slate-400 hover:text-primary transition-colors">
              <RefreshCw size={16}/>
            </button>
          </div>
          <div className="space-y-5">
            {[
            { name: 'Mike T.', role: 'Digital Nomad', avatar: 'https://picsum.photos/seed/mike/100/100' },
            { name: 'Elena R.', role: 'Backpacker', avatar: 'https://picsum.photos/seed/elena/100/100' },
            { name: 'David K.', role: 'Photographer', avatar: 'https://picsum.photos/seed/david/100/100' }
        ].map((user, idx) => (<div key={idx} className="flex items-center justify-between">
                <div className="flex items-center gap-3 cursor-pointer" onClick={() => navigate(`/profile/${user.userId}`)}>
                  <img src={user.avatar} className="w-10 h-10 rounded-full object-cover border-2 border-primary/5" alt={user.name}/>
                  <div>
                    <h4 className="text-sm font-bold leading-tight tracking-tight">{user.name}</h4>
                    <p className="text-[9px] text-slate-500 uppercase tracking-[0.15em] font-extrabold mt-0.5">{user.role}</p>
                  </div>
                </div>
                <button className="w-8 h-8 rounded-full bg-primary/10 hover:bg-primary text-primary hover:text-white flex items-center justify-center transition-all">
                  <UserPlus size={16}/>
                </button>
              </div>))}
          </div>
        </div>

        {/* Trending Now */}
        <div className="bg-gradient-to-br from-primary to-orange-600 rounded-2xl shadow-2xl shadow-primary/20 p-6 text-white relative overflow-hidden">
          <div className="relative z-10">
            <div className="flex items-center gap-2 mb-3 opacity-90">
              <TrendingUp size={14}/>
              <span className="text-[9px] font-extrabold uppercase tracking-[0.25em]">Trending Now</span>
            </div>
            <h3 className="text-2xl font-extrabold mb-2 tracking-tight">Explore Patagonia</h3>
            <p className="text-sm text-orange-50 mb-5 leading-relaxed font-medium">Join 45 other travelers heading south this winter season.</p>
            <button className="bg-white text-primary px-6 py-2.5 rounded-lg text-xs font-bold uppercase tracking-widest hover:bg-orange-50 transition-colors shadow-lg">
              View Guide
            </button>
          </div>
          <div className="absolute -right-6 -bottom-10 w-36 h-36 bg-white/10 rounded-full blur-2xl"/>
          <div className="absolute top-0 right-0 w-24 h-24 bg-orange-400/20 rounded-full blur-xl"/>
        </div>
      </div>
    </div>);
};
export default Dashboard;
