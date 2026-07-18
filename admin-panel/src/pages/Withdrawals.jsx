import { useEffect, useState } from 'react'
import { collection, onSnapshot, query, orderBy } from 'firebase/firestore'
import { httpsCallable } from 'firebase/functions'
import { db, functions } from '../firebase/firebase'
import { InlineSpinner } from '../components/Spinner'

const STATUS_COLORS = {
  PENDING: 'bg-yellow-500/20 text-yellow-400',
  APPROVED: 'bg-green-500/20 text-green-400',
  REJECTED: 'bg-red-500/20 text-red-400',
}

export default function Withdrawals() {
  const [requests, setRequests] = useState([])
  const [loading, setLoading] = useState(true)
  const [filter, setFilter] = useState('PENDING')

  useEffect(() => {
    const q = query(collection(db, 'withdrawalRequests'), orderBy('requestedAt', 'desc'))
    const unsub = onSnapshot(q, (snap) => {
      setRequests(snap.docs.map(d => ({ id: d.id, ...d.data() })))
      setLoading(false)
    })
    return () => unsub()
  }, [])

  const handleProcess = async (item, action) => {
    const note = action === 'REJECT' ? prompt('Reason for rejection:') : ''
    if (action === 'REJECT' && !note) return
    try {
      const fn = httpsCallable(functions, 'processWithdrawal')
      await fn({ requestId: item.id, action, note })
    } catch (err) {
      alert(err.message)
    }
  }

  const filtered = filter === 'ALL' ? requests : requests.filter(r => r.status === filter)

  return (
    <div>
      <h1 className="text-3xl font-bold mb-6">Withdrawals</h1>
      <div className="flex gap-2 mb-4">
        {['PENDING', 'APPROVED', 'REJECTED', 'ALL'].map(s => (
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
        <div className="space-y-3">
          {filtered.map((req) => (
            <div key={req.id} className="bg-brand-dark rounded-xl p-4 flex items-center gap-4">
              <div className="flex-1">
                <h3 className="font-semibold">₹{req.amount} <span className={`ml-2 px-2 py-0.5 rounded text-xs font-medium ${STATUS_COLORS[req.status]}`}>{req.status}</span></h3>
                <p className="text-sm text-gray-400">
                  User: {req.userId} • Method: {req.method} •
                  {req.method === 'UPI' ? ` UPI: ${req.payoutDetails?.upiId}` : ` A/C: ****${req.payoutDetails?.accountNumber?.slice(-4)}`}
                </p>
                {req.adminNote && <p className="text-xs text-gray-500 mt-1">Note: {req.adminNote}</p>}
              </div>
              {req.status === 'PENDING' && (
                <>
                  <button onClick={() => handleProcess(req, 'APPROVE')} className="px-3 py-1.5 bg-green-600 rounded text-sm font-medium">Approve</button>
                  <button onClick={() => handleProcess(req, 'REJECT')} className="px-3 py-1.5 bg-red-600 rounded text-sm font-medium">Reject</button>
                </>
              )}
            </div>
          ))}
          {filtered.length === 0 && <p className="text-gray-400 text-center py-8">No {filter.toLowerCase()} requests</p>}
        </div>
      )}
    </div>
  )
}
