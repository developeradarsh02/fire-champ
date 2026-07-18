package com.firechamp.tournament

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.firechamp.tournament.data.repository.NotificationRepository
import com.firechamp.tournament.presentation.navigation.FireChampNavGraph
import com.firechamp.tournament.presentation.theme.BlackBackground
import com.firechamp.tournament.presentation.theme.FireChampTheme
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Main Activity - single activity pattern.
 * Saare screens Compose Navigation se handle hote hain.
 *
 * FCM setup (Task 14):
 *  - Android 13+ pe POST_NOTIFICATIONS runtime permission request
 *  - FirebaseMessaging.getInstance().token se device token fetch
 *  - Token Firestore me users/{uid}.fcmToken pe save (NotificationRepository)
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var notificationRepository: NotificationRepository

    // IMPORTANT: registerForActivityResult must be called BEFORE onCreate / as field
    // (Android requirement - activity result API contract)
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        android.util.Log.d("FCM", "Notification permission granted: $granted")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermissionIfNeeded()
        fetchAndRegisterFcmToken()
        setContent {
            FireChampTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BlackBackground),
                    color = BlackBackground
                ) {
                    FireChampNavGraph()
                }
            }
        }
    }

    /**
     * Android 13+ me POST_NOTIFICATIONS runtime permission mandatory hai.
     * Bina iske notification show nahi hota (FCM messages silently drop).
     * Wrapped in try-catch to prevent splash hang on any edge case.
     */
    private fun requestNotificationPermissionIfNeeded() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val granted = ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
                if (!granted) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("FCM", "Permission request failed", e)
        }
    }

    /**
     * FCM token fetch karke NotificationRepository ke through Firestore me save.
     * Wrapped in try-catch - FCM failure should NOT block app launch.
     */
    private fun fetchAndRegisterFcmToken() {
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    android.util.Log.w("FCM", "Fetching FCM token failed", task.exception)
                    return@addOnCompleteListener
                }
                val token = task.result
                android.util.Log.d("FCM", "Device token: $token")
                notificationRepository.setFcmToken(token)
                // Sync to Firestore if user already logged in
                CoroutineScope(Dispatchers.IO).launch {
                    notificationRepository.syncFcmTokenToFirestore(token)
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("FCM", "FCM token fetch threw exception", e)
        }
    }
}
