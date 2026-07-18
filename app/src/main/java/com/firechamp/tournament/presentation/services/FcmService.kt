package com.firechamp.tournament.presentation.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.firechamp.tournament.MainActivity
import com.firechamp.tournament.R
import com.firechamp.tournament.data.model.notification.Notification
import com.firechamp.tournament.data.model.notification.NotificationType
import com.firechamp.tournament.data.repository.NotificationRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * FCM Service - Firebase Cloud Messaging notifications receive karta hai.
 *
 * Real me ye Firebase se push messages receive karega aur:
 * 1. Local notification show karega (system tray)
 * 2. App ka NotificationRepository me add karega (in-app center ke liye)
 *
 * Note: Task 14 me Firebase setup ke baad properly kaam karega.
 * Abhi structure ready hai, actual FCM activate hoga jab google-services.json add hoga.
 */
@AndroidEntryPoint
class FcmService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationRepository: NotificationRepository

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        notificationRepository.setFcmToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val data = remoteMessage.data
        val title = remoteMessage.notification?.title ?: data["title"] ?: "Fire Champ"
        val body = remoteMessage.notification?.body ?: data["body"] ?: ""
        val type = data["type"] ?: "GENERAL_ANNOUNCEMENT"

        val notification = Notification(
            id = remoteMessage.messageId ?: System.currentTimeMillis().toString(),
            type = parseType(type),
            title = title,
            body = body
        )
        notificationRepository.addNotification(notification)
        showSystemNotification(title, body)
    }

    private fun parseType(type: String) = when (type) {
        "MATCH_REMINDER" -> NotificationType.MATCH_REMINDER
        "ROOM_ID_RELEASED" -> NotificationType.ROOM_ID_RELEASED
        "RESULT_DECLARED" -> NotificationType.RESULT_DECLARED
        "WITHDRAWAL_UPDATE" -> NotificationType.WITHDRAWAL_UPDATE
        else -> NotificationType.GENERAL_ANNOUNCEMENT
    }

    private fun showSystemNotification(title: String, body: String) {
        val channelId = "fire_champ_default"
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Fire Champ Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Match reminders, results, withdrawals" }
            nm.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TOP }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        nm.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}