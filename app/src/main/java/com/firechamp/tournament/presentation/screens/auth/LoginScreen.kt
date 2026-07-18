package com.firechamp.tournament.presentation.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.firechamp.tournament.R
import androidx.compose.ui.tooling.preview.Preview
import com.firechamp.tournament.presentation.theme.FireChampTheme
import com.firechamp.tournament.presentation.viewmodel.auth.LoginUiState
import com.firechamp.tournament.presentation.components.FireButton
import com.firechamp.tournament.presentation.components.FireTextField
import com.firechamp.tournament.presentation.theme.BlackBackground
import com.firechamp.tournament.presentation.theme.GoldFire
import com.firechamp.tournament.presentation.theme.WhiteSecondary
import com.firechamp.tournament.presentation.theme.WhiteText
import com.firechamp.tournament.presentation.viewmodel.auth.LoginViewModel

/**
 * Login Screen - fire theme (approved mockup #2).
 *
 * Logo + "Welcome Back, Champ!" heading, dark inputs with gold focus,
 * Forgot Password link, fire gradient LOGIN button.
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onSignupClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Navigate when login successful
    LaunchedEffect(state.loggedInUser) {
        if (state.loggedInUser != null) {
            onLoginSuccess()
            viewModel.onNavigationComplete()
        }
    }

    LoginScreenContent(
        state = state,
        onUsernameChange = viewModel::onUsernameChange,
        onPasswordChange = viewModel::onPasswordChange,
        onLoginClick = viewModel::onLoginClick,
        onSignupClick = onSignupClick,
        onForgotPasswordClick = onForgotPasswordClick
    )
}

@Composable
fun LoginScreenContent(
    state: LoginUiState,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onSignupClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.fire_champ_logo),
            contentDescription = "Fire Champ",
            modifier = Modifier
                .size(130.dp)
                .align(Alignment.CenterHorizontally),
            contentScale = ContentScale.Fit
        )

        Text(
            text = "Welcome Back, Champ!",
            color = WhiteText,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Login to continue your tournament journey",
            color = WhiteSecondary,
            fontSize = 13.5.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(28.dp))

        FireTextField(
            value = state.username,
            onValueChange = onUsernameChange,
            placeholder = "Username or Email",
            leadingIcon = Icons.Filled.Person,
            isError = state.isUsernameError,
            errorMessage = state.usernameErrorMsg
        )

        Spacer(modifier = Modifier.height(13.dp))

        FireTextField(
            value = state.password,
            onValueChange = onPasswordChange,
            placeholder = "Password",
            leadingIcon = Icons.Filled.Lock,
            isPassword = true,
            isError = state.isPasswordError,
            errorMessage = state.passwordErrorMsg
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Forgot password - right-aligned gold link
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = "Forgot Password?",
                color = GoldFire,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onForgotPasswordClick() }
                    .padding(4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Server-level error
        state.errorMessage?.let { errMsg ->
            Text(
                text = errMsg,
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        FireButton(
            text = "LOGIN",
            onClick = onLoginClick,
            isLoading = state.isLoading
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Register link
        ClickableText(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = WhiteSecondary)) {
                    append("Don't have an account? ")
                }
                withStyle(style = SpanStyle(color = GoldFire, fontWeight = FontWeight.Bold)) {
                    append("REGISTER")
                }
            },
            onClick = { onSignupClick() },
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    FireChampTheme {
        LoginScreenContent(
            state = LoginUiState(),
            onUsernameChange = {},
            onPasswordChange = {},
            onLoginClick = {},
            onSignupClick = {},
            onForgotPasswordClick = {}
        )
    }
}
