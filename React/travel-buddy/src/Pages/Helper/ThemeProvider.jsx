/* eslint-disable react/prop-types */
// ThemeProvider.jsx
import { useSelector } from "react-redux";
import { useEffect } from "react";

const ThemeProvider = ({ children }) => {
  const theme = useSelector((store) => store.auth.theme); // light or dark

  useEffect(() => {
    if (theme === "dark") {
      document.documentElement.classList.add("dark");
    } else {
      document.documentElement.classList.remove("dark");
    }
  }, [theme]);

  return <>{children}</>;
};

export default ThemeProvider;
