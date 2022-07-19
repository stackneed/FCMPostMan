package com.firebase.test

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class FirebaseNotificationReceiver : FirebaseMessagingService() {
    private var manager: NotificationManager? = null
    val TAG: String = "MessagesController"
    var promotionalBitmap: Bitmap? = null

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        Log.e(TAG, "Message Notification Body: " +p0.data)
        // Check if message contains a notification payload.
        val message = p0.data["short_desc"]
        val appURL = p0.data["app_url"]
        val title = p0.data["title"]
        //imageUri will contain URL of the image to be displayed with Notification
        val promotionalURI = p0.data["feature"]
        //If the key AnotherActivity has  value as True then when the user taps on notification, in the app AnotherActivity will be opened.
        //If the key AnotherActivity has  value as False then when the user taps on notification, in the app MainActivity will be opened.
        //  String TrueOrFlase = remoteMessage.getData().get("AnotherActivity");
        //To get a Bitmap image from the URL received
        promotionalBitmap = getImage(promotionalURI)
        notificationReceived(title, message,appURL )
    }

    private fun notificationReceived(title: String?, messageBody: String?, appURL: String?) {
        val notiID = 0 // ID of notification
        val id = "1205"
        val intent: Intent
        val pendingIntent: PendingIntent
        val builder: NotificationCompat.Builder
        if (manager == null) {
            manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            var mChannel = manager!!.getNotificationChannel(id)
            if (mChannel == null) {
                mChannel = NotificationChannel(id, title, importance)
                mChannel.enableVibration(true)
                mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                manager!!.createNotificationChannel(mChannel)
            }
            builder = NotificationCompat.Builder(applicationContext, id)
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            intent = Intent(this, MainActivity::class.java)
            intent.putExtra("APP_LINK", appURL)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            pendingIntent = PendingIntent.getActivity(
                this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )

            // Get the PendingIntent containing the entire back stack
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

            val bm = BitmapFactory.decodeResource(
                resources, R.mipmap.ic_launcher
            )
            builder.setContentTitle(title) // required
                .setSmallIcon(R.mipmap.ic_launcher) // required
                .setContentText(messageBody) // required
                .setDefaults(Notification.DEFAULT_ALL)
                .setStyle(
                    NotificationCompat.BigPictureStyle()
                        .bigPicture(promotionalBitmap)
                )
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSound(defaultSoundUri)
                .setTicker(title)
                .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
        } else {
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            builder = NotificationCompat.Builder(applicationContext, id)
            intent = Intent(this, MainActivity::class.java)
            intent.putExtra("APP_LINK", appURL)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            pendingIntent = PendingIntent.getActivity(
                this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )


            builder.setContentTitle(title) // required
                .setSmallIcon(R.mipmap.ic_launcher) // required
                .setContentText(messageBody) // required
                .setDefaults(Notification.DEFAULT_ALL)
                .setStyle(
                    NotificationCompat.BigPictureStyle()
                        .bigPicture(promotionalBitmap)
                )
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSound(defaultSoundUri)
                .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)).priority =
                Notification.PRIORITY_HIGH
        }
        val notification = builder.build()
        manager!!.notify(notiID, notification)
    }

    private fun getImage(imageUrl: String?): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}