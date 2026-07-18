import { useEffect, useState } from 'react'
import { collection, getCountFromServer, query, where } from 'firebase/firestore'
import { db } from '../firebase/firebase'
import { Link } from 'react-router-dom'

function StatCard({ label, value, color, icon, link }) {
  const Card = (
    <div className="bg-brand-dark rounded-xl p-5 border border-gray-800 hover:border-brand-primary transition">
      <div className="flex items-start justify-between">
        <div>
          <p className="text-gray-400 text-sm">{label}</p>
          <p className={`text-3xl font-bold mt-2 ${color}`}>{value}</p>
        </div>
        <span className="text-3xl">{icon}</span>
      </div>
    </div>
  )
  return link ? <Link to={link}>{Card}</Link> : Card
}

export default function Dashboard() {
  const [stats, setStats] = useState({ users: 0, ongoing: 0, upcoming: 0, pendingWithdrawals: 0 })
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    async function load() {
      try {
        const [usersSnap, ongoingSnap, upcomingSnap, pendingSnap] = await Promise.all([
          getCountFromServer(collection(db, 'users')),
          getCountFromServer(query(collection(db, 'tournaments'), where('status', '==', 'ONGOING'))),
          getCountFromServer(query(collection(db, 'tournaments'), where('status', '==', 'UPCOMING'))),
          getCountFromServer(query(collection(db, 'withdrawalRequests'), where('status', '==', 'PENDING'))),
        ])
        setStats({
          users: usersSnap.data().count,
          ongoing: ongoingSnap.data().count,
          upcoming: upcomingSnap.data().count,
          pendingWithdrawals: pendingSnap.data().count,
        })
      } catch (err) {
        console.error('Dashboard load error:', err)
      } finally {
        setLoading(false)
      }
    }
    load()
  }, [])

  return (
    <div>
      <h1 className="text-3xl font-bold mb-6">Dashboard</h1>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard label="Total Users" value={loading ? '...' : stats.users} color="text-white" icon="👥" link="/users" />
        <StatCard label="Ongoing Tournaments" value={loading ? '...' : stats.ongoing} color="text-green-400" icon="🎮" link="/tournaments" />
        <StatCard label="Upcoming Tournaments" value={loading ? '...' : stats.upcoming} color="text-yellow-400" icon="⏰" link="/tournaments" />
        <StatCard label="Pending Withdrawals" value={loading ? '...' : stats.pendingWithdrawals} color="text-red-400" icon="💸" link="/withdrawals" />
      </div>

      <div className="mt-8 grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-brand-dark rounded-xl p-6 border border-gray-800">
          <h2 className="text-xl font-semibold mb-4">Quick Actions</h2>
          <div className="space-y-2">
            <Link to="/tournaments/new" className="block p-3 bg-black rounded-lg hover:bg-gray-900">+ Create New Tournament</Link>
            <Link to="/results" className="block p-3 bg-black rounded-lg hover:bg-gray-900">🏆 Review Pending Results</Link>
            <Link to="/withdrawals" className="block p-3 bg-black rounded-lg hover:bg-gray-900">💸 Process Withdrawals</Link>
            <Link to="/announcements" className="block p-3 bg-black rounded-lg hover:bg-gray-900">📢 Post Announcement</Link>
          </div>
        </div>
        <div className="bg-brand-dark rounded-xl p-6 border border-gray-800">
          <h2 className="text-xl font-semibold mb-4">System Status</h2>
          <ul className="space-y-2 text-sm">
            <li className="flex justify-between"><span>Firebase Auth</span><span className="text-green-400">✓ Active</span></li>
            <li className="flex justify-between"><span>Firestore</span><span className="text-green-400">✓ Active</span></li>
            <li className="flex justify-between"><span>Cloud Functions</span><span className="text-green-400">✓ Deployed</span></li>
            <li className="flex justify-between"><span>FCM</span><span className="text-green-400">✓ Active</span></li>
          </ul>
        </div>
      </div>
    </div>
  )
}
