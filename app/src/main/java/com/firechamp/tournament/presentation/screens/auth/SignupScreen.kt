package com.firechamp.tournament.presentation.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.firechamp.tournament.R
import com.firechamp.tournament.presentation.components.FireButton
import com.firechamp.tournament.presentation.components.FireTextField
import com.firechamp.tournament.presentation.theme.BlackBackground
import com.firechamp.tournament.presentation.theme.GoldFire
import com.firechamp.tournament.presentation.theme.GreyHint
import com.firechamp.tournament.presentation.theme.WhiteSecondary
import com.firechamp.tournament.presentation.theme.WhiteText
import com.firechamp.tournament.presentation.viewmodel.auth.SignupViewModel

/**
 * Signup Screen - fire theme (approved mockup #3).
 * "Create Account" heading + dark inputs + fire gradient REGISTER button.
 */
@Composable
fun SignupScreen(
    onSignupSuccess: () -> Unit,
    onLoginClick: () -> Unit,
    viewModel: SignupViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var countryDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(state.signedUpUser) {
        if (state.signedUpUser != null) {
            onSignupSuccess()
            viewModel.onNavigationComplete()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        // Logo (small)
        Image(
            painter = painterResource(id = R.drawable.fire_champ_logo),
            contentDescription = "Fire Champ",
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.CenterHorizontally),
            contentScale = ContentScale.Fit
        )

        Text(
            text = "Create Account",
            color = WhiteText,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Join the arena in less than a minute",
            color = WhiteSecondary,
            fontSize = 13.5.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // First Name | Last Name (row)
        Row(modifier = Modifier.fillMaxWidth()) {
            FireTextField(
                value = state.firstName,
                onValueChange = viewModel::onFirstNameChange,
                placeholder = "First Name",
                isError = state.firstNameError != null,
                errorMessage = state.firstNameError,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            FireTextField(
                value = state.lastName,
                onValueChange = viewModel::onLastNameChange,
                placeholder = "Last Name",
                isError = state.lastNameError != null,
                errorMessage = state.lastNameError,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(13.dp))

        FireTextField(
            value = state.username,
            onValueChange = viewModel::onUsernameChange,
            placeholder = "Username",
            leadingIcon = Icons.Filled.Person,
            isError = state.usernameError != null,
            errorMessage = state.usernameError
        )

        Spacer(modifier = Modifier.height(13.dp))

        FireTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            placeholder = "Email Address",
            leadingIcon = Icons.Filled.Email,
            keyboardType = KeyboardType.Email,
            isError = state.emailError != null,
            errorMessage = state.emailError
        )

        Spacer(modifier = Modifier.height(13.dp))

        // Country code + Mobile number (row)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(modifier = Modifier.width(92.dp)) {
                FireTextField(
                    value = state.countryCode,
                    onValueChange = {},
                    placeholder = "+91",
                    readOnly = true,
                    trailingContent = {
                        IconButton(onClick = { countryDropdownExpanded = true }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = "Country code",
                                tint = GreyHint
                            )
                        }
                    }
                )
                DropdownMenu(
                    expanded = countryDropdownExpanded,
                    onDismissRequest = { countryDropdownExpanded = false }
                ) {
                    listOf("+91", "+1", "+44", "+61", "+971").forEach { code ->
                        DropdownMenuItem(
                            text = { Text(code) },
                            onClick = {
                                viewModel.onCountryCodeChange(code)
                                countryDropdownExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            FireTextField(
                value = state.mobile,
                onValueChange = viewModel::onMobileChange,
                placeholder = "Mobile Number",
                keyboardType = KeyboardType.Phone,
                isError = state.mobileError != null,
                errorMessage = state.mobileError,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(13.dp))

        FireTextField(
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            placeholder = "Password",
            leadingIcon = Icons.Filled.Lock,
            isPassword = true,
            isError = state.passwordError != null,
            errorMessage = state.passwordError
        )

        Spacer(modifier = Modifier.height(13.dp))

        FireTextField(
            value = state.referralCode,
            onValueChange = viewModel::onReferralCodeChange,
            placeholder = "Referral Code (Optional)",
            leadingIcon = Icons.Filled.CardGiftcard
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Terms text
        ClickableText(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = WhiteSecondary.copy(alpha = 0.7f))) {
                    append("By Registering, I agree to the ")
                }
                withStyle(style = SpanStyle(color = GoldFire)) {
                    append("Terms and Conditions")
                }
                withStyle(style = SpanStyle(color = WhiteSecondary.copy(alpha = 0.7f))) {
                    append(" and ")
                }
                withStyle(style = SpanStyle(color = GoldFire)) {
                    append("Privacy Policy")
                }
            },
            onClick = { /* TODO: open webview */ },
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        state.errorMessage?.let { errMsg ->
            Text(
                text = errMsg,
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        FireButton(
            text = "REGISTER",
            onClick = viewModel::onSignupClick,
            isLoading = state.isLoading,
            enabled = state.isSignupEnabled
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Login link
        ClickableText(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = WhiteSecondary)) {
                    append("Already a user? ")
                }
                withStyle(style = SpanStyle(color = GoldFire, fontWeight = FontWeight.Bold)) {
                    append("LOGIN")
                }
            },
            onClick = { onLoginClick() },
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}
