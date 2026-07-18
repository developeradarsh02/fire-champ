package com.firechamp.tournament.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.firechamp.tournament.presentation.theme.BlackBackground
import com.firechamp.tournament.presentation.theme.ErrorRed
import com.firechamp.tournament.presentation.theme.GreyHint
import com.firechamp.tournament.presentation.theme.OrangeWarning
import com.firechamp.tournament.presentation.theme.PurpleDeep
import com.firechamp.tournament.presentation.theme.PurplePrimary
import com.firechamp.tournament.presentation.theme.RedAccent
import com.firechamp.tournament.presentation.theme.WhiteSecondary
import com.firechamp.tournament.presentation.theme.WhiteText

/**
 * Report reasons (Task 13).
 */
enum class ReportReason(val label: String) {
    HACKING_CHEATING("Hacking / Cheating"),
    ABUSIVE_BEHAVIOR("Abusive Behavior"),
    FAKE_SCREENSHOT("Fake Screenshot"),
    OTHER("Other")
}

/**
 * Report Dialog - Match Result screen me player pe report karne ke liye.
 *
 * Long-press ya 3-dot menu se trigger hota hai.
 */
@Composable
fun ReportDialog(
    playerName: String,
    onDismiss: () -> Unit,
    onSubmit: (ReportReason, String) -> Unit
) {
    var selectedReason by remember { mutableStateOf<ReportReason?>(null) }
    var description by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PurpleDeep,
        shape = RoundedCornerShape(20.dp),
        icon = {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(RedAccent.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Filled.Flag, contentDescription = null, tint = RedAccent, modifier = Modifier.size(24.dp))
            }
        },
        title = {
            Column {
                Text(text = "Report Player", color = WhiteText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "Reporting: $playerName", color = WhiteSecondary, fontSize = 12.sp)
            }
        },
        text = {
            Column {
                Text(text = "REASON", color = WhiteSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.5.sp)
                Spacer(modifier = Modifier.height(8.dp))
                ReportReason.values().forEach { reason ->
                    ReasonOption(
                        label = reason.label,
                        selected = selectedReason == reason,
                        onClick = { selectedReason = reason; errorMessage = null }
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "DESCRIPTION (OPTIONAL)", color = WhiteSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.5.sp)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text(text = "Add more details...", color = GreyHint, fontSize = 13.sp) },
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.1f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                        focusedTextColor = WhiteText,
                        unfocusedTextColor = WhiteText,
                        cursorColor = WhiteText,
                        focusedBorderColor = OrangeWarning,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    textStyle = TextStyle(color = WhiteText, fontSize = 13.sp)
                )
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = errorMessage!!, color = ErrorRed, fontSize = 11.sp)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (selectedReason == null) {
                        errorMessage = "Please select a reason"
                    } else {
                        onSubmit(selectedReason!!, description)
                    }
                },
                modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(RedAccent).padding(horizontal = 12.dp)
            ) {
                Text(text = "SUBMIT REPORT", color = WhiteText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "CANCEL", color = WhiteSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }
    )
}

@Composable
private fun ReasonOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) RedAccent.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f))
            .border(1.dp, if (selected) RedAccent else Color.Transparent, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(18.dp).clip(CircleShape)
                .background(if (selected) RedAccent else Color.Transparent)
                .border(2.dp, if (selected) RedAccent else WhiteSecondary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(WhiteText))
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = label, color = WhiteText, fontSize = 13.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
    }
}