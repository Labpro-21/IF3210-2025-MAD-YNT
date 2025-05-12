package com.ynt.purrytify.utils.mediaplayer

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.runtime.MutableState
import com.ynt.purrytify.PlayerState

class SongPlayer {
    private var mediaPlayer: MediaPlayer? = null
    fun playAudioFromUri(context: Context, uri: Uri, isPlaying: MutableState<PlayerState>) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, uri)
                prepare()
                start()
                isPlaying.value = PlayerState.PLAYING
                setOnCompletionListener {
                    it.release()
                    isPlaying.value = PlayerState.STOPPED
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun resumeAudio(currentSongPosition: MutableState<Int>) {
        mediaPlayer?.seekTo(currentSongPosition.value)
        mediaPlayer?.start()
    }

    fun seekAudio(currentSongPosition: MutableState<Int>){
        mediaPlayer?.seekTo(currentSongPosition.value)
    }

    fun pauseAudio(currentSongPosition: MutableState<Int>) {
        currentSongPosition.value = mediaPlayer?.currentPosition!!
        mediaPlayer?.pause()
    }
    fun stopAudio(isPlaying: MutableState<PlayerState>) {
        mediaPlayer?.let {
            if (it.isPlaying || isPlaying.value == PlayerState.PLAYING || isPlaying.value == PlayerState.PAUSED) {
                it.stop()
                it.reset()
                isPlaying.value = PlayerState.STOPPED
            }
        }
    }

    fun getCurrentPosition(): Int{
        return mediaPlayer?.currentPosition ?: 0;
    }
}

