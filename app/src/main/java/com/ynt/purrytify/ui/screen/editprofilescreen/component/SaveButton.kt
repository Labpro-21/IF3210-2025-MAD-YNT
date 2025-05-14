package com.ynt.purrytify.ui.screen.editprofilescreen.component

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ynt.purrytify.ui.screen.editprofilescreen.EditProfileViewModel

@Composable
fun SaveButton(modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White
        )
    ) {
        Text(
            text = "Save",
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterVertically),
        )
    }
}