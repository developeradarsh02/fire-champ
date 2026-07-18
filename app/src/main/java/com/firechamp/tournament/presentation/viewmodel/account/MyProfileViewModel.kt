package com.firechamp.tournament.presentation.viewmodel.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firechamp.tournament.data.model.User
import com.firechamp.tournament.data.repository.AuthRepository
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * MyProfile UI state.
 */
data class MyProfileUiState(
    val user: User? = null,
    val firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val email: String = "",
    val mobile: String = "",
    val dob: String = "",
    val gender: String = "Male",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isChangingPassword: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val showChangePasswordSheet: Boolean = false,
    val oldPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = ""
)

/**
 * MyProfileViewModel - loads user from Firestore, allows editing + saving
 * and password change.
 */
@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(MyProfileUiState())
    val uiState: StateFlow<MyProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        val user = auth.currentUser
        if (user == null) {
            _uiState.update { it.copy(errorMessage = "Not signed in") }
            return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val doc = firestore.collection("users").document(user.uid).get().await()
                val data = doc.toObject(User::class.java) ?: User(id = user.uid, email = user.email ?: "")
                _uiState.update {
                    it.copy(
                        user = data,
                        firstName = data.firstName,
                        lastName = data.lastName,
                        username = data.username,
                        email = data.email,
                        mobile = data.mobile,
                        dob = data.dob,
                        gender = data.gender.ifBlank { "Male" },
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "Failed to load profile")
                }
            }
        }
    }

    fun onFirstNameChange(v: String) = _uiState.update { it.copy(firstName = v, errorMessage = null) }
    fun onLastNameChange(v: String) = _uiState.update { it.copy(lastName = v, errorMessage = null) }
    fun onUsernameChange(v: String) = _uiState.update { it.copy(username = v, errorMessage = null) }
    fun onMobileChange(v: String) = _uiState.update { it.copy(mobile = v.filter { c -> c.isDigit() || c == '+' }.take(13), errorMessage = null) }
    fun onDobChange(v: String) = _uiState.update { it.copy(dob = v, errorMessage = null) }
    fun onGenderChange(v: String) = _uiState.update { it.copy(gender = v) }

    fun onOldPasswordChange(v: String) = _uiState.update { it.copy(oldPassword = v, errorMessage = null) }
    fun onNewPasswordChange(v: String) = _uiState.update { it.copy(newPassword = v, errorMessage = null) }
    fun onConfirmPasswordChange(v: String) = _uiState.update { it.copy(confirmPassword = v, errorMessage = null) }
    fun showChangePasswordSheet(show: Boolean) = _uiState.update { it.copy(showChangePasswordSheet = show) }

    /**
     * Save profile to Firestore.
     */
    fun saveProfile() {
        val state = _uiState.value
        val user = auth.currentUser ?: run {
            _uiState.update { it.copy(errorMessage = "Not signed in") }
            return
        }
        if (state.firstName.isBlank() || state.lastName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "First and last name are required") }
            return
        }
        _uiState.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }
        viewModelScope.launch {
            try {
                val updates = mapOf(
                    "firstName" to state.firstName.trim(),
                    "lastName" to state.lastName.trim(),
                    "username" to state.username.trim(),
                    "mobile" to state.mobile.trim(),
                    "dob" to state.dob.trim(),
                    "gender" to state.gender,
                    "displayName" to "${state.firstName} ${state.lastName}".trim()
                )
                firestore.collection("users").document(user.uid)
                    .update(updates)
                    .await()
                // Also update Firebase Auth profile
                val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                    .setDisplayName("${state.firstName} ${state.lastName}".trim())
                    .build()
                user.updateProfile(profileUpdates).await()

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        successMessage = "✅ Profile updated successfully"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isSaving = false, errorMessage = e.message ?: "Failed to save profile")
                }
            }
        }
    }

    /**
     * Change password (requires re-authentication with old password).
     */
    fun changePassword() {
        val state = _uiState.value
        val user = auth.currentUser ?: run {
            _uiState.update { it.copy(errorMessage = "Not signed in") }
            return
        }
        if (state.oldPassword.isBlank() || state.newPassword.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please fill all fields") }
            return
        }
        if (state.newPassword.length < 6) {
            _uiState.update { it.copy(errorMessage = "New password must be at least 6 characters") }
            return
        }
        if (state.newPassword != state.confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Passwords don't match") }
            return
        }
        if (state.newPassword == state.oldPassword) {
            _uiState.update { it.copy(errorMessage = "New password must be different from old") }
            return
        }
        _uiState.update { it.copy(isChangingPassword = true, errorMessage = null, successMessage = null) }
        viewModelScope.launch {
            try {
                val credential = EmailAuthProvider.getCredential(user.email ?: "", state.oldPassword)
                user.reauthenticate(credential).await()
                user.updatePassword(state.newPassword).await()
                _uiState.update {
                    it.copy(
                        isChangingPassword = false,
                        successMessage = "✅ Password changed successfully",
                        showChangePasswordSheet = false,
                        oldPassword = "",
                        newPassword = "",
                        confirmPassword = ""
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isChangingPassword = false,
                        errorMessage = when {
                            e.message?.contains("INVALID_LOGIN_CREDENTIALS") == true -> "Old password is incorrect"
                            else -> e.message ?: "Failed to change password"
                        }
                    )
                }
            }
        }
    }

    fun clearMessages() = _uiState.update { it.copy(errorMessage = null, successMessage = null) }
}
