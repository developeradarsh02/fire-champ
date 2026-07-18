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

data class SignupUiState(
    val firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val countryCode: String = "+91",
    val mobile: String = "",
    val email: String = "",
    val password: String = "",
    val referralCode: String = "",

    // Field-level errors
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val usernameError: String? = null,
    val mobileError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,

    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val signedUpUser: User? = null
) {
    /** Button enable/disable logic - all required fields filled & valid */
    val isSignupEnabled: Boolean
        get() = firstName.isNotBlank() &&
                lastName.isNotBlank() &&
                username.isNotBlank() &&
                mobile.length == 10 &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                password.length >= 6
}

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignupUiState())
    val uiState: StateFlow<SignupUiState> = _uiState.asStateFlow()

    fun onFirstNameChange(v: String) =
        _uiState.update { it.copy(firstName = v, firstNameError = null, errorMessage = null) }
    fun onLastNameChange(v: String) =
        _uiState.update { it.copy(lastName = v, lastNameError = null, errorMessage = null) }
    fun onUsernameChange(v: String) =
        _uiState.update { it.copy(username = v, usernameError = null, errorMessage = null) }
    fun onCountryCodeChange(v: String) =
        _uiState.update { it.copy(countryCode = v) }
    fun onMobileChange(v: String) {
        // Allow only digits, max 10
        val filtered = v.filter { it.isDigit() }.take(10)
        _uiState.update { it.copy(mobile = filtered, mobileError = null, errorMessage = null) }
    }
    fun onEmailChange(v: String) =
        _uiState.update { it.copy(email = v, emailError = null, errorMessage = null) }
    fun onPasswordChange(v: String) =
        _uiState.update { it.copy(password = v, passwordError = null, errorMessage = null) }
    fun onReferralCodeChange(v: String) =
        _uiState.update { it.copy(referralCode = v) }

    fun onSignupClick() {
        val current = _uiState.value

        // Field-level validation
        val firstNameError = if (current.firstName.isBlank()) "Required" else null
        val lastNameError = if (current.lastName.isBlank()) "Required" else null
        val usernameError = if (current.username.isBlank()) "Required" else null
        val mobileError = when {
            current.mobile.isBlank() -> "Required"
            current.mobile.length != 10 -> "Must be exactly 10 digits"
            else -> null
        }
        val emailError = when {
            current.email.isBlank() -> "Required"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(current.email).matches() -> "Invalid email"
            else -> null
        }
        val passwordError = when {
            current.password.isBlank() -> "Required"
            current.password.length < 6 -> "Minimum 6 characters"
            else -> null
        }

        if (firstNameError != null || lastNameError != null ||
            usernameError != null || mobileError != null ||
            emailError != null || passwordError != null
        ) {
            _uiState.update {
                it.copy(
                    firstNameError = firstNameError,
                    lastNameError = lastNameError,
                    usernameError = usernameError,
                    mobileError = mobileError,
                    emailError = emailError,
                    passwordError = passwordError
                )
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = authRepository.signup(
                firstName = current.firstName.trim(),
                lastName = current.lastName.trim(),
                username = current.username.trim(),
                email = current.email.trim(),
                mobile = current.mobile,
                password = current.password,
                referralCode = current.referralCode.takeIf { it.isNotBlank() }
            )
            result.onSuccess { user ->
                _uiState.update { it.copy(isLoading = false, signedUpUser = user) }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = err.message ?: "Signup failed. Please try again."
                    )
                }
            }
        }
    }

    fun onNavigationComplete() {
        _uiState.update { it.copy(signedUpUser = null) }
    }
}
