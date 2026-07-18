package com.firechamp.tournament.presentation.screens.wallet

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.firechamp.tournament.presentation.theme.*
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject

/**
 * Razorpay Native SDK Checkout Screen.
 */
@Composable
fun RazorpayCheckoutScreen(
    orderId: String,
    key: String,
    amountInPaise: Int,
    userEmail: String = "user@firechamp.app",
    userContact: String = "919522079569",
    onBack: () -> Unit,
    onSuccess: (paymentId: String, orderId: String) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as Activity

    LaunchedEffect(Unit) {
        val checkout = Checkout()
        checkout.setKeyID(key)

        val options = JSONObject()
        options.put("name", "Fire Champ")
        options.put("description", "Wallet Top-up")
        options.put("order_id", orderId)
        options.put("currency", "INR")
        options.put("amount", amountInPaise)
        
        val prefill = JSONObject()
        prefill.put("email", userEmail)
        prefill.put("contact", userContact)
        options.put("prefill", prefill)
        
        val theme = JSONObject()
        theme.put("color", "#6A0DFF")
        options.put("theme", theme)

        // Native SDK Listener
        val listener = object : PaymentResultListener {
            override fun onPaymentSuccess(paymentId: String?) {
                onSuccess(paymentId ?: "", orderId)
            }

            override fun onPaymentError(code: Int, response: String?) {
                Log.e("Razorpay", "Error $code: $response")
                onCancel()
            }
        }

        checkout.open(activity, options)
    }

    BackHandler { onBack() }

    Column(
        modifier = Modifier.fillMaxSize().background(BlackBackground),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Opening Payment Gateway...", color = WhiteText)
    }
}
