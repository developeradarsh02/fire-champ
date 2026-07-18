package com.firechamp.tournament.presentation.viewmodel.play

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firechamp.tournament.data.model.Tournament
import com.firechamp.tournament.data.repository.TournamentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Match Result screen ka UI state.
 */
data class MatchResultUiState(
    val tournament: Tournament? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * MatchResultViewModel - specific tournament ka detail + results load karta hai.
 * Firestore se real-time data.
 */
@HiltViewModel
class MatchResultViewModel @Inject constructor(
    private val tournamentRepository: TournamentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MatchResultUiState())
    val uiState: StateFlow<MatchResultUiState> = _uiState.asStateFlow()

    /**
     * Tournament load karta hai by ID.
     */
    fun loadTournament(tournamentId: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val tournament = tournamentRepository.getTournamentById(tournamentId)
            if (tournament == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Tournament not found"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        tournament = tournament,
                        isLoading = false
                    )
                }
            }
        }
    }
}
