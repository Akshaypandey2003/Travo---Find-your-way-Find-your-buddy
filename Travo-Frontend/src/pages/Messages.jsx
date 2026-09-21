import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
// Added CheckCircle2 and PlaneTakeoff to imports
import { Search, Edit, Send, PlusCircle, Smile, Image as ImageIcon, Phone, Video, Info, FileText, Ticket, CheckCircle2, PlaneTakeoff } from 'lucide-react';
const Messages = () => {
    const navigate = useNavigate();
    const [inputText, setInputText] = useState('');
    const contacts = [
        { id: '1', name: 'Sarah Jenkins', avatar: 'https://picsum.photos/seed/sarah/100/100', lastMsg: 'That looks incredible! Is it near the...', time: 'Now', online: true, active: true },
        { id: '2', name: 'Alex Chen', avatar: 'https://picsum.photos/seed/alex/100/100', lastMsg: 'Can you send me the itinerary PDF?', time: '14m', unread: 2 },
        { id: '3', name: 'Tokyo Trip Group 🇯🇵', avatar: '', isGroup: true, lastMsg: 'Mike: Just booked the tickets!', time: '2h' },
        { id: '4', name: 'Emma Wilson', avatar: 'https://picsum.photos/seed/emma/100/100', lastMsg: 'Thanks for the recommendation!', time: 'Yesterday' },
    ];
    return (<div className="flex h-[calc(100vh-140px)] -m-4 md:-m-8 bg-white dark:bg-background-dark border dark:border-slate-800 rounded-2xl overflow-hidden shadow-2xl">
      {/* Sidebar List */}
      <aside className="w-80 md:w-96 flex flex-col border-r border-slate-200 dark:border-slate-800 bg-slate-50/30 dark:bg-surface-darker shrink-0">
        <div className="p-5">
          <div className="flex items-center justify-between mb-6">
            <h1 className="text-xl font-bold">Messages</h1>
            <button className="p-2 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors">
              <Edit size={20}/>
            </button>
          </div>
          <div className="relative group mb-4">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" size={18}/>
            <input className="w-full bg-white dark:bg-background-dark border-none rounded-lg py-2.5 pl-10 pr-4 text-sm focus:ring-2 focus:ring-primary transition-all" placeholder="Search travelers..."/>
          </div>
          <div className="flex gap-2 overflow-x-auto pb-1 scrollbar-hide">
            {['All', 'Trip Groups', 'Mentions'].map((tab, i) => (<button key={tab} className={`px-4 py-1.5 rounded-full text-xs font-semibold whitespace-nowrap transition-all ${i === 0 ? 'bg-primary text-white shadow-md shadow-primary/20' : 'bg-white dark:bg-slate-800 text-slate-500 hover:bg-slate-100 dark:hover:bg-slate-700'}`}>
                {tab}
              </button>))}
          </div>
        </div>
        <div className="flex-1 overflow-y-auto px-3 space-y-1">
          {contacts.map((c) => (<div key={c.id} className={`p-3 rounded-xl cursor-pointer transition-all ${c.active ? 'bg-primary/10 dark:bg-primary/20 border border-primary/20' : 'hover:bg-slate-100 dark:hover:bg-slate-800/50'}`}>
              <div className="flex gap-3">
                <div className="relative shrink-0">
                  {c.isGroup ? (<div className="w-12 h-12 bg-gradient-to-br from-purple-500 to-indigo-600 rounded-full flex items-center justify-center text-white font-bold text-xs">TK</div>) : (<img src={c.avatar} className="w-12 h-12 rounded-full object-cover border-2 border-white dark:border-slate-900" alt={c.name}/>)}
                  {c.online && <div className="absolute bottom-0 right-0 w-3.5 h-3.5 bg-green-500 border-2 border-white dark:border-slate-900 rounded-full"/>}
                </div>
                <div className="flex-1 min-w-0">
                  <div className="flex justify-between items-start mb-0.5">
                    <h3 className={`text-sm truncate ${c.active || c.unread ? 'font-bold' : 'font-semibold text-slate-600 dark:text-slate-300'}`}>{c.name}</h3>
                    <span className={`text-[10px] ${c.active ? 'text-primary font-bold' : 'text-slate-500'}`}>{c.time}</span>
                  </div>
                  <div className="flex justify-between items-center">
                    <p className={`text-xs truncate ${c.unread ? 'text-slate-900 dark:text-white font-bold' : 'text-slate-500'}`}>{c.lastMsg}</p>
                    {c.unread && <span className="w-5 h-5 flex items-center justify-center bg-primary text-white text-[10px] font-bold rounded-full shadow-lg shadow-primary/20">{c.unread}</span>}
                  </div>
                </div>
              </div>
            </div>))}
        </div>
      </aside>

      {/* Main Chat Area */}
      <main className="flex-1 flex flex-col bg-white dark:bg-background-dark min-w-0">
        <header className="h-20 border-b border-slate-200 dark:border-slate-800 flex items-center justify-between px-6 shrink-0">
          <div className="flex items-center gap-4">
            <div className="relative">
              <img src="https://picsum.photos/seed/sarah/100/100" className="w-10 h-10 rounded-full object-cover ring-2 ring-primary/20" alt="Sarah"/>
              <div className="absolute bottom-0 right-0 w-2.5 h-2.5 bg-green-500 ring-2 ring-white dark:ring-background-dark rounded-full"/>
            </div>
            <div>
              <h2 className="font-bold text-sm">Sarah Jenkins</h2>
              <p className="text-[10px] text-green-500 font-medium flex items-center gap-1"><span className="w-1.5 h-1.5 rounded-full bg-green-500"/> Active now</p>
            </div>
          </div>
          <div className="flex items-center gap-2">
            <button className="p-2 text-slate-500 hover:text-primary transition-colors"><Phone size={20}/></button>
            <button className="p-2 text-slate-500 hover:text-primary transition-colors"><Video size={20}/></button>
            <div className="w-px h-6 bg-slate-200 dark:bg-slate-800 mx-2"/>
            <button className="p-2 text-slate-500 hover:text-primary transition-colors"><Info size={20}/></button>
          </div>
        </header>

        <div className="flex-1 overflow-y-auto p-6 space-y-6">
          <div className="flex justify-center">
            <span className="px-3 py-1 bg-slate-100 dark:bg-slate-800 text-slate-500 dark:text-slate-400 text-[10px] rounded-full font-bold uppercase tracking-wider">Today, 10:23 AM</span>
          </div>

          <div className="flex gap-3 max-w-lg">
            <img src="https://picsum.photos/seed/sarah/100/100" className="w-8 h-8 rounded-full object-cover self-end" alt="S"/>
            <div className="space-y-1">
              <div className="bg-slate-100 dark:bg-surface-dark px-4 py-2.5 rounded-2xl rounded-bl-none">
                <p className="text-sm">Hey! I found this amazing hotel near the beach in Bali we talked about. 🏖️</p>
              </div>
              <div className="overflow-hidden rounded-2xl border dark:border-slate-800">
                <img src="https://picsum.photos/seed/bali/400/300" className="w-64 h-40 object-cover" alt="Resort"/>
              </div>
              <span className="text-[9px] text-slate-400 font-medium ml-1">10:23 AM</span>
            </div>
          </div>

          <div className="flex gap-3 max-w-lg ml-auto flex-row-reverse">
            <img src="https://picsum.photos/seed/me/100/100" className="w-8 h-8 rounded-full object-cover self-end" alt="Me"/>
            <div className="space-y-1 flex flex-col items-end">
              <div className="bg-primary text-white px-4 py-2.5 rounded-2xl rounded-br-none shadow-lg shadow-primary/20">
                <p className="text-sm font-medium">Wow! That looks absolutely stunning. 😍</p>
              </div>
              <span className="text-[9px] text-slate-400 font-medium mr-1 flex items-center gap-1">10:25 AM <CheckCircle2 size={10} className="text-primary fill-current"/></span>
            </div>
          </div>

          <div className="flex gap-3 max-w-lg ml-auto flex-row-reverse">
             <div className="w-8 h-8 shrink-0"/>
             <div className="bg-white dark:bg-surface-dark border border-primary/30 p-4 rounded-2xl rounded-br-none w-64 shadow-sm">
                <div className="flex items-center gap-2 mb-3 pb-2 border-b border-slate-100 dark:border-slate-800">
                  <PlaneTakeoff size={14} className="text-primary"/>
                  <span className="text-[10px] font-bold uppercase text-primary tracking-wider">Trip Proposal</span>
                </div>
                <h4 className="font-bold text-sm mb-1">Bali Getaway 2024</h4>
                <p className="text-[10px] text-slate-500 mb-4">Proposed Dates: Oct 12 - Oct 20</p>
                <button className="w-full py-2 bg-primary hover:bg-primary-hover text-white text-[10px] font-bold rounded-lg transition-all" onClick={() => navigate('/trips/1')}>View Itinerary</button>
             </div>
          </div>
        </div>

        <div className="p-4 bg-white dark:bg-background-dark border-t dark:border-slate-800">
          <div className="max-w-4xl mx-auto flex items-end gap-2 bg-slate-50 dark:bg-surface-dark p-2 rounded-2xl border dark:border-slate-800">
            <button className="p-2 text-slate-400 hover:text-primary transition-colors"><PlusCircle size={22}/></button>
            <div className="flex-1 py-1">
              <textarea value={inputText} onChange={(e) => setInputText(e.target.value)} className="w-full bg-transparent border-none p-2 text-sm focus:ring-0 resize-none max-h-32" placeholder="Type your message..." rows={1}/>
            </div>
            <div className="flex items-center gap-1 pb-1">
              <button className="p-1.5 text-slate-400 hover:text-primary"><Smile size={20}/></button>
              <button className="p-1.5 text-slate-400 hover:text-primary"><ImageIcon size={20}/></button>
              <button className="p-2 bg-primary hover:bg-primary-hover text-white rounded-xl shadow-lg shadow-primary/20 ml-1">
                <Send size={18}/>
              </button>
            </div>
          </div>
        </div>
      </main>

      {/* Context Sidebar */}
      <aside className="w-72 hidden xl:flex flex-col border-l border-slate-200 dark:border-slate-800 shrink-0">
        <div className="p-8 flex flex-col items-center border-b dark:border-slate-800">
          <img src="https://picsum.photos/seed/sarah/200/200" className="w-24 h-24 rounded-full object-cover mb-4 ring-4 ring-primary/10 shadow-xl" alt="S"/>
          <h3 className="text-lg font-bold">Sarah Jenkins</h3>
          <p className="text-xs text-slate-500 mb-6 font-medium">Adventure Seeker & Foodie</p>
          <div className="flex gap-3 w-full">
            <button className="flex-1 py-2 rounded-lg bg-slate-100 dark:bg-slate-800 text-xs font-bold transition-all hover:bg-slate-200" onClick={() => navigate(`/profile/${user.id}`)}>Profile</button>
            <button className="flex-1 py-2 rounded-lg bg-slate-100 dark:bg-slate-800 text-xs font-bold transition-all hover:bg-slate-200">Mute</button>
          </div>
        </div>
        <div className="p-6 space-y-8 overflow-y-auto">
          <div>
            <h4 className="text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-4">Shared Media</h4>
            <div className="grid grid-cols-3 gap-2">
              {[1, 2, 3, 4, 5, 6].map(i => (<img key={i} src={`https://picsum.photos/seed/trip${i}/150/150`} className="aspect-square rounded-lg object-cover cursor-pointer hover:opacity-80 transition-opacity" alt="Trip"/>))}
            </div>
          </div>
          <div>
            <h4 className="text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-4">Files & Trip Plans</h4>
            <div className="space-y-3">
              <div className="flex items-center gap-3 p-2 bg-slate-50 dark:bg-slate-800/50 rounded-lg border dark:border-slate-800">
                <div className="w-10 h-10 rounded-lg bg-orange-100 text-orange-600 flex items-center justify-center shrink-0"><FileText size={18}/></div>
                <div className="min-w-0">
                  <p className="text-xs font-bold truncate">Bali Itinerary</p>
                  <p className="text-[9px] text-slate-400 uppercase">PDF • 2.4 MB</p>
                </div>
              </div>
              <div className="flex items-center gap-3 p-2 bg-slate-50 dark:bg-slate-800/50 rounded-lg border dark:border-slate-800">
                <div className="w-10 h-10 rounded-lg bg-blue-100 text-blue-600 flex items-center justify-center shrink-0"><Ticket size={18}/></div>
                <div className="min-w-0">
                  <p className="text-xs font-bold truncate">Flight Tickets</p>
                  <p className="text-[9px] text-slate-400 uppercase">PKpass • 120 KB</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </aside>
    </div>);
};
export default Messages;
