import { useState } from "react";
import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";
import {
  LayoutDashboard,
  Compass,
  Map as MapIcon,
  MessageSquare,
  Bell,
  Settings as SettingsIcon,
  User as UserIcon,
  LogOut,
  PlusCircle,
  PlaneTakeoff,
  Search,
  Menu,
} from "lucide-react";
import useAuth from "../CustomHooks/useAuth";
import ThemeToggle from "./themeToggle";


const Layout = () => {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const navigate = useNavigate();

  const auth = useSelector((store) => store.auth);

  const {logoutUser} = useAuth();

  console.log("Logged in user in Layout:", auth?.user);

  const menuItems = [
    { path: "/dashboard", label: "Dashboard", icon: LayoutDashboard },
    { path: "/explore", label: "Explore", icon: Compass },
    { path: "/trips", label: "My Trips", icon: MapIcon, badge: 3 },
    { path: "/messages", label: "Messages", icon: MessageSquare },
    { path: "/activity", label: "Activity", icon: Bell, badge: 1 },
  ];
  const settingItems = [
    { path: "/profile/:userId", label: "Profile", icon: UserIcon },
    { path: "/settings", label: "Settings", icon: SettingsIcon },
  ];

  const closeMobileMenu = () => setMobileMenuOpen(false);

  
  const linkClassName = ({ isActive }) =>
    `flex items-center gap-4 w-full px-4 py-3 rounded-xl font-medium transition-all ${
      isActive
        ? "bg-primary/10 text-primary font-semibold"
        : "text-slate-500 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-surface-dark hover:text-primary"
    }`;

  const SidebarContent = () => (
    <div className="flex flex-col h-full bg-white dark:bg-surface-darker border-r border-slate-200 dark:border-slate-800 w-full p-4">
      <NavLink to="/dashboard" onClick={closeMobileMenu} className="flex items-center gap-3 px-4 mb-10">
        <div className="w-10 h-10 bg-primary rounded-xl flex items-center justify-center shadow-lg shadow-primary/30">
          <PlaneTakeoff className="text-white" size={24} />
        </div>
        <h1 className="text-2xl font-bold tracking-tight text-slate-900 dark:text-white">Travo</h1>
      </NavLink>

      <nav className="flex-1 space-y-1.5 overflow-y-auto">
        <p className="px-4 text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2">Menu</p>
        {menuItems.map((item) => {
          const Icon = item.icon;
          return (
            <NavLink key={item.path} to={item.path} onClick={closeMobileMenu} className={linkClassName}>
              <Icon size={20} />
              <span>{item.label}</span>
              {item.badge && (
                <span className="ml-auto bg-primary text-white text-[10px] font-bold px-2 py-0.5 rounded-full">
                  {item.badge}
                </span>
              )}
            </NavLink>
          );
        })}

        <div className="pt-6 pb-2">
          <p className="px-4 text-xs font-semibold text-slate-400 uppercase tracking-wider">Account</p>
        </div>
        {settingItems.map((item) => {
          const Icon = item.icon;
          return (
            <NavLink key={item.path} to={item.path} onClick={closeMobileMenu} className={linkClassName}>
              <Icon size={20} />
              <span>{item.label}</span>
            </NavLink>
          );
        })}
      </nav>

      <div className="mt-auto pt-4 border-t border-slate-200 dark:border-slate-800">
        <button
          onClick={() => logoutUser()}
          className="flex items-center gap-4 w-full px-4 py-3 text-red-500 hover:bg-red-50 dark:hover:bg-red-900/10 rounded-xl font-medium transition-all"
        >
          <LogOut size={20} />
          <span>Logout</span>
        </button>
      </div>
    </div>
  );

  return (
    <div className="flex h-screen w-full bg-background-light dark:bg-background-dark">
      <aside className="hidden lg:block w-72 h-full">
        <SidebarContent />
      </aside>

      {mobileMenuOpen && (
        <div className="lg:hidden fixed inset-0 z-[60] flex">
          <div className="fixed inset-0 bg-black/50 backdrop-blur-sm" onClick={closeMobileMenu} />
          <div className="relative w-72 h-full animate-slide-in-left">
            <SidebarContent />
          </div>
        </div>
      )}

      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <header className="flex-none h-20 px-4 md:px-8 flex items-center justify-between border-b border-slate-200 dark:border-slate-800 bg-white/80 dark:bg-background-dark/80 backdrop-blur-md sticky top-0 z-40">
          <button
            className="lg:hidden p-2 text-slate-500 hover:bg-slate-100 dark:hover:bg-surface-dark rounded-lg"
            onClick={() => setMobileMenuOpen(true)}
          >
            <Menu size={24} />
          </button>

          <div className="flex-1 max-w-xl mx-4">
            <div className="relative group">
              <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 group-focus-within:text-primary transition-colors" size={20} />
              <input className="w-full h-11 pl-12 pr-4 bg-slate-100 dark:bg-surface-dark border-none rounded-xl text-sm text-slate-900 dark:text-white placeholder-slate-500 focus:ring-2 focus:ring-primary transition-all" placeholder="Find destinations, travelers, or trips..." type="text" />
            </div>
          </div>

          <div className="flex items-center gap-3">
            <button className="w-10 h-10 flex items-center justify-center text-slate-500 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-surface-dark rounded-full transition-colors relative">
              <Bell size={20} />
              <span className="absolute top-2.5 right-2.5 w-2 h-2 bg-red-500 rounded-full border-2 border-white dark:border-background-dark" />
            </button>
            <NavLink to="/trips/create" className="hidden sm:flex items-center gap-2 bg-primary hover:bg-primary-hover text-white px-5 py-2.5 rounded-lg text-sm font-semibold shadow-lg shadow-primary/25 transition-all">
              <PlusCircle size={18} />
              <span>Trip</span>
            </NavLink>
            <NavLink to="/blogs/create" className="hidden sm:flex items-center gap-2 bg-primary hover:bg-primary-hover text-white px-5 py-2.5 rounded-lg text-sm font-semibold shadow-lg shadow-primary/25 transition-all">
              <PlusCircle size={18} />
              <span>Blog</span>
            </NavLink>
            <div className="w-px h-6 bg-slate-200 dark:bg-slate-800 mx-2 hidden sm:block" />
            <ThemeToggle />
            <NavLink to={`/profile/${auth?.user?.userId}`} className="flex items-center gap-3 p-1 rounded-full hover:bg-slate-100 dark:hover:bg-surface-dark transition-all">
              <img src={auth?.user?.profilePic} className="w-10 h-10 rounded-full object-cover ring-2 ring-primary/20" alt="Profile" />
            </NavLink>
          </div>
        </header>

        <main className="flex-1 overflow-y-auto bg-slate-50 dark:bg-background-dark p-4 md:p-8">
          <Outlet />
        </main>
      </div>
    </div>
  );
};

export default Layout;
