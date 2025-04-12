package com.ynt.purrytify.ui.library.ui

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.ynt.purrytify.R

@Composable
fun ImagePicker(
    onImagePicked: (Uri?) -> Unit,
    imageUri: Uri?,
    context: Context
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onImagePicked(uri)
    }

    Box(
        modifier = Modifier
            .size(96.dp)
            .clickable { launcher.launch("image/*") }
    ){
        if (imageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(context)
                        .data(imageUri)
                        .crossfade(true)
                        .size(512)
                        .build()
                )
                ,
                contentDescription = "Selected Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .aspectRatio(1f)
                    .size(96.dp)
                    .border(2.dp, color = colorResource(R.color.medium_dark_gray), shape = RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))

            )
        } else {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.choose_image),
                contentDescription = "Choose Image",
                modifier = Modifier
                    .padding(horizontal = 0.dp)
            )
        }
    }
}

