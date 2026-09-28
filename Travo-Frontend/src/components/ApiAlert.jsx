import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { clearApiStatus } from "../Redux/Slices/apiStatusSlice";

const ApiAlert = () => {
  const dispatch = useDispatch();
  const { status, message } = useSelector((state) => state.apiStatus);

  useEffect(() => {
    if (status === "succeeded" || status === "failed") {
      const timer = setTimeout(() => dispatch(clearApiStatus()), 3000);
      return () => clearTimeout(timer);
    }
  }, [dispatch, status, message]);

  if (!message || (status !== "succeeded" && status !== "failed")) return null;

  return (
    <div
      role="alert"
      className={`fixed left-1/2 top-4 z-[100] -translate-x-1/2 rounded-lg border px-4 py-3 text-sm shadow-lg ${
        status === "failed"
          ? "border-red-400/30 bg-red-500/15 text-red-100"
          : "border-emerald-400/30 bg-emerald-500/15 text-emerald-50"
      }`}
    >
      {message}
    </div>
  );
};

export default ApiAlert;