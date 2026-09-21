import { Sun, Moon } from "lucide-react";
import { useTheme } from "@/hooks/useTheme";
export default function ThemeToggle() {
    const { theme, toggleTheme } = useTheme();
    console.log("Current theme: ", theme);
    return (<button onClick={toggleTheme} className="
        relative inline-flex items-center justify-center
        w-10 h-10 rounded-full
        bg-surface-light dark:bg-surface-dark

        hover:bg-primary/10
        transition-all duration-300
      " aria-label="Toggle Theme">
      {theme === "dark" ? (<Sun size={18} className="text-primary"/>) : (<Moon size={18} className="text-primary"/>)}
    </button>);
}
