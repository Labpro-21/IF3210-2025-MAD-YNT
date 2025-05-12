package com.ynt.purrytify.ui.screen.player

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.ynt.purrytify.PlayerState
import com.ynt.purrytify.R
import com.ynt.purrytify.models.Song
import com.ynt.purrytify.ui.screen.libraryscreen.LibraryViewModel
import com.ynt.purrytify.utils.mediaplayer.SongPlayerLiveData
import com.ynt.purrytify.utils.queue.QueueManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongPlayerSheet(
    setShowPopupSong: (Boolean)->Unit,
    libraryViewModel: LibraryViewModel,
    sheetState: SheetState,
    queueManager: QueueManager,
    currentSong: MutableState<Song?>,
    currentPosition: MutableState<Int>,
    isPlaying: MutableState<PlayerState>,
    songPlayerLiveData: SongPlayerLiveData

){
    val coroutineScope = rememberCoroutineScope()
    val duration = currentSong.value?.duration
    val interactionSource = remember {MutableInteractionSource()}
    ModalBottomSheet(
        onDismissRequest = {
            coroutineScope.launch {
                setShowPopupSong(false)
            }
        },
        sheetState = sheetState,
        containerColor = colorResource(R.color.dark_gray),
        contentColor = colorResource(R.color.dark_gray),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color(0xFF166C00),
                            0.7f to Color(0xFF0F110E),
                            1.0f to Color(0xFF0F110E)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(96.dp))

                Image(
                    painter = rememberAsyncImagePainter(currentSong.value?.image),
                    contentDescription = "Song Cover",
                    modifier = Modifier
                        .size(300.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(currentSong.value?.title ?: "Unknown Title", color = Color.White, fontSize = 24.sp)
                Text(currentSong.value?.artist ?: "Unknown Artist", color = Color.Gray, fontSize = 16.sp)

                Spacer(modifier = Modifier.height(16.dp))

                IconButton(onClick = {
                    val updatedSong = currentSong.value?.copy(isLiked = if (currentSong.value!!.isLiked == 1) 0 else 1)
                    libraryViewModel.update(updatedSong!!)
                    currentSong.value!!.isLiked = updatedSong.isLiked
                }) {
                    Icon(
                        imageVector = if (currentSong.value?.isLiked ?: 0 == 1) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (currentSong.value?.isLiked ?: 0 == 1) Color.Red else Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(formatTime(currentPosition.value), color = Color.White, fontSize = 12.sp)
                    Slider(
                        value = currentPosition.value.toFloat(),
                        onValueChange = {
                            currentPosition.value = it.toInt()
                            songPlayerLiveData.songPlayer.value?.seekAudio(currentPosition)

                        },
                        valueRange = 0f..(duration?.toFloat() ?: 0 ).toFloat(),
                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                        thumb = {
                            SliderDefaults.Thumb(
                                interactionSource = interactionSource ,
                                thumbSize = DpSize(width = 8.dp,height = 24.dp),
                                colors =  SliderDefaults.colors(
                                    thumbColor = Color(0xFF1BB452),
                                ),
                            )
                        },
                        colors = SliderDefaults.colors(
                            activeTrackColor =  Color(0xFF1BB452),
                            activeTickColor =  Color(0xFF1BB452),
                            inactiveTickColor = Color.White,
                            inactiveTrackColor = Color.White
                        ),

                    )
                    Text(formatTime(duration!!), color = Color.White, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        val prevSong = queueManager.skipToPrevious()
                        currentSong.value = prevSong
                        val songCopy = prevSong?.copy(lastPlayed = System.currentTimeMillis())
                        if (songCopy != null) {
                            libraryViewModel.update(songCopy)
                        }
                        currentPosition.value = 0
                        if (queueManager.size.value == 0) {
                            isPlaying.value = PlayerState.STOPPED
                        } else {
                            isPlaying.value = PlayerState.STARTED

                        }
                    }) {
                        Icon(Icons.Default.SkipPrevious, contentDescription = null, tint = Color.White)
                    }
                    IconButton(onClick = {
                        when(isPlaying.value){
                            PlayerState.PLAYING -> {
                                isPlaying.value = PlayerState.PAUSED
                            }
                            PlayerState.PAUSED -> {
                                isPlaying.value = PlayerState.PLAYING
                            }
                            PlayerState.STARTED -> {
                                isPlaying.value = PlayerState.PAUSED
                            }
                            PlayerState.STOPPED -> {
                                isPlaying.value = PlayerState.STARTED
                            }
                        }

                    }) {
                        Icon(
                            imageVector = if (isPlaying.value == PlayerState.PLAYING) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    IconButton(onClick = {
                        val nextSong = queueManager.skipToNext()
                        currentSong.value = nextSong
                        val songCopy = nextSong?.copy(lastPlayed = System.currentTimeMillis())
                        if (songCopy != null) {
                            libraryViewModel.update(songCopy)
                        }
                        currentPosition.value = 0
                        if (queueManager.size.value == 0) {
                            isPlaying.value = PlayerState.STOPPED
                        } else {
                            isPlaying.value = PlayerState.STARTED

                        }
                    }) {
                        Icon(Icons.Default.SkipNext, contentDescription = null, tint = Color.White)
                    }
                }
            }
        }
    }
}

fun formatTime(millis: Int): String {
    val minutes = (millis / 1000) / 60
    val seconds = (millis / 1000) % 60
    return String.format("%d:%02d", minutes, seconds)
}
