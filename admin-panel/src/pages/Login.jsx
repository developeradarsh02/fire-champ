import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { signInWithEmailAndPassword } from 'firebase/auth'
import { auth } from '../firebase/firebase'

export default function Login() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()

  const handleLogin = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const cred = await signInWithEmailAndPassword(auth, email, password)
      const tokenResult = await cred.user.getIdTokenResult()
      if (!tokenResult.claims.admin) {
        setError('You do not have admin access')
        await auth.signOut()
        return
      }
      navigate('/')
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-black p-4">
      <div className="w-full max-w-md bg-brand-dark rounded-2xl p-8 shadow-2xl">
        <div className="text-center mb-8">
          <img src="/logo.png" alt="Fire Champ" className="w-32 mx-auto mb-4" />
          <h1 className="text-3xl font-bold text-brand-primary mb-2">Fire Champ</h1>
          <p className="text-gray-400">Admin Panel Login</p>
        </div>
        <form onSubmit={handleLogin} className="space-y-4">
          <div>
            <label className="block text-sm text-gray-400 mb-1">Email</label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="w-full px-4 py-3 bg-black border border-gray-700 rounded-lg text-white focus:border-brand-primary focus:outline-none"
              required
            />
          </div>
          <div>
            <label className="block text-sm text-gray-400 mb-1">Password</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full px-4 py-3 bg-black border border-gray-700 rounded-lg text-white focus:border-brand-primary focus:outline-none"
              required
            />
          </div>
          {error && <p className="text-sm text-red-400">{error}</p>}
          <button
            type="submit"
            disabled={loading}
            className="w-full py-3 bg-brand-primary text-white rounded-lg font-semibold hover:opacity-90 disabled:opacity-50"
          >
            {loading ? 'Logging in...' : 'Login'}
          </button>
        </form>
        <p className="text-xs text-gray-500 mt-6 text-center">
          Only authorized admin accounts can access this panel.
        </p>
      </div>
    </div>
  )
}
