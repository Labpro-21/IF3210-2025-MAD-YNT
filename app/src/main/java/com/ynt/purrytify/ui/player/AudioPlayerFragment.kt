package com.ynt.purrytify.ui.player

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import coil.compose.rememberAsyncImagePainter
import com.ynt.purrytify.database.song.Song
import com.ynt.purrytify.ui.library.LibraryViewModel
import kotlinx.coroutines.delay
import kotlinx.parcelize.Parcelize

class AudioPlayerFragment : Fragment() {

    private val viewModel: LibraryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val song = arguments?.getParcelable<Song>("song")
                song?.let {
                    AudioPlayerScreen(song = it, viewModel = viewModel)
                } ?: run {
                    Text("No song data provided", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun AudioPlayerScreen(song: Song, viewModel: LibraryViewModel) {
    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableIntStateOf(0) }
    var duration by remember { mutableIntStateOf(song.duration) }
    val handler = remember { Handler(Looper.getMainLooper()) }

    // MediaPlayer init
    LaunchedEffect(song) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(context, Uri.parse(song.audio ?: ""))
            prepare()
            start()
            isPlaying = true
            duration = duration
        }

        while (isPlaying) {
            currentPosition = mediaPlayer?.currentPosition ?: 0
            delay(1000L)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
            handler.removeCallbacksAndMessages(null)
        }
    }

    // UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(Color(0xFF8B0000), Color(0xFF191414))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Image(
                painter = rememberAsyncImagePainter(song.image),
                contentDescription = "Song Cover",
                modifier = Modifier
                    .size(300.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = song.title ?: "Unknown Title",
                color = Color.White,
                fontSize = 24.sp
            )
            Text(
                text = song.artist ?: "Unknown Artist",
                color = Color.Gray,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            IconButton(onClick = {
                val updatedSong = song.copy(isLiked = if (song.isLiked == 1) 0 else 1)
                viewModel.update(updatedSong)
            }) {
                Icon(
                    imageVector = if (song.isLiked == 1) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (song.isLiked == 1) Color.Red else Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Seek Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = formatTime(currentPosition), color = Color.White, fontSize = 12.sp)
                Slider(
                    value = currentPosition.toFloat(),
                    onValueChange = {
                        mediaPlayer?.seekTo(it.toInt())
                        currentPosition = it.toInt()
                    },
                    valueRange = 0f..duration.toFloat(),
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                )
                Text(text = formatTime(duration), color = Color.White, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Previous song logic */ }) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = null, tint = Color.White)
                }
                IconButton(onClick = {
                    isPlaying = !isPlaying
                    if (isPlaying) {
                        mediaPlayer?.start()
                    } else {
                        mediaPlayer?.pause()
                    }
                }) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
                IconButton(onClick = { /* Next song logic */ }) {
                    Icon(Icons.Default.SkipNext, contentDescription = null, tint = Color.White)
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
