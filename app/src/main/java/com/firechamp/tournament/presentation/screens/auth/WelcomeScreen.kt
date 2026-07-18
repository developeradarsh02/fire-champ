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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.firechamp.tournament.R
import com.firechamp.tournament.presentation.components.FireButton
import com.firechamp.tournament.presentation.components.FireOutlineButton
import com.firechamp.tournament.presentation.theme.BlackBackground
import com.firechamp.tournament.presentation.theme.GoldFire
import com.firechamp.tournament.presentation.theme.WhiteSecondary
import com.firechamp.tournament.presentation.theme.WhiteText

/**
 * Welcome Screen - Splash ke baad pehli screen (logged-out users ke liye).
 *
 * Layout (approved mockup #1):
 *  - Upar 62%: Fire Champ hero artwork (bottom black fade ke saath)
 *  - Neeche: "WELCOME TO THE ARENA" + subtitle
 *  - REGISTER (fire gradient) + LOGIN (gold outline) buttons
 *  - Terms & Privacy footer
 */
@Composable
fun WelcomeScreen(
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackBackground)
    ) {
        // Hero artwork with fade-to-black bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.60f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.welcome_hero),
                contentDescription = "Fire Champ",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Bottom gradient fade so art blends into black section
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Transparent, BlackBackground),
                            startY = 0f
                        )
                    )
            )
        }

        // Bottom content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.40f)
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Text(
                text = "WELCOME TO THE ARENA",
                color = WhiteText,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Join Free Fire tournaments, dominate the battlefield & win real cash rewards.",
                color = WhiteSecondary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            FireButton(text = "REGISTER", onClick = onRegisterClick)
            Spacer(modifier = Modifier.height(14.dp))
            FireOutlineButton(text = "LOGIN", onClick = onLoginClick)

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = WhiteSecondary.copy(alpha = 0.6f))) {
                        append("By continuing you agree to our ")
                    }
                    withStyle(SpanStyle(color = GoldFire, fontWeight = FontWeight.SemiBold)) {
                        append("Terms")
                    }
                    withStyle(SpanStyle(color = WhiteSecondary.copy(alpha = 0.6f))) {
                        append(" & ")
                    }
                    withStyle(SpanStyle(color = GoldFire, fontWeight = FontWeight.SemiBold)) {
                        append("Privacy Policy")
                    }
                },
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
