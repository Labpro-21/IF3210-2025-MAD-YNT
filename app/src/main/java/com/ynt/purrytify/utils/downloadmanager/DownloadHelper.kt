package com.ynt.purrytify.utils.downloadmanager

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.ynt.purrytify.models.OnlineSong
import com.ynt.purrytify.models.Song
import com.ynt.purrytify.ui.screen.topchartscreen.TopChartViewModel
import java.io.File

class DownloadHelper(private val context: Context) {
    private var downloadId: Long = -1L
    private var onCompleteListener: (() -> Unit)? = null
    private val downloadListeners = mutableMapOf<Long, () -> Unit>()

    private val downloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
            val listener = downloadListeners[id]
            listener?.invoke()
            downloadListeners.remove(id)
            if (downloadListeners.isEmpty()) {
                unregister()
            }
        }
    }

    fun startDownload(song : OnlineSong, viewModel: TopChartViewModel, user : String) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val outputDir = File(context.getExternalFilesDir("songs"), "${song.title}.mp3")
        val destinationUri = Uri.fromFile(outputDir)

        val request = DownloadManager.Request(song.url.toUri())
            .setTitle("Downloading ${song.title}.mp3")
            .setDescription("Please wait...")
            .setMimeType("audio/mpeg")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
            .setDestinationUri(destinationUri)
//            .setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, "$fileName.mp3")

        context.registerReceiver(downloadReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_EXPORTED)
        downloadId = downloadManager.enqueue(request)

        downloadListeners[downloadId] = {
            val savedSong = Song(
                title = song.title,
                artist = song.artist,
                owner = user,
                image = song.artwork,
                audio = destinationUri.toString(),
                duration = parseDurationToSeconds(song.duration)
            )
            viewModel.insert(savedSong)
            Log.d("SONG DOWNLOAD", song.title)
        }
    }

    private fun unregister() {
        try {
            context.unregisterReceiver(downloadReceiver)
        } catch (e: IllegalArgumentException) {
        }
    }
}
