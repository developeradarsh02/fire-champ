package com.firechamp.tournament.data.repository

import com.firechamp.tournament.data.model.notification.Notification
import com.firechamp.tournament.data.model.notification.NotificationType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Notification Repository - in-memory + Firestore persistence.
 *
 * FCM token aur received notifications manage karta hai.
 *  - fcmToken: in-memory + Firestore users/{uid}.fcmToken
 *  - notifications: in-memory list (jo FcmService se aati hain)
 *
 * Cloud Functions (FCM triggers) users/{uid}.fcmToken read karke push bhejte hain.
 */
@Singleton
class NotificationRepository @Inject constructor() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    private val _fcmToken = MutableStateFlow<String?>(null)
    val fcmToken: StateFlow<String?> = _fcmToken.asStateFlow()

    init {
        // Mock initial notifications (real me FcmService se aayengi)
        _notifications.value = listOf(
            Notification(
                id = "n1",
                type = NotificationType.MATCH_REMINDER,
                title = "Match starts in 10 minutes",
                body = "SOLO – Match #32345 is starting soon. Get ready!",
                timestamp = System.currentTimeMillis() - 5 * 60 * 1000
            ),
            Notification(
                id = "n2",
                type = NotificationType.ROOM_ID_RELEASED,
                title = "Room ID Available",
                body = "Room ID for Match #32344 is now available. Check Joined Matches.",
                timestamp = System.currentTimeMillis() - 30 * 60 * 1000
            ),
            Notification(
                id = "n3",
                type = NotificationType.RESULT_DECLARED,
                title = "Result Verified ✅",
                body = "Your result for Match #32300 has been verified. ₹50 credited to wallet.",
                timestamp = System.currentTimeMillis() - 2 * 60 * 60 * 1000
            )
        )
    }

    fun addNotification(notification: Notification) {
        _notifications.update { listOf(notification) + it }
    }

    fun markAsRead(notificationId: String) {
        _notifications.update { list ->
            list.map { if (it.id == notificationId) it.copy(isRead = true) else it }
        }
    }

    fun markAllAsRead() {
        _notifications.update { list -> list.map { it.copy(isRead = true) } }
    }

    fun getUnreadCount(): Int = _notifications.value.count { !it.isRead }

    /**
     * FCM token save - in-memory + Firestore me users/{uid}.fcmToken field update.
     * Called by FcmService.onNewToken().
     * Agar user logged in nahi hai to sirf in-memory me save hoga (next login pe sync).
     */
    fun setFcmToken(token: String) {
        _fcmToken.value = token
        // Best-effort: Firestore me save if user logged in
        val uid = auth.currentUser?.uid
        if (uid != null) {
            try {
                firestore.collection("users").document(uid)
                    .update("fcmToken", token)
            } catch (e: Exception) {
                // Silently fail - next login pe sync hoga
                android.util.Log.w("FCM", "Failed to save token: ${e.message}")
            }
        }
    }

    /**
     * Force sync FCM token to Firestore for current user.
     * Call this from MainActivity onCreate after login.
     */
    suspend fun syncFcmTokenToFirestore(token: String) {
        val uid = auth.currentUser?.uid ?: return
        try {
            firestore.collection("users").document(uid)
                .update("fcmToken", token)
                .await()
        } catch (e: Exception) {
            android.util.Log.e("FCM", "syncFcmTokenToFirestore failed: ${e.message}")
        }
    }
}
