package com.firechamp.tournament.presentation.viewmodel.play

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firechamp.tournament.data.model.Tournament
import com.firechamp.tournament.data.model.TournamentStatus
import com.firechamp.tournament.data.repository.TournamentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Tournament list ka UI state.
 */
data class TournamentListUiState(
    val gameModeId: String? = null,
    val gameModeName: String = "Tournaments",
    val selectedTab: TournamentStatus = TournamentStatus.UPCOMING,
    val tournaments: List<Tournament> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showJoinDialog: Boolean = false,
    val pendingJoinTournament: Tournament? = null,
    val lastJoinedTournamentId: String? = null
)

/**
 * TournamentViewModel - Firestore se live tournaments stream karta hai.
 * Admin panel me add kiya hua data real-time yahan reflect hota hai.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TournamentViewModel @Inject constructor(
    private val tournamentRepository: TournamentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TournamentListUiState())

    /**
     * Firestore se real-time tournament stream.
     * When gameModeId changes, switches to filtered stream.
     */
    private val tournamentsStream = _uiState
        .flatMapLatest { state ->
            if (state.gameModeId == null) {
                tournamentRepository.observeAllTournaments()
            } else {
                tournamentRepository.observeTournamentsByGameMode(state.gameModeId)
            }
        }

    /**
     * Combined UI state - filters by selected tab from real-time Firestore stream.
     */
    val uiState: StateFlow<TournamentListUiState> = combine(
        _uiState,
        tournamentsStream
    ) { state, allTournaments ->
        val filtered = allTournaments.filter { it.status == state.selectedTab }
        state.copy(
            tournaments = filtered,
            isLoading = false,
            errorMessage = if (allTournaments.isEmpty() && !state.gameModeId.isNullOrBlank().not()) "No tournaments yet" else null
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TournamentListUiState(isLoading = true)
    )

    /**
     * Screen initialize karte time call hota hai.
     * gameModeId null ho to saare tournaments dikhao, warna filter.
     */
    fun loadTournaments(gameModeId: String?, gameModeName: String) {
        _uiState.update {
            it.copy(
                gameModeId = gameModeId,
                gameModeName = gameModeName,
                isLoading = true
            )
        }
    }

    fun onTabSelected(tab: TournamentStatus) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun onJoinClick(tournament: Tournament) {
        _uiState.update {
            it.copy(
                showJoinDialog = true,
                pendingJoinTournament = tournament
            )
        }
    }

    fun onJoinCancel() {
        _uiState.update {
            it.copy(
                showJoinDialog = false,
                pendingJoinTournament = null
            )
        }
    }

    fun onJoinConfirm() {
        val pending = _uiState.value.pendingJoinTournament ?: return
        viewModelScope.launch {
            val success = tournamentRepository.joinTournament(pending.id)
            if (success) {
                _uiState.update {
                    it.copy(
                        showJoinDialog = false,
                        pendingJoinTournament = null,
                        lastJoinedTournamentId = pending.id
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        showJoinDialog = false,
                        pendingJoinTournament = null,
                        errorMessage = "Failed to join tournament"
                    )
                }
            }
        }
    }

    fun onNavigationComplete() {
        _uiState.update { it.copy(lastJoinedTournamentId = null) }
    }
}
