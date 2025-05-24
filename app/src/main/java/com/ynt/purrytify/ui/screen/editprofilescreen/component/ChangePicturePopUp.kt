package com.ynt.purrytify.ui.screen.editprofilescreen.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.Manifest
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.ynt.purrytify.R
import com.ynt.purrytify.ui.screen.editprofilescreen.EditProfileViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ChangePicturePopUp(
    onDismiss: () -> Unit,
    onSelectPicture: () -> Unit,
    onTakePicture: () -> Unit,
) {
//    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
//    var showBottomSheet by remember { mutableStateOf(false) }
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        containerColor = colorResource(R.color.dark_gray),
        contentColor = colorResource(R.color.dark_gray),
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Change Profile Picture",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                TextButton(
                    onClick = {
                        onSelectPicture()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(colorResource(R.color.dark_gray)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Choose Picture",
                        color = Color.White,
                        )
                }
                TextButton(
                    onClick = {
                        cameraPermissionState.launchPermissionRequest()
                        onTakePicture()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(colorResource(R.color.dark_gray)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Take Picture",
                        color = Color.White,
                        )
                }
            }
        }
    }
}