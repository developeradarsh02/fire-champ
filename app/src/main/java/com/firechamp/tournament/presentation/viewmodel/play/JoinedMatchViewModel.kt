package com.firechamp.tournament.presentation.viewmodel.play

import androidx.lifecycle.ViewModel
import com.firechamp.tournament.data.model.JoinedMatch
import com.firechamp.tournament.data.model.RoomUnlockStatus
import com.firechamp.tournament.data.model.SubmissionStatus
import com.firechamp.tournament.data.repository.TournamentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import javax.inject.Inject

/**
 * UI state for JoinedMatch screen.
 */
data class JoinedMatchUiState(
    val match: JoinedMatch? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    // Room unlock countdown
    val secondsUntilUnlock: Long = 0,

    // Result submission
    val resultDialogOpen: Boolean = false,
    val killsInput: String = "",
    val rankInput: String = "",
    val screenshotUri: String? = null,
    val resultError: String? = null,
    val submissionSuccess: Boolean = false
)

@HiltViewModel
class JoinedMatchViewModel @Inject constructor(
    private val tournamentRepository: TournamentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(JoinedMatchUiState())
    val uiState: StateFlow<JoinedMatchUiState> = _uiState.asStateFlow()

    /**
     * Mock join - real me Firebase se aayega (Task 14 me).
     * Yahan pe ek sample joined match create karta hai with room details.
     */
    fun loadJoinedMatch(tournamentId: String) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val tournament = tournamentRepository.getTournamentById(tournamentId)
            if (tournament == null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Match not found") }
                return@launch
            }

            // Mock join - create joined match
            // Match start: 12 min from now (so unlock in 2 min - we can see UNLOCKED state quickly)
            val now = System.currentTimeMillis()
            val matchStartTime = now + 12 * 60 * 1000  // 12 minutes from now
            val roomUnlockTime = now + 2 * 60 * 1000   // 2 minutes from now (10 min before start)

            val joinedMatch = JoinedMatch(
                matchId = tournament.id,
                tournamentTitle = tournament.title,
                matchNumber = "Match #${tournament.matchId}",
                matchStartTime = matchStartTime,
                roomUnlockTime = roomUnlockTime,
                // Mock room ID (real me admin panel se aayega)
                roomId = "ABC${tournament.matchId}XYZ".take(8),
                roomPassword = tournament.matchId.takeLast(4)
            )

            _uiState.update {
                it.copy(
                    match = joinedMatch,
                    isLoading = false,
                    secondsUntilUnlock = ((roomUnlockTime - now) / 1000).coerceAtLeast(0)
                )
            }
            startCountdown()
        }
    }

    private fun startCountdown() {
        viewModelScope.launch {
            while (true) {
                delay(1000)
                val match = _uiState.value.match ?: return@launch
                val now = System.currentTimeMillis()
                val seconds = ((match.roomUnlockTime - now) / 1000).coerceAtLeast(0)
                _uiState.update { it.copy(secondsUntilUnlock = seconds) }
                if (seconds <= 0) return@launch
            }
        }
    }

    // ============== RESULT SUBMISSION ==============

    fun onResultDialogOpen() {
        _uiState.update { it.copy(resultDialogOpen = true, resultError = null) }
    }

    fun onResultDialogClose() {
        _uiState.update {
            it.copy(
                resultDialogOpen = false,
                killsInput = "",
                rankInput = "",
                screenshotUri = null,
                resultError = null
            )
        }
    }

    fun onKillsChange(v: String) {
        val filtered = v.filter { it.isDigit() }.take(3)
        _uiState.update { it.copy(killsInput = filtered, resultError = null) }
    }

    fun onRankChange(v: String) {
        val filtered = v.filter { it.isDigit() }.take(3)
        _uiState.update { it.copy(rankInput = filtered, resultError = null) }
    }

    fun onScreenshotSelected(uri: String?) {
        _uiState.update { it.copy(screenshotUri = uri) }
    }

    fun submitResult() {
        val state = _uiState.value
        val match = state.match ?: return
        val kills = state.killsInput.toIntOrNull()
        val rank = state.rankInput.toIntOrNull()

        if (kills == null) {
            _uiState.update { it.copy(resultError = "Enter kills count") }
            return
        }
        if (rank == null) {
            _uiState.update { it.copy(resultError = "Enter your rank/position") }
            return
        }
        if (state.screenshotUri == null) {
            _uiState.update { it.copy(resultError = "Upload screenshot (mandatory for verification)") }
            return
        }

        // Mock submit - real me Cloud Function se admin review ke liye jayega
        _uiState.update {
            it.copy(
                match = match.copy(
                    submissionStatus = SubmissionStatus.PENDING,
                    submittedKills = kills,
                    submittedRank = rank,
                    screenshotUri = state.screenshotUri
                ),
                resultDialogOpen = false,
                submissionSuccess = true
            )
        }
    }
}