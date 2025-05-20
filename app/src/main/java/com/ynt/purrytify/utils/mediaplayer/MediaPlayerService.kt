package com.ynt.purrytify.utils.mediaplayer

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.ynt.purrytify.CHANNEL_ID
import com.ynt.purrytify.R
import com.ynt.purrytify.models.Song
import android.Manifest.permission.POST_NOTIFICATIONS
import android.media.AudioDeviceInfo
import android.util.Log
import androidx.core.net.toUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.math.log

const val PREV = "prev"
const val NEXT = "next"
const val PLAYPAUSE = "playpause"

class MediaPlayerService : Service() {

    val binder = MediaBinder()
    var mediaPlayer = MediaPlayer()
    var currentSong = MutableStateFlow(Song())
    var songList = mutableListOf<Song>()

    val maxDuration = MutableStateFlow(0f)
    val currentDuration = MutableStateFlow(0f)
    val isPlaying = MutableStateFlow(false)

    private val durationScope = CoroutineScope(Dispatchers.Main)
    private var durationJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
    }

    inner class MediaBinder : Binder() {
        fun getService() = this@MediaPlayerService

        fun setSongList(list: List<Song>?) {
            if (list != null) {
                list.forEach { song ->
                    Log.d("Song Set", song.title.toString())
                }
            }
            try {
                if (list == null) {
                    this@MediaPlayerService.songList = mutableListOf()
                    return
                }
                this@MediaPlayerService.songList = list.toMutableList()
            } catch (e: Exception) {
                this@MediaPlayerService.songList = mutableListOf()
            }
            songList.forEach { song ->
                Log.d("Song List Set", song.title.toString())
            }
        }

        fun setCurrentSong(song: Song) {
            this@MediaPlayerService.currentSong.value = song
            play(song)
        }

        fun currentDuration() = this@MediaPlayerService.currentDuration
        fun isPlaying() = this@MediaPlayerService.isPlaying
        fun getCurrentSong() = this@MediaPlayerService.currentSong

        fun setPreferredOutputDevice(device: AudioDeviceInfo) {
            this@MediaPlayerService.setPreferredOutputDevice(device)
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            PREV -> prev()
            NEXT -> next()
            PLAYPAUSE -> playPause()
            else -> {
            }
        }
        return START_STICKY
    }

    fun play(song: Song) {
        val index = songList.indexOfFirst {
            Log.d("INDEX IS", it.id.toString())
            it.id == currentSong.value.id
        }
        Log.d("INDEX IS","INDEX IS ${currentSong.value}")

        Log.d("INDEX IS","INDEX IS $index")
        Log.d("INDEX IS","INDEX IS $songList")
        try {
            mediaPlayer.reset()
            val uri = song.audio?.toUri() ?: "".toUri()
            mediaPlayer.setDataSource(this, uri)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                mediaPlayer.start()
                sendNotification(song)
                updateDuration()
            }
            mediaPlayer.setOnCompletionListener {
                next()
            }
            mediaPlayer.setOnErrorListener { _, _, _ ->
                next()
                true
            }
        } catch (e: Exception) {
            Log.d("Play Exception", e.message.toString())
        }
    }

    fun updateDuration() {
        durationJob?.cancel()
        durationJob = durationScope.launch {
            try {
                maxDuration.value = mediaPlayer.duration.toFloat()
                while (mediaPlayer.isPlaying) {
                    currentDuration.value = mediaPlayer.currentPosition.toFloat()
                    delay(1000)
                }
            } catch (e: Exception) {
            }
        }
    }

    fun playPause() {
        try {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            } else {
                mediaPlayer.start()
                updateDuration()
            }
            sendNotification(currentSong.value)
            isPlaying.value = mediaPlayer.isPlaying
        } catch (e: Exception) {
        }
    }

    fun seekTo(position: Float) {
        try {
            mediaPlayer.seekTo(position.toInt())
            currentDuration.value = position
        } catch (e: Exception) {
        }
    }

    fun prev() {
        try {
            val index = songList.indexOfFirst { it.id == currentSong.value.id }
            currentSong.value = when {
                index > 0 -> songList[index - 1]
                songList.isNotEmpty() -> songList.last()
                else -> currentSong.value
            }
            play(currentSong.value)
        } catch (e: Exception) {
        }
    }

    fun next() {
        try {
            Log.d("Lengtho", songList.size.toString())
            val index = songList.indexOfFirst { it.id == currentSong.value.id }
            currentSong.value = when {
                index >= 0 && index < songList.size - 1 -> songList[index + 1]
                songList.isNotEmpty() -> songList.first()
                else -> currentSong.value
            }
            play(currentSong.value)
        } catch (e: Exception) {
        }
    }

    private fun sendNotification(song: Song) {
        try {
            isPlaying.value = mediaPlayer.isPlaying
            val style = androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0, 1, 2)

            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setStyle(style)
                .setContentTitle(song.title ?: "Unknown Title")
                .setContentText(song.artist ?: "Unknown Artist")
                .addAction(R.drawable.baseline_skip_previous_24, "prev", prevPendingIntent())
                .addAction(
                    if (mediaPlayer.isPlaying) R.drawable.baseline_pause_circle_24 else R.drawable.baseline_play_circle_24,
                    "playpause",
                    playPausePendingIntent()
                )
                .addAction(R.drawable.baseline_skip_next_24, "next", nextPendingIntent())
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.logo))
                .setVibrate(null)
                .setOngoing(true)
                .build()

            if (ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                startForeground(1, notification)
            }
        } catch (e: Exception) {
        }
    }

    private fun prevPendingIntent(): PendingIntent {
        val intent = Intent(this, MediaPlayerService::class.java).apply {
            action = PREV
        }
        return PendingIntent.getService(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun nextPendingIntent(): PendingIntent {
        val intent = Intent(this, MediaPlayerService::class.java).apply {
            action = NEXT
        }
        return PendingIntent.getService(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun playPausePendingIntent(): PendingIntent {
        val intent = Intent(this, MediaPlayerService::class.java).apply {
            action = PLAYPAUSE
        }
        return PendingIntent.getService(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun onDestroy() {
        try {
            durationJob?.cancel()
            mediaPlayer.release()
        } catch (e: Exception) {
        }
        super.onDestroy()
    }


    fun previous() {
        prev()
    }

    private var preferredDevice: AudioDeviceInfo? = null

    fun setPreferredOutputDevice(device: AudioDeviceInfo) {
        preferredDevice = device
        mediaPlayer?.setPreferredDevice(device)
    }
}