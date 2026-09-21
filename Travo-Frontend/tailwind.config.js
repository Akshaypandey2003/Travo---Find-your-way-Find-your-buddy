/** @type {import('tailwindcss').Config} */
export default {
  darkMode: 'class',

   content: [
    "./index.html",

    // scan ALL source files
    "./src/**/*.{js,ts,jsx,tsx}",

    // important if using components folder
    "./src/**/**/*.{js,ts,jsx,tsx}",
  ],

  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: '#ff782e',
          hover: '#e66a25',
        },
        background: {
          light: '#ffffff',
          dark: '#000000',
        },
        surface: {
          dark: '#0f0f0f',
          darker: '#080808',
          lighter: '#1a1a1a',
        }
      },

      fontFamily: {
        sans: ['Plus Jakarta Sans', 'sans-serif'],
        display: ['Plus Jakarta Sans', 'sans-serif'],
      },

      borderRadius: {
        DEFAULT: '0.5rem',
        lg: '1rem',
        xl: '1.5rem',
        '2xl': '2rem',
        full: '9999px',
      },
    },
  },

  plugins: [],
}