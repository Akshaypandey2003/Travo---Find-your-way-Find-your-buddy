import React from 'react';
/* Replaced non-existent FlightTakeoff with PlaneTakeoff */
import { UserPlus, PlaneTakeoff, Heart, MessageSquare, CheckCircle2, Gift, ArrowRight } from 'lucide-react';
const Activity = () => {
    const sections = [
        {
            title: 'New',
            items: [
                { type: 'request', user: { name: 'Alex Chen', avatar: 'https://picsum.photos/seed/ac/100/100' }, content: 'wants to join your trip "Japan 2024"', time: '2M AGO', isNew: true, actions: true },
                { type: 'update', content: 'Flight details updated for "Hiking the Alps". Check the new departure times.', time: '15M AGO', isNew: true, link: 'View Itinerary' }
            ]
        },
        {
            title: 'Earlier',
            items: [
                { type: 'like', users: [{ name: 'Marcus' }, { name: 'Elena' }], content: 'liked your photo from Santorini', time: '2H AGO' },
                { type: 'comment', user: { name: 'Sarah Jenkins', avatar: 'https://picsum.photos/seed/sj/100/100' }, content: 'commented on your post:', subContent: 'This looks absolutely amazing! Did you stay at the hostel near the cliff?', time: '5H AGO', actions: ['Reply', 'Like'] },
                { type: 'system', content: 'Your profile verification was successful! You can now host trips.', time: '1D AGO', icon: CheckCircle2, iconColor: 'text-primary' }
            ]
        }
    ];
    return (<div className="max-w-4xl mx-auto flex flex-col xl:flex-row gap-10">
      <div className="flex-1 space-y-12">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-3xl font-extrabold tracking-tight">Activity</h1>
            <p className="text-[10px] font-bold text-slate-500 uppercase tracking-widest mt-1.5">Stay updated with your travel network</p>
          </div>
          <button className="text-[10px] font-black text-primary uppercase tracking-[0.2em] hover:underline flex items-center gap-2">
            <CheckCircle2 size={16}/> Mark all read
          </button>
        </div>

        <div className="flex gap-3 overflow-x-auto pb-2 scrollbar-hide border-b border-slate-100 dark:border-gray-900">
          {['All Activity', 'Requests', 'Likes', 'Comments', 'Trip Updates'].map((t, i) => (<button key={t} className={`px-6 py-2.5 rounded-xl text-[10px] font-black uppercase tracking-[0.15em] transition-all whitespace-nowrap ${i === 0 ? 'bg-primary text-white shadow-xl shadow-primary/25' : 'bg-white dark:bg-surface-dark border border-slate-100 dark:border-gray-900 text-slate-500 hover:border-primary/50 hover:text-primary'}`}>
              {t} {i === 1 && <span className="ml-2 bg-white text-primary px-1.5 rounded-md text-[8px]">2</span>}
            </button>))}
        </div>

        {sections.map((sec) => (<div key={sec.title} className="space-y-6">
            <h2 className="text-[10px] font-black uppercase tracking-[0.3em] text-slate-400 ml-1">{sec.title}</h2>
            <div className="space-y-4">
              {sec.items.map((item, idx) => {
                const Icon = item.icon;
                return (<div key={idx} className={`bg-white dark:bg-surface-dark rounded-3xl p-6 border border-slate-100 dark:border-gray-900 flex items-start gap-6 group hover:border-primary/40 transition-all duration-300 cursor-pointer ${item.isNew ? 'ring-1 ring-primary/20 shadow-2xl shadow-primary/5' : 'opacity-90 hover:opacity-100 shadow-sm'}`}>
                  <div className="relative shrink-0 pt-1">
                    {item.type === 'request' || item.type === 'comment' ? (<div className="relative">
                        <img src={item.user?.avatar} className="w-14 h-14 rounded-2xl object-cover border-2 border-primary/10 shadow-lg" alt="u"/>
                        <div className={`absolute -bottom-2 -right-2 w-7 h-7 rounded-lg flex items-center justify-center ring-4 ring-white dark:ring-surface-dark ${item.type === 'request' ? 'bg-primary' : 'bg-primary shadow-lg shadow-primary/20'}`}>
                          {item.type === 'request' ? <UserPlus size={14} className="text-white"/> : <MessageSquare size={14} className="text-white"/>}
                        </div>
                      </div>) : item.type === 'update' ? (<div className="w-14 h-14 rounded-2xl bg-primary/10 flex items-center justify-center text-primary border border-primary/20 shadow-inner"><PlaneTakeoff size={28}/></div>) : item.type === 'system' ? (<div className="w-14 h-14 rounded-2xl bg-primary/10 flex items-center justify-center border border-primary/20 shadow-inner">
                        {Icon && <Icon size={28} className={item.iconColor}/>}
                      </div>) : (<div className="relative">
                        <div className="flex -space-x-5">
                          {[1, 2].map(i => <img key={i} src={`https://picsum.photos/seed/not${idx}${i}/100/100`} className="w-12 h-12 rounded-2xl border-4 border-white dark:border-surface-dark object-cover shadow-lg" alt="p"/>)}
                        </div>
                        <div className="absolute -bottom-1 -right-1 w-6 h-6 bg-primary rounded-lg flex items-center justify-center ring-4 ring-white dark:ring-surface-dark text-white shadow-xl"><Heart size={12} className="fill-current"/></div>
                      </div>)}
                  </div>
                  <div className="flex-1">
                    <div className="flex justify-between items-start gap-6">
                      <div className="space-y-1.5">
                        <p className="text-sm leading-relaxed font-medium text-slate-900 dark:text-white">
                          {item.user && <span className="font-extrabold hover:text-primary transition-colors">{item.user.name} </span>}
                          {item.user && <><span className="font-extrabold text-primary">{item.user.name}</span> and <span className="font-extrabold text-primary">4 others</span> </>}
                          <span className="text-slate-600 dark:text-slate-400">{item.content}</span>
                        </p>
                        {item.subContent && (<p className="text-xs text-slate-500 italic border-l-4 border-primary/20 pl-4 py-2 mt-3 bg-slate-50 dark:bg-background-dark/50 rounded-r-2xl font-medium">"{item.subContent}"</p>)}
                        {item.link && (<button className="text-[10px] font-black text-primary hover:text-primary-hover flex items-center gap-2 mt-4 uppercase tracking-[0.2em]">
                            {item.link} <ArrowRight size={14}/>
                          </button>)}
                        {item.actions === true && (<div className="flex gap-4 mt-5">
                            <button className="px-8 py-2.5 bg-primary hover:bg-primary-hover text-white text-[10px] font-black uppercase tracking-[0.2em] rounded-xl shadow-xl shadow-primary/30 transition-all">Accept</button>
                            <button className="px-8 py-2.5 bg-slate-100 dark:bg-surface-lighter hover:bg-slate-200 dark:hover:bg-surface-dark border border-slate-200 dark:border-gray-800 text-slate-500 text-[10px] font-black uppercase tracking-[0.2em] rounded-xl transition-all">Decline</button>
                          </div>)}
                        {Array.isArray(item.actions) && (<div className="flex gap-6 mt-5">
                            {item.actions.map(a => <button key={a} className="text-[9px] font-black text-primary uppercase tracking-[0.25em] hover:underline decoration-2 underline-offset-4">{a}</button>)}
                          </div>)}
                      </div>
                      <span className="text-[9px] font-black text-slate-400 whitespace-nowrap tracking-widest">{item.time}</span>
                    </div>
                  </div>
                  {item.isNew && <div className="w-2.5 h-2.5 rounded-full bg-primary mt-3 ml-2 shadow-[0_0_12px_rgba(255,120,46,0.8)] animate-pulse"/>}
                  </div>);
            })}
            </div>
          </div>))}
      </div>

      <div className="w-85 shrink-0 space-y-10 hidden xl:block">
        <div className="space-y-6">
          <h3 className="text-[10px] font-black text-slate-400 uppercase tracking-[0.3em] ml-1">Trending Now</h3>
          <div className="space-y-4">
            {[
            { name: 'Amalfi Coast', count: 12, img: 'https://picsum.photos/seed/amalfi/400/200' },
            { name: 'Chiang Mai', count: 8, img: 'https://picsum.photos/seed/thai/400/200' }
        ].map(loc => (<div key={loc.name} className="relative h-36 rounded-3xl overflow-hidden group cursor-pointer border border-slate-100 dark:border-gray-900">
                <img src={loc.img} className="w-full h-full object-cover transition duration-1000 group-hover:scale-110 group-hover:rotate-1" alt="loc"/>
                <div className="absolute inset-0 bg-gradient-to-t from-black/90 via-black/40 to-transparent p-6 flex flex-col justify-end">
                  <h4 className="font-extrabold text-lg text-white tracking-tight">{loc.name}</h4>
                  <p className="text-[9px] text-primary font-black uppercase tracking-[0.2em] mt-1">{loc.count} ACTIVE TRIPS</p>
                </div>
              </div>))}
          </div>
        </div>

        <div className="bg-primary/5 border border-primary/20 rounded-[2.5rem] p-10 text-center space-y-8 shadow-inner relative overflow-hidden group">
           <div className="relative z-10">
             <div className="w-16 h-16 rounded-2xl bg-primary flex items-center justify-center mx-auto text-white shadow-2xl shadow-primary/40 transform group-hover:rotate-12 transition-transform duration-500"><Gift size={32}/></div>
             <div className="mt-8">
               <h4 className="text-xl font-extrabold mb-3 tracking-tight dark:text-white">Invite friends</h4>
               <p className="text-[11px] text-slate-500 font-bold leading-relaxed uppercase tracking-wider">Earn travel credits for every friend who books a trip via your link.</p>
             </div>
             <button className="text-[10px] font-black text-primary uppercase tracking-[0.3em] hover:text-primary-hover mt-8 underline decoration-2 underline-offset-8 transition-all">Get Invite Link</button>
           </div>
           {/* Decorative elements */}
           <div className="absolute top-0 right-0 w-24 h-24 bg-primary/10 rounded-full blur-2xl -mr-12 -mt-12"/>
           <div className="absolute bottom-0 left-0 w-16 h-16 bg-primary/10 rounded-full blur-xl -ml-8 -mb-8"/>
        </div>
      </div>
    </div>);
};
export default Activity;
