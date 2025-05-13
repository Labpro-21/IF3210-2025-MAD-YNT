package com.ynt.purrytify.ui.screen.editprofilescreen.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.ynt.purrytify.ui.screen.editprofilescreen.EditProfileViewModel
import com.ynt.purrytify.utils.SessionManager
import kotlinx.coroutines.launch

@Composable
fun EditProfileHeader(
    navController: NavController,
    viewModel: EditProfileViewModel,
    sessionManager: SessionManager
) {
    val coroutineScope = rememberCoroutineScope()
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        BackButton(
            modifier = Modifier.weight(1f)
        ) { navController.navigate("profile") }

        Text(
            text = "Edit Profile",
            color = Color.White,
            modifier = Modifier
                .weight(2f)
                .align(Alignment.CenterVertically),
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        SaveButton(
            modifier = Modifier.weight(1f)
        ) {
            coroutineScope.launch {
                viewModel.editProfile(sessionManager)
            }
            navController.navigate("profile")
        }
    }
}