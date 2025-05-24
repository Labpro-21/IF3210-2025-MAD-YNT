package com.ynt.purrytify.ui.screen.topchartscreen.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ynt.purrytify.R
import com.ynt.purrytify.models.OnlineSong
import com.ynt.purrytify.models.Song

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongOptions(
    onDismiss: () -> Unit,
    onDownload: () -> Unit,
    songID: Int,
    showShareSheetState: MutableState<Boolean>,
    closeSelf: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.DarkGray
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            TextButton(onClick = {
                closeSelf()
                showShareSheetState.value = true
            }) {
                Text("Share", color = Color.White)
            }

            TextButton(onClick = onDownload) {
                Text("Download", color = Color.White)
            }
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
