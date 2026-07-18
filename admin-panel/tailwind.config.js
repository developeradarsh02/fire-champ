/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,jsx,ts,tsx}"],
  theme: {
    extend: {
      colors: {
        brand: {
          primary: '#6A0DFF',
          dark: '#1A0A3D',
          deep: '#000000',
          accent: '#E53935',
          success: '#4CAF50',
        }
      }
    }
  },
  plugins: []
}
