# 🔥 Fire Champ Admin Panel

React + Vite + Tailwind CSS + Firebase admin dashboard for managing Fire Champ tournaments, users, withdrawals, and announcements.

## 🚀 Quick Start (5 minutes)

### 1. Add Web App to Firebase Project

1. Open [Firebase Console](https://console.firebase.google.com) → project **fire-champ-214b4**
2. Project Settings (⚙️ icon) → **Your apps** → Click **Web icon `</>`** to add a web app
3. Nickname: `Fire Champ Admin`
4. **Don't** enable Firebase Hosting (we'll do it manually)
5. Copy the `firebaseConfig` object

### 2. Update Firebase Config

Edit `src/firebase/firebase.js` and replace the placeholder values with your actual config:

```js
const firebaseConfig = {
  apiKey: "AIza...",              // From Firebase Console
  authDomain: "fire-champ-214b4.firebaseapp.com",
  projectId: "fire-champ-214b4",
  storageBucket: "fire-champ-214b4.firebasestorage.app",
  messagingSenderId: "22518820947",
  appId: "1:22518820947:web:xxxxx"  // From Firebase Console
}
```

### 3. Enable Authentication Methods

Firebase Console → **Authentication** → **Sign-in method**:
- ✅ Email/Password — **Enable**

### 4. Set First Admin User

After you create your first user account in the Android app, you need to make it admin:

**Option A: Firebase Console**
1. Firebase Console → Firestore → `users/{uid}` collection
2. Find the user doc, click it
3. Add a new field `isAdmin: true`

**Option B: Cloud Function (recommended)**
```bash
# Install Firebase CLI first
npm install -g firebase-tools
firebase login
firebase use fire-champ-214b4

# Deploy the setAdminClaim function
firebase deploy --only functions:setAdminClaim

# Call it with your UID
firebase functions:call setAdminClaim --data '{"uid":"YOUR_USER_UID"}'
```

For now, manually set the admin claim using this temporary script (we'll add it to Cloud Functions later):

```bash
# Run in functions/ folder
node -e "
const admin = require('firebase-admin');
const serviceAccount = require('./serviceAccountKey.json');
admin.initializeApp({ credential: admin.credential.cert(serviceAccount) });
admin.auth().setCustomUserClaims('YOUR_UID_HERE', { admin: true })
  .then(() => console.log('Admin claim set!'))
  .catch(console.error);
"
```

### 5. Razorpay Setup

1. Sign up at [razorpay.com](https://razorpay.com) → Dashboard
2. **Settings** → **API Keys** → **Generate Test Key** (or Live for production)
3. Copy Key ID and Key Secret
4. Update `src/firebase/razorpay.js`:
   ```js
   TEST_KEY_ID: 'rzp_test_xxxxxxxxxxxxx',  // Your test key
   ```
5. **Important**: Set the same key in Cloud Functions:
   ```bash
   firebase functions:config:set razorpay.key_id="rzp_test_xxxxx"
   firebase functions:config:set razorpay.key_secret="your_key_secret"
   ```

### 6. Install & Run Locally

```bash
cd admin-panel
npm install
npm run dev
```

Open http://localhost:5173 — login with your admin email/password.

### 7. Build for Production

```bash
npm run build
firebase deploy --only hosting
```

Your admin panel will be live at: `https://fire-champ-214b4.web.app`

---

## 📋 Admin Panel Features

| Page | What it does |
|---|---|
| **Dashboard** | Stats overview - total users, ongoing/upcoming tournaments, pending withdrawals |
| **Tournaments** | View all tournaments, filter by status, edit, set room ID |
| **Create Tournament** | Form to create new tournaments with banner upload |
| **Results** | View pending result submissions, approve/reject with screenshots |
| **Withdrawals** | View withdrawal requests, approve (manual UPI) or reject (auto-refund) |
| **Users** | View all users, search, ban/unban |
| **Announcements** | Post announcements to all users (visible in app) |

---

## 🔐 Security Model

- **Admin login required** — only users with `admin: true` custom claim
- **Firestore rules** (in `firestore.rules`):
  - Public read: tournaments, banners, announcements, results
  - User-private: own wallet, transactions, withdrawal requests
  - Admin-only: tournaments (write), announcements, results (verify)
  - **Wallet balance fields** are locked from client write (only Cloud Functions can update)

---

## 🛠️ Tech Stack

- **React 18** + **Vite** — fast dev server
- **Tailwind CSS** — utility-first styling
- **React Router 6** — routing
- **Firebase SDK 10** — auth, firestore, functions
- **Firebase Hosting** — production deploy

---

## 📞 Support

For issues: Check `firebase-debug.log` after `firebase deploy` or browser console for runtime errors.
