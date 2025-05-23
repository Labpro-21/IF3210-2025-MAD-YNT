package com.ynt.purrytify.ui.screen.player

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
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
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import com.ynt.purrytify.R
import com.ynt.purrytify.models.Song
import com.ynt.purrytify.ui.screen.libraryscreen.LibraryViewModel
import kotlinx.coroutines.launch
import androidx.core.graphics.scale
import androidx.core.graphics.get


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongPlayerSheet(
    setShowPopupSong: (Boolean)->Unit,
    libraryViewModel: LibraryViewModel,
    sheetState: SheetState,
    playbackViewModel: PlaybackViewModel,
){
    val coroutineScope = rememberCoroutineScope()
    val interactionSource = remember {MutableInteractionSource()}
    val currentPosition = if(playbackViewModel.currentPosition.toFloat() >= 0) playbackViewModel.currentPosition.toFloat() else 0f
    val duration = if(playbackViewModel.duration.toFloat() >= 0) playbackViewModel.duration.toFloat() else 0f
    val contentResolver = LocalContext.current.contentResolver
    val imageBitmap = getBitmap(contentResolver, playbackViewModel.currentSong?.image?.toUri())
    val dominantColor = getDominantColor(
        bitmap = imageBitmap
    )
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
                            0.0f to Color(dominantColor),
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
                    painter = rememberAsyncImagePainter(playbackViewModel.currentSong?.image),
                    contentDescription = "Song Cover",
                    modifier = Modifier
                        .size(300.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(playbackViewModel.currentSong?.title ?: "Unknown Title", color = Color.White, fontSize = 24.sp)
                Text(playbackViewModel.currentSong?.artist ?: "Unknown Artist", color = Color.Gray, fontSize = 16.sp)

                Spacer(modifier = Modifier.height(16.dp))

                IconButton(onClick = {
                    val updatedSong = playbackViewModel.currentSong?.copy(isLiked = if (playbackViewModel.currentSong?.isLiked == 1) 0 else 1)
                    libraryViewModel.update(updatedSong?:Song())
                    playbackViewModel.currentSong =  updatedSong
                }) {
                    Icon(
                        imageVector = if (playbackViewModel.currentSong?.isLiked == 1) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (playbackViewModel.currentSong?.isLiked == 1) Color.Red else Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(formatTime(currentPosition), color = Color.White, fontSize = 12.sp)
                    Slider(
                        value = currentPosition,
                        onValueChange = {
                            playbackViewModel.seek(it.toLong())
                        },
                        valueRange = 0f..duration,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
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
                    Text(formatTime(duration), color = Color.White, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {playbackViewModel.previous()}) {
                        Icon(Icons.Default.SkipPrevious, contentDescription = null, tint = Color.White)
                    }
                    IconButton(onClick = {playbackViewModel.playPause()}) {
                        Icon(
                            imageVector = if (playbackViewModel.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    IconButton(onClick = {playbackViewModel.next()}) {
                        Icon(Icons.Default.SkipNext, contentDescription = null, tint = Color.White)
                    }
                }
            }
        }
    }
}

fun formatTime(millis: Float): String {
    val minutes = (millis / 1000) / 60
    val seconds = (millis / 1000) % 60
    return String.format("%d:%02d", minutes.toInt(), seconds.toInt())
}

fun getDominantColor(bitmap: Bitmap?): Int {
    if (bitmap == null) return Color(0xFF1BB452).toArgb()
    return try {
        val newBitmap = bitmap.scale(1, 1)
        val color = newBitmap[0, 0]
        newBitmap.recycle()
        color
    } catch (e: Exception) {
        Color(0xFF1BB452).toArgb()
    }
}

fun getBitmap(contentResolver: ContentResolver, fileUri: Uri?): Bitmap? {
    if (fileUri == null) return null
    return try {
        val source = ImageDecoder.createSource(contentResolver, fileUri)
        ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
        }
    } catch (e: Exception) {
        null
    }
}