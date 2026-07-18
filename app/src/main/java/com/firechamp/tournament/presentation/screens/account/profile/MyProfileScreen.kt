package com.firechamp.tournament.presentation.screens.account.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.firechamp.tournament.presentation.components.FireButton
import com.firechamp.tournament.presentation.components.SubScreenTopBar
import com.firechamp.tournament.presentation.theme.BlackBackground
import com.firechamp.tournament.presentation.theme.GoldFire
import com.firechamp.tournament.presentation.theme.GreenSuccess
import com.firechamp.tournament.presentation.theme.PurpleDeep
import com.firechamp.tournament.presentation.theme.RedAccent
import com.firechamp.tournament.presentation.theme.WhiteSecondary
import com.firechamp.tournament.presentation.theme.WhiteText
import com.firechamp.tournament.presentation.viewmodel.account.MyProfileViewModel

/**
 * My Profile Screen - Fully reactive with Firestore.
 *
 * Features:
 *  - Load real user data from Firestore on open
 *  - Edit fields and save back to Firestore
 *  - Change password (re-auth required)
 *  - Display avatar with initials
 */
@Composable
fun MyProfileScreen(
    onBack: () -> Unit,
    viewModel: MyProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackBackground)
            .verticalScroll(rememberScrollState())
    ) {
        SubScreenTopBar(title = "My Profile", onBack = onBack)

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Spacer(modifier = Modifier.height(8.dp))

            // Loading indicator
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = GoldFire)
                }
            } else {
                Text(
                    text = "Edit Profile",
                    color = WhiteText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Avatar with initials
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(PurpleDeep)
                            .border(width = 2.dp, color = GoldFire, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = getInitials(state.firstName, state.lastName, state.username),
                            color = GoldFire,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                // First Name | Last Name
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.weight(1f)) {
                        LabeledField(
                            label = "First Name",
                            value = state.firstName,
                            onValueChange = viewModel::onFirstNameChange
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        LabeledField(
                            label = "Last Name",
                            value = state.lastName,
                            onValueChange = viewModel::onLastNameChange
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                LabeledField(
                    label = "Username",
                    value = state.username,
                    onValueChange = viewModel::onUsernameChange
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Email - locked
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(PurpleDeep.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = state.email.ifBlank { "Email (locked)" },
                        color = WhiteSecondary,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 18.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Mobile with +91 prefix
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(64.dp)
                            .height(56.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+91",
                            color = Color.Black,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        FireTextField(
                            value = state.mobile,
                            onValueChange = viewModel::onMobileChange,
                            placeholder = "Mobile Number"
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                LabeledField(
                    label = "Date of Birth",
                    value = state.dob,
                    onValueChange = viewModel::onDobChange
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Gender
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Gender:",
                        color = WhiteText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(24.dp))
                    GenderOption(
                        label = "Male",
                        selected = state.gender == "Male"
                    ) { viewModel.onGenderChange("Male") }
                    Spacer(modifier = Modifier.width(28.dp))
                    GenderOption(
                        label = "Female",
                        selected = state.gender == "Female"
                    ) { viewModel.onGenderChange("Female") }
                }
                Spacer(modifier = Modifier.height(20.dp))

                // Update profile button
                FireButton(
                    text = if (state.isSaving) "SAVING..." else "UPDATE PROFILE",
                    onClick = viewModel::saveProfile,
                    enabled = !state.isSaving
                )

                // Status messages
                if (state.successMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = state.successMessage!!,
                        color = GreenSuccess,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (state.errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = state.errorMessage!!,
                        color = RedAccent,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Reset Password section
                Text(
                    text = "Reset Password",
                    color = WhiteText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                FireButton(
                    text = "CHANGE PASSWORD",
                    onClick = { viewModel.showChangePasswordSheet(true) }
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }

    // Change password bottom sheet
    if (state.showChangePasswordSheet) {
        ChangePasswordDialog(
            oldPassword = state.oldPassword,
            newPassword = state.newPassword,
            confirmPassword = state.confirmPassword,
            isChanging = state.isChangingPassword,
            errorMessage = state.errorMessage,
            onOldChange = viewModel::onOldPasswordChange,
            onNewChange = viewModel::onNewPasswordChange,
            onConfirmChange = viewModel::onConfirmPasswordChange,
            onSubmit = viewModel::changePassword,
            onCancel = { viewModel.showChangePasswordSheet(false) }
        )
    }
}

@Composable
private fun LabeledField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column {
        Text(label, color = WhiteSecondary, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp, bottom = 4.dp))
        FireTextField(value = value, onValueChange = onValueChange, placeholder = label)
    }
}

@Composable
private fun FireTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(PurpleDeep),
        contentAlignment = Alignment.CenterStart
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(
                color = WhiteText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            ),
            cursorBrush = androidx.compose.ui.graphics.SolidColor(GoldFire),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text
            ),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(placeholder, color = WhiteSecondary, fontSize = 14.sp)
                }
                innerTextField()
            }
        )
    }
}

@Composable
private fun GenderOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onClick() }) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .border(2.dp, if (selected) GoldFire else WhiteSecondary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(GoldFire)
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, color = WhiteText, fontSize = 14.sp)
    }
}

private fun getInitials(firstName: String, lastName: String, username: String): String {
    val first = firstName.firstOrNull()?.uppercase() ?: ""
    val last = lastName.firstOrNull()?.uppercase() ?: ""
    return when {
        first.isNotBlank() && last.isNotBlank() -> "$first$last"
        first.isNotBlank() -> first
        username.length >= 2 -> username.take(2).uppercase()
        else -> "FC"
    }
}

/**
 * Change password dialog (modal).
 */
@Composable
private fun ChangePasswordDialog(
    oldPassword: String,
    newPassword: String,
    confirmPassword: String,
    isChanging: Boolean,
    errorMessage: String?,
    onOldChange: (String) -> Unit,
    onNewChange: (String) -> Unit,
    onConfirmChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onCancel) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(BlackBackground)
                .padding(20.dp)
        ) {
            Text(
                "Change Password",
                color = WhiteText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Enter your current password, then choose a new one.",
                color = WhiteSecondary,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            FireTextField(
                value = oldPassword,
                onValueChange = onOldChange,
                placeholder = "Current Password",
                isPassword = true
            )
            Spacer(modifier = Modifier.height(10.dp))
            FireTextField(
                value = newPassword,
                onValueChange = onNewChange,
                placeholder = "New Password (min 6 chars)",
                isPassword = true
            )
            Spacer(modifier = Modifier.height(10.dp))
            FireTextField(
                value = confirmPassword,
                onValueChange = onConfirmChange,
                placeholder = "Confirm New Password",
                isPassword = true
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(errorMessage, color = RedAccent, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                androidx.compose.material3.OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("CANCEL", color = WhiteText)
                }
                FireButton(
                    text = if (isChanging) "CHANGING..." else "CHANGE",
                    onClick = onSubmit,
                    enabled = !isChanging
                )
            }
        }
    }
}
