import React from 'react';
import { User, Lock, Eye, Bell, CreditCard, Camera, ShieldAlert, Edit2 } from 'lucide-react';
const Settings = () => {
    return (<div className="max-w-6xl mx-auto">
       <div className="mb-12">
        <h1 className="text-4xl font-extrabold tracking-tight">Settings</h1>
        <p className="text-[10px] font-bold text-slate-500 uppercase tracking-[0.25em] mt-2">Manage your account details & privacy preferences</p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-12">
        {/* Nav */}
        <aside className="lg:col-span-3">
          <nav className="space-y-2">
            {[
            { label: 'Account', icon: User, active: true },
            { label: 'Security', icon: Lock },
            { label: 'Privacy', icon: Eye },
            { label: 'Notifications', icon: Bell },
            { label: 'Billing', icon: CreditCard },
        ].map(item => (<button key={item.label} className={`w-full flex items-center gap-4 px-6 py-4 rounded-2xl text-[10px] font-black uppercase tracking-[0.2em] transition-all ${item.active ? 'bg-primary text-white shadow-2xl shadow-primary/30' : 'text-slate-500 hover:bg-slate-50 dark:hover:bg-surface-dark hover:text-primary'}`}>
                <item.icon size={18}/>
                {item.label}
              </button>))}
          </nav>
          <div className="mt-12 p-8 bg-primary/5 border border-primary/10 rounded-[2rem] shadow-inner">
            <div className="flex items-center gap-3 text-primary mb-5">
              <ShieldAlert size={22}/>
              <h4 className="font-black text-[10px] uppercase tracking-[0.3em]">Safety Check</h4>
            </div>
            <p className="text-[10px] text-slate-500 leading-relaxed mb-8 font-bold uppercase tracking-wider">Update your emergency contact information before your next solo trip.</p>
            <button className="w-full py-3 bg-white dark:bg-surface-dark border border-primary/30 text-primary hover:bg-primary hover:text-white transition-all rounded-xl text-[10px] font-black uppercase tracking-[0.2em] shadow-sm">Update Info</button>
          </div>
        </aside>

        {/* Content */}
        <div className="lg:col-span-9 space-y-12">
          <section className="bg-white dark:bg-surface-dark border border-slate-100 dark:border-gray-900 rounded-[2.5rem] shadow-2xl shadow-slate-200/50 dark:shadow-none overflow-hidden">
            <div className="p-10 border-b border-slate-100 dark:border-gray-900 flex justify-between items-center bg-slate-50/50 dark:bg-surface-darker/50">
              <div>
                <h3 className="text-xl font-extrabold tracking-tight mb-1.5">Public Profile</h3>
                <p className="text-[10px] text-slate-500 font-black uppercase tracking-widest">Your information is visible to the community.</p>
              </div>
              <span className="bg-emerald-500/10 text-emerald-500 px-4 py-1.5 rounded-full text-[9px] font-black uppercase tracking-[0.2em] border border-emerald-500/20 shadow-lg shadow-emerald-500/10">Active Profile</span>
            </div>
            <div className="p-10 space-y-12">
               <div className="flex flex-col md:flex-row gap-12 items-start">
                 <div className="relative group cursor-pointer shrink-0">
                    <img src="https://picsum.photos/seed/me_settings/300/300" className="w-32 h-32 rounded-3xl object-cover ring-4 ring-slate-100 dark:ring-surface-lighter shadow-2xl transition-transform duration-500 group-hover:scale-105" alt="m"/>
                    <div className="absolute inset-0 bg-primary/60 rounded-3xl flex items-center justify-center opacity-0 group-hover:opacity-100 transition-all duration-300"><Camera className="text-white" size={32}/></div>
                    <div className="absolute -bottom-3 -right-3 bg-primary text-white p-2.5 rounded-2xl shadow-2xl border-4 border-white dark:border-surface-dark"><Edit2 size={16}/></div>
                 </div>
                 <div className="flex-1 space-y-10 w-full">
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                       <div className="space-y-2">
                         <label className="text-[10px] font-black text-slate-400 uppercase tracking-[0.3em] ml-1">Username</label>
                         <div className="relative">
                            <span className="absolute left-5 top-1/2 -translate-y-1/2 text-primary text-[10px] font-black uppercase tracking-widest opacity-60">travo.app/</span>
                            <input className="w-full bg-slate-50 dark:bg-background-dark border border-slate-100 dark:border-gray-900 rounded-2xl py-4 pl-24 pr-5 text-sm font-bold focus:ring-2 focus:ring-primary outline-none transition-all" defaultValue="sarah_travels"/>
                         </div>
                       </div>
                       <div className="space-y-2">
                         <label className="text-[10px] font-black text-slate-400 uppercase tracking-[0.3em] ml-1">Email Address</label>
                         <input className="w-full bg-slate-50 dark:bg-background-dark border border-slate-100 dark:border-gray-900 rounded-2xl py-4 px-5 text-sm font-bold focus:ring-2 focus:ring-primary outline-none transition-all" defaultValue="sarah@example.com"/>
                       </div>
                    </div>
                    <div className="space-y-2">
                       <label className="text-[10px] font-black text-slate-400 uppercase tracking-[0.3em] ml-1">Bio</label>
                       <textarea className="w-full bg-slate-50 dark:bg-background-dark border border-slate-100 dark:border-gray-900 rounded-[2rem] p-6 text-sm font-medium focus:ring-2 focus:ring-primary outline-none resize-none h-40 leading-relaxed" defaultValue="Digital nomad exploring South East Asia. Coffee addict and mountain hiker. 🏔️☕️"/>
                       <div className="flex justify-between items-center px-1">
                          <p className="text-[9px] text-slate-400 font-bold uppercase tracking-widest">Max 500 characters</p>
                          <p className="text-[9px] text-primary font-black uppercase tracking-[0.2em]">Hyperlinks enabled</p>
                       </div>
                    </div>
                 </div>
               </div>
               <div className="flex justify-end pt-8 border-t border-slate-100 dark:border-gray-900">
                  <button className="px-12 py-4 bg-primary hover:bg-primary-hover text-white rounded-2xl font-black uppercase tracking-[0.2em] shadow-2xl shadow-primary/30 transition-all transform hover:-translate-y-1 active:scale-95 text-xs">Save Changes</button>
               </div>
            </div>
          </section>

          <section className="bg-white dark:bg-surface-dark border border-slate-100 dark:border-gray-900 rounded-[2.5rem] shadow-xl overflow-hidden">
             <div className="p-10 border-b border-slate-100 dark:border-gray-900 bg-slate-50/50 dark:bg-surface-darker/50">
                <h3 className="text-xl font-extrabold tracking-tight mb-1.5">Privacy Preferences</h3>
                <p className="text-[10px] text-slate-500 font-black uppercase tracking-widest">Control who can interact with you.</p>
             </div>
             <div className="p-10 space-y-10">
                {[
            { label: 'Profile Visibility', desc: 'Allow non-friends to view your travel history.', active: true },
            { label: 'Location Sharing', desc: 'Share your approximate real-time location with partners.' },
            { label: 'Friend Discovery', desc: 'Let others find you by email or username.', active: true }
        ].map((item, idx) => (<div key={idx} className="flex items-center justify-between gap-10 pb-10 border-b last:border-0 last:pb-0 border-slate-50 dark:border-gray-900">
                    <div className="flex-1">
                      <p className="text-base font-extrabold mb-1 tracking-tight">{item.label}</p>
                      <p className="text-xs text-slate-500 font-medium leading-relaxed">{item.desc}</p>
                    </div>
                    <button className={`w-16 h-8 rounded-full p-1.5 shadow-inner transition-all duration-500 ${item.active ? 'bg-primary' : 'bg-slate-200 dark:bg-surface-lighter'}`}>
                      <div className={`w-5 h-5 bg-white rounded-full shadow-lg transition-transform duration-300 ${item.active ? 'translate-x-8' : 'translate-x-0'}`}/>
                    </button>
                  </div>))}
             </div>
          </section>

          <div className="p-10 bg-red-500/[0.03] border border-red-500/10 rounded-[2.5rem] flex flex-col md:flex-row items-center justify-between gap-10">
             <div className="flex-1">
                <h3 className="text-xl font-extrabold text-red-500 mb-3 tracking-tight">Danger Zone</h3>
                <p className="text-xs text-slate-500 leading-relaxed font-bold uppercase tracking-wider">Once you delete your account, there is no going back. All your data will be permanently purged from our servers.</p>
             </div>
             <button className="px-10 py-4 bg-red-500/10 text-red-500 border border-red-500/20 hover:bg-red-500 hover:text-white transition-all rounded-2xl font-black uppercase tracking-[0.2em] text-[10px] shadow-lg shadow-red-500/10 shrink-0">Delete Account</button>
          </div>
        </div>
      </div>
    </div>);
};
export default Settings;
