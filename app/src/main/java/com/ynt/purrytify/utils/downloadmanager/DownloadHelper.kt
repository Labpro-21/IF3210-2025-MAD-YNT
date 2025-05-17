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

    private val downloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("DownloadReceiver", "Here")
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
            Log.d("DownloadReceiver", "Received download complete for id=$id, our id=$downloadId")
            if (id == downloadId) {
                Log.d("DownloadReceiver", "Download completed, invoking listener.")
                onCompleteListener?.invoke()
            }
        }
    }

    fun startDownload(song : OnlineSong, viewModel: TopChartViewModel, user : String) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val outputDir = File(context.getExternalFilesDir("songs"), "${song.title}.mp3")
        val destinationUri = Uri.fromFile(outputDir)
        onCompleteListener = {
            val savedSong = Song(
                title = song.title,
                artist = song.artist,
                owner = user,
                image = song.artwork,
                audio = destinationUri.toString(),
                duration = parseDurationToSeconds(song.duration)
            )
            viewModel.insert(savedSong)

            unregister()
        }

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
    }

    private fun unregister() {
        try {
            context.unregisterReceiver(downloadReceiver)
        } catch (e: IllegalArgumentException) {
            Log.d("Receiver", e.message.toString())
        }
    }

    private fun parseDurationToSeconds(duration: String): Int {
        val parts = duration.split(":")
        val minutes = parts[0].toIntOrNull() ?: 0
        val seconds = parts.getOrNull(1)?.toIntOrNull() ?: 0
        val result = (minutes * 60 + seconds)*1000
        return result
    }

}
