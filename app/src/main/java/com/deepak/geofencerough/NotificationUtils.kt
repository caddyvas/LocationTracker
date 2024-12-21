package com.deepak.geofencerough

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import java.util.Random

// Notification ID.
private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0

private val channelId: String = "Main_Channel"

fun NotificationManager.sendNotification(
    title: String,
    messageBody: String,
    applicationContext: Context
) {
    // Create the content intent for the notification, which launches
    // this activity
    // TODO: Step 1.11 create intent

    // TODO: Step 1.12 create PendingIntent

    // TODO: Step 2.0 add style

    // TODO: Step 2.2 add snooze action

    // TODO: Step 1.2 get an instance of NotificationCompat.Builder
    // Build the notification
    val builder = NotificationCompat.Builder(
        applicationContext,
        channelId
    )

        // TODO: Step 1.8 use the new 'breakfast' notification channel

        // TODO: Step 1.3 set title, text and icon to builder
        .setSmallIcon(R.drawable.ic_launcher_background)
        .setContentTitle(title)
        .setContentText(messageBody)

    // TODO: Step 1.13 set content intent

    // TODO: Step 2.1 add style to builder

    // TODO: Step 2.3 add snooze action

    // TODO: Step 2.5 set priority

    // TODO: Step 1.4 call notify
    notify(NOTIFICATION_ID, builder.build())
}

@RequiresApi(Build.VERSION_CODES.O)
fun NotificationManager.sendNotification2(context: Context, title: String, activityName: Class<*>?) {
    val intent = Intent(context, activityName)
    val pendingIntent =
        PendingIntent.getActivity(context, 187, intent, PendingIntent.FLAG_IMMUTABLE)
    val notificationChannel: NotificationChannel = NotificationChannel(
        channelId,
        "description", NotificationManager.IMPORTANCE_HIGH
    )
    notificationChannel.enableLights(true)
    notificationChannel.lightColor = Color.GREEN
    notificationChannel.enableVibration(false)
    val manager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    manager.createNotificationChannel(notificationChannel)

    val notification: Notification =
        NotificationCompat.Builder(context, channelId) //                .setContentTitle(title)
            //                .setContentText(body)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(
                NotificationCompat.BigTextStyle().setSummaryText("summary")
                    .setBigContentTitle(title).bigText("")
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        return
    }
    NotificationManagerCompat.from(context).notify(Random().nextInt(), notification)
}