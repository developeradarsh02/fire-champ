package com.firechamp.tournament.presentation.viewmodel.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firechamp.tournament.data.local.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Account/Settings UI state - notification preferences.
 */
data class SettingsUiState(
    val pushEnabled: Boolean = true,
    val matchReminders: Boolean = true,
    val resultAlerts: Boolean = true,
    val promoAlerts: Boolean = false
)

/**
 * Settings ViewModel - persistent notification preferences via DataStore.
 * State survives screen navigation, app restart, logout (until explicit reset).
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = kotlinx.coroutines.flow.combine(
        sessionManager.pushEnabledFlow,
        sessionManager.matchRemindersFlow,
        sessionManager.resultAlertsFlow,
        sessionManager.promoAlertsFlow
    ) { push, reminders, results, promos ->
        SettingsUiState(
            pushEnabled = push,
            matchReminders = reminders,
            resultAlerts = results,
            promoAlerts = promos
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState()
    )

    fun setPushEnabled(enabled: Boolean) {
        viewModelScope.launch { sessionManager.setPushEnabled(enabled) }
    }

    fun setMatchReminders(enabled: Boolean) {
        viewModelScope.launch { sessionManager.setMatchReminders(enabled) }
    }

    fun setResultAlerts(enabled: Boolean) {
        viewModelScope.launch { sessionManager.setResultAlerts(enabled) }
    }

    fun setPromoAlerts(enabled: Boolean) {
        viewModelScope.launch { sessionManager.setPromoAlerts(enabled) }
    }
}
