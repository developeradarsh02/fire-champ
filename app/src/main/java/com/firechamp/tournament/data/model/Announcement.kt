package com.firechamp.tournament.data.model

/**
 * Announcement - admin panel se post hota hai, user app me dikhta hai.
 *
 * Firestore: `announcements` collection
 * Schema:
 *  - title: String
 *  - message: String
 *  - imageUrl: String? (optional)
 *  - createdAt: Timestamp
 *  - active: Boolean
 */
data class Announcement(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val imageUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val active: Boolean = true
)
