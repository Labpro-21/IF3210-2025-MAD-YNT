package com.ynt.purrytify.ui.profile

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.ynt.purrytify.utils.logout

@Composable
fun LogoutButton() {
    val context = LocalContext.current
    Button(
        onClick = { logout(context) },
        modifier = Modifier
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Red,
            contentColor = Color.White
        )
    ) {
        Text("Sign Out")
    }
}