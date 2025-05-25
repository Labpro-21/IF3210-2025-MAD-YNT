package com.ynt.purrytify.ui.screen.profilescreen.component

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.ynt.purrytify.models.ProfileResponse

@Composable
fun ProfileDetail(
    data: ProfileResponse?
) {
    Image(
        painter = rememberAsyncImagePainter(data?.photoURL),
        contentDescription = "Profile Image",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(150.dp)
            .clip(CircleShape)
    )

    Log.d("profile image", data?.photoURL ?: "")

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = data?.username ?: "No username",
        fontSize = 25.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
    )

    Spacer(modifier = Modifier.height(5.dp))

    Text(
        text = data?.location ?: "No location",
        fontSize = 16.sp,
        color = Color.White
    )
}