package com.firechamp.tournament.presentation.viewmodel.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firechamp.tournament.data.model.notification.Notification
import com.firechamp.tournament.data.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationsUiState(
    val notifications: List<Notification> = emptyList(),
    val unreadCount: Int = 0
)

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        // Auto-load on creation
        viewModelScope.launch {
            notificationRepository.notifications.collect { list ->
                _uiState.update {
                    it.copy(
                        notifications = list,
                        unreadCount = list.count { n -> !n.isRead }
                    )
                }
            }
        }
    }

    fun markAsRead(id: String) {
        notificationRepository.markAsRead(id)
    }

    fun markAllAsRead() {
        notificationRepository.markAllAsRead()
    }
}