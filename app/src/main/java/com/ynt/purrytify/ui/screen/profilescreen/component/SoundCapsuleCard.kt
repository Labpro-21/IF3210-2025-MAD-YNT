package com.ynt.purrytify.ui.screen.profilescreen.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter

@Composable
fun SoundCapsuleCard(
    title: String,
    content: String,
    textContentColor: Color,
    image: String?,
    onClick: () -> Unit = {},
    modifier: Modifier,
    isArtistOrSong: Boolean = false,
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF212121)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(20.dp))

            var fontSize: TextUnit = 30.sp
            if (isArtistOrSong) {
                fontSize = 15.sp
            }

            Text(
                text = content,
                color = textContentColor,
                fontSize = fontSize,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(35.dp))

            Image(
                painter = rememberAsyncImagePainter(image),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )
        }
    }
}