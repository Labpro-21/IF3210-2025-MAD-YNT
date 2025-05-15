package com.ynt.purrytify.ui.screen.editprofilescreen.component
//
//import android.os.Build
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.ui.platform.LocalContext
//import androidx.core.content.ContextCompat
//import java.util.jar.Manifest
//
//@Composable
//fun RequestReadImagesPermission(
//    onGranted: () -> Unit,
//    onDenied: () -> Unit
//) {
//    val context = LocalContext.current
//    val permissionLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission()
//    ) { isGranted ->
//        if (isGranted) {
//            onGranted()
//        } else {
//            onDenied()
//        }
//    }
//
//    LaunchedEffect(Unit) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            val permission = com.ynt.purrytify.Manifest.permission.READ_MEDIA_IMAGES
//            val isGranted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
//            if (isGranted) {
//                onGranted()
//            } else {
//                permissionLauncher.launch(permission)
//            }
//        } else {
//            // Versi Android lama: tidak perlu permission
//            onGranted()
//        }
//    }
//}