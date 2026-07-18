import { useState, useEffect } from 'react'
import { collection, addDoc, serverTimestamp, onSnapshot, query, orderBy, deleteDoc, doc } from 'firebase/firestore'
import { db } from '../firebase/firebase'
import { InlineSpinner } from '../components/Spinner'

export default function Announcements() {
  const [title, setTitle] = useState('')
  const [message, setMessage] = useState('')
  const [imageUrl, setImageUrl] = useState('')
  const [sending, setSending] = useState(false)
  const [success, setSuccess] = useState('')
  const [announcements, setAnnouncements] = useState([])
  const [loading, setLoading] = useState(true)

  // Live listener for announcements
  useEffect(() => {
    const q = query(collection(db, 'announcements'), orderBy('createdAt', 'desc'))
    const unsubscribe = onSnapshot(q, (snapshot) => {
      const list = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }))
      setAnnouncements(list)
      setLoading(false)
    }, (err) => {
      console.error('Announcements listener error:', err)
      setLoading(false)
    })
    return () => unsubscribe()
  }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setSending(true)
    setSuccess('')
    try {
      await addDoc(collection(db, 'announcements'), {
        title,
        message,
        imageUrl: imageUrl || null,
        createdAt: serverTimestamp(),
        active: true,
      })
      setSuccess('✅ Announcement posted!')
      setTitle('')
      setMessage('')
      setImageUrl('')
    } catch (err) {
      setSuccess('❌ ' + err.message)
    } finally {
      setSending(false)
    }
  }

  const handleDelete = async (id) => {
    if (!confirm('Delete this announcement?')) return
    try {
      await deleteDoc(doc(db, 'announcements', id))
    } catch (err) {
      alert('Delete failed: ' + err.message)
    }
  }

  const formatDate = (ts) => {
    if (!ts) return '...'
    const date = ts.toDate ? ts.toDate() : new Date(ts)
    return date.toLocaleString('en-IN', { dateStyle: 'medium', timeStyle: 'short' })
  }

  return (
    <div>
      <h1 className="text-3xl font-bold mb-6">📢 Announcements</h1>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* POST FORM */}
        <div>
          <h2 className="text-xl font-semibold mb-4">Post New</h2>
          <form onSubmit={handleSubmit} className="bg-brand-dark rounded-xl p-6 space-y-4">
            <div>
              <label className="block text-sm text-gray-400 mb-1">Title</label>
              <input
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                className="w-full px-4 py-2.5 bg-black border border-gray-700 rounded-lg text-white"
                placeholder="FREE FIRE ❌ FREE FIRE MAX ✅"
                required
              />
            </div>
            <div>
              <label className="block text-sm text-gray-400 mb-1">Message</label>
              <textarea
                value={message}
                onChange={(e) => setMessage(e.target.value)}
                className="w-full px-4 py-2.5 bg-black border border-gray-700 rounded-lg text-white h-32"
                placeholder="Use line breaks for bullet points..."
                required
              />
            </div>
            <div>
              <label className="block text-sm text-gray-400 mb-1">Image URL (optional)</label>
              <input
                value={imageUrl}
                onChange={(e) => setImageUrl(e.target.value)}
                className="w-full px-4 py-2.5 bg-black border border-gray-700 rounded-lg text-white"
                placeholder="https://..."
              />
            </div>
            {success && <p className={`text-sm ${success.startsWith('✅') ? 'text-green-400' : 'text-red-400'}`}>{success}</p>}
            <button type="submit" disabled={sending} className="w-full py-3 bg-brand-primary text-white rounded-lg font-semibold disabled:opacity-50">
              {sending ? 'Posting...' : 'Post Announcement'}
            </button>
          </form>
        </div>

        {/* EXISTING LIST */}
        <div>
          <h2 className="text-xl font-semibold mb-4">Recent ({announcements.length})</h2>
          {loading ? (
            <div className="flex justify-center p-12"><InlineSpinner /></div>
          ) : announcements.length === 0 ? (
            <div className="bg-brand-dark rounded-xl p-8 text-center text-gray-500 border border-gray-800">
              No announcements yet. Post one to get started!
            </div>
          ) : (
            <div className="space-y-3 max-h-[600px] overflow-y-auto">
              {announcements.map((a) => (
                <div key={a.id} className="bg-brand-dark rounded-xl p-4 border border-gray-800">
                  <div className="flex justify-between items-start mb-2">
                    <h3 className="font-semibold text-white">{a.title}</h3>
                    <button
                      onClick={() => handleDelete(a.id)}
                      className="text-red-400 hover:text-red-300 text-sm"
                    >
                      Delete
                    </button>
                  </div>
                  <p className="text-sm text-gray-300 whitespace-pre-wrap mb-2">{a.message}</p>
                  {a.imageUrl && (
                    <a href={a.imageUrl} target="_blank" rel="noreferrer" className="text-xs text-brand-primary hover:underline block mb-2">
                      📎 Image
                    </a>
                  )}
                  <p className="text-xs text-gray-500">{formatDate(a.createdAt)}</p>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
