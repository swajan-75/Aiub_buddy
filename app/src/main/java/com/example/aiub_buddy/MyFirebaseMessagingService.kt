package com.example.aiub_buddy


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM", "From: ${remoteMessage.from}")

        val title = remoteMessage.notification?.title ?: "New Notice"
        val body = remoteMessage.notification?.body ?: ""
        val link = remoteMessage.data["link"] ?: ""
//        android.os.Handler(android.os.Looper.getMainLooper()).post {
//            Toast.makeText(applicationContext, "Title: $title\nBody: $body\nLink: $link", Toast.LENGTH_LONG).show()
//        }
        showNotification(title, body, link)
    }

    override fun onNewToken(token: String) {
        Log.d("FCM", "Refreshed token: $token")
        // Optional: send token to backend if you want device-specific messages
    }

//    private fun showNotification(title: String, body: String, link: String) {
//        val channelId = "notice_channel"
//        val notificationManager =
//            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                channelId, "AIUB Notices", NotificationManager.IMPORTANCE_HIGH
//            )
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        val intent = Intent(this, MainActivity::class.java)
//        intent.putExtra("notice_link", link)
//        val pendingIntent = PendingIntent.getActivity(
//            this, 0, intent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//
//        val notification = NotificationCompat.Builder(this, channelId)
//            .setContentTitle(title)
//            .setContentText(body)
//            .setSmallIcon(R.drawable.ic_notification)
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(true)
//            .build()
//
//        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
//    }
private fun showNotification(title: String, body: String, link: String) {
    val channelId = "notice_channel"
    val notificationManager =
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Create notification channel for Android O and above
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "AIUB Notices",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel for AIUB notices"
        }
        notificationManager.createNotificationChannel(channel)
    }

    // Open MainActivity when notification is clicked
    val intent = Intent(this, MainActivity::class.java).apply {
        putExtra("notice_link", link)
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
    }

    val pendingIntent = PendingIntent.getActivity(
        this, 0, intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(this, channelId)
        .setContentTitle(title)
        .setContentText(body)
        .setSmallIcon(R.drawable.aiub_ic) // Make sure you have this icon
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH) // For heads-up notifications
        .build()

    notificationManager.notify(System.currentTimeMillis().toInt(), notification)
}
}
