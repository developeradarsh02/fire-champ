package com.firechamp.tournament.data.model.notification

/**
 * Notification types (Task 10 - Push Notifications).
 *
 * Real app me ye Firebase Cloud Messaging se aayengi.
 * Abhi mock data use kar raha hu.
 */
enum class NotificationType {
    MATCH_REMINDER,       // "Your match starts in 10 minutes"
    ROOM_ID_RELEASED,     // "Room ID for Match #32345 is now available"
    RESULT_DECLARED,      // "Your result for Match #32344 has been verified"
    WITHDRAWAL_UPDATE,    // "Your withdrawal of ₹500 is completed"
    GENERAL_ANNOUNCEMENT  // Promotional/updates from admin
}

/**
 * Single notification entry.
 */
data class Notification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val body: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val deepLink: String? = null    // Route to navigate when tapped
)