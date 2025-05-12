package com.ynt.purrytify.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.ynt.purrytify.PlayerState
import com.ynt.purrytify.R
import com.ynt.purrytify.models.Song

@Composable
fun Miniplayer(
    currentSong: MutableState<Song?>,
    onPlay: () -> Unit,
    onSkip: () -> Unit,
    onClick: () -> Unit,
    isPlaying: MutableState<PlayerState>,
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .height(64.dp)
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF212121))

    ){
        Image(
            painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(context)
                    .data(currentSong.value?.image)
                    .crossfade(true)
                    .size(96)
                    .build()
            ),
            contentDescription = "Currently Played",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(start = 8.dp)
                .size(56.dp)
                .border(1.dp, color = colorResource(R.color.medium_dark_gray), shape = RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .align(Alignment.CenterVertically)
                .clickable(
                    onClick = {
                        onClick()
                    }
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .fillMaxHeight()
                .padding(vertical = 12.dp, horizontal = 12.dp)
                .weight(1f)
                .clickable(
                    indication = null,
                    interactionSource = remember {MutableInteractionSource()}
                ){
                    onClick()
                }
        ) {
            Text(
                text = currentSong.value?.title ?: "Idle",
                fontSize = 16.sp,
                color = Color.White,
                modifier = Modifier.fillMaxHeight().weight(1f)
            )
            Text(
                text = currentSong.value?.artist ?: "",
                fontSize = 10.sp,
                color = Color.LightGray,
                modifier = Modifier.fillMaxHeight().weight(1f)
            )
        }
        Row(
            modifier = Modifier.align(Alignment.CenterVertically)
                .padding(horizontal = 8.dp)
        ) {
            IconButton(
             onClick = onPlay
            ){
                Icon(
                    imageVector = if(isPlaying.value==PlayerState.STARTED || isPlaying.value==PlayerState.PLAYING) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = "Play/Pause Song",
                    tint = Color.White
                )
            }
            IconButton(
                onClick = onSkip
            ){
                Icon(
                    imageVector = Icons.Filled.SkipNext,
                    contentDescription = "Play Song",
                    tint = Color.White
                )
            }
        }

    }
}