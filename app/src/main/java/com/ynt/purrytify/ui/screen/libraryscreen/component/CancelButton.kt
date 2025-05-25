package com.ynt.purrytify.ui.screen.libraryscreen.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.ynt.purrytify.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CancelButton(coroutineScope: CoroutineScope, sheetState: SheetState, setShowPopupSong: (Boolean) -> Unit){
    Button(
        colors = ButtonDefaults.buttonColors(colorResource(R.color.medium_dark_gray)),
        onClick = {
            coroutineScope.launch {
                sheetState.hide()
                setShowPopupSong(false)
            }
        },
        modifier = Modifier
            .height(36.dp)
            .fillMaxWidth(1/2f)
            .padding(horizontal = 10.dp)){
        Text(
            text = "Cancel",
            color = Color.White
        )
    }

}
