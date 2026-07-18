package com.firechamp.tournament.data.remote

import com.firechamp.tournament.data.model.Announcement
import com.firechamp.tournament.data.model.Banner
import com.firechamp.tournament.data.model.BannerType
import com.firechamp.tournament.data.model.GameMode
import com.firechamp.tournament.data.model.Tournament
import com.firechamp.tournament.data.model.TournamentMap
import com.firechamp.tournament.data.model.TournamentMode
import com.firechamp.tournament.data.model.TournamentStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firestore data source - real Firebase backend se data fetch karta hai.
 *
 * Real flow:
 *  - observeBanners(): Real-time listener se banners fetch
 *  - observeTournaments(): Query with filters
 *  - observeGameModes(): Earn screen grid
 *  - observeAnnouncements(): Account > Announcements list
 */
@Singleton
class FirebaseDataSource @Inject constructor() {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // ============ BANNERS ============

    fun observeBanners(): Flow<List<Banner>> = callbackFlow {
        val listener = firestore.collection("banners")
            .orderBy("order", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val banners = snapshot?.documents?.mapNotNull { doc ->
                    runCatching {
                        Banner(
                            id = doc.id,
                            type = runCatching { BannerType.valueOf(doc.getString("type") ?: "HOW_TO_ADD_COINS") }
                                .getOrDefault(BannerType.HOW_TO_ADD_COINS),
                            title = doc.getString("title") ?: "",
                            subtitle = doc.getString("subtitle"),
                            amount = doc.getString("amount"),
                            imageUrl = doc.getString("imageUrl")
                        )
                    }.getOrNull()
                } ?: emptyList()
                trySend(banners)
            }
        awaitClose { listener.remove() }
    }

    // ============ GAME MODES ============

    fun observeGameModes(): Flow<List<GameMode>> = callbackFlow {
        val listener = firestore.collection("gameModes")
            .orderBy("order", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val modes = snapshot?.documents?.mapNotNull { doc ->
                    runCatching {
                        GameMode(
                            id = doc.id,
                            name = doc.getString("name") ?: doc.id,
                            label = doc.getString("label") ?: "",
                            imageUrl = doc.getString("icon") ?: doc.getString("imageUrl")
                        )
                    }.getOrNull()
                } ?: emptyList()
                trySend(modes)
            }
        awaitClose { listener.remove() }
    }

    // ============ TOURNAMENTS ============

    fun observeTournaments(gameModeId: String?): Flow<List<Tournament>> = callbackFlow {
        val query = if (gameModeId == null) {
            firestore.collection("tournaments")
        } else {
            firestore.collection("tournaments").whereEqualTo("gameModeId", gameModeId)
        }
        val listener = query
            .orderBy("dateTime", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    runCatching { parseTournament(doc.id, doc.data ?: emptyMap()) }.getOrNull()
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun getTournament(tournamentId: String): Tournament? {
        val doc = firestore.collection("tournaments").document(tournamentId).get().await()
        return doc.data?.let { parseTournament(doc.id, it) }
    }

    suspend fun joinTournament(tournamentId: String): Boolean {
        // Real: val result = functions.getHttpsCallable("joinTournament").call(...)
        return true
    }

    // ============ ANNOUNCEMENTS ============
    // NOTE: No compound query (whereEqualTo + orderBy) — that needs composite index.
    // We just fetch all + sort client-side. With small data, this is fine.
    // To enable server-side sorting later, deploy composite index in firestore.indexes.json.

    fun observeAnnouncements(): Flow<List<Announcement>> = callbackFlow {
        val listener = firestore.collection("announcements")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e("Firestore", "Announcements listener error", error)
                    close(error)
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    runCatching {
                        Announcement(
                            id = doc.id,
                            title = doc.getString("title") ?: "",
                            message = doc.getString("message") ?: "",
                            imageUrl = doc.getString("imageUrl"),
                            createdAt = doc.getTimestamp("createdAt")?.toDate()?.time
                                ?: doc.getLong("createdAt") ?: System.currentTimeMillis(),
                            active = doc.getBoolean("active") ?: true
                        )
                    }.getOrNull()
                }?.filter { it.active }
                    ?.sortedByDescending { it.createdAt }
                ?: emptyList()
                android.util.Log.d("Firestore", "Announcements received: ${list.size} items")
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    // ============ HELPERS ============

    private fun parseTournament(id: String, data: Map<String, Any?>): Tournament {
        return Tournament(
            id = id,
            gameModeId = data["gameModeId"] as? String ?: "",
            title = data["title"] as? String ?: "",
            matchId = data["matchId"] as? String ?: "",
            mode = runCatching { TournamentMode.valueOf(data["mode"] as? String ?: "SOLO") }
                .getOrDefault(TournamentMode.SOLO),
            map = runCatching { TournamentMap.valueOf(data["map"] as? String ?: "BERMUDA") }
                .getOrDefault(TournamentMap.BERMUDA),
            bannerUrl = data["bannerUrl"] as? String,
            dateTime = data["dateTime"] as? String ?: "",
            prizePool = (data["prizePool"] as? Long)?.toInt() ?: 0,
            perKill = (data["perKill"] as? Long)?.toInt() ?: 0,
            entryFee = (data["entryFee"] as? Long)?.toInt() ?: 0,
            slotsFilled = (data["slotsFilled"] as? Long)?.toInt() ?: 0,
            totalSlots = (data["totalSlots"] as? Long)?.toInt() ?: 48,
            status = runCatching { TournamentStatus.valueOf(data["status"] as? String ?: "UPCOMING") }
                .getOrDefault(TournamentStatus.UPCOMING)
        )
    }
}
