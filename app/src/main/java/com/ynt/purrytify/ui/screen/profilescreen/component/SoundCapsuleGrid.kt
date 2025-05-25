package com.ynt.purrytify.ui.screen.profilescreen.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.ynt.purrytify.models.TimeListened

@Composable
fun SoundCapsuleGrid(
    timeListened: Long,
    songTitle: String,
    artistName: String,
    songImage: String,
    artistImage: String,
    isStreak: Boolean = false
) {
    Column(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 10.dp)
    ) {
        SoundCapsuleCard(
            title = "Time Listened",
            content = "${(timeListened/60000)} minutes",
            textContentColor = Color(0xFF4CAF50),
            image = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SoundCapsuleCard(
                title = "Top Artist",
                content = artistName,
                textContentColor = Color(0xFF7EA6F6),
                image = artistImage,
                modifier = Modifier
                    .weight(1f)
                    .height(250.dp),
                isArtistOrSong = true
            )

            Spacer(modifier = Modifier.width(10.dp))

            SoundCapsuleCard(
                title = "Top Song",
                content = songTitle,
                textContentColor = Color.Yellow,
                image = songImage,
                modifier = Modifier
                    .weight(1f)
                    .height(250.dp),
                isArtistOrSong = true
            )
        }

        if (isStreak) {
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF212121)
                )
            ) {
                Column (
                    modifier = Modifier.padding(20.dp)
                ){
                    Image(
                        painter = rememberAsyncImagePainter(songImage),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "You had a 5-day streak",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(13.dp))

                    Text(
                        text = "You played Loose by Daniel Caesar day after day. You were on fire",
                        color = Color.Gray,
                        fontSize = 13.sp,
                    )
                }
            }
        }
    }
}