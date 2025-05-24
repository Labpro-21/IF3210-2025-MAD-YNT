package com.ynt.purrytify.ui.screen.topchartscreen.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.ynt.purrytify.Screen
import com.ynt.purrytify.utils.sharing.ShareViaURL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareOptions(
    onDismiss : () -> (Unit),
    songID: Int,
    navController: NavController
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.DarkGray
    ) {
        TextButton(
            onClick = {
                ShareViaURL(context, songID)
                onDismiss()
            }
        ) {
            Text("Share via Link", color= Color.White)
        }

        TextButton(
            onClick = {
                navController.navigate(Screen.QRSharing.createRoute(songID))
                onDismiss()
            }
        ) {
            Text("Share via QR", color= Color.White)
        }
    }
}