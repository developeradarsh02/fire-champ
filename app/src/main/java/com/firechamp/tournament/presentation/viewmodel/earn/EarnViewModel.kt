package com.firechamp.tournament.presentation.viewmodel.earn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firechamp.tournament.data.model.Banner
import com.firechamp.tournament.data.model.GameMode
import com.firechamp.tournament.data.repository.EarnRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Earn tab ka UI state - sab data jo screen pe dikhana hai.
 */
data class EarnUiState(
    val banners: List<Banner> = emptyList(),
    val gameModes: List<GameMode> = emptyList(),
    val marqueeText: String = "",
    val loading: Boolean = true
)

/**
 * EarnViewModel - banners, game modes, marquee text expose karta hai.
 * Firestore se live stream karta hai (with hardcoded fallback).
 */
@HiltViewModel
class EarnViewModel @Inject constructor(
    private val earnRepository: EarnRepository
) : ViewModel() {

    val uiState: StateFlow<EarnUiState> = combine(
        earnRepository.observeBanners().catch { emit(earnRepository.getDefaultBanners()) },
        earnRepository.observeGameModes().catch { emit(earnRepository.getDefaultGameModes()) },
        earnRepository.observeAnnouncements().catch { emit(emptyList()) }
    ) { banners, gameModes, announcements ->
        EarnUiState(
            banners = if (banners.isEmpty()) earnRepository.getDefaultBanners() else banners,
            gameModes = if (gameModes.isEmpty()) earnRepository.getDefaultGameModes() else gameModes,
            marqueeText = announcements.firstOrNull()?.message ?: earnRepository.getMarqueeText(),
            loading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = EarnUiState(
            banners = earnRepository.getDefaultBanners(),
            gameModes = earnRepository.getDefaultGameModes(),
            marqueeText = earnRepository.getMarqueeText(),
            loading = true
        )
    )
}
