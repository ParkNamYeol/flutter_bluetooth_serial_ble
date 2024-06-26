package io.github.edufolly.flutterbluetoothserial.bg

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

object NotificationUtil {

    private const val channelId = "ble_background_notification_channel"
    private const val channelName = "ble_background_notification_channel"

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun hasPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(context: Context) {
        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        NotificationManagerCompat.from(context).createNotificationChannel(channel)
    }

    fun createNotification(context: Context, title: String, content: String): Notification {
        val className = "${context.packageName}.MainActivity"
        val intent = Intent().apply {
            component = ComponentName(context.packageName, className)
        }
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val cancelIntent = PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, StopReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val cancelAction = NotificationCompat.Action(null, "중지", cancelIntent)
        return NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(content)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .addAction(cancelAction)
            .build()
    }

    fun showNotification(context: Context, notification: Notification, id: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(context)
        }

        NotificationManagerCompat.from(context).notify(id, notification)
    }
}