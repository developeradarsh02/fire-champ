import { useEffect, useState } from 'react'
import { collection, getDocs, orderBy, query } from 'firebase/firestore'
import { db } from '../firebase/firebase'
import { InlineSpinner } from '../components/Spinner'

export default function Users() {
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(true)
  const [search, setSearch] = useState('')

  useEffect(() => {
    async function load() {
      try {
        const snap = await getDocs(query(collection(db, 'users'), orderBy('createdAt', 'desc')))
        setUsers(snap.docs.map(d => ({ id: d.id, ...d.data() })))
      } catch (err) {
        console.error('Users load error:', err)
      } finally {
        setLoading(false)
      }
    }
    load()
  }, [])

  const filtered = users.filter(u =>
    (u.username || '').toLowerCase().includes(search.toLowerCase()) ||
    (u.email || '').toLowerCase().includes(search.toLowerCase())
  )

  return (
    <div>
      <h1 className="text-3xl font-bold mb-6">Users</h1>
      <input
        type="text"
        placeholder="Search by username or email..."
        value={search}
        onChange={(e) => setSearch(e.target.value)}
        className="w-full mb-4 px-4 py-2.5 bg-brand-dark border border-gray-700 rounded-lg text-white"
      />
      {loading ? (
        <div className="flex justify-center p-12"><InlineSpinner /></div>
      ) : (
        <div className="bg-brand-dark rounded-xl border border-gray-800 overflow-hidden">
          <table className="w-full">
            <thead className="bg-black">
              <tr className="text-left text-xs text-gray-400 uppercase">
                <th className="p-3">Username</th>
                <th className="p-3">Email</th>
                <th className="p-3">Deposited</th>
                <th className="p-3">Winning</th>
                <th className="p-3">Matches</th>
                <th className="p-3">Verified</th>
                <th className="p-3">Actions</th>
              </tr>
            </thead>
            <tbody>
              {filtered.map((u) => (
                <tr key={u.id} className="border-t border-gray-800">
                  <td className="p-3 text-sm font-medium">{u.username || '-'}</td>
                  <td className="p-3 text-sm">{u.email || '-'}</td>
                  <td className="p-3 text-sm">₹{u.depositedBalance || 0}</td>
                  <td className="p-3 text-sm text-yellow-400">₹{u.winningBalance || 0}</td>
                  <td className="p-3 text-sm">{u.matchesPlayed || 0}</td>
                  <td className="p-3 text-sm">{u.isVerified ? '✅' : '❌'}</td>
                  <td className="p-3 text-sm">
                    <button className="text-brand-primary hover:underline mr-2">View</button>
                    <button className="text-red-400 hover:underline">Ban</button>
                  </td>
                </tr>
              ))}
              {filtered.length === 0 && (
                <tr><td colSpan="7" className="p-8 text-center text-gray-500">No users found</td></tr>
              )}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
