package com.ynt.purrytify.ui.screen.topchartscreen.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import com.ynt.purrytify.models.OnlineSong
import com.ynt.purrytify.models.Song

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongOptions(
    onDismiss : () -> (Unit),
    onDownload : () -> (Unit),
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        TextButton(
            onClick = {  }
        ) {
            Text("Share")
        }

        TextButton(
            onClick = onDownload
        ) {
            Text("Download")
        }
    }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun TempPopUp(id: Int) {
//    Dialog(
//        onDismissRequest = { }
//    ) {
//        Text("ID: $id")
//    }
//}
