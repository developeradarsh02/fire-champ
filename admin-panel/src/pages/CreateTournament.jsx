import { useState, useCallback, memo } from 'react'
import { useNavigate } from 'react-router-dom'
import { collection, addDoc, serverTimestamp } from 'firebase/firestore'
import { db } from '../firebase/firebase'
import { useAuth } from '../hooks/useAuth'

/**
 * Create Tournament - admin panel.
 *
 * NOTE: Direct Firestore write (no Cloud Function) since Blaze plan not active.
 * Admin custom claim (`request.auth.token.admin == true`) allows write via rules.
 *
 * Cursor focus fix: input handlers use useCallback + Field component is memoized
 * to prevent re-render on every parent state change.
 */
export default function CreateTournament() {
  const navigate = useNavigate()
  const { isAdmin } = useAuth()
  const [form, setForm] = useState({
    title: '',
    gameModeId: 'FULL_MAP',
    mode: 'SOLO',
    map: 'BERMUDA',
    dateTime: '',
    prizePool: 500,
    perKill: 7,
    entryFee: 30,
    totalSlots: 48,
    bannerUrl: '',
  })
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  // Stable handler - doesn't recreate on every render
  const handleChange = useCallback((e) => {
    const { name, value, type } = e.target
    setForm(prev => ({
      ...prev,
      [name]: type === 'number' ? (value === '' ? '' : Number(value)) : value
    }))
  }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!isAdmin) { setError('Admin only'); return }
    setSaving(true)
    setError('')
    setSuccess('')
    try {
      // Direct Firestore write - admin custom claim allows this
      const docData = {
        ...form,
        prizePool: Number(form.prizePool),
        perKill: Number(form.perKill),
        entryFee: Number(form.entryFee),
        totalSlots: Number(form.totalSlots),
        slotsFilled: 0,
        status: 'UPCOMING',
        createdAt: serverTimestamp(),
        createdBy: 'admin',
      }
      // Remove empty bannerUrl
      if (!docData.bannerUrl) delete docData.bannerUrl
      // Auto-generate matchId
      docData.matchId = `M-${Date.now().toString().slice(-6)}`

      const docRef = await addDoc(collection(db, 'tournaments'), docData)
      setSuccess(`✅ Tournament created! ID: ${docRef.id}`)
      setTimeout(() => navigate('/tournaments'), 1200)
    } catch (err) {
      console.error('Create tournament error:', err)
      setError(err.message || 'Failed to create tournament. Make sure you are admin.')
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="max-w-2xl">
      <h1 className="text-3xl font-bold mb-6">Create Tournament</h1>
      <form onSubmit={handleSubmit} className="bg-brand-dark rounded-xl p-6 space-y-4" autoComplete="off">
        <Field name="title" label="Title" value={form.title} onChange={handleChange} required />

        <div className="grid grid-cols-2 gap-3">
          <Field
            name="gameModeId"
            label="Game Mode"
            value={form.gameModeId}
            onChange={handleChange}
            options={['FULL_MAP', 'CS_1V1', 'LW_1V1', 'CS_2V2', 'CS_4V4', 'LW_2V2']}
          />
          <Field
            name="mode"
            label="Match Mode"
            value={form.mode}
            onChange={handleChange}
            options={['SOLO', 'DUO', 'SQUAD']}
          />
        </div>

        <div className="grid grid-cols-2 gap-3">
          <Field
            name="map"
            label="Map"
            value={form.map}
            onChange={handleChange}
            options={['BERMUDA', 'PURGATORY', 'KALAHARI', 'ALPINE', 'SOLO']}
          />
          <Field
            name="dateTime"
            label="Date/Time"
            value={form.dateTime}
            onChange={handleChange}
            placeholder="13/07/2026 08:30 pm"
            required
          />
        </div>

        <div className="grid grid-cols-2 gap-3">
          <Field name="prizePool" label="Prize Pool" type="number" value={form.prizePool} onChange={handleChange} />
          <Field name="perKill" label="Per Kill" type="number" value={form.perKill} onChange={handleChange} />
        </div>

        <div className="grid grid-cols-2 gap-3">
          <Field name="entryFee" label="Entry Fee" type="number" value={form.entryFee} onChange={handleChange} />
          <Field name="totalSlots" label="Total Slots" type="number" value={form.totalSlots} onChange={handleChange} />
        </div>

        <Field name="bannerUrl" label="Banner URL (optional)" value={form.bannerUrl} onChange={handleChange} />

        {error && <p className="text-red-400 text-sm">❌ {error}</p>}
        {success && <p className="text-green-400 text-sm">{success}</p>}

        <button
          type="submit"
          disabled={saving}
          className="w-full py-3 bg-brand-primary text-white rounded-lg font-semibold disabled:opacity-50"
        >
          {saving ? 'Creating...' : 'Create Tournament'}
        </button>

        <p className="text-xs text-gray-500 text-center">
          Direct Firestore write (Cloud Functions not deployed). Admin claim required.
        </p>
      </form>
    </div>
  )
}

/**
 * Memoized Field component - prevents re-render on parent state changes
 * to avoid losing cursor focus in inputs.
 */
const Field = memo(function Field({ name, label, type = 'text', value, onChange, options, required, placeholder }) {
  return (
    <div>
      <label className="block text-sm text-gray-400 mb-1">{label}</label>
      {options ? (
        <select
          name={name}
          value={value}
          onChange={onChange}
          className="w-full px-4 py-2.5 bg-black border border-gray-700 rounded-lg text-white focus:border-brand-primary focus:outline-none"
        >
          {options.map(o => <option key={o} value={o}>{o}</option>)}
        </select>
      ) : (
        <input
          type={type}
          name={name}
          value={value ?? ''}
          onChange={onChange}
          required={required}
          placeholder={placeholder}
          autoComplete="off"
          className="w-full px-4 py-2.5 bg-black border border-gray-700 rounded-lg text-white focus:border-brand-primary focus:outline-none"
        />
      )}
    </div>
  )
})
