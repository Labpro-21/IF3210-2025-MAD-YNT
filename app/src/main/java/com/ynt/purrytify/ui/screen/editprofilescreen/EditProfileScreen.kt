package com.ynt.purrytify.ui.screen.editprofilescreen

import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.ynt.purrytify.ui.screen.editprofilescreen.component.ChangePicturePopUp
import com.ynt.purrytify.ui.screen.editprofilescreen.component.EditProfileHeader
import java.io.File

@Composable
fun EditProfileScreen(
    navController: NavController,
) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    fun createImageUri(): Uri {
        val photoFile = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "profile_${System.currentTimeMillis()}.jpg"
        )
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imageUri = photoUri
        }
    }

    var showDialog by remember { mutableStateOf(false) }

    val photoURL = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<String>("photoURL")

    val location = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<String>("location")

    val imageToDisplay = imageUri ?: photoURL

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EditProfileHeader(navController = navController)
        if (photoURL != null) {
            Log.d("Picture", photoURL)
        }
        Box(modifier = Modifier.size(150.dp)) {
            Image(
                painter = rememberAsyncImagePainter(imageToDisplay),
                contentDescription = "Profile Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(40.dp)
                    .background(Color.White, RoundedCornerShape(10.dp))
                    .clickable(onClick = { showDialog = true }),
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(30.dp)
                        .align(Alignment.Center)
                )
            }

            if (showDialog) {
                ChangePicturePopUp(
                    onDismiss = { showDialog = false },
                    onSelectPicture = { launcher.launch("image/*") },
                    onTakePicture = {
                        var uri = createImageUri()
                        photoUri = uri
                        cameraLauncher.launch(uri)
                    }
                )

            }
        }

//        Maps
    }
}
