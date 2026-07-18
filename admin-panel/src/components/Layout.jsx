import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { signOut } from 'firebase/auth'
import { auth } from '../firebase/firebase'

const navItems = [
  { path: '/', label: 'Dashboard', icon: '📊' },
  { path: '/tournaments', label: 'Tournaments', icon: '🎮' },
  { path: '/results', label: 'Results', icon: '🏆' },
  { path: '/withdrawals', label: 'Withdrawals', icon: '💸' },
  { path: '/users', label: 'Users', icon: '👥' },
  { path: '/announcements', label: 'Announcements', icon: '📢' },
]

export default function Layout() {
  const navigate = useNavigate()
  const handleLogout = async () => {
    await signOut(auth)
    navigate('/login')
  }

  return (
    <div className="min-h-screen flex bg-black text-white">
      {/* Sidebar */}
      <aside className="w-64 bg-brand-dark border-r border-gray-800 p-4 flex flex-col">
        <div className="mb-8">
          <h1 className="text-2xl font-bold text-brand-primary">🔥 Fire Champ</h1>
          <p className="text-xs text-gray-400 mt-1">Admin Panel</p>
        </div>
        <nav className="flex-1 space-y-1">
          {navItems.map((item) => (
            <NavLink
              key={item.path}
              to={item.path}
              end={item.path === '/'}
              className={({ isActive }) =>
                `flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition ${
                  isActive
                    ? 'bg-brand-primary text-white'
                    : 'text-gray-300 hover:bg-gray-800'
                }`
              }
            >
              <span className="text-lg">{item.icon}</span>
              {item.label}
            </NavLink>
          ))}
        </nav>
        <button
          onClick={handleLogout}
          className="mt-4 px-3 py-2 text-sm text-red-400 hover:bg-red-500/10 rounded-lg"
        >
          Logout
        </button>
      </aside>

      {/* Main content */}
      <main className="flex-1 overflow-y-auto p-6">
        <Outlet />
      </main>
    </div>
  )
}
