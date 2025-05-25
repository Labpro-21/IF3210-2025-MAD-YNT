package com.ynt.purrytify.ui.screen.topchartscreen.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.navigation.NavController
import com.ynt.purrytify.R
import com.ynt.purrytify.Screen
import com.ynt.purrytify.utils.sharing.ShareViaURL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareOptions(
    onDismiss : () -> (Unit),
    songID: Int,
    songTitle: String,
    songArtist: String,
    navController: NavController
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colorResource(R.color.dark_gray),
        contentColor = colorResource(R.color.dark_gray),
    ) {
        TextButton(
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
            onClick = {
                ShareViaURL(context, songID)
                onDismiss()
            }
        ) {
            Text("Share via Link", color= Color.White)
        }

        TextButton(
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
            onClick = {
                navController.navigate(Screen.QRSharing.createRoute(
                    songId = songID,
                    songTitle = songTitle,
                    songArtist = songArtist
                ))
                onDismiss()
            }
        ) {
            Text("Share via QR", color= Color.White)
        }
    }
}