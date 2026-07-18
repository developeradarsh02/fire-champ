package com.firechamp.tournament.data.model

/**
 * Tournament status - list screen me tabs ke liye use hota hai.
 * ONGOING: live/match chal raha hai
 * UPCOMING: future match, abhi join ho sakta hai
 * RESULTS: match complete, results declared
 */
enum class TournamentStatus { ONGOING, UPCOMING, RESULTS }

/**
 * Mode tag - Solo/Duo/Squad etc. (Tournament card ke red pill me dikhta hai)
 */
enum class TournamentMode(val displayName: String) {
    SOLO("Solo"),
    DUO("Duo"),
    SQUAD("Squad")
}

/**
 * Map tag - BERMUDA, PURGATORY, KALAHARI etc. (orange pill me dikhta hai)
 */
enum class TournamentMap(val displayName: String) {
    BERMUDA("BERMUDA"),
    PURGATORY("PURGATORY"),
    KALAHARI("KALAHARI"),
    ALPINE("ALPINE"),
    SOLO("SOLO")
}

/**
 * Tournament data model.
 *
 * Real app me ye Firebase Firestore se aayega (Task 14 me).
 * Abhi hardcoded dummy data use kar rahe hain.
 *
 * Fields (PDF spec ke according):
 *  - id, title, matchId, mode, map
 *  - bannerUrl (placeholder abhi, real image later)
 *  - dateTime, prizePool, perKill, entryFee
 *  - slotsFilled, totalSlots
 *  - status (ONGOING/UPCOMING/RESULTS)
 *  - winners: List<Winner> (for RESULTS tab)
 */
data class Tournament(
    val id: String,
    val gameModeId: String,            // "FULL MAP", "CS 1V1" etc. - filtering ke liye
    val title: String,
    val matchId: String,
    val mode: TournamentMode,
    val map: TournamentMap,
    val bannerUrl: String? = null,
    val dateTime: String,              // "13/07/2026 08:30 pm"
    val prizePool: Int,                // 500
    val perKill: Int,                  // 7
    val entryFee: Int,                 // 6
    val slotsFilled: Int,              // 3
    val totalSlots: Int,               // 48
    val status: TournamentStatus,
    val winners: List<Winner> = emptyList(),       // Top 3 winners (Winner section ke liye)
    val results: List<PlayerResult> = emptyList()  // Full results list (Match Result table ke liye)
)

/**
 * Winner data - Tournament results me dikhta hai.
 * Winner section me top 1-3 winners dikhte hain.
 */
data class Winner(
    val rank: Int,
    val playerName: String,
    val kills: Int,
    val winningAmount: Int
)

/**
 * Player result data - Match Result table me har player ke liye.
 * Task 4 me full match result screen me use hoga.
 *
 * isFlagged: agar true hai to warning icon dikhega (reported/cheating player)
 */
data class PlayerResult(
    val rank: Int,
    val playerName: String,
    val kills: Int,
    val winningAmount: Int,
    val isFlagged: Boolean = false
)
