package com.firechamp.tournament.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.firechamp.tournament.presentation.theme.ErrorRed
import com.firechamp.tournament.presentation.theme.FireCardBg
import com.firechamp.tournament.presentation.theme.FireCardBorder
import com.firechamp.tournament.presentation.theme.GoldFire
import com.firechamp.tournament.presentation.theme.GreyHint
import com.firechamp.tournament.presentation.theme.OrangeFire
import com.firechamp.tournament.presentation.theme.WhiteText

/**
 * Fire theme shared components - naya orange/gold design system.
 *
 * Logo ke fire colors (#FF6A00 → #FFB300) pe based:
 *  - FireButton: gradient CTA (REGISTER, LOGIN, SEND OTP...)
 *  - FireOutlineButton: gold outline secondary CTA
 *  - FireTextField: dark input with gold focus border
 */

/** Fire gradient brush - saare CTAs me use hota hai */
val FireGradient = Brush.horizontalGradient(listOf(OrangeFire, GoldFire))

/**
 * Primary fire-gradient button (mockup ke "btn-fire" jaisa).
 * Black bold text on orange→gold gradient.
 */
@Composable
fun FireButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    val alpha = if (enabled && !isLoading) 1f else 0.5f
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .alpha(alpha)
            .clip(RoundedCornerShape(14.dp))
            .background(FireGradient)
            .clickable(enabled = enabled && !isLoading) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.Black,
                    strokeWidth = 2.dp
                )
                Text(
                    text = "  $text",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.2.sp,
                    color = Color.Black
                )
            } else {
                Text(
                    text = text,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.2.sp,
                    color = Color.Black
                )
            }
        }
    }
}

/**
 * Secondary gold-outline button (mockup ke "btn-outline" jaisa).
 */
@Composable
fun FireOutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(width = 2.dp, color = GoldFire, shape = RoundedCornerShape(14.dp))
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.2.sp,
            color = GoldFire
        )
    }
}

/**
 * Dark rounded text field with gold focus border (mockup ke ".field" jaisa).
 * PillTextField (white) ka fire-theme replacement.
 */
@Composable
fun FireTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    label: String? = null,
    leadingIcon: ImageVector? = null,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    readOnly: Boolean = false,
    trailingContent: (@Composable () -> Unit)? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        if (label != null) {
            Text(
                text = label,
                color = GreyHint,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 4.dp, bottom = 5.dp)
            )
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(text = placeholder, color = GreyHint, fontSize = 15.sp)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = FireCardBg,
                unfocusedContainerColor = FireCardBg,
                disabledContainerColor = Color(0xFF0E0E0E),
                focusedTextColor = WhiteText,
                unfocusedTextColor = WhiteText,
                disabledTextColor = GreyHint,
                focusedBorderColor = if (isError) ErrorRed else GoldFire,
                unfocusedBorderColor = if (isError) ErrorRed else FireCardBorder,
                disabledBorderColor = FireCardBorder,
                cursorColor = GoldFire
            ),
            textStyle = TextStyle(
                color = WhiteText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal
            ),
            leadingIcon = leadingIcon?.let {
                { Icon(imageVector = it, contentDescription = null, tint = GreyHint, modifier = Modifier.size(20.dp)) }
            },
            singleLine = singleLine,
            enabled = enabled,
            readOnly = readOnly,
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            trailingIcon = when {
                isPassword -> {
                    {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = null, tint = GreyHint)
                        }
                    }
                }
                trailingContent != null -> trailingContent
                else -> null
            }
        )
        if (isError && !errorMessage.isNullOrEmpty()) {
            Text(
                text = errorMessage,
                color = ErrorRed,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}
