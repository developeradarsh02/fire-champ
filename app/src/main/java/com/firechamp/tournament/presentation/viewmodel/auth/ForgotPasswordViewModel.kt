package com.firechamp.tournament.presentation.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firechamp.tournament.data.repository.PasswordResetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Forgot password 3-step flow state (Cloud Function based).
 */
data class ForgotPasswordUiState(
    val step: Int = 0,                         // 0=email, 1=otp, 2=new-password, 3=success
    val email: String = "",
    val otpDigits: List<String> = listOf("", "", "", "", "", ""),  // 6 digits
    val newPassword: String = "",
    val confirmPassword: String = "",
    val username: String = "",
    val otpId: String = "",                    // From server
    val resetToken: String = "",               // From server (after OTP verify)
    val expiresAtSeconds: Long = 0,           // For resend timer
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null
)

/**
 * ForgotPasswordViewModel - 3-step password reset via Cloud Function.
 *
 * Real production flow:
 *  1. User enters email → Cloud Function sends OTP via email
 *  2. User enters 6-digit OTP → Cloud Function verifies + returns resetToken
 *  3. User sets new password → Cloud Function updates via Admin SDK
 *  → Back to Login screen
 */
@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val repository: PasswordResetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
    }

    fun sendOtp() {
        val email = _uiState.value.email.trim()
        if (email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter your email") }
            return
        }
        if (!email.contains("@")) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid email") }
            return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = repository.sendOtp(email)
            if (result.success) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        step = 1,
                        otpId = result.otpId ?: "",
                        expiresAtSeconds = result.expiresAt ?: 0L,
                        infoMessage = "OTP sent to your email. Please check inbox/spam."
                    )
                }
            } else {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun onOtpDigitChange(index: Int, value: String) {
        val digit = value.take(1).filter { it.isDigit() }
        val newList = _uiState.value.otpDigits.toMutableList()
        newList[index] = digit
        _uiState.update { it.copy(otpDigits = newList, errorMessage = null) }
    }

    fun verifyOtp() {
        val state = _uiState.value
        val otp = state.otpDigits.joinToString("")
        if (otp.length != 6) {
            _uiState.update { it.copy(errorMessage = "Please enter all 6 digits") }
            return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = repository.verifyOtp(state.email, otp)
            if (result.success) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        step = 2,
                        otpId = result.otpId ?: it.otpId,
                        resetToken = result.resetToken ?: "",
                        username = result.username ?: it.email.substringBefore("@")
                    )
                }
            } else {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun onNewPasswordChange(value: String) {
        _uiState.update { it.copy(newPassword = value, errorMessage = null) }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update { it.copy(confirmPassword = value, errorMessage = null) }
    }

    fun resetPassword() {
        val state = _uiState.value
        if (state.newPassword.length < 6) {
            _uiState.update { it.copy(errorMessage = "Password must be at least 6 characters") }
            return
        }
        if (state.newPassword != state.confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Passwords don't match") }
            return
        }
        if (state.resetToken.isBlank() || state.otpId.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Session expired. Please start over.") }
            return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = repository.resetPassword(
                otpId = state.otpId,
                resetToken = state.resetToken,
                newPassword = state.newPassword
            )
            if (result.success) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        step = 3,
                        infoMessage = result.message
                    )
                }
            } else {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun resetFlow() {
        _uiState.value = ForgotPasswordUiState()
    }
}
