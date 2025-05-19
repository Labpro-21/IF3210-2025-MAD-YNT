package com.ynt.purrytify

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.ynt.purrytify.database.SongRepository
import com.ynt.purrytify.utils.mediaplayer.SongRepositoryProvider

const val CHANNEL_ID = "channel_id"
const val CHANNEL_NAME = "channel_name"

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SongRepositoryProvider.repository = SongRepository(this)

        val channel = NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW).apply {
            enableVibration(false)
            vibrationPattern = longArrayOf(0)
        }
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}