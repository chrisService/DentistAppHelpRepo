package com.dentify.dentify.firebaseCloudMessaging

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.dentify.dentify.R
import com.dentify.dentify.`interface`.SuccessListener
import com.dentify.dentify.ui.MainActivity
import com.dentify.dentify.util.Constants

class DentifyFCMService: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        sendNotification(message)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun notify(mTitle: String, mMessage: String
                       , intent: Intent
    ) {

        val contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val NOTIFICATION_CHANNEL_ID = "dentify-notifications"
        @SuppressLint("WrongConstant") val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Dentify Notifications",
            NotificationManager.IMPORTANCE_MAX
        )
        notificationChannel.description = "notification chanel for Dentify app"
        notificationChannel.setShowBadge(true)
        notificationManager.createNotificationChannel(notificationChannel)
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
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

    private fun sendNotification(remoteMessage: RemoteMessage) {
        var notificationType: String = ""
        var title: String = ""
        var message: String = ""
        var roomName: String = ""
        var notifyIntent: Intent? = null

        try {
            val data = remoteMessage.data
            notificationType = data.get("notification_type")!!
            roomName = data.get("appointment_id")!!
            notifyIntent = Intent(this, MainActivity::class.java)

            if (notificationType.equals(Constants.NOTIFICATION_TYPE_ROOM_CREATED, true)){
                title = getString(R.string.notification_title)
                message = getString(R.string.notification_message)
                notifyIntent.data = Uri.parse("https://dentify.app.com/patients/video?roomid=$roomName")
            }else if(notificationType.equals(Constants.NOTIFICATION_TYPE_CLINICIAN_JOINED, true)){
                title = getString(R.string.notification_title)
                message = getString(R.string.notification_message_joined)
                notifyIntent.data = Uri.parse("https://dentify.app.com/patients/video?roomid=$roomName")
            }else if(notificationType.equals(Constants.NOTIFICATION_TYPE_APPOINTMENT_REMINDER, true)){
                title = getString(R.string.notification_title)
                message = getString(R.string.appointment_soon)
                notifyIntent.data = Uri.parse("https://dentify.app.com/patients/appointment?appointmentid=$roomName")
            }else if(notificationType.equals(Constants.NOTIFICATION_TYPE_APPOINTMENT_REQUEST, true)){
                title = getString(R.string.notification_title)
                message = getString(R.string.new_appointemtn_request)
                notifyIntent.data = Uri.parse("https://dentify.app.com/patients/appointment?requestid=$roomName")

                val intent = Intent()
                intent.setAction("request.action.intent")
                intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                sendBroadcast(intent)
            }

            notify(title, message
                , notifyIntent
            )
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
}