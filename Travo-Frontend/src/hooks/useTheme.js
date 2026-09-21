import { useEffect, useState } from "react";
export function useTheme() {
    const getInitialTheme = () => {
        if (typeof window === "undefined")
            return "light";
        const saved = localStorage.getItem("theme");
        if (saved)
            return saved;
        return window.matchMedia("(prefers-color-scheme: dark)").matches
            ? "dark"
            : "light";
    };
    const [theme, setTheme] = useState(getInitialTheme);
    useEffect(() => {
        const root = window.document.documentElement;
        if (theme === "dark") {
            root.classList.add("dark");
        }
        else {
            root.classList.remove("dark");
        }
        localStorage.setItem("theme", theme);
    }, [theme]);
    const toggleTheme = () => {
        setTheme(prev => (prev === "dark" ? "light" : "dark"));
        console.log(document.documentElement.classList.contains("dark"));
    };
    return { theme, toggleTheme };
}
