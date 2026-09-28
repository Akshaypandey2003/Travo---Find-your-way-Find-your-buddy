import React from "react";
import { useNavigate } from "react-router-dom";
import { useState } from "react";
import useAuth from "../CustomHooks/useAuth";
import { useSelector } from "react-redux";
import { useEffect } from "react";

import { Mail, Lock, PlaneTakeoff, Github, Chrome } from "lucide-react";

const Login = () => {
  const navigate = useNavigate();

  const [forgotPasswordOpen, setForgotPasswordOpen] = useState(false);
  const [forgotEmail, setForgotEmail] = useState("");

  // Resend password reset link cooldown
  const [resendCooldown, setResendCooldown] = useState(0);

  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });

  const [errors, setErrors] = useState({});

  const auth = useSelector((state) => state.auth);
  const apiStatus = useSelector((state) => state.apiStatus);

  const {
    loginUser,
    forgotPassword,
    forgotLoading,
    forgotMessage,
    forgotError,
  } = useAuth();

  const handleChange = (e) => {
    const { name, value } = e.target;

    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));

    // Remove the error as the user starts correcting the field
    setErrors((prev) => ({
      ...prev,
      [name]: "",
    }));
  };

  const validateForm = () => {
    const newErrors = {};

    // Email validation
    if (!formData.email.trim()) {
      newErrors.email = "Email is required";
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      newErrors.email = "Please enter a valid email address";
    }

    // Password validation
    if (!formData.password) {
      newErrors.password = "Password is required";
    } else if (formData.password.length < 8) {
      newErrors.password = "Password must be at least 8 characters";
    }

    setErrors(newErrors);

    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    console.log("Login data:", formData);

    loginUser(formData);

    // Later, you will call your backend API here
    // navigate("/dashboard");
  };

  // Countdown timer for resend reset link
  useEffect(() => {
    if (resendCooldown <= 0) {
      return;
    }

    const timer = setInterval(() => {
      setResendCooldown((prev) => prev - 1);
    }, 1000);

    return () => clearInterval(timer);
  }, [resendCooldown]);

  // Send / resend password reset link
  const handleForgotPassword = async () => {
    if (!forgotEmail.trim() || resendCooldown > 0) {
      return;
    }

    const result = await forgotPassword(forgotEmail.trim());

    // Start countdown only when API request succeeds
    if (result?.success) {
      setResendCooldown(60);
    }
  };

  return (
    <div className="h-screen w-full flex bg-background-dark text-white overflow-hidden">
      {/* Visual Side */}
      <div className="hidden lg:flex w-[45%] relative bg-slate-900">
        <img
          src="https://lh3.googleusercontent.com/aida-public/AB6AXuCYqycrNP9f0PJ2nwuCTmnaxgSjkKG396oZhYvLSpXYchr-4XSQIBmm2KcFWGzwnyjQE4wNP8mcfWlilHjSGqvBOQJw9rfCwLNKiAdzFfeKeKMTSrtf9Vy1PxbTlhGZh1MLVcKmZHEtIc9LPlutwZk0ncOzH0h0qUW5AdgTwPvZTAw-xua2i1hDGJSWRPe4lXrA9BsIKLuNQTQNipAvh3s_2DRsfOegDRs4wQE_feaNoV3IGU-5ruXUezFdZDnailoWU4JdRqTuwmE8"
          className="absolute inset-0 w-full h-full object-cover opacity-70"
          alt="Adventure"
        />

        <div className="absolute inset-0 bg-gradient-to-t from-background-dark via-background-dark/20 to-transparent" />

        <div className="absolute bottom-16 left-16 right-16">
          <div className="flex -space-x-3 mb-6">
            {[1, 2, 3].map((i) => (
              <img
                key={i}
                src={`https://picsum.photos/seed/${i + 10}/100/100`}
                className="w-10 h-10 rounded-full border-2 border-slate-900"
                alt="Avatar"
              />
            ))}

            <div className="w-10 h-10 rounded-full bg-primary/20 border-2 border-slate-900 flex items-center justify-center text-[10px] font-bold">
              40k+
            </div>
          </div>

          <h2 className="text-4xl font-extrabold mb-4 leading-tight">
            Find your next <br />
            <span className="text-primary">adventure buddy.</span>
          </h2>

          <p className="text-slate-400 text-lg">
            Connect with like-minded explorers, share your itineraries, and make
            memories that last a lifetime.
          </p>
        </div>
      </div>

      {/* Form Side */}
      <div className="flex-1 flex flex-col items-center justify-center p-8 relative">
        <div className="absolute top-12 left-12 flex items-center gap-2">
          <PlaneTakeoff className="text-primary" size={28} />
          <span className="text-2xl font-bold">Travo</span>
        </div>

        <div className="w-full max-w-md space-y-10">
          <div className="space-y-2">
            <h1 className="text-4xl font-extrabold tracking-tight">
              Welcome back
            </h1>

            <p className="text-slate-500">
              Please enter your details to sign in.
            </p>

            {apiStatus.status === "failed" && apiStatus.message && (
              <span className="mt-4 text-sm text-red-400">
                {apiStatus.message}
              </span>
            )}
          </div>

          <form className="space-y-6" onSubmit={handleSubmit}>
            <div className="space-y-2">
              <label className="text-xs font-bold text-slate-500 uppercase tracking-widest">
                Email Address
              </label>

              <div className="relative group">
                <Mail
                  className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-500 group-focus-within:text-primary transition-colors"
                  size={20}
                />

                <input
                  type="email"
                  name="email"
                  value={formData.email}
                  onChange={handleChange}
                  className="w-full bg-surface-dark border-slate-800 rounded-2xl py-4 pl-12 pr-4 text-sm focus:ring-2 focus:ring-primary focus:border-transparent transition-all outline-none"
                  placeholder="name@example.com"
                  required
                />

                {errors.email && (
                  <p className="text-red-500 text-xs mt-1">
                    {errors.email}
                  </p>
                )}
              </div>
            </div>

            <div className="space-y-2">
              <div className="flex justify-between items-center">
                <label className="text-xs font-bold text-slate-500 uppercase tracking-widest">
                  Password
                </label>

                <button
                  type="button"
                  onClick={() => {
                    setForgotPasswordOpen(true);
                    setForgotEmail(formData.email);
                  }}
                  className="text-xs font-bold text-primary hover:underline uppercase tracking-widest"
                >
                  Forgot?
                </button>
              </div>

              <div className="relative group">
                <Lock
                  className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-500 group-focus-within:text-primary transition-colors"
                  size={20}
                />

                <input
                  type="password"
                  name="password"
                  value={formData.password}
                  onChange={handleChange}
                  className="w-full bg-surface-dark border-slate-800 rounded-2xl py-4 pl-12 pr-4 text-sm focus:ring-2 focus:ring-primary focus:border-transparent transition-all outline-none"
                  placeholder="••••••••"
                  required
                />

                {errors.password && (
                  <p className="text-red-500 text-xs mt-1">
                    {errors.password}
                  </p>
                )}
              </div>
            </div>

            <div className="flex items-center gap-3 px-1">
              <input
                type="checkbox"
                id="remember"
                className="w-5 h-5 rounded-md border-slate-800 bg-surface-dark text-primary focus:ring-primary"
              />

              <label
                htmlFor="remember"
                className="text-sm text-slate-500 font-medium"
              >
                Remember me for 30 days
              </label>
            </div>

            <button
              type="submit"
              className="w-full bg-primary hover:bg-primary-hover text-white py-4 rounded-2xl font-bold shadow-xl shadow-primary/30 transition-all transform active:scale-[0.98]"
            >
              {auth?.loading ? "Logging in..." : "Login"}
            </button>
          </form>

          <div className="relative py-4">
            <div className="absolute inset-0 flex items-center">
              <div className="w-full border-t border-slate-800" />
            </div>

            <div className="relative flex justify-center text-xs uppercase tracking-widest">
              <span className="bg-background-dark px-4 text-slate-600 font-bold">
                Or continue with
              </span>
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <button className="flex items-center justify-center gap-3 py-3 px-4 bg-slate-800 hover:bg-slate-700 rounded-xl transition-all font-semibold text-sm">
              <Chrome size={18} className="text-primary" /> Google
            </button>

            <button className="flex items-center justify-center gap-3 py-3 px-4 bg-slate-800 hover:bg-slate-700 rounded-xl transition-all font-semibold text-sm">
              <Github size={18} /> Github
            </button>
          </div>

          <p className="text-center text-slate-500 text-sm">
            Don't have an account?{" "}
            <button
              onClick={() => navigate("/onboarding")}
              className="text-primary font-bold hover:underline"
            >
              Sign up for free
            </button>
          </p>
        </div>
      </div>

      {/* Forgot Password Modal */}
      {forgotPasswordOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 backdrop-blur-sm px-4">
          <div className="w-full max-w-md bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-2xl">

            {/* Header */}
            <div className="flex items-center justify-between mb-6">
              <div>
                <h2 className="text-2xl font-bold text-white">
                  Forgot Password?
                </h2>

                <p className="text-sm text-slate-400 mt-1">
                  Enter your email and we'll send you instructions to reset your
                  password.
                </p>
              </div>

              <button
                type="button"
                onClick={() => setForgotPasswordOpen(false)}
                className="text-slate-400 hover:text-white text-xl"
              >
                ×
              </button>
            </div>

            {/* Email */}
            <div className="space-y-2">
              <label className="text-xs font-bold text-slate-500 uppercase tracking-widest">
                Email Address
              </label>

              <div className="relative">
                <Mail
                  className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-500"
                  size={20}
                />

                <input
                  type="email"
                  value={forgotEmail}
                  onChange={(e) => {
                    setForgotEmail(e.target.value);
                  }}
                  placeholder="name@example.com"
                  className="w-full bg-slate-800 border border-slate-700 rounded-xl py-3 pl-12 pr-4 text-sm text-white outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
                />
              </div>
            </div>

            {/* Error */}
            {forgotError && (
              <p className="mt-3 text-sm text-red-400">
                {forgotError}
              </p>
            )}

            {/* Success */}
            {forgotMessage && (
              <div className="mt-4">
                <p className="text-sm text-green-400">
                  {forgotMessage}
                </p>

                <div className="mt-3 text-sm text-slate-400">
                  Didn't receive the email?
                </div>

                {resendCooldown > 0 ? (
                  <p className="mt-1 text-sm text-slate-500">
                    You can resend the reset link in{" "}
                    <span className="text-primary font-semibold">
                      {resendCooldown}s
                    </span>
                  </p>
                ) : (
                  <button
                    type="button"
                    onClick={handleForgotPassword}
                    disabled={
                      forgotLoading || !forgotEmail.trim()
                    }
                    className="mt-2 text-sm font-bold text-primary hover:underline disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    {forgotLoading
                      ? "Sending..."
                      : "Resend Reset Link"}
                  </button>
                )}
              </div>
            )}

            {/* Submit */}
            {!forgotMessage && (
              <button
                type="button"
                disabled={
                  forgotLoading || !forgotEmail.trim()
                }
                onClick={handleForgotPassword}
                className="w-full mt-6 bg-primary hover:bg-primary-hover disabled:opacity-50 disabled:cursor-not-allowed text-white py-3 rounded-xl font-bold transition-all"
              >
                {forgotLoading
                  ? "Sending..."
                  : "Send Reset Instructions"}
              </button>
            )}

            {/* Back */}
            <button
              type="button"
              onClick={() => setForgotPasswordOpen(false)}
              className="w-full mt-3 text-sm text-slate-400 hover:text-white"
            >
              Back to Login
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default Login;
