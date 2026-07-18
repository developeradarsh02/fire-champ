/**
 * Firebase config - Fire Champ Admin Panel
 *
 * Firebase project: fire-champ-214b4
 * Setup steps:
 *  1. Firebase Console → Project Settings → Your apps → Web app → Config
 *  2. firebaseConfig object copy karke yahan paste karna
 *  3. Hosting enable karna (admin-panel/dist deploy hoga)
 */
import { initializeApp } from 'firebase/app'
import { getAuth } from 'firebase/auth'
import { getFirestore } from 'firebase/firestore'
import { getStorage } from 'firebase/storage'
import { getFunctions } from 'firebase/functions'

const firebaseConfig = {
  apiKey: "AIzaSyBznPjYHKL6vLOC76JUIP1sMpqF1QpzuWc",
  authDomain: "fire-champ-214b4.firebaseapp.com",
  projectId: "fire-champ-214b4",
  storageBucket: "fire-champ-214b4.firebasestorage.app",
  messagingSenderId: "22518820947",
  appId: "1:22518820947:web:841812f6832a864d59d6a6"
}

const app = initializeApp(firebaseConfig)
export const auth = getAuth(app)
export const db = getFirestore(app)
export const storage = getStorage(app)
export const functions = getFunctions(app)
