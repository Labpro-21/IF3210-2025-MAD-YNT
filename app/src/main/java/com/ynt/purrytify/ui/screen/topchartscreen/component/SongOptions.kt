package com.ynt.purrytify.ui.screen.topchartscreen.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongOptions(
    onDismiss : () -> (Unit)
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        TextButton(
            onClick = { }
        ) {
            Text("Share")
        }

        TextButton(
            onClick = { }
        ) {
            Text("Download")
        }
    }
}