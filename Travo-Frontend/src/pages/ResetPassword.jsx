import React, { useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { Lock, PlaneTakeoff, CheckCircle, AlertCircle } from "lucide-react";
import { apiRequest, getApiErrorMessage } from "../lib/api";

const ResetPassword = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  // Get reset token from:
  // http://localhost:3000/reset-password?token=xxxxx
  const token = searchParams.get("token");

  const [formData, setFormData] = useState({
    password: "",
    confirmPassword: "",
  });

  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const [successMessage, setSuccessMessage] = useState("");
  const [apiError, setApiError] = useState("");

  const handleChange = (e) => {
    const { name, value } = e.target;

    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));

    setErrors((prev) => ({
      ...prev,
      [name]: "",
    }));

    setApiError("");
    setSuccessMessage("");
  };

  const validateForm = () => {
    const newErrors = {};

    if (!token) {
      setApiError(
        "Invalid or missing password reset token. Please request a new reset link."
      );
      return false;
    }

    if (!formData.password) {
      newErrors.password = "Password is required";
    } else if (formData.password.length < 8) {
      newErrors.password = "Password must be at least 8 characters";
    }

    if (!formData.confirmPassword) {
      newErrors.confirmPassword = "Please confirm your password";
    } else if (formData.password !== formData.confirmPassword) {
      newErrors.confirmPassword = "Passwords do not match";
    }

    setErrors(newErrors);

    return Object.keys(newErrors).length === 0 && !!token;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    setApiError("");
    setSuccessMessage("");

    if (!validateForm()) {
      return;
    }

    setLoading(true);

    try {
      const { data } = await apiRequest(
        "http://localhost:8085/api/v1/auth/reset-password",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            token: token,
            password: formData.password,
          }),
        }
      );

      setSuccessMessage(
        data.message ||
          "Your password has been reset successfully."
      );

      setFormData({
        password: "",
        confirmPassword: "",
      });

      // Redirect to login after successful reset
      setTimeout(() => {
        navigate("/login");
      }, 2000);
    } catch (error) {
      console.error("Password reset failed:", error);
      setApiError(getApiErrorMessage(error));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="h-screen w-full flex bg-background-dark text-white overflow-hidden">

      {/* Visual Side */}
      <div className="hidden lg:flex w-[45%] relative bg-slate-900">

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
            Your next <br />
            <span className="text-primary">adventure awaits.</span>
          </h2>

          <p className="text-slate-400 text-lg">
            Secure your account and get back to connecting with
            like-minded explorers.
          </p>
        </div>
      </div>

      {/* Form Side */}
      <div className="flex-1 flex flex-col items-center justify-center p-8 relative">

        {/* Logo */}
        <div className="absolute top-12 left-12 flex items-center gap-2">
          <PlaneTakeoff className="text-primary" size={28} />
          <span className="text-2xl font-bold">
            Travo
          </span>
        </div>

        <div className="w-full max-w-md space-y-10">

          {/* Header */}
          <div className="space-y-2">
            <h1 className="text-4xl font-extrabold tracking-tight">
              Reset Password
            </h1>

            <p className="text-slate-500">
              Create a new password for your Travo account.
            </p>
          </div>

          {/* Invalid Token */}
          {!token && (
            <div className="flex items-start gap-3 rounded-xl border border-red-500/20 bg-red-500/10 p-4">
              <AlertCircle
                size={20}
                className="text-red-400 mt-0.5 shrink-0"
              />

              <p className="text-sm text-red-400">
                Invalid or missing reset token. Please request a
                new password reset link.
              </p>
            </div>
          )}

          {/* Success Message */}
          {successMessage && (
            <div className="flex items-start gap-3 rounded-xl border border-green-500/20 bg-green-500/10 p-4">
              <CheckCircle
                size={20}
                className="text-green-400 mt-0.5 shrink-0"
              />

              <p className="text-sm text-green-400">
                {successMessage}
                <br />
                <span className="text-green-500/80">
                  Redirecting you to login...
                </span>
              </p>
            </div>
          )}

          {/* API Error */}
          {apiError && token && (
            <div className="flex items-start gap-3 rounded-xl border border-red-500/20 bg-red-500/10 p-4">
              <AlertCircle
                size={20}
                className="text-red-400 mt-0.5 shrink-0"
              />

              <p className="text-sm text-red-400">
                {apiError}
              </p>
            </div>
          )}

          {/* Form */}
          <form
            className="space-y-6"
            onSubmit={handleSubmit}
          >

            {/* Password */}
            <div className="space-y-2">

              <label className="text-xs font-bold text-slate-500 uppercase tracking-widest">
                New Password
              </label>

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
                  disabled={!token || loading || !!successMessage}
                  className="w-full bg-surface-dark border-slate-800 rounded-2xl py-4 pl-12 pr-4 text-sm focus:ring-2 focus:ring-primary focus:border-transparent transition-all outline-none disabled:opacity-50 disabled:cursor-not-allowed"
                  placeholder="Enter new password"
                  autoComplete="new-password"
                />

                {errors.password && (
                  <p className="text-red-500 text-xs mt-1">
                    {errors.password}
                  </p>
                )}
              </div>
            </div>

            {/* Confirm Password */}
            <div className="space-y-2">

              <label className="text-xs font-bold text-slate-500 uppercase tracking-widest">
                Confirm Password
              </label>

              <div className="relative group">

                <Lock
                  className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-500 group-focus-within:text-primary transition-colors"
                  size={20}
                />

                <input
                  type="password"
                  name="confirmPassword"
                  value={formData.confirmPassword}
                  onChange={handleChange}
                  disabled={!token || loading || !!successMessage}
                  className="w-full bg-surface-dark border-slate-800 rounded-2xl py-4 pl-12 pr-4 text-sm focus:ring-2 focus:ring-primary focus:border-transparent transition-all outline-none disabled:opacity-50 disabled:cursor-not-allowed"
                  placeholder="Confirm new password"
                  autoComplete="new-password"
                />

                {errors.confirmPassword && (
                  <p className="text-red-500 text-xs mt-1">
                    {errors.confirmPassword}
                  </p>
                )}
              </div>
            </div>

            {/* Submit */}
            <button
              type="submit"
              disabled={
                !token ||
                loading ||
                !!successMessage
              }
              className="w-full bg-primary hover:bg-primary-hover disabled:opacity-50 disabled:cursor-not-allowed text-white py-4 rounded-2xl font-bold shadow-xl shadow-primary/30 transition-all transform active:scale-[0.98]"
            >
              {loading
                ? "Resetting Password..."
                : "Reset Password"}
            </button>
          </form>

          {/* Back to Login */}
          <p className="text-center text-slate-500 text-sm">
            Remember your password?{" "}
            <button
              type="button"
              onClick={() => navigate("/login")}
              className="text-primary font-bold hover:underline"
            >
              Back to Login
            </button>
          </p>

        </div>
      </div>
    </div>
  );
};

export default ResetPassword;

