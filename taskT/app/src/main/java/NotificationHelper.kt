package com.r_mit.taskt

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class NotificationHelper(context: Context) {
    private val channelId = "TrackerNotificationChannel"
    private val channelName = "Tracker Notifications"
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications to remind you to fill the tracker"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun getChannelId(): String {
        return channelId
    }
}
