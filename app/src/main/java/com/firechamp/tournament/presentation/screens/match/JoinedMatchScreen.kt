package com.firechamp.tournament.presentation.screens.match

import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.firechamp.tournament.data.model.JoinedMatch
import com.firechamp.tournament.data.model.RoomUnlockStatus
import com.firechamp.tournament.data.model.SubmissionStatus
import com.firechamp.tournament.presentation.components.PillTextField
import com.firechamp.tournament.presentation.components.PrimaryButton
import com.firechamp.tournament.presentation.theme.BlackBackground
import com.firechamp.tournament.presentation.theme.ErrorRed
import com.firechamp.tournament.presentation.theme.GoldCoin
import com.firechamp.tournament.presentation.theme.GreenSuccess
import com.firechamp.tournament.presentation.theme.OrangeWarning
import com.firechamp.tournament.presentation.theme.PurpleDark
import com.firechamp.tournament.presentation.theme.PurpleDeep
import com.firechamp.tournament.presentation.theme.PurplePrimary
import com.firechamp.tournament.presentation.theme.RedAccent
import com.firechamp.tournament.presentation.theme.WhiteSecondary
import com.firechamp.tournament.presentation.theme.WhiteText
import com.firechamp.tournament.presentation.viewmodel.play.JoinedMatchUiState
import com.firechamp.tournament.presentation.viewmodel.play.JoinedMatchViewModel

/**
 * JoinedMatchScreen - match details + room ID reveal + result submission.
 *
 * Flow:
 *  1. Banner + tournament title
 *  2. Countdown timer (room unlocks in X)
 *  3. Status indicator (LOCKED / UNLOCKED / STARTED / ENDED)
 *  4. Room ID + Password card (visible when UNLOCKED)
 *  5. Submit Result button (visible when ENDED)
 *  6. Submission status (PENDING / VERIFIED / REJECTED)
 */
@Composable
fun JoinedMatchScreen(
    tournamentId: String,
    onBack: () -> Unit,
    viewModel: JoinedMatchViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(tournamentId) {
        viewModel.loadJoinedMatch(tournamentId)
    }

    val match = state.match
    if (match == null) {
        Box(modifier = Modifier.fillMaxSize().background(BlackBackground), contentAlignment = Alignment.Center) {
            Text(text = state.errorMessage ?: "Loading...", color = WhiteText, fontSize = 14.sp)
        }
        return
    }

    // Show success toast
    LaunchedEffect(state.submissionSuccess) {
        if (state.submissionSuccess) {
            Toast.makeText(context, "✅ Result submitted! Admin will verify within 30 min.", Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(BlackBackground).verticalScroll(rememberScrollState())
    ) {
        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).clickable { onBack() }, contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = WhiteText)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Joined Match", color = WhiteText, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        }

        // Banner
        Box(
            modifier = Modifier.fillMaxWidth().height(120.dp).background(Brush.horizontalGradient(listOf(PurpleDark, RedAccent))),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = match.tournamentTitle,
                color = WhiteText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            Spacer(modifier = Modifier.height(16.dp))

            // Status indicator
            val status = match.currentStatus()
            StatusCard(status = status, secondsUntilUnlock = state.secondsUntilUnlock)

            Spacer(modifier = Modifier.height(16.dp))

            // Room ID + Password (visible only when UNLOCKED/STARTED)
            if (status == RoomUnlockStatus.UNLOCKED || status == RoomUnlockStatus.STARTED) {
                RoomDetailsCard(
                    match = match,
                    onCopy = { text ->
                        clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(text))
                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                    }
                )
            } else if (status == RoomUnlockStatus.LOCKED || status == RoomUnlockStatus.UNLOCKING_SOON) {
                LockedRoomCard(secondsUntilUnlock = state.secondsUntilUnlock)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Submission status
            if (match.submissionStatus != SubmissionStatus.NOT_SUBMITTED) {
                SubmissionStatusCard(match = match)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Submit Result button (visible when ENDED + no submission yet)
            if (status == RoomUnlockStatus.ENDED && match.submissionStatus == SubmissionStatus.NOT_SUBMITTED) {
                PrimaryButton(
                    text = "SUBMIT RESULT",
                    onClick = { viewModel.onResultDialogOpen() }
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Result submission dialog
    if (state.resultDialogOpen) {
        ResultSubmissionDialog(
            state = state,
            onKillsChange = viewModel::onKillsChange,
            onRankChange = viewModel::onRankChange,
            onScreenshotSelected = { viewModel.onScreenshotSelected("mock://screenshot_${System.currentTimeMillis()}") },
            onSubmit = viewModel::submitResult,
            onClose = viewModel::onResultDialogClose
        )
    }
}

@Composable
private fun StatusCard(status: RoomUnlockStatus, secondsUntilUnlock: Long) {
    val (color, icon, text) = when (status) {
        RoomUnlockStatus.LOCKED -> Triple(PurpleDeep, Icons.Filled.Lock, "Room details will be shared 10 minutes before match")
        RoomUnlockStatus.UNLOCKING_SOON -> Triple(OrangeWarning.copy(alpha = 0.2f), Icons.Filled.Schedule, "Unlocking soon!")
        RoomUnlockStatus.UNLOCKED -> Triple(GreenSuccess.copy(alpha = 0.2f), Icons.Filled.LockOpen, "Room ID is now available!")
        RoomUnlockStatus.STARTED -> Triple(RedAccent.copy(alpha = 0.2f), Icons.Filled.Schedule, "Match is LIVE - Join now!")
        RoomUnlockStatus.ENDED -> Triple(PurpleDeep, Icons.Filled.CheckCircle, "Match ended - Submit your result")
    }
    val textColor = when (status) {
        RoomUnlockStatus.LOCKED -> WhiteSecondary
        RoomUnlockStatus.UNLOCKING_SOON -> OrangeWarning
        RoomUnlockStatus.UNLOCKED -> GreenSuccess
        RoomUnlockStatus.STARTED -> RedAccent
        RoomUnlockStatus.ENDED -> WhiteText
    }

    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(color).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = textColor, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = text, color = textColor, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            if (status == RoomUnlockStatus.LOCKED || status == RoomUnlockStatus.UNLOCKING_SOON) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Room ID unlocks in ${formatHMS(secondsUntilUnlock)}",
                    color = WhiteText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun RoomDetailsCard(match: JoinedMatch, onCopy: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(PurpleDeep).padding(16.dp)
    ) {
        Text(text = "ROOM DETAILS", color = GreenSuccess, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(12.dp))

        // Room ID
        Text(text = "Room ID", color = WhiteSecondary, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = match.roomId ?: "---",
                color = GoldCoin,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier.size(32.dp).clip(CircleShape).background(PurplePrimary).clickable { onCopy(match.roomId ?: "") },
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Filled.ContentCopy, contentDescription = "Copy", tint = WhiteText, modifier = Modifier.size(14.dp))
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Tap to copy room ID",
            color = WhiteSecondary.copy(alpha = 0.6f),
            fontSize = 9.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Room Password
        Text(text = "Password", color = WhiteSecondary, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = match.roomPassword ?: "---",
                color = GoldCoin,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier.size(32.dp).clip(CircleShape).background(PurplePrimary).clickable { onCopy(match.roomPassword ?: "") },
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Filled.ContentCopy, contentDescription = "Copy", tint = WhiteText, modifier = Modifier.size(14.dp))
            }
        }
    }
}

@Composable
private fun LockedRoomCard(secondsUntilUnlock: Long) {
    Box(
        modifier = Modifier.fillMaxWidth().height(140.dp).clip(RoundedCornerShape(12.dp))
            .background(PurpleDeep.copy(alpha = 0.5f)).padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = Icons.Filled.Lock, contentDescription = null, tint = WhiteSecondary, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Room Locked", color = WhiteText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Unlocks in ${formatHMS(secondsUntilUnlock)}",
                color = OrangeWarning,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun SubmissionStatusCard(match: JoinedMatch) {
    val (bg, textColor, icon, statusText) = when (match.submissionStatus) {
        SubmissionStatus.PENDING -> Quadruple(OrangeWarning.copy(alpha = 0.15f), OrangeWarning, Icons.Filled.Schedule, "Under Review - Admin will verify within 30 min")
        SubmissionStatus.VERIFIED -> Quadruple(GreenSuccess.copy(alpha = 0.15f), GreenSuccess, Icons.Filled.CheckCircle, "Verified ✅ - Winning credited to your wallet")
        SubmissionStatus.REJECTED -> Quadruple(RedAccent.copy(alpha = 0.15f), RedAccent, Icons.Filled.Lock, "Rejected: ${match.rejectionReason ?: "Invalid screenshot"}")
        else -> Quadruple(PurpleDeep, WhiteText, Icons.Filled.Lock, "")
    }
    if (statusText.isBlank()) return

    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(bg).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = textColor, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "RESULT STATUS", color = textColor, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = statusText, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            if (match.submissionStatus == SubmissionStatus.VERIFIED || match.submissionStatus == SubmissionStatus.REJECTED) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "Kills: ${match.submittedKills}  •  Rank: #${match.submittedRank}", color = textColor.copy(alpha = 0.8f), fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun ResultSubmissionDialog(
    state: JoinedMatchUiState,
    onKillsChange: (String) -> Unit,
    onRankChange: (String) -> Unit,
    onScreenshotSelected: () -> Unit,
    onSubmit: () -> Unit,
    onClose: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onClose,
        containerColor = PurpleDeep,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(text = "Submit Result", color = WhiteText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                // Kills input
                Text(text = "Kills", color = WhiteSecondary, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                PillTextField(
                    value = state.killsInput,
                    onValueChange = onKillsChange,
                    placeholder = "Number of kills"
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Rank input
                Text(text = "Rank / Position", color = WhiteSecondary, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                PillTextField(
                    value = state.rankInput,
                    onValueChange = onRankChange,
                    placeholder = "Your finishing position"
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Screenshot
                Text(text = "Screenshot (mandatory)", color = WhiteSecondary, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().height(80.dp).clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = if (state.screenshotUri != null) 0.1f else 0.05f))
                        .clickable { onScreenshotSelected() },
                    contentAlignment = Alignment.Center
                ) {
                    if (state.screenshotUri != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.Image, contentDescription = null, tint = GreenSuccess, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Screenshot selected ✅", color = GreenSuccess, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.Image, contentDescription = null, tint = WhiteSecondary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Tap to select screenshot", color = WhiteSecondary, fontSize = 12.sp)
                        }
                    }
                }

                if (state.resultError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = state.resultError, color = ErrorRed, fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "⚠️ Submitting fake screenshots will lead to permanent ban.",
                    color = OrangeWarning,
                    fontSize = 10.sp
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onSubmit,
                modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(PurplePrimary).padding(horizontal = 8.dp)
            ) {
                Text(text = "SUBMIT", color = WhiteText, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onClose) {
                Text(text = "CANCEL", color = WhiteSecondary, fontSize = 13.sp)
            }
        }
    )
}

private data class Quadruple<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)

private fun formatHMS(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) String.format("%02d:%02d:%02d", h, m, s) else String.format("%02d:%02d", m, s)
}