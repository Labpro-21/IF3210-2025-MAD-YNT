package com.ynt.purrytify.ui.library.ui

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynt.purrytify.R
import com.ynt.purrytify.ui.library.LibraryViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSong(
    setShowPopupSong: (Boolean)->Unit,
    libraryViewModel: LibraryViewModel,
    loggedInUser: String,
    context: Context,
    sheetState: SheetState,
){
    val title = remember  { mutableStateOf("") }
    val artist = remember{ mutableStateOf("") }
    val selectedImageUri = remember { mutableStateOf<Uri?>(null) }
    val selectedSongUri = remember { mutableStateOf<Uri?>(null) }
    val coroutineScope = rememberCoroutineScope()
    if ((loggedInUser == "") || (loggedInUser == "Error fetching user data") ){
        Toast.makeText(LocalContext.current, "You are not logged in", Toast.LENGTH_SHORT).show()
        setShowPopupSong(false)
    }
    else {
        ModalBottomSheet(
            onDismissRequest = {
                coroutineScope.launch {
                    setShowPopupSong(false)
                }
            },
            sheetState = sheetState,
            containerColor = colorResource(R.color.dark_gray),
            contentColor = colorResource(R.color.dark_gray),
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    modifier = Modifier

                ) {
                    Text(
                        text = "Upload Song",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 10.dp)
                    )
                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(
                                horizontal = 0.dp
                            ),
                    ) {
                        ImagePicker(
                            imageUri = selectedImageUri.value,
                            onImagePicked = { selectedImageUri.value = it },
                            context = context
                        )
                        Spacer(
                            modifier = Modifier
                                .width(24.dp)
                        )
                        SongPicker(
                            songUri = selectedSongUri.value,
                            onSongPicked = { selectedSongUri.value = it },
                            context = context
                        )
                    }
                    AddSongTextField(title, "Title", false)
                    AddSongTextField(artist, "Artist", true)
                }
                Spacer(
                    modifier = Modifier
                        .height(10.dp)
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp
                        ),
                ) {
                    CancelButton(coroutineScope, sheetState, setShowPopupSong)
                    SaveButton(
                        title = title.value,
                        artist = artist.value,
                        libraryViewModel = libraryViewModel,
                        imageUri = selectedImageUri.value,
                        songUri = selectedSongUri.value,
                        songOwner = loggedInUser,
                        coroutineScope = coroutineScope,
                        sheetState = sheetState,
                        setShowPopupSong = setShowPopupSong,
                        context = context
                    )
                }
            }
        }
    }
}
