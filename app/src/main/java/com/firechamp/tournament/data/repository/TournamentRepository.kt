package com.firechamp.tournament.data.repository

import com.firechamp.tournament.data.model.Tournament
import com.firechamp.tournament.data.model.TournamentMap
import com.firechamp.tournament.data.model.TournamentMode
import com.firechamp.tournament.data.model.TournamentStatus
import com.firechamp.tournament.data.model.PlayerResult
import com.firechamp.tournament.data.model.Winner
import com.firechamp.tournament.data.remote.FirebaseDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Tournament Repository - abhi MOCK/DUMMY data.
 *
 * Firestore se real-time tournaments fetch karta hai (Task 14 complete).
 * Admin panel se create kiya hua data YAHAN se aata hai.
 */
@Singleton
class TournamentRepository @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource
) {

    /**
     * Sab tournaments as Flow (Firestore real-time listener).
     * UI layer collect karega aur status filter karega.
     */
    fun observeAllTournaments(): Flow<List<Tournament>> =
        firebaseDataSource.observeTournaments(null)

    /**
     * Specific game mode ke tournaments as Flow.
     */
    fun observeTournamentsByGameMode(gameModeId: String): Flow<List<Tournament>> =
        firebaseDataSource.observeTournaments(gameModeId)

    /**
     * Legacy sync method - returns first snapshot.
     * New code should use observeAllTournaments() instead.
     */
    suspend fun getAllTournaments(): List<Tournament> =
        firebaseDataSource.observeTournaments(null).first()

    /**
     * Legacy sync method - returns first snapshot.
     * New code should use observeTournamentsByGameMode() instead.
     */
    suspend fun getTournamentsByGameMode(gameModeId: String): List<Tournament> =
        firebaseDataSource.observeTournaments(gameModeId).first()

    /**
     * Specific tournament ka detail return karta hai.
     */
    suspend fun getTournamentById(id: String): Tournament? =
        firebaseDataSource.getTournament(id)

    /**
     * Mock join - simulate karta hai user ne tournament join kar liya.
     * Real me slotsFilled Firestore transaction se increment hoga.
     */
    fun joinTournament(tournamentId: String): Boolean {
        // Mock: always success
        return true
    }

    companion object {
        // Sample results list - 48 players, sorted by rank
        // (PDF ke screenshot jaisa 11 se 28 tak dikhega)
        // Note: must be declared BEFORE sampleTournaments because tournaments reference it
        private val sampleMatchResults: List<PlayerResult> = listOf(
            PlayerResult(1, "EroxNvrDie", 6, 30),
            PlayerResult(2, "X7ERROR", 5, 25),
            PlayerResult(3, "MR OBITO", 4, 20),
            PlayerResult(4, "Adarsh99x", 4, 20),
            PlayerResult(5, "FreeFireKing", 4, 20),
            PlayerResult(6, "Phoenix07", 3, 15),
            PlayerResult(7, "WarriorX", 3, 15),
            PlayerResult(8, "SilentShot", 3, 15),
            PlayerResult(9, "GGxHunter", 2, 10),
            PlayerResult(10, "ProPlayer", 2, 10),
            PlayerResult(11, "Bull776", 1, 5),
            PlayerResult(12, "PRIMETM ALONE", 1, 5),
            PlayerResult(13, "UK_Chauhan0", 0, 0),
            PlayerResult(14, "KRISHNA EXE", 0, 0),
            PlayerResult(15, "TOXIC", 0, 0, isFlagged = true),
            PlayerResult(16, "MANAS", 0, 0),
            PlayerResult(17, "BLACKDEMON", 0, 0),
            PlayerResult(18, "FG.FLASH", 0, 0),
            PlayerResult(19, "sam ruddhi", 0, 0),
            PlayerResult(20, "OP ASHISH", 0, 0),
            PlayerResult(21, "EXOTeam2028P", 0, 0),
            PlayerResult(22, "ITZ DADDY", 0, 0),
            PlayerResult(23, "Exc.panda", 0, 0),
            PlayerResult(24, "NDV_COX_7", 0, 0),
            PlayerResult(25, "marcoose 7", 0, 0),
            PlayerResult(26, "FFXANiJATIN", 0, 0),
            PlayerResult(27, "Itachi", 0, 0),
            PlayerResult(28, "johan", 0, 0)
        )

        private val sampleTournaments: List<Tournament> = listOf(
            // ========== FULL MAP - Solo ==========
            Tournament(
                id = "t1",
                gameModeId = "FULL MAP",
                title = "SOLO – Per Kill + Top Prize | Ryden BAN – – Match #32345",
                matchId = "32345",
                mode = TournamentMode.SOLO,
                map = TournamentMap.BERMUDA,
                dateTime = "13/07/2026 08:30 pm",
                prizePool = 500,
                perKill = 7,
                entryFee = 6,
                slotsFilled = 3,
                totalSlots = 48,
                status = TournamentStatus.UPCOMING
            ),
            Tournament(
                id = "t2",
                gameModeId = "FULL MAP",
                title = "SOLO – Per Kill + Top Prize | Ryden BAN – – Match #32344",
                matchId = "32344",
                mode = TournamentMode.SOLO,
                map = TournamentMap.BERMUDA,
                dateTime = "13/07/2026 07:30 pm",
                prizePool = 300,
                perKill = 5,
                entryFee = 6,
                slotsFilled = 48,
                totalSlots = 48,
                status = TournamentStatus.ONGOING,
                winners = listOf(
                    Winner(1, "EroxNvrDie", 6, 30),
                    Winner(2, "X7ERROR", 5, 25),
                    Winner(3, "MR OBITO", 4, 20)
                ),
                results = sampleMatchResults
            ),
            Tournament(
                id = "t3",
                gameModeId = "FULL MAP",
                title = "DUO – Per Kill + Top Prize | Ryden BAN – – Match #32359",
                matchId = "32359",
                mode = TournamentMode.DUO,
                map = TournamentMap.BERMUDA,
                dateTime = "14/07/2026 09:00 pm",
                prizePool = 800,
                perKill = 10,
                entryFee = 12,
                slotsFilled = 12,
                totalSlots = 24,
                status = TournamentStatus.UPCOMING
            ),

            // ========== CS 1V1 ==========
            Tournament(
                id = "t4",
                gameModeId = "CS 1V1",
                title = "CLASH SQUAD 1V1 – Per Kill | Match #11001",
                matchId = "11001",
                mode = TournamentMode.SOLO,
                map = TournamentMap.SOLO,
                dateTime = "13/07/2026 06:00 pm",
                prizePool = 200,
                perKill = 8,
                entryFee = 5,
                slotsFilled = 8,
                totalSlots = 16,
                status = TournamentStatus.UPCOMING
            ),
            Tournament(
                id = "t5",
                gameModeId = "CS 1V1",
                title = "CLASH SQUAD 1V1 – Per Kill | Match #11002",
                matchId = "11002",
                mode = TournamentMode.SOLO,
                map = TournamentMap.SOLO,
                dateTime = "13/07/2026 04:00 pm",
                prizePool = 150,
                perKill = 6,
                entryFee = 5,
                slotsFilled = 16,
                totalSlots = 16,
                status = TournamentStatus.ONGOING
            ),

            // ========== LW 1V1 ==========
            Tournament(
                id = "t6",
                gameModeId = "LW 1V1",
                title = "LONE WOLF 1V1 – Per Kill + Top Prize | Match #22001",
                matchId = "22001",
                mode = TournamentMode.SOLO,
                map = TournamentMap.SOLO,
                dateTime = "14/07/2026 07:00 pm",
                prizePool = 250,
                perKill = 9,
                entryFee = 5,
                slotsFilled = 5,
                totalSlots = 16,
                status = TournamentStatus.UPCOMING
            ),

            // ========== BR SURVIVAL ==========
            Tournament(
                id = "t7",
                gameModeId = "BR SURVIVAL",
                title = "BATTLE ROYALE SURVIVAL – Top Prize | Match #33001",
                matchId = "33001",
                mode = TournamentMode.SQUAD,
                map = TournamentMap.PURGATORY,
                dateTime = "14/07/2026 10:00 pm",
                prizePool = 1500,
                perKill = 15,
                entryFee = 20,
                slotsFilled = 25,
                totalSlots = 50,
                status = TournamentStatus.UPCOMING
            ),

            // ========== CS 2V2 ==========
            Tournament(
                id = "t8",
                gameModeId = "CS 2V2",
                title = "CLASH SQUAD 2V2 – Per Kill | Match #44001",
                matchId = "44001",
                mode = TournamentMode.DUO,
                map = TournamentMap.SOLO,
                dateTime = "15/07/2026 05:00 pm",
                prizePool = 400,
                perKill = 10,
                entryFee = 10,
                slotsFilled = 6,
                totalSlots = 24,
                status = TournamentStatus.UPCOMING
            ),

            // ========== LW 2V2 ==========
            Tournament(
                id = "t9",
                gameModeId = "LW 2V2",
                title = "LONE WOLF 2V2 – Per Kill + Top Prize | Match #55001",
                matchId = "55001",
                mode = TournamentMode.DUO,
                map = TournamentMap.SOLO,
                dateTime = "15/07/2026 08:00 pm",
                prizePool = 600,
                perKill = 12,
                entryFee = 15,
                slotsFilled = 4,
                totalSlots = 20,
                status = TournamentStatus.UPCOMING
            ),

            // ========== Results sample (paar completed tournaments) ==========
            Tournament(
                id = "t10",
                gameModeId = "FULL MAP",
                title = "SOLO – Per Kill + Top Prize | Match #32300",
                matchId = "32300",
                mode = TournamentMode.SOLO,
                map = TournamentMap.BERMUDA,
                dateTime = "12/07/2026 08:30 pm",
                prizePool = 500,
                perKill = 7,
                entryFee = 6,
                slotsFilled = 48,
                totalSlots = 48,
                status = TournamentStatus.RESULTS,
                winners = listOf(
                    Winner(1, "PROxGamer", 8, 50),
                    Winner(2, "NinjaBoy99", 6, 30),
                    Winner(3, "ShadowHunt", 5, 20)
                ),
                results = sampleMatchResults
            )
        )
    }
}
