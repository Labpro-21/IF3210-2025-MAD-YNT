package com.ynt.purrytify.utils.mediaplayer

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.ynt.purrytify.models.Song

class PlaybackService : MediaSessionService() {

    private inner class MediaSessionCallback : MediaSession.Callback {

        @OptIn(UnstableApi::class)
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {
            val sessionCommands = MediaSession.ConnectionResult.DEFAULT_SESSION_COMMANDS.buildUpon()
                .add(SessionCommand("sync_playlist", Bundle.EMPTY))
                .add(SessionCommand("set_output_device",Bundle.EMPTY))
                .add(SessionCommand("next", Bundle.EMPTY))
                .add(SessionCommand("previous", Bundle.EMPTY))
                .add(SessionCommand("play_by_id",Bundle.EMPTY))
                .build()
            return MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                .setAvailableSessionCommands(sessionCommands)
                .build()
        }

        @OptIn(UnstableApi::class)
        override fun onCustomCommand(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            customCommand: SessionCommand,
            args: Bundle
        ): ListenableFuture<SessionResult> {
            return when (customCommand.customAction) {
                "sync_playlist" -> {
                    val songs = args.getParcelableArrayList<Song>("songs")
                    val sourceName = args.getString("source")
                    if (songs != null) {
                        syncPlaylist(songs, sourceName!!)
                    }
                    Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
                }
                "set_output_device" -> {
                    val deviceId = args.getInt("device_id", -1)
                    val deviceType = args.getInt("device_type", -1)
                    val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                    val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
                    val targetDevice = devices.firstOrNull {
                        it.id == deviceId && it.type == deviceType
                    }
                    if (targetDevice != null) {
                        exoPlayer.setPreferredAudioDevice(targetDevice)
                    }
                    Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
                }
                "next"->{
                    next()
                    Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
                }
                "previous"->{
                    previous()
                    Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
                }
                "play_by_id" ->{
                    val id = args.getString("song_id")
                    playSongById(id!!)
                    Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
                }
                else -> {
                    super.onCustomCommand(session, controller, customCommand, args)
                }
            }
        }
    }


    private lateinit var exoPlayer: ExoPlayer
    private lateinit var mediaSession: MediaSession

    private lateinit var sourceName : String

    private var currentPlayingIndex = -1
    private var currentPlayingSongId: String? = null

    override fun onCreate() {
        super.onCreate()
        exoPlayer = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(this, exoPlayer)
            .setCallback(MediaSessionCallback())
            .build()

        sourceName = ""

        exoPlayer.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                currentPlayingSongId = mediaItem?.mediaId
                currentPlayingIndex = exoPlayer.currentMediaItemIndex
            }
        })
        exoPlayer.prepare()
    }

    fun syncPlaylist(songs: List<Song>,source: String) {
        if(sourceName != source){
            exoPlayer.clearMediaItems()
            sourceName = source
        }
        val currentMediaItems = (0 until exoPlayer.mediaItemCount).map { exoPlayer.getMediaItemAt(it) }
        val currentMediaIds = currentMediaItems.map { it.mediaId }.toSet()
        for ((index, song) in songs.withIndex()) {
            val existingIndex = currentMediaItems.indexOfFirst { it.mediaId == song.id.toString() }
            val newMediaItem = MediaItem.Builder()
                .setMediaId(song.id.toString())
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(song.title)
                        .setArtist(song.artist)
                        .setArtworkUri(song.image?.toUri())
                        .setAlbumTitle("")
                        .build()
                )
                .setUri(song.audio?.toUri())
                .build()

            if (existingIndex == -1) {
                exoPlayer.addMediaItem(index, newMediaItem)
                if (currentPlayingIndex >= index) currentPlayingIndex++
            } else {
                val existing = currentMediaItems[existingIndex]
                val existingMetadata = existing.mediaMetadata
                val isMetadataDifferent =
                    existingMetadata.title != song.title ||
                            existingMetadata.artist != song.artist ||
                            existingMetadata.artworkUri != song.image?.toUri()
                if (isMetadataDifferent) {
                    if (currentPlayingSongId == song.id.toString()) {
                        mediaSession.player.replaceMediaItem(currentPlayingIndex,newMediaItem)
                    } else {
                        exoPlayer.removeMediaItem(existingIndex)
                        exoPlayer.addMediaItem(existingIndex, newMediaItem)
                        if (currentPlayingIndex >= existingIndex) currentPlayingIndex++
                    }
                }
            }
        }

        for (i in currentMediaItems.size - 1 downTo 0) {
            val mediaItem = currentMediaItems[i]
            if (!songs.any { it.id.toString() == mediaItem.mediaId }) {
                exoPlayer.removeMediaItem(i)
                if (currentPlayingIndex > i) currentPlayingIndex--
                else if (currentPlayingIndex == i) {
                    currentPlayingIndex = -1
                    currentPlayingSongId = null
                    exoPlayer.stop()
                }
            }
        }
    }

    fun playSongById(songId: String) {
        val index = (0 until exoPlayer.mediaItemCount).indexOfFirst { exoPlayer.getMediaItemAt(it).mediaId == songId }
        if (index != -1) {
            exoPlayer.seekTo(index, 0)
            exoPlayer.play()
            currentPlayingIndex = index
            currentPlayingSongId = songId
        }
    }

    fun next() {
        if (exoPlayer.hasNextMediaItem()) exoPlayer.seekToNext()
        exoPlayer.play()
    }

    fun previous() {
        if (exoPlayer.hasPreviousMediaItem()) exoPlayer.seekToPrevious()
        exoPlayer.play()
    }

    fun pause() = exoPlayer.pause()
    fun play() = exoPlayer.play()

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession {
        return mediaSession
    }


    override fun onDestroy() {
        super.onDestroy()
    }
}
