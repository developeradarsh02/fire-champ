import { useEffect, useState } from 'react'
import { collection, getDocs } from 'firebase/firestore'
import { httpsCallable } from 'firebase/functions'
import { db, functions } from '../firebase/firebase'
import { InlineSpinner } from '../components/Spinner'

export default function Results() {
  const [pending, setPending] = useState([])
  const [loading, setLoading] = useState(true)
  const [filter, setFilter] = useState('PENDING')

  const load = async () => {
    setLoading(true)
    try {
      const tournamentsSnap = await getDocs(collection(db, 'tournaments'))
      const list = []
      for (const tourDoc of tournamentsSnap.docs) {
        const participantsSnap = await getDocs(collection(db, 'tournaments', tourDoc.id, 'participants'))
        participantsSnap.forEach((pDoc) => {
          const data = pDoc.data()
          if (data.submissionStatus === filter) {
            list.push({
              tournamentId: tourDoc.id,
              tournamentTitle: tourDoc.data().title,
              userId: pDoc.id,
              ...data,
            })
          }
        })
      }
      setPending(list)
    } catch (err) {
      console.error('Results load error:', err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { load() }, [filter])

  const handleApprove = async (item) => {
    const processFn = httpsCallable(functions, 'processResult')
    await processFn({ tournamentId: item.tournamentId, userId: item.userId, action: 'APPROVE' })
    load()
  }

  const handleReject = async (item) => {
    const reason = prompt('Rejection reason:')
    if (!reason) return
    const processFn = httpsCallable(functions, 'processResult')
    await processFn({ tournamentId: item.tournamentId, userId: item.userId, action: 'REJECT', reason })
    load()
  }

  return (
    <div>
      <h1 className="text-3xl font-bold mb-6">Result Verification</h1>
      <div className="flex gap-2 mb-4">
        {['PENDING', 'VERIFIED', 'REJECTED'].map(s => (
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
      ) : pending.length === 0 ? (
        <p className="text-gray-400">No {filter.toLowerCase()} results</p>
      ) : (
        <div className="space-y-3">
          {pending.map((item) => (
            <div key={`${item.tournamentId}-${item.userId}`} className="bg-brand-dark rounded-xl p-4 flex items-center gap-4">
              <div className="flex-1">
                <h3 className="font-semibold">{item.tournamentTitle}</h3>
                <p className="text-sm text-gray-400">User: {item.userId} • Kills: {item.submittedKills} • Rank: #{item.submittedRank}</p>
              </div>
              {item.screenshotUrl && (
                <a href={item.screenshotUrl} target="_blank" rel="noreferrer" className="px-3 py-1.5 bg-black rounded text-sm">📷 View Screenshot</a>
              )}
              {filter === 'PENDING' && (
                <>
                  <button onClick={() => handleApprove(item)} className="px-3 py-1.5 bg-green-600 rounded text-sm font-medium">Approve</button>
                  <button onClick={() => handleReject(item)} className="px-3 py-1.5 bg-red-600 rounded text-sm font-medium">Reject</button>
                </>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
