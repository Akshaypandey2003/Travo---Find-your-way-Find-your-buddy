import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Star, Eye, CheckCircle2, ArrowRight } from 'lucide-react';
const Review = () => {
    const navigate = useNavigate();
    const [rating, setRating] = useState(4);
    const [hoverRating, setHoverRating] = useState(0);
    return (<div className="max-w-3xl mx-auto py-12 px-4">
      {/* Trip Header Context */}
      <div className="relative overflow-hidden rounded-3xl bg-black border border-gray-900 shadow-2xl mb-12">
        <div className="absolute inset-0">
          <img src="https://picsum.photos/seed/vietnam-rev/1000/600" className="w-full h-full object-cover opacity-30" alt="v"/>
          <div className="absolute inset-0 bg-gradient-to-r from-black via-black/80 to-transparent"/>
        </div>
        <div className="relative z-10 p-10 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-8">
          <div className="space-y-4">
            <div className="flex items-center gap-3">
              <span className="px-4 py-1.5 rounded-full text-[10px] font-extrabold uppercase tracking-[0.2em] bg-primary/20 text-primary border border-primary/20 shadow-xl shadow-primary/10">Trip Completed</span>
              <span className="text-slate-500 text-[10px] font-extrabold tracking-[0.2em] uppercase">OCT 12 - OCT 24</span>
            </div>
            <h1 className="text-4xl font-extrabold text-white tracking-tight">Backpacking through Vietnam</h1>
            <p className="text-slate-400 text-xs font-bold uppercase tracking-widest">HOSTED BY YOU • 2 PARTNERS</p>
          </div>
          <div className="hidden sm:block w-28 h-28 rounded-2xl overflow-hidden border-2 border-primary/20 shadow-2xl transform rotate-6 hover:rotate-0 transition-all duration-700">
            <img src="https://picsum.photos/seed/vfood/300/300" className="w-full h-full object-cover" alt="trip"/>
          </div>
        </div>
      </div>

      {/* Progress */}
      <div className="flex justify-between items-center text-[10px] font-extrabold uppercase tracking-[0.25em] mb-6 px-4">
        <span className="text-slate-500">Reviewing partner <span className="text-primary font-black">1 of 2</span></span>
        <div className="flex gap-2">
          <div className="h-1.5 w-16 rounded-full bg-primary shadow-lg shadow-primary/30"/>
          <div className="h-1.5 w-16 rounded-full bg-slate-200 dark:bg-surface-lighter"/>
        </div>
      </div>

      {/* Main Review Card */}
      <div className="bg-white dark:bg-surface-dark border border-slate-100 dark:border-gray-900 rounded-3xl p-10 md:p-14 shadow-2xl space-y-14">
        <div className="flex items-center gap-8 border-b border-slate-100 dark:border-gray-900 pb-12">
          <div className="relative">
            <div className="w-24 h-24 rounded-full p-1.5 bg-gradient-to-tr from-primary to-orange-400 shadow-2xl">
              <img src="https://picsum.photos/seed/alex-j/200/200" className="w-full h-full rounded-full object-cover border-4 border-white dark:border-surface-dark" alt="a"/>
            </div>
          </div>
          <div className="flex-1">
            <h2 className="text-3xl font-extrabold tracking-tight mb-2">Alex Johnson</h2>
            <div className="flex flex-wrap gap-2">
              <span className="text-[10px] font-extrabold px-3 py-1.5 rounded-lg bg-primary/10 text-primary border border-primary/10 uppercase tracking-widest">Adventurer</span>
              <span className="text-[10px] font-extrabold px-3 py-1.5 rounded-lg bg-slate-100 dark:bg-surface-lighter text-slate-500 uppercase tracking-widest">Foodie</span>
            </div>
          </div>
          <div className="text-right hidden sm:block">
            <p className="text-[9px] font-black text-slate-400 uppercase tracking-[0.2em] mb-1.5">JOINED 2022</p>
            <p className="text-[9px] font-black text-primary uppercase tracking-[0.2em]">12 TRIPS DONE</p>
          </div>
        </div>

        <div className="space-y-12 text-center">
          <h3 className="text-2xl font-extrabold tracking-tight">How would you rate your experience with Alex?</h3>
          <div className="flex items-center justify-center gap-4">
            {[1, 2, 3, 4, 5].map((s) => (<button key={s} onMouseEnter={() => setHoverRating(s)} onMouseLeave={() => setHoverRating(0)} onClick={() => setRating(s)} className="transition-transform active:scale-90">
                <Star size={56} className={`transition-all duration-300 ${(hoverRating || rating) >= s
                ? 'text-primary fill-current drop-shadow-[0_0_12px_rgba(255,120,46,0.5)]'
                : 'text-slate-200 dark:text-surface-lighter'}`}/>
              </button>))}
          </div>
          <p className="text-xs font-black text-primary uppercase tracking-[0.3em] animate-pulse">Great Experience</p>
        </div>

        <div className="space-y-5">
          <label className="text-[10px] font-black text-slate-400 uppercase tracking-[0.25em] block ml-2">Share your experience</label>
          <textarea className="w-full bg-slate-50 dark:bg-background-dark border border-slate-100 dark:border-gray-900 rounded-2xl p-8 text-sm font-medium focus:ring-2 focus:ring-primary outline-none h-48 resize-none leading-relaxed shadow-inner" placeholder="What was the highlight of traveling with Alex? Did they stick to the itinerary? Were they good company?"/>
          <div className="flex justify-end pr-2"><span className="text-[10px] font-bold text-slate-500 tracking-widest uppercase opacity-60">0 / 500</span></div>
        </div>

        <div className="space-y-6">
          <label className="text-[10px] font-black text-slate-400 uppercase tracking-[0.25em] block ml-2">What was Alex good at?</label>
          <div className="flex flex-wrap gap-3">
            {['Navigation', 'Planning', 'Budgeting', 'Socializing', 'Photography'].map(tag => (<button key={tag} className="px-6 py-3 rounded-xl border border-slate-100 dark:border-gray-900 text-[10px] font-bold uppercase tracking-widest hover:border-primary hover:text-primary hover:bg-primary/5 transition-all shadow-sm">
                {tag}
              </button>))}
          </div>
        </div>

        <div className="bg-slate-50 dark:bg-background-dark border border-slate-100 dark:border-gray-900 rounded-3xl p-6 flex items-center justify-between shadow-inner">
          <div className="flex items-center gap-5">
            <div className="w-12 h-12 rounded-2xl bg-white dark:bg-surface-dark flex items-center justify-center text-primary shadow-xl"><Eye size={24}/></div>
            <div>
              <p className="text-sm font-extrabold tracking-tight">Public Review</p>
              <p className="text-[10px] text-slate-500 font-bold uppercase tracking-widest">Visible on Alex's profile to everyone</p>
            </div>
          </div>
          <button className="w-14 h-7 bg-primary rounded-full p-1 shadow-inner transition-all"><div className="w-5 h-5 bg-white rounded-full ml-auto shadow-md"/></button>
        </div>

        <div className="flex flex-col sm:flex-row items-center justify-between gap-8 pt-10 border-t border-slate-100 dark:border-gray-900">
           <button className="text-[10px] font-black text-slate-400 uppercase tracking-[0.25em] hover:text-primary transition-colors">Skip & Review Later</button>
           <button onClick={() => navigate('/dashboard')} className="w-full sm:w-auto px-14 py-4.5 bg-primary hover:bg-primary-hover text-white rounded-2xl font-bold uppercase tracking-[0.15em] shadow-2xl shadow-primary/40 flex items-center justify-center gap-3 transform transition hover:-translate-y-1 active:scale-95 text-xs">
             Submit Review <ArrowRight size={20}/>
           </button>
        </div>
      </div>

      <div className="mt-12 flex items-center justify-center gap-4 text-slate-500 text-center">
        <CheckCircle2 size={18} className="text-primary"/>
        <p className="text-[10px] font-black uppercase tracking-[0.2em]">You'll earn <span className="text-primary">+50 TRAVO POINTS</span> for completing this review.</p>
      </div>
    </div>);
};
export default Review;
