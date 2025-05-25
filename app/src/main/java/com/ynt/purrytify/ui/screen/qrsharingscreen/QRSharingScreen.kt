package com.ynt.purrytify.ui.screen.qrsharingscreen

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynt.purrytify.utils.sharing.CreateQRCode
import com.ynt.purrytify.utils.sharing.ShareQRCode

@Composable
fun QRSharingScreen(
    songId: Int,
    songTitle: String,
    songArtist: String
) {
    val songQRCodeBitmap = CreateQRCode(
        songId=songId,
        songArtist=songArtist,
        songTitle = songTitle
    )
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(18.dp)
    ) {
        Text(
            text = "Share via QR Code",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        SongInfo(title = songTitle, artist = songArtist)

        Spacer(modifier = Modifier.height(16.dp))

        QRImage(bitmap = songQRCodeBitmap)
        Spacer(modifier = Modifier.height(40.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1DB955),
                    contentColor = Color.White
                ),
                onClick = {
                    songQRCodeBitmap?.let {
                        ShareQRCode(context, it)
                    }
                }
            ) {
                Text("Share QR Code")
            }
        }
    }
}


@Composable
fun SongInfo(title: String, artist: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = artist,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = Color.White
        )
    }
}

@Composable
fun QRImage(bitmap: Bitmap?) {
    bitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "QR Code",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, Color.Gray, RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(8.dp)
        )
    }
}
