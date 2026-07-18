package com.firechamp.tournament.presentation.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firechamp.tournament.data.model.User
import com.firechamp.tournament.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for the Login screen.
 * StateFlow use kar rahe hain taaki recomposition automatic ho.
 */
data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isUsernameError: Boolean = false,
    val isPasswordError: Boolean = false,
    val usernameErrorMsg: String? = null,
    val passwordErrorMsg: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loggedInUser: User? = null
)

/**
 * LoginViewModel - form state, validation aur submission logic hold karta hai.
 * UI (Composable) directly state observe karta hai.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onUsernameChange(value: String) {
        _uiState.update {
            it.copy(
                username = value,
                isUsernameError = false,
                usernameErrorMsg = null,
                errorMessage = null
            )
        }
    }

    fun onPasswordChange(value: String) {
        _uiState.update {
            it.copy(
                password = value,
                isPasswordError = false,
                passwordErrorMsg = null,
                errorMessage = null
            )
        }
    }

    fun onLoginClick() {
        val current = _uiState.value
        val usernameValid = current.username.isNotBlank()
        val passwordValid = current.password.length >= 6

        if (!usernameValid || !passwordValid) {
            _uiState.update {
                it.copy(
                    isUsernameError = !usernameValid,
                    usernameErrorMsg = if (!usernameValid) "Username cannot be empty" else null,
                    isPasswordError = !passwordValid,
                    passwordErrorMsg = if (!passwordValid) "Password must be at least 6 characters" else null
                )
            }
            return
        }

        // Submit
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = authRepository.login(current.username.trim(), current.password)
            result.onSuccess { user ->
                _uiState.update { it.copy(isLoading = false, loggedInUser = user) }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = err.message ?: "Login failed. Please try again."
                    )
                }
            }
        }
    }

    /** Called by UI after navigation to reset state. */
    fun onNavigationComplete() {
        _uiState.update { it.copy(loggedInUser = null) }
    }
}
