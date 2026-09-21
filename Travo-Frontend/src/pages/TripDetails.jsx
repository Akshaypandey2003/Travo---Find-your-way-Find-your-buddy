import React from 'react';
import { MapPin, Calendar, Share2, Edit3, MessageCircle, MoreVertical, Navigation, Camera, Star } from 'lucide-react';
const TripDetails = () => {
    const itinerary = [
        { day: '01', date: '02 APR', title: 'Arrival in Kyoto & Welcome Dinner', desc: 'Check-in at The Millenials Kyoto. Meet & Greet at 18:00 followed by a traditional Kaiseki dinner in Gion district.', tags: ['Dinner', 'Check-in'], img: 'https://picsum.photos/seed/kyoto1/300/300' },
        { day: '02', date: '03 APR', title: 'Fushimi Inari Shrine Hike', desc: 'Early morning hike to beat the crowds. Approximately 2-3 hours walking. Bring comfortable shoes and water.', tags: ['Nature', 'Photography'], img: 'https://picsum.photos/seed/fushimi/300/300' }
    ];
    const requests = [
        { name: 'Sarah Jenkins', avatar: 'https://picsum.photos/seed/s/100/100', mut: '2 mutual trips', note: 'Love photography! I have a Sony A7III and would love to join the photo walks.' },
        { name: 'Mike Ross', avatar: 'https://picsum.photos/seed/m/100/100', mut: 'New to Travo', note: 'First time in Japan. Looking for a friendly group.' }
    ];
    return (<div className="max-w-7xl mx-auto grid grid-cols-1 lg:grid-cols-12 gap-8">
      {/* Header Info */}
      <div className="lg:col-span-12">
        <div className="relative w-full h-80 rounded-3xl overflow-hidden group">
          <img src="https://picsum.photos/seed/kyoto-banner/1200/600" className="w-full h-full object-cover transition duration-700 group-hover:scale-105" alt="Kyoto"/>
          <div className="absolute inset-0 bg-gradient-to-t from-background-dark via-background-dark/40 to-transparent"/>
          <div className="absolute bottom-0 left-0 p-8 w-full flex flex-col sm:flex-row sm:items-end justify-between gap-6">
            <div className="space-y-4">
              <div className="flex gap-2">
                <span className="px-3 py-1 bg-primary text-white text-[10px] font-bold uppercase tracking-widest rounded-full">Active Trip</span>
                <span className="px-3 py-1 bg-white/10 backdrop-blur-md text-white text-[10px] font-bold uppercase tracking-widest rounded-full border border-white/20">8 Days Left</span>
              </div>
              <h1 className="text-4xl font-extrabold text-white tracking-tight">Cherry Blossom Tour 2024</h1>
              <div className="flex items-center text-slate-300 text-xs font-bold uppercase tracking-widest gap-6">
                <span className="flex items-center gap-2"><MapPin size={16}/> Kyoto & Osaka, Japan</span>
                <span className="flex items-center gap-2"><Calendar size={16}/> Apr 2 - Apr 10, 2024</span>
              </div>
            </div>
            <div className="flex gap-3">
              <button className="p-3 bg-white/10 hover:bg-white/20 backdrop-blur-md text-white rounded-xl transition border border-white/10"><Share2 size={20}/></button>
              <button className="p-3 bg-white/10 hover:bg-white/20 backdrop-blur-md text-white rounded-xl transition border border-white/10"><Edit3 size={20}/></button>
            </div>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="lg:col-span-8 space-y-8">
        <nav className="flex space-x-8 border-b dark:border-slate-800">
          <button className="pb-4 text-sm font-bold text-primary border-b-2 border-primary flex items-center gap-2"><Navigation size={16}/> Itinerary</button>
          <button className="pb-4 text-sm font-bold text-slate-500 hover:text-slate-300 flex items-center gap-2"><Camera size={16}/> Gallery</button>
          <button className="pb-4 text-sm font-bold text-slate-500 hover:text-slate-300 flex items-center gap-2"><Star size={16}/> Reviews <span className="ml-1 opacity-50 bg-slate-800 px-1.5 rounded">0</span></button>
        </nav>

        <div className="space-y-6">
          {itinerary.map((day) => (<div key={day.day} className="bg-white dark:bg-surface-dark rounded-2xl p-6 border dark:border-slate-800 hover:border-primary/50 transition duration-300 flex flex-col sm:flex-row gap-6">
              <div className="flex-none text-center w-16">
                <div className="text-[10px] font-extrabold text-slate-500 uppercase tracking-widest">Day {day.day}</div>
                <div className="text-3xl font-extrabold text-primary my-1 leading-none">{day.day}</div>
                <div className="text-[10px] font-bold text-slate-400 uppercase">{day.date.split(' ')[1]}</div>
              </div>
              <div className="flex-1 min-w-0">
                <div className="flex justify-between items-start mb-2">
                  <h3 className="text-xl font-bold leading-tight">{day.title}</h3>
                  <span className="bg-emerald-500/10 text-emerald-500 text-[9px] font-bold uppercase tracking-widest px-2 py-0.5 rounded">Confirmed</span>
                </div>
                <p className="text-sm text-slate-500 mb-4 leading-relaxed">{day.desc}</p>
                <div className="flex flex-wrap gap-2">
                  {day.tags.map(t => (<span key={t} className="px-2.5 py-1 bg-slate-50 dark:bg-slate-800 text-slate-500 text-[9px] font-bold uppercase tracking-widest rounded-lg">{t}</span>))}
                </div>
              </div>
              <div className="hidden sm:block w-28 h-28 rounded-2xl overflow-hidden shrink-0">
                <img src={day.img} className="w-full h-full object-cover" alt="day"/>
              </div>
            </div>))}
        </div>

        <div className="p-12 border-2 border-dashed border-slate-200 dark:border-slate-800 rounded-3xl text-center bg-slate-50/30 dark:bg-slate-900/30">
          <Star size={40} className="mx-auto text-slate-300 mb-4"/>
          <p className="text-slate-500 font-bold uppercase tracking-widest text-sm">Trip Reviews will appear here after April 10.</p>
        </div>
      </div>

      {/* Sidebar Content */}
      <div className="lg:col-span-4 space-y-6">
        <button className="w-full bg-primary hover:bg-primary-hover text-white py-4 rounded-2xl font-bold shadow-xl shadow-primary/20 flex items-center justify-center gap-3 transition transform hover:-translate-y-1">
          <MessageCircle size={20}/>
          Open Trip Chat
        </button>

        <div className="bg-white dark:bg-surface-dark rounded-2xl border dark:border-slate-800 overflow-hidden shadow-sm">
          <div className="p-4 bg-slate-50 dark:bg-slate-900/50 border-b dark:border-slate-800 flex justify-between items-center">
            <h3 className="font-bold text-sm uppercase tracking-widest">Join Requests <span className="bg-primary text-white text-[10px] px-2 rounded-full ml-2">2</span></h3>
          </div>
          <div className="divide-y dark:divide-slate-800">
            {requests.map((r, i) => (<div key={i} className="p-4 hover:bg-slate-50 dark:hover:bg-slate-900/30 transition">
                <div className="flex items-start gap-3 mb-4">
                  <img src={r.avatar} className="w-10 h-10 rounded-full object-cover border-2 dark:border-slate-800" alt="r"/>
                  <div>
                    <h4 className="text-sm font-bold">{r.name}</h4>
                    <p className="text-[10px] text-slate-500 font-medium">{r.mut}</p>
                  </div>
                </div>
                <div className="bg-slate-100 dark:bg-background-dark p-3 rounded-xl mb-4 relative">
                  <div className="absolute -top-1 left-4 w-2 h-2 bg-slate-100 dark:bg-background-dark transform rotate-45"/>
                  <p className="text-[11px] text-slate-500 italic leading-relaxed">"{r.note}"</p>
                </div>
                <div className="grid grid-cols-2 gap-2">
                  <button className="py-2 border dark:border-slate-800 rounded-lg text-[10px] font-bold uppercase tracking-widest text-slate-500 hover:bg-red-50 hover:text-red-500">Decline</button>
                  <button className="py-2 bg-primary/10 text-primary hover:bg-primary hover:text-white rounded-lg text-[10px] font-bold uppercase tracking-widest transition-all">Approve</button>
                </div>
              </div>))}
          </div>
        </div>

        <div className="bg-white dark:bg-surface-dark rounded-2xl p-6 border dark:border-slate-800 shadow-sm">
          <div className="flex justify-between items-center mb-6">
            <h3 className="text-xs font-bold text-slate-500 uppercase tracking-widest">Confirmed Travelers</h3>
            <button className="text-[10px] font-bold text-primary hover:underline uppercase tracking-widest">View All</button>
          </div>
          <div className="space-y-4">
            {[
            { name: 'Alex T.', role: 'Organizer', avatar: 'https://picsum.photos/seed/a/100/100', isOrg: true },
            { name: 'Elena R.', role: 'Traveler', avatar: 'https://picsum.photos/seed/e/100/100' },
            { name: 'John D.', role: 'Traveler', avatar: 'https://picsum.photos/seed/j/100/100' }
        ].map((u, i) => (<div key={i} className="flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <div className="relative">
                    <img src={u.avatar} className={`w-10 h-10 rounded-full object-cover ${u.isOrg ? 'ring-2 ring-primary ring-offset-2 dark:ring-offset-slate-900' : ''}`} alt="u"/>
                    {u.isOrg && <div className="absolute -bottom-1 -right-1 bg-primary text-white text-[8px] font-bold px-1 rounded-full border border-slate-900">ORG</div>}
                  </div>
                  <div>
                    <p className="text-sm font-bold">{u.name}</p>
                    <p className="text-[10px] text-slate-500 font-bold uppercase tracking-widest">{u.role}</p>
                  </div>
                </div>
                <MoreVertical size={16} className="text-slate-500 cursor-pointer hover:text-white"/>
              </div>))}
          </div>
        </div>

        <div className="bg-white dark:bg-surface-dark rounded-2xl overflow-hidden border dark:border-slate-800 h-64 relative group">
          <img src="https://picsum.photos/seed/map/400/400" className="w-full h-full object-cover opacity-40 group-hover:scale-110 transition duration-1000" alt="map"/>
          <div className="absolute inset-0 flex flex-col items-center justify-center p-6 text-center">
             <div className="bg-background-dark/80 backdrop-blur-md p-3 rounded-full mb-3 shadow-2xl border border-white/10">
                <MapPin size={24} className="text-primary"/>
             </div>
             <h4 className="text-lg font-bold">Kyoto, Japan</h4>
             <p className="text-xs text-slate-400 mb-6 font-medium">Explore the ancient capital</p>
             <button className="bg-slate-100 dark:bg-slate-800 hover:bg-white text-slate-900 dark:text-white dark:hover:text-slate-900 px-6 py-2.5 rounded-xl text-[10px] font-extrabold uppercase tracking-widest transition-all">View Full Map</button>
          </div>
        </div>
      </div>
    </div>);
};
export default TripDetails;
