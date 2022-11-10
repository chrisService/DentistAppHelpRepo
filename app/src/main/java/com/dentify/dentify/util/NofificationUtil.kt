package com.dentify.dentify.util

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.dentify.dentify.R
import com.dentify.dentify.ui.MainActivity

object NofificationUtil {

    fun sendNotification(context: Context) {
        val title: String = context.getString(R.string.notification_title_ongoing)
        val message: String = context.getString(R.string.notification_message_ongoing)
        var notifyIntent: Intent? = null

        try {
            notifyIntent = Intent(context, MainActivity::class.java)
            notify(title, message, notifyIntent, context)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun notify(mTitle: String, mMessage: String, intent: Intent, context: Context) {

        val contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val notificationManager = context.getSystemService(FirebaseMessagingService.NOTIFICATION_SERVICE) as NotificationManager
        val NOTIFICATION_CHANNEL_ID = "dentify-notifications-in-video"
        @SuppressLint("WrongConstant") val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Dentify Notifications in video",
            NotificationManager.IMPORTANCE_MAX
        )
        notificationChannel.description = "notification chanel for Dentify app in Video"
        notificationChannel.setShowBadge(true)
        notificationManager.createNotificationChannel(notificationChannel)
        val notificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        notificationBuilder
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(mTitle)
            .setContentText(mMessage)
            .setContentInfo("info")
            .setContentIntent(contentIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(mMessage))
        notificationManager.notify(1, notificationBuilder.build())
    }
}