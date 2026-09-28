import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Heart, MessageCircle, Share2, UserPlus, MoreHorizontal, Camera, RefreshCw, TrendingUp } from 'lucide-react';
import { useEffect } from "react";
import useBlog from "../CustomHooks/useBlog";
import useNotificationsData from '../CustomHooks/useNotificationsData';

const Dashboard = () => {
    const navigate = useNavigate();
    const [postText, setPostText] = useState('');
  const [feedPosts, setFeedPosts] = useState([]);
  const [feedLoading, setFeedLoading] = useState(true);
  const { getUserFeed } = useBlog();
  const {getAllNotifications} = useNotificationsData();

  useEffect(() => {
    let mounted = true;
    const loadFeed = async () => {
      setFeedLoading(true);
      const feed = await getUserFeed(20);
      if (mounted) {
        setFeedPosts(feed);
        setFeedLoading(false);
      }
    };

    loadFeed();
    getAllNotifications();
    return () => { mounted = false; };
  }, []);

  const posts = feedPosts.map((item) => ({
    id: item.resourceId || item.eventId,
    user: {
      userId: item.authorId,
      name: item.authorName || "TRAVO traveler",
      avatar: item.authorProfilePic || "https://picsum.photos/seed/travo-user/100/100",
    },
    time: item.createdAt ? new Date(item.createdAt).toLocaleString() : "Recently",
    content: item.caption || "Shared a travel update.",
    image: item.images?.[0] || item.thumbnailUrl,
    likes: 0,
    comments: 0,
  }));

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
        {feedLoading ? (
          <div className="bg-white dark:bg-surface-dark rounded-2xl p-8 text-center text-sm text-slate-500">Loading your feed...</div>
        ) : posts.length === 0 ? (
          <div className="bg-white dark:bg-surface-dark rounded-2xl p-8 text-center text-sm text-slate-500">No feed posts yet. Follow travelers or publish a blog to get started.</div>
        ) : posts.map((post) => (<div key={post.id} className="bg-white dark:bg-surface-dark rounded-2xl shadow-sm border border-slate-100 dark:border-gray-900 overflow-hidden">
            <div className="p-4 flex items-center justify-between">
              <div className="flex items-center gap-3 cursor-pointer" onClick={() => navigate(`/profile/${post.user.userId}`)}>
                <div className="relative">
                  <img src={post.user.avatar} className="w-11 h-11 rounded-full object-cover border-2 border-primary/10" alt={post.user.name}/>
                  <div className="absolute -bottom-0.5 -right-0.5 w-3.5 h-3.5 bg-emerald-500 rounded-full border-2 border-white dark:border-surface-dark"/>
                </div>
                <div>
                  <h3 className="font-bold text-sm tracking-tight">{post.user.name}</h3>
                  <p className="text-[10px] text-slate-500 font-bold uppercase tracking-widest flex items-center gap-1.5 mt-0.5">
                    TRAVO feed <span className="w-1 h-1 bg-slate-300 dark:bg-slate-700 rounded-full"/> {post.time}
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
            
            {post.image && (<div className="relative aspect-video">
                <img src={post.image} className="w-full h-full object-cover" alt="Travel post"/>
                <div className="absolute bottom-4 right-4 bg-black/50 backdrop-blur-md text-white text-[10px] font-bold px-3 py-1 rounded-full flex items-center gap-2 uppercase tracking-widest border border-white/10">
                  <Camera size={14}/> {post.user.name}
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
