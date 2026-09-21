// import React, { useState } from 'react';
// import { useNavigate } from 'react-router-dom';
// import { Camera, MapPin, CheckCircle2, ChevronRight, Palmtree, Mountain, History, Camera as CameraIcon, Users, Music } from 'lucide-react';
// const Onboarding = () => {
//     const navigate = useNavigate();
//     const [step, setStep] = useState(1);
//     const [selectedInterests, setSelectedInterests] = useState(['Beaches', 'Foodie']);
//     const interests = [
//         { label: 'Beaches', icon: Palmtree },
//         { label: 'Mountains', icon: Mountain },
//         { label: 'History', icon: History },
//         { label: 'Foodie', icon: CameraIcon },
//         { label: 'Photography', icon: Camera },
//         { label: 'Solo Travel', icon: Users },
//         { label: 'Nightlife', icon: Music },
//     ];
//     const toggleInterest = (label) => {
//         if (selectedInterests.includes(label)) {
//             setSelectedInterests(selectedInterests.filter(i => i !== label));
//         }
//         else {
//             setSelectedInterests([...selectedInterests, label]);
//         }
//     };
//     return (<div className="min-h-screen bg-background-light dark:bg-background-dark flex flex-col">
//       <header className="p-8 flex justify-between items-center">
//         <div className="flex items-center gap-2">
//           <div className="w-8 h-8 rounded-lg bg-primary flex items-center justify-center text-white font-bold text-lg">T</div>
//           <span className="font-bold text-xl">Travo</span>
//         </div>
//         <button onClick={() => navigate('/dashboard')} className="text-slate-500 hover:text-primary transition-colors text-sm font-medium">Skip for now</button>
//       </header>

//       <main className="flex-1 flex items-center justify-center px-4 py-12">
//         <div className="w-full max-w-3xl">
//           <div className="mb-12">
//             <div className="flex justify-between items-end mb-4">
//               <div>
//                 <h1 className="text-4xl font-extrabold mb-2 tracking-tight">Let's set up your base camp.</h1>
//                 <p className="text-slate-500 dark:text-slate-400">Help us connect you with the right travelers and destinations.</p>
//               </div>
//               <span className="text-xs font-bold text-primary uppercase tracking-widest">Step {step} of 3</span>
//             </div>
//             <div className="h-1.5 w-full bg-slate-200 dark:bg-slate-800 rounded-full overflow-hidden">
//               <div className="h-full bg-primary transition-all duration-500" style={{ width: `${(step / 3) * 100}%` }}/>
//             </div>
//           </div>

//           <div className="bg-white dark:bg-surface-dark rounded-3xl p-8 md:p-12 shadow-2xl border border-slate-200 dark:border-slate-800">
//             <div className="grid md:grid-cols-12 gap-12">
//               <div className="md:col-span-4 flex flex-col items-center">
//                 <div className="relative group cursor-pointer">
//                   <div className="w-40 h-40 rounded-full bg-slate-100 dark:bg-slate-800 border-2 border-dashed border-slate-300 dark:border-slate-700 flex flex-col items-center justify-center transition-all group-hover:border-primary">
//                     <Camera size={40} className="text-slate-400 mb-2 group-hover:text-primary transition-colors"/>
//                     <span className="text-[10px] font-bold text-slate-500 tracking-wider">UPLOAD PHOTO</span>
//                   </div>
//                   <div className="absolute inset-0 rounded-full bg-primary/10 opacity-0 group-hover:opacity-100 transition-opacity"/>
//                 </div>
//                 <p className="mt-4 text-[10px] text-slate-500 text-center uppercase tracking-widest font-bold">Recommended: 400x400px</p>
//               </div>

//               <div className="md:col-span-8 space-y-10">
//                 <div className="space-y-6">
//                   <div className="space-y-1.5">
//                     <label className="text-xs font-bold text-slate-400 uppercase tracking-widest">Current Location</label>
//                     <div className="relative">
//                       <MapPin className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400" size={18}/>
//                       <input className="w-full bg-slate-50 dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-xl py-3.5 pl-12 pr-4 text-sm focus:ring-2 focus:ring-primary transition-all" placeholder="e.g. Bali, Indonesia"/>
//                     </div>
//                   </div>

//                   <div className="space-y-1.5">
//                     <label className="text-xs font-bold text-slate-400 uppercase tracking-widest">Short Bio</label>
//                     <textarea className="w-full bg-slate-50 dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-xl py-3.5 px-4 text-sm focus:ring-2 focus:ring-primary transition-all resize-none h-32" placeholder="Tell us about your travel style..."/>
//                   </div>
//                 </div>

//                 <div className="space-y-4">
//                   <div className="flex items-center gap-2">
//                     <CheckCircle2 className="text-primary" size={18}/>
//                     <h3 className="font-bold text-sm uppercase tracking-widest">Travel Interests</h3>
//                   </div>
//                   <div className="flex flex-wrap gap-3">
//                     {interests.map((item) => {
//             const isSelected = selectedInterests.includes(item.label);
//             return (<button key={item.label} onClick={() => toggleInterest(item.label)} className={`flex items-center gap-2 px-4 py-2 rounded-full border text-sm font-medium transition-all ${isSelected
//                     ? 'bg-primary/20 border-primary text-primary'
//                     : 'bg-slate-50 dark:bg-slate-900 border-slate-200 dark:border-slate-800 text-slate-500 hover:border-slate-400'}`}>
//                           <item.icon size={16}/>
//                           {item.label}
//                         </button>);
//         })}
//                   </div>
//                 </div>

//                 <button onClick={() => navigate('/dashboard')} className="w-full bg-primary hover:bg-primary-hover text-white py-4 rounded-2xl font-bold shadow-xl shadow-primary/30 transition-all transform active:scale-95 flex items-center justify-center gap-2">
//                   Complete Profile
//                   <ChevronRight size={20}/>
//                 </button>
//               </div>
//             </div>
//           </div>
//         </div>
//       </main>
//     </div>);
// };
// export default Onboarding;


//---------------- Updated Onboarding/Register form ----------------------------

import React, { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  Camera,
  MapPin,
  CheckCircle2,
  ChevronRight,
  Palmtree,
  Mountain,
  History,
  Camera as CameraIcon,
  Users,
  Music,
} from "lucide-react";
import useAuth from "../CustomHooks/useAuth";
import { useSelector } from "react-redux";


const Onboarding = () => {
  const navigate = useNavigate();
  const { registerUser } = useAuth();

  const auth = useSelector((store) => store.auth);

  // Backend registration error from Redux
  const error = useSelector((store) => store.auth.error);

  // Current onboarding step
  const [step, setStep] = useState(1);

  // Registration form fields
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [gender, setGender] = useState("");
  const [password, setPassword] = useState("");

  // // Existing onboarding fields
  // const [location, setLocation] = useState("");
  // const [bio, setBio] = useState("");

  // // Travel interests
  // const [selectedInterests, setSelectedInterests] = useState([
  //   "Beaches",
  //   "Foodie",
  // ]);

  // Profile image
  // const fileInputRef = useRef(null);
  // const [profileImage, setProfileImage] = useState(null);
  // const [profileImagePreview, setProfileImagePreview] = useState(null);

  // Client-side validation errors
  const [validationErrors, setValidationErrors] = useState({});

  const interests = [
    { label: "Beaches", icon: Palmtree },
    { label: "Mountains", icon: Mountain },
    { label: "History", icon: History },
    { label: "Foodie", icon: CameraIcon },
    { label: "Photography", icon: Camera },
    { label: "Solo Travel", icon: Users },
    { label: "Nightlife", icon: Music },
  ];

  /*
   * Clean up object URL created for image preview.
   */
  // useEffect(() => {
  //   return () => {
  //     if (profileImagePreview) {
  //       URL.revokeObjectURL(profileImagePreview);
  //     }
  //   };
  // }, [profileImagePreview]);

  /*
   * Toggle travel interest.
   */
  const toggleInterest = (label) => {
    setSelectedInterests((previous) => {
      if (previous.includes(label)) {
        return previous.filter((interest) => interest !== label);
      }

      return [...previous, label];
    });
  };

  /*
   * Handle profile image selection.
   */
  const handleImageChange = (event) => {
    const file = event.target.files?.[0];

    if (!file) {
      return;
    }

    const allowedTypes = [
      "image/jpeg",
      "image/png",
      "image/webp",
    ];

    if (!allowedTypes.includes(file.type)) {
      setValidationErrors((previous) => ({
        ...previous,
        profileImage: "Please upload JPG, PNG or WEBP image.",
      }));
      return;
    }

    // 2 MB maximum
    if (file.size > 2 * 1024 * 1024) {
      setValidationErrors((previous) => ({
        ...previous,
        profileImage: "Profile image must be smaller than 2 MB.",
      }));
      return;
    }

    if (profileImagePreview) {
      URL.revokeObjectURL(profileImagePreview);
    }

    setProfileImage(file);
    setProfileImagePreview(URL.createObjectURL(file));

    setValidationErrors((previous) => ({
      ...previous,
      profileImage: "",
    }));
  };

  /*
   * Remove selected profile image.
   */
  const handleRemoveImage = () => {
    if (profileImagePreview) {
      URL.revokeObjectURL(profileImagePreview);
    }

    setProfileImage(null);
    setProfileImagePreview(null);

    if (fileInputRef.current) {
      fileInputRef.current.value = "";
    }
  };

  /*
   * Validate registration fields.
   *
   * These validations are aligned with your old Register.jsx
   * and backend RegisterRequest.
   */
  const validateForm = () => {
    const errors = {};

    // Name validation
    if (!name.trim()) {
      errors.name = "Name is required";
    } else if (name.trim().length < 4) {
      errors.name = "Name must be at least 4 characters long";
    } else if (!/^[a-zA-Z ]+$/.test(name.trim())) {
      errors.name = "Enter valid name (Eg. John Wick)";
    }

    // Email validation
    if (!email.trim()) {
      errors.email = "Email is required";
    } else if (
      !/^[a-zA-Z0-9._%+-]+@[a-zA-Z.-]+\.[a-zA-Z]{2,}$/.test(
        email.trim()
      )
    ) {
      errors.email = "Invalid email address";
    }

    // Gender validation
    if (!gender) {
      errors.gender = "Gender is required";
    }

    // Password validation
    if (!password) {
      errors.password = "Password is required";
    } else if (password.length < 8 || password.length > 20) {
      errors.password =
        "Password must be between 8 and 20 characters";
    } else if (!/[A-Z]/.test(password)) {
      errors.password = "Must have uppercase letter";
    } else if (!/[a-z]/.test(password)) {
      errors.password = "Must have lowercase letter";
    } else if (!/\d/.test(password)) {
      errors.password = "Must have a number";
    } else if (!/[@$!%*?&]/.test(password)) {
      errors.password = "Must have special character";
    }

    return errors;
  };

  /*
   * Submit registration form.
   *
   * IMPORTANT:
   * Only fields accepted by RegisterRequest are sent
   * to your backend.
   */
  const handleSubmit = async (event) => {
    event.preventDefault();

    const errors = validateForm();

    if (Object.keys(errors).length > 0) {
      setValidationErrors(errors);
      return;
    }

    setValidationErrors({});

    const registerData = {
      name: name.trim(),
      email: email.trim(),
      gender,
      password,
    };

    console.log("Registered User:", registerData);

    registerUser(registerData);
  };

  /*
   * Go to dashboard.
   *
   * Keeping your existing "Skip for now" functionality intact.
   */
  const handleSkip = () => {
    navigate("/dashboard");
  };

  return (
    <div className="min-h-screen bg-background-light dark:bg-background-dark flex flex-col">
      <header className="p-8 flex justify-between items-center">
        <div className="flex items-center gap-2">
          <div className="w-8 h-8 rounded-lg bg-primary flex items-center justify-center text-white font-bold text-lg">
            T
          </div>

          <span className="font-bold text-xl">Travo</span>
        </div>

        <button
          type="button"
          onClick={handleSkip}
          className="text-slate-500 hover:text-primary transition-colors text-sm font-medium"
        >
          Skip for now
        </button>
      </header>

      <main className="flex-1 flex items-center justify-center px-4 py-12">
        <div className="w-full max-w-3xl">

          {/* Header */}
          <div className="mb-12">
            <div className="flex justify-between items-end mb-4">
              <div>
                <h1 className="text-4xl font-extrabold mb-2 tracking-tight">
                  Let's set up your base camp.
                </h1>

                <p className="text-slate-500 dark:text-slate-400">
                  Help us connect you with the right travelers and destinations.
                </p>
              </div>

              <span className="text-xs font-bold text-primary uppercase tracking-widest">
                Step {step} of 3
              </span>
            </div>

            <div className="h-1.5 w-full bg-slate-200 dark:bg-slate-800 rounded-full overflow-hidden">
              <div
                className="h-full bg-primary transition-all duration-500"
                style={{
                  width: `${(step / 3) * 100}%`,
                }}
              />
            </div>
          </div>

          {/* Main Card */}
          <div className="bg-white dark:bg-surface-dark rounded-3xl p-8 md:p-12 shadow-2xl border border-slate-200 dark:border-slate-800">

            <form onSubmit={handleSubmit}>
              <div className="grid md:grid-cols-12 gap-12 ">

                {/* Profile Image
                <div className="md:col-span-4 flex flex-col items-center">

                  <input
                    ref={fileInputRef}
                    type="file"
                    accept="image/jpeg,image/png,image/webp"
                    className="hidden"
                    onChange={handleImageChange}
                  />

                  <div className="relative group cursor-pointer">
                    <div
                      onClick={() =>
                        fileInputRef.current?.click()
                      }
                      className="w-40 h-40 rounded-full bg-slate-100 dark:bg-slate-800 border-2 border-dashed border-slate-300 dark:border-slate-700 flex flex-col items-center justify-center transition-all group-hover:border-primary overflow-hidden"
                    >
                      {profileImagePreview ? (
                        <img
                          src={profileImagePreview}
                          alt="Profile preview"
                          className="w-full h-full object-cover"
                        />
                      ) : (
                        <>
                          <Camera
                            size={40}
                            className="text-slate-400 mb-2 group-hover:text-primary transition-colors"
                          />

                          <span className="text-[10px] font-bold text-slate-500 tracking-wider">
                            UPLOAD PHOTO
                          </span>
                        </>
                      )}
                    </div>

                    <div className="absolute inset-0 rounded-full bg-primary/10 opacity-0 group-hover:opacity-100 transition-opacity pointer-events-none" />
                  </div>

                  {profileImagePreview && (
                    <button
                      type="button"
                      onClick={handleRemoveImage}
                      className="mt-3 text-[10px] font-bold text-red-500 hover:text-red-600 uppercase tracking-widest"
                    >
                      Remove Photo
                    </button>
                  )}

                  <p className="mt-4 text-[10px] text-slate-500 text-center uppercase tracking-widest font-bold">
                    Recommended: 400x400px
                  </p>

                  {validationErrors.profileImage && (
                    <p className="mt-2 text-red-500 text-xs text-center">
                      {validationErrors.profileImage}
                    </p>
                  )}
                </div> */}

                {/* Form Content */}
                <div className="md:col-span-8 space-y-10">

                   {/* Backend Error */}
                  {error && (
                    <p className="text-red-500 text-xs text-center">
                      {error}
                    </p>
                  )}

                  {/* Registration Fields */}
                  <div className="space-y-6">

                    {/* Name */}
                    <div className="space-y-1.5">
                      <label className="text-xs font-bold text-slate-400 uppercase tracking-widest">
                        Name
                      </label>

                      <input
                        name="name"
                        value={name}
                        onChange={(event) =>
                          setName(event.target.value)
                        }
                        className={`w-full bg-slate-50 dark:bg-slate-900 border ${
                          validationErrors.name
                            ? "border-red-500"
                            : "border-slate-200 dark:border-slate-800"
                        } rounded-xl py-3.5 px-4 text-sm focus:ring-2 focus:ring-primary transition-all`}
                        placeholder="e.g. John Wick"
                      />

                      {validationErrors.name && (
                        <p className="text-red-500 text-xs">
                          {validationErrors.name}
                        </p>
                      )}
                    </div>

                    {/* Email */}
                    <div className="space-y-1.5">
                      <label className="text-xs font-bold text-slate-400 uppercase tracking-widest">
                        Email
                      </label>

                      <input
                        name="email"
                        type="email"
                        value={email}
                        onChange={(event) =>
                          setEmail(event.target.value)
                        }
                        className={`w-full bg-slate-50 dark:bg-slate-900 border ${
                          validationErrors.email
                            ? "border-red-500"
                            : "border-slate-200 dark:border-slate-800"
                        } rounded-xl py-3.5 px-4 text-sm focus:ring-2 focus:ring-primary transition-all`}
                        placeholder="e.g. john@example.com"
                      />

                      {validationErrors.email && (
                        <p className="text-red-500 text-xs">
                          {validationErrors.email}
                        </p>
                      )}
                    </div>

                    {/* Gender */}
                    <div className="space-y-1.5">
                      <label className="text-xs font-bold text-slate-400 uppercase tracking-widest">
                        Gender
                      </label>

                      <select
                        name="gender"
                        value={gender}
                        onChange={(event) =>
                          setGender(event.target.value)
                        }
                        className={`w-full bg-slate-50 dark:bg-slate-900 border ${
                          validationErrors.gender
                            ? "border-red-500"
                            : "border-slate-200 dark:border-slate-800"
                        } rounded-xl py-3.5 px-4 text-sm focus:ring-2 focus:ring-primary transition-all`}
                      >
                        <option value="">
                          Select Gender
                        </option>
                        <option value="male">Male</option>
                        <option value="female">Female</option>
                        <option value="other">Other</option>
                      </select>

                      {validationErrors.gender && (
                        <p className="text-red-500 text-xs">
                          {validationErrors.gender}
                        </p>
                      )}
                    </div>

                    {/* Password */}
                    <div className="space-y-1.5">
                      <label className="text-xs font-bold text-slate-400 uppercase tracking-widest">
                        Password
                      </label>

                      <input
                        name="password"
                        type="password"
                        value={password}
                        onChange={(event) =>
                          setPassword(event.target.value)
                        }
                        className={`w-full bg-slate-50 dark:bg-slate-900 border ${
                          validationErrors.password
                            ? "border-red-500"
                            : "border-slate-200 dark:border-slate-800"
                        } rounded-xl py-3.5 px-4 text-sm focus:ring-2 focus:ring-primary transition-all`}
                        placeholder="Create a strong password"
                      />

                      {validationErrors.password && (
                        <p className="text-red-500 text-xs">
                          {validationErrors.password}
                        </p>
                      )}
                    </div>

                   
                  </div>

                  {/* Complete Profile */}
                  <button
                    type="submit"
                    disabled={auth?.loading}
                    className="w-full bg-primary hover:bg-primary-hover text-white py-4 rounded-2xl font-bold shadow-xl shadow-primary/30 transition-all transform active:scale-95 flex items-center justify-center gap-2 disabled:opacity-60 disabled:cursor-not-allowed"
                  >
                    {auth?.loading
                      ? "Creating Account..."
                      : "Sign Up"}

                    {!auth?.loading && <ChevronRight size={20} />}
                  </button>

                 
                </div>
              </div>
            </form>
          </div>
        </div>
      </main>
    </div>
  );
};

export default Onboarding;
