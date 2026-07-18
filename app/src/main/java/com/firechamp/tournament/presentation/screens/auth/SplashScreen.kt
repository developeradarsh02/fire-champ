package com.firechamp.tournament.presentation.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.firechamp.tournament.R
import com.firechamp.tournament.presentation.theme.BlackBackground
import com.firechamp.tournament.presentation.theme.PurplePrimary
import com.firechamp.tournament.presentation.theme.WhiteText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import androidx.lifecycle.ViewModel

enum class SplashDestination { WELCOME, MAIN }

data class SplashUiState(
    val destination: SplashDestination? = null
)

@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    suspend fun checkSession() {
        // 1.5s splash delay (logo + branding display)
        delay(1500)
        // Check Firebase Auth state (session is auto-persisted by Firebase)
        val isLoggedIn = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser != null
        _uiState.update {
            it.copy(
                destination = if (isLoggedIn) SplashDestination.MAIN else SplashDestination.WELCOME
            )
        }
    }
}

/**
 * Splash Screen - app launch pe dikhti hai.
 *
 * Flow:
 *  1. 1.5s delay (logo display)
 *  2. Check Firebase Auth session (auto-persisted)
 *  3. Route to Main (if logged in) ya Welcome screen
 */
@Composable
fun SplashScreen(
    onNavigateToMain: () -> Unit,
    onNavigateToWelcome: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.checkSession()
    }

    LaunchedEffect(state.destination) {
        when (state.destination) {
            SplashDestination.MAIN -> onNavigateToMain()
            SplashDestination.WELCOME -> onNavigateToWelcome()
            else -> { /* loading */ }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(BlackBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
        ) {
            // Fire Champ logo (full size)
            Image(
                painter = painterResource(id = R.drawable.fire_champ_logo),
                contentDescription = "Fire Champ Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .size(280.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(40.dp))
            CircularProgressIndicator(color = com.firechamp.tournament.presentation.theme.GoldFire, modifier = Modifier.padding(0.dp))
        }
    }
}
