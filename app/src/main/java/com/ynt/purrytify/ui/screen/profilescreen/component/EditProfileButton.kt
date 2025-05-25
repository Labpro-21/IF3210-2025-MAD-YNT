package com.ynt.purrytify.ui.screen.profilescreen.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun EditProfileButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .border(2.dp, Color.White, shape = RoundedCornerShape(25.dp))
            .height(35.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White
        )
    ) {
        Text(
            text = "Edit Profile",
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterVertically),
        )
    }
}