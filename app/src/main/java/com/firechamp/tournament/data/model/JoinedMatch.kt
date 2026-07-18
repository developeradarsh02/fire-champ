package com.firechamp.tournament.data.model

/**
 * Room ID/Password unlock status (Task 9).
 *
 *  - LOCKED: match abhi start nahi hua, room details nahi milenge
 *  - UNLOCKING_SOON: 10-15 min pehle (countdown chalu)
 *  - UNLOCKED: match se 10 min pehle, room ID reveal hua
 *  - STARTED: match chal raha hai
 *  - ENDED: match khatam, result submit karna hai
 */
enum class RoomUnlockStatus {
    LOCKED,
    UNLOCKING_SOON,
    UNLOCKED,
    STARTED,
    ENDED
}

/**
 * Result submission status.
 */
enum class SubmissionStatus {
    NOT_SUBMITTED,    // Match ended but result pending
    PENDING,          // User ne submit kiya, admin review kar raha
    VERIFIED,         // Admin ne verify kiya, winning credit ho gayi
    REJECTED          // Admin ne reject kiya (reason dekhna)
}

/**
 * JoinedMatch - jab user tournament join karta hai to ye state create hota hai.
 *
 * Real app me ye Firestore me save hoga (Task 14 me).
 * Abhi mock data use kar raha hu.
 */
data class JoinedMatch(
    val matchId: String,                  // Tournament ID
    val tournamentTitle: String,
    val matchNumber: String,              // e.g. "Match #32345"
    val roomId: String? = null,           // null until unlocked
    val roomPassword: String? = null,
    val matchStartTime: Long,             // epoch millis
    val roomUnlockTime: Long,             // start - 10 min
    val joinedAt: Long = System.currentTimeMillis(),

    // Result submission
    val submissionStatus: SubmissionStatus = SubmissionStatus.NOT_SUBMITTED,
    val submittedKills: Int = 0,
    val submittedRank: Int = 0,
    val screenshotUri: String? = null,
    val rejectionReason: String? = null
) {
    fun currentStatus(): RoomUnlockStatus {
        val now = System.currentTimeMillis()
        return when {
            now < roomUnlockTime -> {
                val minsUntilUnlock = (roomUnlockTime - now) / 60000
                if (minsUntilUnlock <= 15) RoomUnlockStatus.UNLOCKING_SOON
                else RoomUnlockStatus.LOCKED
            }
            now < matchStartTime -> RoomUnlockStatus.UNLOCKED
            now < matchStartTime + 30 * 60 * 1000 -> RoomUnlockStatus.STARTED  // 30 min match
            else -> RoomUnlockStatus.ENDED
        }
    }
}