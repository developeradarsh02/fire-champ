package com.firechamp.tournament.data.repository

import com.firechamp.tournament.data.local.SessionManager
import com.firechamp.tournament.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Authentication Repository - REAL Firebase Auth (Spark plan compatible).
 *
 * Flow:
 *  - login(email, password) → FirebaseAuth.signInWithEmailAndPassword
 *  - signup(...) → createUserWithEmailAndPassword + Firestore user doc create
 *  - logout() → FirebaseAuth.signOut() + DataStore clear
 *  - currentUser → FirebaseAuth + Firestore data combined
 *
 * Note: Cloud Functions ke bina bhi ye sab kaam karega kyunki
 * Firebase Auth + Firestore SDK calls direct hain, server compute nahi chahiye.
 */
@Singleton
class AuthRepository @Inject constructor(
    private val sessionManager: SessionManager
) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Coroutine scope for background work (init block, session restore etc.)
    // Independent of ViewModel scope to survive configuration changes
    private val repoScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    init {
        // Check if user already signed in (persisted Firebase Auth session)
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            _isLoggedIn.value = true
            // Load full profile from Firestore (background, best-effort)
            repoScope.launch {
                try {
                    loadAndSetCurrentUser(firebaseUser)
                } catch (e: Exception) {
                    android.util.Log.w("Auth", "Session restore failed: ${e.message}")
                }
            }
        }
    }

    /**
     * Login with email + password.
     * Username bhi accept karta hai — internally email me convert karta hai
     * by appending @firechamp.app agar '@' nahi hai.
     */
    suspend fun login(usernameOrEmail: String, password: String): Result<User> {
        val email = normalizeEmail(usernameOrEmail)
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Email and password required"))
        }
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: throw Exception("Login failed")
            val user = loadAndSetCurrentUser(firebaseUser)
            sessionManager.saveSession(user.id, user.username, user.email)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(mapAuthError(e))
        }
    }

    /**
     * Signup - new Firebase Auth user + Firestore user doc.
     */
    suspend fun signup(
        firstName: String,
        lastName: String,
        username: String,
        email: String,
        mobile: String,
        password: String,
        referralCode: String?
    ): Result<User> {
        if (email.isBlank() || password.isBlank() || username.isBlank()) {
            return Result.failure(Exception("All fields required"))
        }
        return try {
            // 1. Create Firebase Auth user
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: throw Exception("Signup failed")

            // 2. Update display name
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName("$firstName $lastName")
                .build()
            firebaseUser.updateProfile(profileUpdates).await()

            // 3. Create Firestore user doc
            val newUser = User(
                id = firebaseUser.uid,
                firstName = firstName,
                lastName = lastName,
                username = username,
                email = email,
                mobile = mobile,
                countryCode = "+91",
                referralCode = referralCode ?: "",
                isVerified = false,
                walletBalance = 0.0,
                depositedBalance = 0.0,
                winningBalance = 0.0,
                earnings = 0.0,
                payouts = 0.0,
                matchesPlayed = 0,
                totalKilled = 0,
                coinsWon = 0.0,
                createdAt = System.currentTimeMillis()
            )
            firestore.collection("users").document(firebaseUser.uid)
                .set(newUser, SetOptions.merge())
                .await()

            // 4. If referral code given, link referrer
            if (!referralCode.isNullOrBlank()) {
                try {
                    firestore.collection("users").document(firebaseUser.uid)
                        .update("referredBy", referralCode.trim())
                        .await()
                } catch (_: Exception) { /* non-fatal */ }
            }

            // 4.5. FIRST USER = AUTO ADMIN
            // Check if this is the first user in the system. If yes, mark as admin
            // (booster for owner / first deployer). After that, admin must be set
            // manually via Firestore console.
            try {
                val usersSnap = firestore.collection("users").limit(2).get().await()
                val isFirstUser = usersSnap.size() == 1  // only this user exists
                if (isFirstUser) {
                    firestore.collection("users").document(firebaseUser.uid)
                        .update("isAdmin", true)
                        .await()
                }
            } catch (_: Exception) { /* non-fatal */ }

            // 5. Save FCM token if already available (post-signup token sync)
            try {
                val token = com.google.firebase.messaging.FirebaseMessaging.getInstance().token.await()
                firestore.collection("users").document(firebaseUser.uid)
                    .update("fcmToken", token)
                    .await()
            } catch (_: Exception) { /* non-fatal, FcmService will sync on token refresh */ }

            // 6. Set state
            _currentUser.value = newUser
            _isLoggedIn.value = true
            sessionManager.saveSession(newUser.id, newUser.username, newUser.email)
            Result.success(newUser)
        } catch (e: Exception) {
            Result.failure(mapAuthError(e))
        }
    }

    /**
     * Logout - Firebase Auth + DataStore + in-memory state clear.
     */
    fun logout() {
        auth.signOut()
        _currentUser.value = null
        _isLoggedIn.value = false
    }

    suspend fun clearStoredSession() {
        sessionManager.clearSession()
    }

    // ============ Helper functions ============

    /**
     * Load user data from Firestore and update _currentUser state.
     * Falls back to basic FirebaseUser data if Firestore doc doesn't exist.
     */
    private suspend fun loadAndSetCurrentUser(firebaseUser: FirebaseUser): User {
        val doc = firestore.collection("users").document(firebaseUser.uid).get().await()
        val user = if (doc.exists()) {
            doc.toObject(User::class.java)?.copy(id = firebaseUser.uid) ?: userFromFirebase(firebaseUser)
        } else {
            // Firestore doc missing - create it now (first login after signup)
            val basicUser = userFromFirebase(firebaseUser)
            firestore.collection("users").document(firebaseUser.uid)
                .set(basicUser, SetOptions.merge())
                .await()
            basicUser
        }
        _currentUser.value = user
        _isLoggedIn.value = true
        return user
    }

    private fun userFromFirebase(firebaseUser: FirebaseUser): User {
        val name = firebaseUser.displayName?.split(" ") ?: emptyList()
        return User(
            id = firebaseUser.uid,
            firstName = name.firstOrNull() ?: "",
            lastName = name.drop(1).joinToString(" "),
            username = firebaseUser.email?.substringBefore("@") ?: "",
            email = firebaseUser.email ?: "",
            mobile = firebaseUser.phoneNumber ?: "",
            isVerified = firebaseUser.isEmailVerified,
            createdAt = System.currentTimeMillis()
        )
    }

    /**
     * "username" → "username@firechamp.app"
     * "user@example.com" → "user@example.com" (unchanged)
     */
    private fun normalizeEmail(input: String): String {
        val trimmed = input.trim()
        val normalized = if (trimmed.contains("@")) trimmed else "${trimmed.lowercase()}@firechamp.app"
        android.util.Log.d("AuthRepository", "Normalizing '$trimmed' to '$normalized'")
        return normalized
    }

    private fun mapAuthError(e: Exception): Exception {
        return when (e) {
            is FirebaseAuthUserCollisionException ->
                Exception("Email already registered. Please login instead.")
            is FirebaseAuthWeakPasswordException ->
                Exception("Password too weak. Use at least 6 characters.")
            else -> {
                val msg = e.message ?: "Authentication failed"
                when {
                    msg.contains("no user record") || msg.contains("INVALID_LOGIN_CREDENTIALS") ->
                        Exception("Invalid email or password")
                    msg.contains("network") || msg.contains("UNAVAILABLE") ->
                        Exception("Network error. Check your internet connection.")
                    msg.contains("email address is badly") || msg.contains("INVALID_EMAIL") ->
                        Exception("Invalid email format")
                    msg.contains("blocked all requests") || msg.contains("TOO_MANY_ATTEMPTS") ->
                        Exception("Too many attempts. Try again later.")
                    else -> Exception(msg)
                }
            }
        }
    }
}
