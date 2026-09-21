import React from "react";
import { useNavigate } from "react-router-dom";
import { PlaneTakeoff, ArrowRight, Play, Star, Map, Group, BookOpen, } from "lucide-react";
import LandingPageHome from "./LandingPage/LandingPageHome";
import ThemeToggle from "../components/themeToggle";
import { Footer } from "../components/Footer";
const Landing = () => {
    const navigate = useNavigate();
    return (<div className="bg-background-light dark:bg-background-dark text-slate-900 dark:text-white">
      {/* Navigation */}
      <nav className="fixed w-[70vw] z-50 top-1 start-56 rounded-xl border border-gray-100 dark:border-gray-900 bg-background-light/80 dark:bg-background-dark/80 backdrop-blur-md">
        <div className="max-w-7xl mx-auto flex items-center justify-between px-6 py-4 ">
          <div className="flex items-center space-x-3">
            <div className="bg-primary p-1.5 rounded-lg text-white">
              <PlaneTakeoff size={24}/>
            </div>
            <span className="text-2xl font-bold tracking-tight">Travo</span>
          </div>
          {/* <div className="hidden md:flex items-center space-x-8">
          <a
            href="#"
            className="hover:text-primary transition-colors text-sm font-medium"
          >
            Explore
          </a>
          <a
            href="#"
            className="hover:text-primary transition-colors text-sm font-medium"
          >
            Stories
          </a>
          <a
            href="#"
            className="hover:text-primary transition-colors text-sm font-medium"
          >
            Community
          </a>
          <a
            href="#"
            className="hover:text-primary transition-colors text-sm font-medium"
          >
            About
          </a>
        </div> */}
          <div className="flex items-center space-x-4">
            <button onClick={() => navigate("/login")} className="text-gray-900 dark:text-white hover:text-primary font-bold px-4 py-2 hidden sm:block text-sm">
              Log In
            </button>
            <button onClick={() => navigate("/login")} className="text-white bg-primary hover:bg-primary-hover font-bold rounded-full px-6 py-2.5 shadow-lg shadow-primary/25 transition-all text-sm uppercase tracking-wide">
              Sign Up
            </button>
            <ThemeToggle />
          </div>
        </div>
      </nav>

      {/* Hero Section */}
      <section className="relative pt-24 pb-20 lg:pt-48 lg:pb-32 overflow-hidden bg-grid-pattern">
        <div className="max-w-7xl mx-auto px-6 relative z-10 pb-20 mb-20">
          <div className="grid lg:grid-cols-2 gap-16 items-center">
            <div>
              <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-primary/10 border border-primary/20 text-primary text-xs font-bold mb-6 uppercase tracking-wider">
                <span className="flex h-2 w-2 rounded-full bg-primary animate-pulse"></span>
                New: Group Itineraries
              </div>
              <h1 className="text-5xl lg:text-7xl font-extrabold tracking-tight leading-[1.05] mb-6">
                Find Your Way <br />
                <span className="bg-gradient-to-r from-primary to-orange-700 bg-clip-text text-transparent text-5xl">
                  Find Your Buddy
                </span>
                {/* text-transparent bg-clip-text bg-gradient-to-r from-primary to-blue-400 */}
              </h1>
              <p className="text-lg text-slate-500 dark:text-slate-400 mb-8 leading-relaxed max-w-lg">
                Connect with like-minded explorers, plan your dream itinerary
                together, and share your journey with a community that
                understands your wanderlust.
              </p>
              <div className="flex flex-col sm:flex-row gap-4">
                <button onClick={() => navigate("/login")} className="inline-flex justify-center items-center py-4 px-8 text-sm font-bold text-white rounded-full bg-primary hover:bg-primary-hover transition-all shadow-xl shadow-primary/30 uppercase tracking-widest">
                  Get Started
                  <ArrowRight className="ml-2" size={18}/>
                </button>
                <button className="inline-flex justify-center items-center py-4 px-8 text-sm font-bold text-slate-900 dark:text-white rounded-full bg-white dark:bg-surface-dark border border-gray-200 dark:border-gray-800 hover:bg-gray-50 dark:hover:bg-surface-lighter transition-all uppercase tracking-widest">
                  <Play className="mr-2 text-primary fill-current" size={18}/>
                  Watch Demo
                </button>
              </div>
              <div className="mt-10 flex items-center gap-4">
                <div className="flex -space-x-3">
                  {[1, 2, 3].map((i) => (<img key={i} src={`https://picsum.photos/seed/${i + 50}/100/100`} className="w-10 h-10 rounded-full border-2 border-white dark:border-background-dark object-cover" alt="User"/>))}
                  <div className="flex items-center justify-center w-10 h-10 text-[10px] font-bold text-white bg-slate-900 border-2 border-white dark:border-background-dark rounded-full">
                    +2k
                  </div>
                </div>
                <div className="text-sm">
                  <p className="font-bold text-slate-900 dark:text-white">
                    Trusted by Travelers
                  </p>
                  <div className="flex text-primary">
                    {[1, 2, 3, 4, 5].map((i) => (<Star key={i} size={14} className="fill-current"/>))}
                  </div>
                </div>
              </div>
            </div>
            <div className="relative group">
              <div className="absolute inset-0 bg-primary/20 blur-[100px] rounded-full"/>
              <div className="relative z-10  border border-gray-800 p-2 rounded-2xl shadow-2xl transform rotate-2 group-hover:rotate-0 transition-transform duration-500 max-w-md mx-auto">
                <img src="https://lh3.googleusercontent.com/aida-public/AB6AXuDPK7ot8w96LDWF-tWTiC0e7WC4Wl_P88X-SWAN8qJx_MHkXtiCtxWJSyspC7sAD4y9BTenmdBMhd_YEy0g1_yHFLPZN0BhOf5kRbnNvN9zzqOSegiFvpfEvHC1CTctdWtPtq9domlDNrZjYolnr5BjoJS1GAP_YGrXkRIPHdsqoRnEScKAHTN_C8OC3xYet-mbjmZE-M5GRk3hr2VEX2IBKAStAMo8HXm5Em0tOfKQ7skGpPIaGIkslBO5Iox7aZw0cJxR8YcF5EjJ" className="rounded-xl w-full aspect-[4/3] object-cover" alt="Destination"/>
                <div className="p-4">
                  <div className="flex justify-between items-start">
                    <div>
                      <h3 className="font-bold text-lg text-white">
                        Alpine Expedition
                      </h3>
                      <p className="text-slate-400 text-xs flex items-center gap-1 mt-1 font-medium tracking-wide">
                        <Map size={14}/> SWITZERLAND
                      </p>
                    </div>
                    <span className="bg-primary/20 text-primary text-[10px] font-bold px-3 py-1 rounded uppercase tracking-widest">
                      5 Days
                    </span>
                  </div>
                </div>
              </div>
              <div className="absolute -bottom-6 -right-4 z-20 bg-background-dark/90 backdrop-blur-sm border border-gray-800 p-3 rounded-xl shadow-xl animate-bounce">
                <div className="flex items-center gap-3">
                  <div className="bg-primary/20 p-2 rounded-lg text-primary">
                    <Star className="fill-current" size={20}/>
                  </div>
                  <div>
                    <p className="text-[10px] text-gray-500 font-bold tracking-widest uppercase">
                      Match Found
                    </p>
                    <p className="text-sm font-bold text-white">
                      Sarah matches 98%
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <LandingPageHome />
      </section>

      {/* Features Section */}
      <section className="pt-10 pb-32 dark:bg-background-dark ">
        <div className="max-w-7xl mx-auto px-6">
          <div className="text-center max-w-2xl mx-auto mb-16">
            <span className="text-primary font-bold tracking-[0.2em]  uppercase">
              Why Choose Travo
            </span>
            <h2 className="text-4xl font-extrabold mt-2 mb-4 tracking-tight">
              Everything You Need for the Perfect Trip
            </h2>
            <p className="text-slate-500 dark:text-slate-400 font-medium">
              We bring together the best tools for solo travelers looking for
              company and groups planning their next adventure.
            </p>
          </div>
          <div className="grid md:grid-cols-3 gap-8">
            {[
            {
                title: "Smart Partner Matching",
                icon: Group,
                desc: "Our AI algorithm connects you with travelers who share your interests, budget, and travel style.",
            },
            {
                title: "Collaborative Planning",
                icon: Map,
                desc: "Build your itinerary together in real-time. Pin locations, vote on activities, and organize bookings.",
            },
            {
                title: "Share Your Stories",
                icon: BookOpen,
                desc: "Create beautiful travel blogs and photo journals. Share your experiences and inspire others.",
            },
        ].map((f, i) => (<div key={i} className="group bg-slate-50 dark:bg-surface-dark border border-slate-100 dark:border-gray-900 p-8 rounded-2xl hover:border-primary/50 hover:shadow-2xl hover:shadow-primary/5 transition-all duration-300">
                <div className="w-14 h-14 bg-primary/10 rounded-xl flex items-center justify-center mb-6 group-hover:bg-primary transition-colors">
                  <f.icon className="text-primary group-hover:text-white transition-colors" size={30}/>
                </div>
                <h3 className="text-xl font-bold mb-3 tracking-tight">
                  {f.title}
                </h3>
                <p className="text-slate-500 dark:text-slate-400 leading-relaxed text-sm font-medium">
                  {f.desc}
                </p>
              </div>))}
          </div>
        </div>
      </section>

      {/* Popular Destinations / Mini Gallery */}
      <section className="py-20">
        <div className="max-w-[1140px] mx-auto px-6">
          <div className="flex flex-col md:flex-row justify-between items-end mb-10 gap-4">
            <div>
              <h2 className="text-3xl font-bold mb-2">
                Trending Destinations
              </h2>
              <p className="text-slate-500 dark:text-slate-400 font-medium">
                See where the Travo community is heading next.
              </p>
            </div>
            <a className="text-primary hover:text-primary-hover font-semibold flex items-center tracking-tight" href="#">
              View all destinations
              <span className="material-icons ml-1">
                <ArrowRight />
              </span>
            </a>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            {/* <!-- Card 1 --> */}
            <div className="relative group rounded-2xl overflow-hidden aspect-[3/4] cursor-pointer">
              <img alt="Traditional Japanese street in Kyoto" className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110" data-alt="Kyoto street with cherry blossoms" src="https://lh3.googleusercontent.com/aida-public/AB6AXuBqHNbgQ4QuzgUgjmLxTO78Zo7YCStYORqja6D_AGrnTUq-Y_37S_vgnPbvW6lpCfyDoJ7HVFx9phwyx9YslzT09cyItltHkbDvaktSWBFhud0PNbPMUoM_Nv4uIosh9mW0jagcMCsMBmZakEkI7PXsBiUtC5WCb3FlxG-cz-HvJb0wUU3C3hZttDhT-XEdXWs0X6RQGdqiCnTwa_8gtMzktcu2EyEKWoHsIpw1-9WWiOuIFbqH9oTJAb8Z1alslO_U7mPGId9P0U1R"/>
              <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-black/20 to-transparent p-6 flex flex-col justify-end">
                <h3 className="text-white font-bold text-xl">Kyoto, Japan</h3>
                <p className="text-gray-300 text-sm mt-1 opacity-0 group-hover:opacity-100 transition-opacity transform translate-y-2 group-hover:translate-y-0 duration-300">
                  128 Travelers looking for partners
                </p>
              </div>
            </div>
            {/* <!-- Card 2 --> */}
            <div className="relative group rounded-2xl overflow-hidden aspect-[3/4] cursor-pointer lg:translate-y-8">
              <img alt="Cinque Terre coastal village Italy" className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110" data-alt="Colorful Italian coastal buildings" src="https://lh3.googleusercontent.com/aida-public/AB6AXuAWF9gtAGNKeaQMdbGIXOkso4oSSwsF-rQ7nOJP_IgziKnmKvGWvYsIfDx3GpVpcySkNFnk_wZ10oqj97U7q9N0IHqLwb1ZX9HuDLwNI-2tkIGl5fBJFak8EOZNANIZa0ywdQn6EBBtJr-3qFXi6bRnHoP1syGtmEr_A4yzv5YozvOvQUkaPmD8Lo6Cs9hY2Jgdgc0wlvpxYo4-8NJyqEBr2WhAjLbeHBoa73tnIAMy-Atkl3_ZYzCCm6f_XYGxrczgZCcpsg-IsMpj"/>
              <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-black/20 to-transparent p-6 flex flex-col justify-end">
                <h3 className="text-white font-bold text-xl">
                  Cinque Terre, Italy
                </h3>
                <p className="text-gray-300 text-sm mt-1 opacity-0 group-hover:opacity-100 transition-opacity transform translate-y-2 group-hover:translate-y-0 duration-300">
                  84 Groups planning trips
                </p>
              </div>
            </div>
            {/* <!-- Card 3 --> */}
            <div className="relative group rounded-2xl overflow-hidden aspect-[3/4] cursor-pointer">
              <img alt="Tokyo neon street at night" className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110" data-alt="Tokyo city lights at night" src="https://lh3.googleusercontent.com/aida-public/AB6AXuBPQvAdZO_xGpH-nABJCoe-oslb18YnP3JOGSbrjl3_2FmJiIk2WFXa1401TIM12tucmK-20BWtVTb6kUJcY3hBlqS83OYRSfFrSMLD2qHHOrYUvY9epba-Fnl1o7FAw-_cQwKkJhIXDStfdppSZap-hJM0dd29N4HJB6ScCPDM-ZMwxXgRWPwrylUkKTg3kSfKcwNO5uiOmiPYPBo0OSBqSf6TpTYp9MEWBczVBjbNFnXmS1WBcpCfap1j26VlVN2aoDoR-gPKGMDu"/>
              <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-black/20 to-transparent p-6 flex flex-col justify-end">
                <h3 className="text-white font-bold text-xl">Tokyo, Japan</h3>
                <p className="text-gray-300 text-sm mt-1 opacity-0 group-hover:opacity-100 transition-opacity transform translate-y-2 group-hover:translate-y-0 duration-300">
                  215 Travelers nearby
                </p>
              </div>
            </div>
            {/* <!-- Card 4 --> */}
            <div className="relative group rounded-2xl overflow-hidden aspect-[3/4] cursor-pointer lg:translate-y-8">
              <img alt="Paris Eiffel Tower view" className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110" data-alt="Paris cityscape with Eiffel Tower" src="https://lh3.googleusercontent.com/aida-public/AB6AXuAePYCmX6lechJs0Mgo4bE_rQiOg3b6xm-9fl_ct5Z6IlvNmitU6WQSfh7H282TUa3Z4nSeOQPpuLiCZ7s_-AQlM-6FijD4GVNZfWS41za6JN_X4Gz6Ba1LrYQz1U1gjVjaI5WvuktbGJGmaFKQsoAwxb4S1__hGHIJS4fkoQmqTRYJSL5P75q6XkaRcyw-TwnQq_hG5rDQJjj4B442aPeB1F3rrVP2wfAvFbftD03gr-FQeQz1xAgI9oEAUsrMmMRLBmr38WX5x5Ky"/>
              <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-black/20 to-transparent p-6 flex flex-col justify-end">
                <h3 className="text-white font-bold text-xl">Paris, France</h3>
                <p className="text-gray-300 text-sm mt-1 opacity-0 group-hover:opacity-100 transition-opacity transform translate-y-2 group-hover:translate-y-0 duration-300">
                  302 Active discussions
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-20">
        <div className="max-w-[1140px] mx-auto px-6">
          <div className="bg-gradient-to-tr from-primary to-blue-600 rounded-3xl p-10 md:p-16 text-center relative overflow-hidden">
            <div className="absolute top-0 right-0 -mr-10 -mt-10 w-64 h-64 bg-white/10 rounded-full blur-2xl"></div>
            <div className="absolute bottom-0 left-0 -ml-10 -mb-10 w-64 h-64 bg-black/10 rounded-full blur-2xl"></div>
            <div className="relative z-10 max-w-2xl mx-auto">
              <h2 className="text-3xl md:text-4xl font-bold text-white mb-6">
                Ready to Start Your Adventure?
              </h2>
              <p className="text-blue-100 text-lg mb-8">
                Join thousands of travelers who are already exploring the world
                together. Sign up today and find your perfect travel partner.
              </p>
              <div className="flex flex-col sm:flex-row gap-4 justify-center">
                <button className="bg-white text-primary hover:bg-gray-100 font-bold py-4 px-8 rounded-full transition shadow-lg">
                  Create Free Account
                </button>
                <button className="bg-transparent border border-white text-white hover:bg-white/10 font-bold py-4 px-8 rounded-full transition">
                  Explore Stories
                </button>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Footer */}
      <Footer />
    </div>);
};
export default Landing;
