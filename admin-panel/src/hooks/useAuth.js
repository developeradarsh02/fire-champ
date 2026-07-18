import { useEffect, useState } from 'react'
import { onAuthStateChanged } from 'firebase/auth'
import { auth } from '../firebase/firebase'

/**
 * useAuth - Firebase Auth state observe karta hai.
 *
 * Returns:
 *  - user: Currently logged in user (null if not logged in)
 *  - loading: Initial check ho raha hai
 *  - isAdmin: user.admin custom claim true hai?
 */
export function useAuth() {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)
  const [isAdmin, setIsAdmin] = useState(false)

  useEffect(() => {
    const unsubscribe = onAuthStateChanged(auth, async (firebaseUser) => {
      if (firebaseUser) {
        const tokenResult = await firebaseUser.getIdTokenResult()
        setIsAdmin(!!tokenResult.claims.admin)
        setUser(firebaseUser)
      } else {
        setUser(null)
        setIsAdmin(false)
      }
      setLoading(false)
    })
    return () => unsubscribe()
  }, [])

  return { user, loading, isAdmin }
}
