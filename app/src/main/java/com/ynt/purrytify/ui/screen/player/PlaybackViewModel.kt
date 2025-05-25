package com.ynt.purrytify.ui.screen.player

import android.app.Application
import android.content.ComponentName
import android.media.AudioDeviceInfo
import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.ynt.purrytify.database.SongRepository
import com.ynt.purrytify.models.Song
import com.ynt.purrytify.models.SongStat
import com.ynt.purrytify.utils.auth.SessionManager
import com.ynt.purrytify.utils.mediaplayer.PlaybackService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class PlaybackViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    private var mediaController: MediaController? = null
    var isPlaying by mutableStateOf(false)
    var currentMediaTitle by mutableStateOf<String?>(null)
    var currentUser by mutableStateOf<String?>(null)
    var currentMediaId by mutableIntStateOf(-1)
    var currentPosition by mutableLongStateOf(0L)
    var duration by mutableLongStateOf(0L)
    private val _selectedDeviceId = MutableStateFlow<Int?>(null)
    val selectedDeviceId: StateFlow<Int?> = _selectedDeviceId.asStateFlow()
    var localSongList by mutableStateOf(listOf<Song>())
    var onlineSongMap by mutableStateOf(mapOf<String, List<Song>>())
    var songList by mutableStateOf(listOf<Song>())
    var currentSong by mutableStateOf<Song?>(null)
    var sourceName by mutableStateOf<String?>(null)
    private var positionUpdateJob: Job? = null
    lateinit var mSongRepository: SongRepository
    private var playerListener: Player.Listener? = null
    private var isConnecting = false

    @UnstableApi
    fun connect() {
        if (isConnecting) {
            Log.d("PlaybackViewModel","It is connecting")
            return
        }
        if (mediaController != null) {
            Log.d("PlaybackViewModel","Media controller exist")
            return
        }
        isConnecting = true
        Log.d("PlaybackViewModel","Connecting I guess")
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        mSongRepository = SongRepository(getApplication())

        controllerFuture.addListener({
            try {
                mediaController = controllerFuture.get()

                playerListener?.let { mediaController?.removeListener(it) }

                val player = mediaController!!

                playerListener = object : Player.Listener {
                    override fun onIsPlayingChanged(isPlayingNow: Boolean) {
                        isPlaying = isPlayingNow
                        if (isPlaying) {
                            startPositionUpdates()
                        } else {
                            stopPositionUpdates()
                        }
                    }

                    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                        currentMediaTitle = mediaItem?.mediaMetadata?.title?.toString()
                        currentMediaId = mediaItem?.mediaId?.toInt() ?: -1
                        currentSong = songList.firstOrNull { it.id == currentMediaId }
                    }

                    override fun onPlaybackStateChanged(state: Int) {
                        duration = player.duration
                    }
                }

                player.addListener(playerListener!!)
                isPlaying = player.isPlaying
                currentPosition = player.currentPosition
                duration = player.duration
                currentMediaTitle = player.currentMediaItem?.mediaMetadata?.title?.toString()
                currentMediaId = player.currentMediaItem?.mediaId?.toInt() ?: -1
                currentSong = songList.firstOrNull { it.id == currentMediaId }


                val resultFuture = mediaController?.sendCustomCommand(
                    SessionCommand("get_current_state", Bundle.EMPTY),
                    Bundle.EMPTY
                )

                resultFuture?.addListener({
                    val result = resultFuture.get()

                    if (result.resultCode == SessionResult.RESULT_SUCCESS) {
                        val data = result.extras
                        val currentSong = data?.getParcelable<Song>("current_song")
                        val source = data?.getString("source")
                        val isPlayingVal = data?.getBoolean("is_playing", false) ?: false
                        val position = data?.getLong("position", 0L) ?: 0L
                        val durationVal = data?.getLong("duration", 0L) ?: 0L
                        val songs = data?.getParcelableArrayList<Song>("songs") ?: emptyList()
                        this.currentSong = currentSong
                        this.sourceName = source ?: ""
                        this.isPlaying = isPlayingVal
                        this.currentPosition = position
                        this.duration = durationVal
                        this.songList = songs
                        if (isPlayingVal) startPositionUpdates()
                    }
                    isConnecting = false
                }, MoreExecutors.directExecutor())

            } catch (e: Exception) {
                Log.e("Connect", "Failed to connect mediaController: ${e.message}")
                isConnecting = false
            }
        }, MoreExecutors.directExecutor())
    }

    fun disconnect() {
        playerListener?.let {
            mediaController?.removeListener(it)
            playerListener = null
        }
        mediaController?.release()
        mediaController = null
        isConnecting = false
    }

    fun play() {
        mediaController?.play()
    }

    fun pause() {
        mediaController?.pause()
    }

    fun playPause(){
        if(isPlaying){
            pause()
        }
        else{
            play()
        }
    }

    fun seek(positionMs: Long){
        mediaController?.seekTo(positionMs)
    }

    fun playSongById(songId: String) {
        val controller = mediaController ?: return
        val bundle = Bundle().apply {
            putString("song_id", songId)
        }
        val command = SessionCommand("play_by_id",Bundle.EMPTY)
        controller.sendCustomCommand(command, bundle)
        mediaController?.play()
    }


    fun syncSongs() {
        val songs = songList
        val controller = mediaController ?: return
        val bundle = Bundle().apply {
            putParcelableArrayList("songs", ArrayList(songs))
            putString("source", sourceName)
        }
        val command = SessionCommand("sync_playlist",Bundle.EMPTY)
        controller.sendCustomCommand(command, bundle)
    }

    fun syncLocal(songs: List<Song>) {
        localSongList = songs
    }

    fun syncOnline(songs: List<Song>, region: String) {
        onlineSongMap = onlineSongMap.toMutableMap().apply {
            this[region] = songs
        }
    }

    fun setLocal(){
        songList = localSongList
        sourceName ="local"
        if(currentMediaId>=0) currentSong = songList.firstOrNull { it.id==currentMediaId }
        syncSongs()
    }

    fun setOnline(region: String){
        songList = onlineSongMap[region]!!
        sourceName = region
        syncSongs()
    }

    fun next() {
        val controller = mediaController ?: return
        val command = SessionCommand("next",Bundle.EMPTY)
        controller.sendCustomCommand(command, Bundle.EMPTY)
    }

    fun previous() {
        val controller = mediaController ?: return
        val command = SessionCommand("previous",Bundle.EMPTY)
        controller.sendCustomCommand(command, Bundle.EMPTY)
    }

    fun sendUser(currentUser: String){
        val controller = mediaController ?: return
        val bundle = Bundle().apply {
            putString("user", currentUser)
        }
        Log.d("PlaybackViewModel","Current user is $currentUser")
        val command = SessionCommand("send_user",Bundle.EMPTY)
        controller.sendCustomCommand(command, bundle)
    }

    fun setPreferredOutputDevice(savedDevice: AudioDeviceInfo) {
        val controller = mediaController ?: return
        _selectedDeviceId.value = savedDevice.id
        val bundle = Bundle().apply {
            putInt("device_id", savedDevice.id)
            putInt("device_type", savedDevice.type)
            putString("device_product_name", savedDevice.productName?.toString())
        }
        val command = SessionCommand("set_output_device", Bundle.EMPTY)
        controller.sendCustomCommand(command, bundle)
    }

    private fun startPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = viewModelScope.launch {
            while (isPlaying) {
                mediaController?.let { player ->
                    currentPosition = player.currentPosition
                }
                delay(10L)
            }
        }
    }


    private fun stopPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = null
    }

    fun kill() {
        sourceName = null
        mediaController?.stop()
        val controller = mediaController ?: return
        val command = SessionCommand("kill",Bundle.EMPTY)
        controller.sendCustomCommand(command, Bundle.EMPTY)
    }

}

