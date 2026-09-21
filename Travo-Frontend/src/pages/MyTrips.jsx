import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Plus, Search, Filter, MoreHorizontal, ArrowRight, Globe } from 'lucide-react';
const MyTrips = () => {
    const navigate = useNavigate();
    const trips = [
        { id: '1', title: 'Weekend in Paris', dates: 'Oct 12-14', loc: 'Paris, France', type: 'Solo Trip', daysLeft: '3 Days Left', desc: 'Exploring the artistic districts of Montmartre and visiting the Louvre.', img: 'https://picsum.photos/seed/paris/400/300', status: 'Upcoming' },
        { id: '2', title: 'Vietnam Adventure', dates: 'Oct 05-20', loc: 'Vietnam', type: 'Group Trip', live: true, desc: 'Backpacking through Hanoi, Ha Long Bay, and Ho Chi Minh City.', img: 'https://picsum.photos/seed/vietnam/400/300', status: 'Ongoing', partners: 3 },
        { id: '3', title: 'Bali Yoga Retreat', dates: 'Dec 10-20', loc: 'Bali, Indonesia', type: 'Group Trip', timeTo: 'In 2 Months', desc: '10 days of mindfulness and yoga in Ubud.', img: 'https://picsum.photos/seed/bali/400/300', status: 'Upcoming', partners: 1 },
        { id: '4', title: 'Swiss Alps Hiking', dates: 'TBD', loc: 'Switzerland', type: 'Solo Trip', draft: true, desc: 'Planning a summer hiking trip. Need to finalize dates.', img: 'https://picsum.photos/seed/swiss/400/300', status: 'Draft' },
    ];
    return (<div className="max-w-7xl mx-auto">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-6 mb-10">
        <div>
          <h1 className="text-3xl font-extrabold tracking-tight">My Trips</h1>
          <p className="text-slate-500 mt-1">Manage your itineraries, travel partners, and requests.</p>
        </div>
        <button onClick={() => navigate('/trips/create')} className="bg-primary hover:bg-primary-hover text-white px-6 py-3 rounded-xl font-bold shadow-xl shadow-primary/20 transition-all flex items-center justify-center gap-2">
          <Plus size={20}/>
          Plan New Trip
        </button>
      </div>

      <div className="flex flex-col lg:flex-row items-start lg:items-center justify-between gap-6 mb-8 border-b dark:border-slate-800 pb-1">
        <div className="flex items-center gap-8 overflow-x-auto w-full lg:w-auto hide-scrollbar">
          {['Upcoming', 'Ongoing', 'Completed', 'Requests'].map((tab, idx) => (<button key={tab} className={`relative pb-4 text-sm font-bold whitespace-nowrap transition-all ${idx === 0 ? 'text-primary' : 'text-slate-500 hover:text-slate-300'}`}>
              {tab}
              {idx === 0 && <span className="ml-2 text-[10px] bg-primary/20 text-primary px-2 py-0.5 rounded-full">3</span>}
              {idx === 3 && <span className="ml-2 text-[10px] bg-orange-500/10 text-orange-500 px-2 py-0.5 rounded-full">2</span>}
              {idx === 0 && <div className="absolute bottom-0 left-0 w-full h-0.5 bg-primary rounded-t-full"/>}
            </button>))}
        </div>
        <div className="flex items-center gap-3 w-full lg:w-auto mb-2 lg:mb-0">
          <div className="relative flex-1 lg:w-64">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" size={16}/>
            <input className="w-full bg-white dark:bg-surface-dark border dark:border-slate-800 text-sm rounded-lg pl-10 pr-4 py-2.5 focus:ring-2 focus:ring-primary outline-none" placeholder="Search destinations..."/>
          </div>
          <button className="p-2.5 bg-white dark:bg-surface-dark border dark:border-slate-800 rounded-lg text-slate-500 hover:text-primary transition-all">
            <Filter size={18}/>
          </button>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
        {trips.map((trip) => (<div key={trip.id} className="bg-white dark:bg-surface-dark rounded-2xl overflow-hidden border dark:border-slate-800 group hover:border-primary/50 transition-all hover:shadow-2xl">
            <div className="relative h-48">
              <img src={trip.img} className={`w-full h-full object-cover transition-transform duration-700 group-hover:scale-105 ${trip.draft ? 'grayscale' : ''}`} alt={trip.title}/>
              <div className="absolute inset-0 bg-gradient-to-t from-black/70 to-transparent"/>
              
              <div className="absolute top-3 left-3 flex gap-2">
                <span className={`px-2.5 py-1 rounded-lg text-[10px] font-bold uppercase tracking-wider ${trip.live ? 'bg-primary text-white animate-pulse' : 'bg-white/90 dark:bg-slate-900/90 text-slate-900 dark:text-white'}`}>
                  {trip.live ? 'Live Now' : (trip.daysLeft || trip.timeTo || trip.status)}
                </span>
              </div>
              
              <div className="absolute top-3 right-3">
                <button className="bg-black/30 backdrop-blur-md p-1.5 rounded-full text-white hover:bg-black/50"><MoreHorizontal size={14}/></button>
              </div>

              <div className="absolute bottom-3 left-3">
                 <span className={`px-2 py-0.5 rounded text-[9px] font-bold text-white uppercase tracking-widest ${trip.type.includes('Solo') ? 'bg-blue-500' : 'bg-purple-500'}`}>{trip.type}</span>
              </div>
            </div>

            <div className="p-5">
              <div className="flex justify-between items-start mb-2">
                <h3 className="text-lg font-bold group-hover:text-primary transition-colors">{trip.title}</h3>
                <span className="text-[10px] font-bold text-slate-500 bg-slate-100 dark:bg-slate-800 px-2 py-1 rounded tracking-widest">{trip.dates}</span>
              </div>
              <p className="text-xs text-slate-500 line-clamp-2 mb-4 leading-relaxed">{trip.desc}</p>
              
              <div className="flex items-center justify-between pt-4 border-t dark:border-slate-800">
                <div className="flex -space-x-2">
                  <img src="https://picsum.photos/seed/me/100/100" className="w-8 h-8 rounded-full border-2 border-white dark:border-surface-dark" alt="me"/>
                  {trip.partners && Array.from({ length: trip.partners }).map((_, i) => (<img key={i} src={`https://picsum.photos/seed/p${trip.id}${i}/100/100`} className="w-8 h-8 rounded-full border-2 border-white dark:border-surface-dark" alt="partner"/>))}
                </div>
                <button onClick={() => navigate(trip.draft ? '/trips/create' : `/trips/${trip.id}`)} className="text-xs font-bold text-primary hover:translate-x-1 transition-all flex items-center gap-1">
                  {trip.draft ? 'Resume' : 'Manage'} <ArrowRight size={14}/>
                </button>
              </div>
            </div>
          </div>))}

        <button onClick={() => navigate('/trips/create')} className="flex flex-col items-center justify-center min-h-[380px] rounded-2xl border-2 border-dashed border-slate-200 dark:border-slate-800 hover:border-primary hover:bg-primary/5 transition-all group">
          <div className="w-16 h-16 rounded-full bg-slate-100 dark:bg-slate-800 flex items-center justify-center mb-4 group-hover:bg-primary group-hover:text-white transition-all">
            <Plus size={32}/>
          </div>
          <span className="font-bold text-lg">Create New Trip</span>
          <p className="text-sm text-slate-500 mt-1">Start planning your next adventure</p>
        </button>
      </div>

      <div className="mt-12 bg-white dark:bg-surface-dark rounded-2xl border dark:border-slate-800 p-6 flex flex-col md:flex-row items-center gap-6">
        <div className="w-24 h-24 shrink-0 rounded-xl overflow-hidden bg-slate-100 dark:bg-slate-800"><Globe className="w-full h-full p-6 text-slate-300"/></div>
        <div className="flex-1 text-center md:text-left">
          <h4 className="text-lg font-bold mb-1">Global Travel Stats</h4>
          <p className="text-sm text-slate-500">You've traveled 12,450 miles across 8 countries this year. Ready for more?</p>
          <div className="flex gap-4 mt-4 justify-center md:justify-start">
            <div className="text-center"><p className="text-lg font-bold text-primary">8</p><p className="text-[10px] text-slate-500 uppercase font-bold">Countries</p></div>
            <div className="text-center"><p className="text-lg font-bold text-primary">24</p><p className="text-[10px] text-slate-500 uppercase font-bold">Cities</p></div>
            <div className="text-center"><p className="text-lg font-bold text-primary">3</p><p className="text-[10px] text-slate-500 uppercase font-bold">Badges</p></div>
          </div>
        </div>
        <button className="px-6 py-3 border-2 border-primary text-primary hover:bg-primary hover:text-white transition-all rounded-xl font-bold">Explore Analytics</button>
      </div>
    </div>);
};
export default MyTrips;
