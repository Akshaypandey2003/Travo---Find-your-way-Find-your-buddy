import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
// Added Search to imports
import { MapPin, Calendar, Users, Lock, ArrowRight, Camera, Search } from 'lucide-react';
const CreateTrip = () => {
    const navigate = useNavigate();
    const [participants, setParticipants] = useState(4);
    const [privateMode, setPrivateMode] = useState(false);
    return (<div className="max-w-3xl mx-auto py-8">
      <div className="bg-white dark:bg-surface-darker border dark:border-slate-800 rounded-3xl shadow-2xl overflow-hidden relative z-10">
        <header className="px-8 pt-8 pb-6 border-b dark:border-slate-800">
          <h1 className="text-3xl font-extrabold tracking-tight mb-2">Plan your next adventure</h1>
          <p className="text-slate-500 text-sm">Fill in the details to find the perfect travel buddies.</p>
        </header>

        <form className="p-8 space-y-12" onSubmit={(e) => { e.preventDefault(); navigate('/trips'); }}>
          {/* Section: Basics */}
          <section className="space-y-6">
            <div className="flex items-center gap-2 mb-4">
              <div className="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center text-primary"><MapPin size={16}/></div>
              <h2 className="text-lg font-bold">The Basics</h2>
            </div>

            <div className="space-y-6">
              <div className="space-y-1.5">
                <label className="text-[10px] font-bold text-slate-500 uppercase tracking-widest ml-1">Where are you going?</label>
                <div className="relative group">
                  <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400" size={18}/>
                  <input className="w-full bg-slate-50 dark:bg-surface-dark border dark:border-slate-800 rounded-xl py-3.5 pl-12 pr-4 text-sm focus:ring-2 focus:ring-primary transition-all outline-none" placeholder="e.g. Kyoto, Japan"/>
                </div>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
                <div className="space-y-1.5">
                  <label className="text-[10px] font-bold text-slate-500 uppercase tracking-widest ml-1">Start Date</label>
                  <div className="relative">
                    <Calendar className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400" size={18}/>
                    <input type="date" className="w-full bg-slate-50 dark:bg-surface-dark border dark:border-slate-800 rounded-xl py-3.5 pl-12 pr-4 text-sm focus:ring-2 focus:ring-primary outline-none"/>
                  </div>
                </div>
                <div className="space-y-1.5">
                  <label className="text-[10px] font-bold text-slate-500 uppercase tracking-widest ml-1">End Date</label>
                  <div className="relative">
                    <Calendar className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400" size={18}/>
                    <input type="date" className="w-full bg-slate-50 dark:bg-surface-dark border dark:border-slate-800 rounded-xl py-3.5 pl-12 pr-4 text-sm focus:ring-2 focus:ring-primary outline-none"/>
                  </div>
                </div>
              </div>
            </div>
          </section>

          <hr className="dark:border-slate-800"/>

          {/* Section: Vibe */}
          <section className="space-y-6">
            <div className="flex items-center gap-2 mb-4">
              <div className="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center text-primary"><Camera size={16}/></div>
              <h2 className="text-lg font-bold">Trip Vibe</h2>
            </div>

            <div className="space-y-6">
              <div className="space-y-3">
                <label className="text-[10px] font-bold text-slate-500 uppercase tracking-widest ml-1">What kind of trip is it?</label>
                <div className="flex flex-wrap gap-3">
                  {['Backpacking', 'Road Trip', 'Luxury', 'Business', 'Foodie'].map((vibe, idx) => (<button key={vibe} type="button" className={`px-5 py-2 rounded-full border text-xs font-bold transition-all ${idx === 0 ? 'bg-primary border-primary text-white shadow-lg shadow-primary/20' : 'bg-white dark:bg-surface-dark border-slate-200 dark:border-slate-800 text-slate-500 hover:border-slate-400'}`}>
                      {vibe}
                    </button>))}
                </div>
              </div>

              <div className="space-y-1.5">
                <label className="text-[10px] font-bold text-slate-500 uppercase tracking-widest ml-1">Description</label>
                <textarea className="w-full bg-slate-50 dark:bg-surface-dark border dark:border-slate-800 rounded-2xl p-6 text-sm focus:ring-2 focus:ring-primary outline-none resize-none h-40" placeholder="Tell us what you have in mind... e.g. We plan to hike up to the viewpoint at sunrise and then grab coffee."/>
              </div>
            </div>
          </section>

          <hr className="dark:border-slate-800"/>

          {/* Section: People */}
          <section className="space-y-6">
            <div className="flex items-center gap-2 mb-4">
              <div className="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center text-primary"><Users size={16}/></div>
              <h2 className="text-lg font-bold">People & Privacy</h2>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-8">
              <div className="space-y-3">
                <label className="text-[10px] font-bold text-slate-500 uppercase tracking-widest ml-1">Participant Limit</label>
                <div className="flex items-center gap-6">
                  <button type="button" onClick={() => setParticipants(Math.max(1, participants - 1))} className="w-10 h-10 rounded-xl bg-slate-100 dark:bg-slate-800 flex items-center justify-center font-bold hover:bg-slate-200">-</button>
                  <span className="text-xl font-bold w-4 text-center">{participants}</span>
                  <button type="button" onClick={() => setParticipants(participants + 1)} className="w-10 h-10 rounded-xl bg-slate-100 dark:bg-slate-800 flex items-center justify-center font-bold hover:bg-slate-200">+</button>
                </div>
              </div>

              <div className="space-y-3">
                <label className="text-[10px] font-bold text-slate-500 uppercase tracking-widest ml-1">Visibility</label>
                <div className="flex items-center justify-between p-4 bg-slate-50 dark:bg-slate-900 rounded-xl border dark:border-slate-800">
                  <div className="flex items-center gap-3">
                    <Lock size={16} className="text-slate-500"/>
                    <div className="flex flex-col">
                      <span className="text-xs font-bold">Private Mode</span>
                      <span className="text-[10px] text-slate-500">Only invited people can see</span>
                    </div>
                  </div>
                  <button type="button" onClick={() => setPrivateMode(!privateMode)} className={`w-12 h-6 rounded-full p-1 transition-all ${privateMode ? 'bg-primary' : 'bg-slate-300 dark:bg-slate-700'}`}>
                    <div className={`w-4 h-4 bg-white rounded-full transition-transform ${privateMode ? 'translate-x-6' : 'translate-x-0'}`}/>
                  </button>
                </div>
              </div>
            </div>
          </section>

          <div className="pt-6">
            <button type="submit" className="w-full bg-primary hover:bg-primary-hover text-white py-4 rounded-2xl font-bold shadow-xl shadow-primary/30 transition-all flex items-center justify-center gap-2 group">
              Create Trip
              <ArrowRight size={20} className="group-hover:translate-x-1 transition-transform"/>
            </button>
            <p className="text-center text-[10px] text-slate-500 mt-6 uppercase tracking-widest font-bold">By creating this trip, you agree to Travo's <a href="#" className="underline text-primary">Community Guidelines</a></p>
          </div>
        </form>
      </div>
      
      {/* Decorative Blur */}
      <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-full h-[600px] bg-primary/10 blur-[150px] -z-10 rounded-full"/>
    </div>);
};
export default CreateTrip;
