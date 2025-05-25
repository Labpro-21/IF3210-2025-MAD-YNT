package com.ynt.purrytify.ui.screen.player

import android.content.ContentResolver
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.Log
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.SliderPositions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import com.ynt.purrytify.R
import com.ynt.purrytify.models.Song
import com.ynt.purrytify.ui.screen.libraryscreen.LibraryViewModel
import kotlinx.coroutines.launch
import androidx.core.graphics.scale
import androidx.core.graphics.get
import com.ynt.purrytify.utils.auth.SessionManager


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongPlayerSheet(
    setShowPopupSong: (Boolean) -> Unit,
    libraryViewModel: LibraryViewModel,
    sheetState: SheetState,
    playbackViewModel: PlaybackViewModel,
    sessionManager: SessionManager
) {
    val coroutineScope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }
    val duration = if (playbackViewModel.duration.toFloat() >= 0) playbackViewModel.duration.toFloat() else 0f
    val currentPosition = playbackViewModel.currentPosition.coerceAtLeast(0).toFloat()
    var sliderPosition by remember { mutableStateOf(currentPosition) }
    var isSliderDragging by remember { mutableStateOf(false) }
    val contentResolver = LocalContext.current.contentResolver
    val imageBitmap = getBitmap(contentResolver, playbackViewModel.currentSong?.image?.toUri())
    val dominantColor = getDominantColor(bitmap = imageBitmap)
    val username = sessionManager.getUser()
    val localSongList by libraryViewModel.getAllSongs(username).observeAsState()

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val screenWidth = configuration.screenWidthDp.dp

    val imageSize = if (isLandscape) 160.dp else 300.dp
    val topSpacer = if (isLandscape) 16.dp else 96.dp
    val buttonSize = if (isLandscape) 36.dp else 48.dp
    val sliderThumbHeight = if (isLandscape) 16.dp else 18.dp
    val sliderThumbWidth = if (isLandscape) 6.dp else 8.dp
    val titleFontSize = if (isLandscape) 20.sp else 24.sp
    val artistFontSize = if (isLandscape) 14.sp else 16.sp
    val likeButtonSize = if (isLandscape) 20.dp else 24.dp

    val imageToLikeSpacer = if (isLandscape) 8.dp else 16.dp
    val likeToSliderSpacer = if (isLandscape) 0.dp else 16.dp
    val sliderToControlsSpacer = if (isLandscape) 8.dp else 16.dp
    val sliderMaxWidth = if (isLandscape) screenWidth * 0.4f else screenWidth * 0.65f

    LaunchedEffect(localSongList) {
        if (playbackViewModel.sourceName == "local") {
            val list = localSongList
            if (list != null) {
                playbackViewModel.syncLocal(list)
                playbackViewModel.setLocal()
            }
        }
    }

    LaunchedEffect(currentPosition) {
        if (!isSliderDragging) {
            sliderPosition = currentPosition
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            coroutineScope.launch {
                setShowPopupSong(false)
            }
        },
        sheetState = sheetState,
        containerColor = Color.Transparent,
        contentColor = colorResource(R.color.dark_gray),
        dragHandle = {},
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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(topSpacer))

                Image(
                    painter = rememberAsyncImagePainter(playbackViewModel.currentSong?.image),
                    contentDescription = "Song Cover",
                    modifier = Modifier
                        .size(imageSize)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    playbackViewModel.currentSong?.title ?: "Unknown Title",
                    color = Color.White,
                    fontSize = titleFontSize
                )
                Text(
                    playbackViewModel.currentSong?.artist ?: "Unknown Artist",
                    color = Color.Gray,
                    fontSize = artistFontSize
                )

                Spacer(modifier = Modifier.height(imageToLikeSpacer))

                IconButton(
                    modifier = Modifier.size(likeButtonSize),
                    onClick = {
                    if (playbackViewModel.sourceName == "local") {
                        val updatedSong = playbackViewModel.currentSong?.copy(
                            isLiked = if (playbackViewModel.currentSong?.isLiked == 1) 0 else 1
                        )
                        libraryViewModel.update(updatedSong ?: Song())
                        playbackViewModel.currentSong = updatedSong
                    }
                }) {
                    if (playbackViewModel.sourceName == "local") {
                        Icon(
                            imageVector = if (playbackViewModel.currentSong?.isLiked == 1) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = if (playbackViewModel.currentSong?.isLiked == 1) Color.Red else Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(likeToSliderSpacer))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(formatTime(currentPosition), color = Color.White, fontSize = 12.sp)

                    Box(
                        modifier = Modifier
                            .width(sliderMaxWidth)
                            .padding(horizontal = 8.dp)
                    ) {
                        Slider(
                            value = sliderPosition,
                            onValueChange = {
                                sliderPosition = it
                                isSliderDragging = true
                            },
                            onValueChangeFinished = {
                                isSliderDragging = false
                                playbackViewModel.seek(sliderPosition.toLong())
                            },
                            valueRange = 0f..duration,
                            modifier = Modifier.fillMaxWidth(),
                            thumb = {
                                SliderDefaults.Thumb(
                                    interactionSource = interactionSource,
                                    thumbSize = DpSize(width = sliderThumbWidth, height = sliderThumbHeight),
                                    colors = SliderDefaults.colors(
                                        thumbColor = Color(0xFF1BB452),
                                    ),
                                )
                            },
                            track = { sliderState ->
                                SliderDefaults.Track(
                                    modifier = Modifier.height(5.dp),
                                    sliderState = sliderState,
                                    drawStopIndicator = null,
                                    colors = SliderDefaults.colors(
                                        activeTrackColor = Color.White,
                                        activeTickColor = Color.White,
                                        inactiveTickColor = Color.LightGray,
                                        inactiveTrackColor = Color.LightGray
                                    ),
                                )
                            },
                        )
                    }
                    Text(formatTime(duration), color = Color.White, fontSize = 12.sp)
                }


                Spacer(modifier = Modifier.height(sliderToControlsSpacer))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(36.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { playbackViewModel.previous() }) {
                            Icon(
                                Icons.Default.SkipPrevious,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(buttonSize)
                            )
                        }
                        IconButton(onClick = { playbackViewModel.playPause() }) {
                            Icon(
                                imageVector = if (playbackViewModel.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(buttonSize)
                            )
                        }
                        IconButton(onClick = { playbackViewModel.next() }) {
                            Icon(
                                Icons.Default.SkipNext,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(buttonSize)
                            )
                        }
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