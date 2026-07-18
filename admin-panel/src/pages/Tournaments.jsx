import { useEffect, useState } from 'react'
import { collection, onSnapshot, query, orderBy } from 'firebase/firestore'
import { Link } from 'react-router-dom'
import { db } from '../firebase/firebase'
import { InlineSpinner } from '../components/Spinner'

const STATUS_COLORS = {
  ONGOING: 'bg-green-500/20 text-green-400',
  UPCOMING: 'bg-yellow-500/20 text-yellow-400',
  RESULTS: 'bg-gray-500/20 text-gray-400',
}

export default function Tournaments() {
  const [tournaments, setTournaments] = useState([])
  const [loading, setLoading] = useState(true)
  const [filter, setFilter] = useState('ALL')

  useEffect(() => {
    const q = query(collection(db, 'tournaments'), orderBy('dateTime', 'desc'))
    const unsubscribe = onSnapshot(q, (snapshot) => {
      const list = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }))
      setTournaments(list)
      setLoading(false)
    })
    return () => unsubscribe()
  }, [])

  const filtered = filter === 'ALL' ? tournaments : tournaments.filter(t => t.status === filter)

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold">Tournaments</h1>
        <Link to="/tournaments/new" className="px-4 py-2 bg-brand-primary rounded-lg font-medium">+ New Tournament</Link>
      </div>

      <div className="flex gap-2 mb-4">
        {['ALL', 'UPCOMING', 'ONGOING', 'RESULTS'].map((s) => (
          <button
            key={s}
            onClick={() => setFilter(s)}
            className={`px-4 py-1.5 rounded-full text-sm font-medium ${
              filter === s ? 'bg-brand-primary text-white' : 'bg-brand-dark text-gray-400'
            }`}
          >
            {s}
          </button>
        ))}
      </div>

      {loading ? (
        <div className="flex justify-center p-12"><InlineSpinner /></div>
      ) : (
        <div className="bg-brand-dark rounded-xl border border-gray-800 overflow-hidden">
          <table className="w-full">
            <thead className="bg-black">
              <tr className="text-left text-xs text-gray-400 uppercase">
                <th className="p-3">Title</th>
                <th className="p-3">Mode</th>
                <th className="p-3">Date</th>
                <th className="p-3">Prize</th>
                <th className="p-3">Slots</th>
                <th className="p-3">Status</th>
                <th className="p-3">Actions</th>
              </tr>
            </thead>
            <tbody>
              {filtered.map((t) => (
                <tr key={t.id} className="border-t border-gray-800 hover:bg-black/50">
                  <td className="p-3 text-sm">{t.title}</td>
                  <td className="p-3 text-sm">{t.mode}</td>
                  <td className="p-3 text-sm">{t.dateTime}</td>
                  <td className="p-3 text-sm">₹{t.prizePool}</td>
                  <td className="p-3 text-sm">{t.slotsFilled}/{t.totalSlots}</td>
                  <td className="p-3">
                    <span className={`px-2 py-1 rounded-full text-xs font-medium ${STATUS_COLORS[t.status]}`}>
                      {t.status}
                    </span>
                  </td>
                  <td className="p-3 text-sm">
                    <button className="text-brand-primary hover:underline">Edit</button>
                  </td>
                </tr>
              ))}
              {filtered.length === 0 && (
                <tr><td colSpan="7" className="p-8 text-center text-gray-500">No tournaments found</td></tr>
              )}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
