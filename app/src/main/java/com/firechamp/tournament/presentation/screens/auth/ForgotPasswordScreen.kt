package com.firechamp.tournament.presentation.screens.auth

import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.navigation.compose.hiltViewModel
import com.firechamp.tournament.presentation.components.FireButton
import com.firechamp.tournament.presentation.viewmodel.auth.ForgotPasswordUiState
import com.firechamp.tournament.presentation.components.FireOutlineButton
import com.firechamp.tournament.presentation.theme.BlackBackground
import com.firechamp.tournament.presentation.theme.GoldFire
import com.firechamp.tournament.presentation.theme.GreenSuccess
import com.firechamp.tournament.presentation.theme.PurpleDeep
import com.firechamp.tournament.presentation.theme.RedAccent
import com.firechamp.tournament.presentation.theme.WhiteSecondary
import com.firechamp.tournament.presentation.theme.WhiteText
import com.firechamp.tournament.presentation.viewmodel.auth.ForgotPasswordViewModel

/**
 * Forgot Password Screen - 3-step flow.
 *
 * Step 0: Email entry
 * Step 1: 6-digit OTP entry (sent via email)
 * Step 2: New password + confirm
 * Step 3: Success!
 *
 * All steps use Cloud Functions (no client-side OTP generation).
 */
@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    onResetComplete: () -> Unit = onBack,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).clickable {
                    if (state.step > 0) viewModel.resetFlow()
                    onBack()
                },
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = WhiteText)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Reset Password",
                color = WhiteText,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
            Spacer(modifier = Modifier.height(16.dp))

            when (state.step) {
                0 -> Step0Email(state, viewModel)
                1 -> Step1Otp(state, viewModel, focusManager)
                2 -> Step2NewPassword(state, viewModel)
                3 -> Step3Success(state, onResetComplete)
            }

            if (state.errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = state.errorMessage!!,
                    color = RedAccent,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (state.infoMessage != null && state.step != 3) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = state.infoMessage!!,
                    color = GoldFire,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun Step0Email(state: ForgotPasswordUiState, viewModel: ForgotPasswordViewModel) {
    Text(text = "Forgot Password?", color = WhiteText, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "Enter your registered email. We'll send you a 6-digit OTP to reset your password.",
        color = WhiteSecondary,
        fontSize = 13.sp
    )
    Spacer(modifier = Modifier.height(24.dp))
    FireEmailField(
        value = state.email,
        onValueChange = viewModel::onEmailChange,
        placeholder = "you@email.com"
    )
    Spacer(modifier = Modifier.height(20.dp))
    FireButton(
        text = if (state.isLoading) "SENDING..." else "SEND OTP",
        onClick = viewModel::sendOtp,
        enabled = !state.isLoading && state.email.isNotBlank()
    )
}

@Composable
private fun Step1Otp(
    state: ForgotPasswordUiState,
    viewModel: ForgotPasswordViewModel,
    focusManager: androidx.compose.ui.focus.FocusManager
) {
    Text(text = "Verify OTP", color = WhiteText, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "We've sent a 6-digit code to ${state.email.replace(Regex("(?<=.{2}).(?=[^@]*?@)"), "•")}",
        color = WhiteSecondary,
        fontSize = 13.sp
    )
    Spacer(modifier = Modifier.height(24.dp))

    // 6 OTP boxes
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (i in 0..5) {
            Box(modifier = Modifier.weight(1f)) {
                OtpDigitBox(
                    value = state.otpDigits[i],
                    onValueChange = { v -> viewModel.onOtpDigitChange(i, v) },
                    index = i
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(24.dp))
    FireButton(
        text = if (state.isLoading) "VERIFYING..." else "VERIFY OTP",
        onClick = viewModel::verifyOtp,
        enabled = !state.isLoading && state.otpDigits.all { it.isNotBlank() }
    )
    Spacer(modifier = Modifier.height(12.dp))
    Text(
        text = "Didn't get the code? Wait 30s, then tap resend.",
        color = WhiteSecondary,
        fontSize = 11.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun OtpDigitBox(value: String, onValueChange: (String) -> Unit, index: Int) {
    // The .weight(1f) modifier is applied at the call site (inside Row).
    // This composable just renders the box; the parent Row gives it equal width.
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
            .background(PurpleDeep),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(
                color = if (value.isNotBlank()) GoldFire else WhiteText,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            ),
            cursorBrush = androidx.compose.ui.graphics.SolidColor(GoldFire),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier = Modifier.fillMaxWidth().padding(4.dp),
            decorationBox = { innerTextField ->
                Box(contentAlignment = Alignment.Center) {
                    if (value.isEmpty()) {
                        Text("•", color = WhiteSecondary.copy(alpha = 0.3f), fontSize = 20.sp)
                    }
                    innerTextField()
                }
            }
        )
    }
}

@Composable
private fun Step2NewPassword(state: ForgotPasswordUiState, viewModel: ForgotPasswordViewModel) {
    Text(text = "New Password", color = WhiteText, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "Hi ${state.username}, set a new password for your account.",
        color = WhiteSecondary,
        fontSize = 13.sp
    )
    Spacer(modifier = Modifier.height(24.dp))
    FireEmailField(
        value = state.newPassword,
        onValueChange = viewModel::onNewPasswordChange,
        placeholder = "New Password (min 6 chars)",
        isPassword = true
    )
    Spacer(modifier = Modifier.height(12.dp))
    FireEmailField(
        value = state.confirmPassword,
        onValueChange = viewModel::onConfirmPasswordChange,
        placeholder = "Confirm Password",
        isPassword = true
    )
    Spacer(modifier = Modifier.height(20.dp))
    FireButton(
        text = if (state.isLoading) "RESETTING..." else "RESET PASSWORD",
        onClick = viewModel::resetPassword,
        enabled = !state.isLoading && state.newPassword.isNotBlank() && state.confirmPassword.isNotBlank()
    )
}

@Composable
private fun Step3Success(state: ForgotPasswordUiState, onResetComplete: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = GreenSuccess,
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Password Reset!", color = WhiteText, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = state.infoMessage ?: "Your password has been changed successfully.",
            color = WhiteSecondary,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        FireButton(text = "BACK TO LOGIN", onClick = onResetComplete)
    }
}

/**
 * Fire-themed text field for email/password inputs.
 */
@Composable
private fun FireEmailField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(14.dp))
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
                keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Email
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
