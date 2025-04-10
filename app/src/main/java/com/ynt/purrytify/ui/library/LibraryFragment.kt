package com.ynt.purrytify.ui.library

import android.content.Context
import android.database.Cursor
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.ynt.purrytify.R
import com.ynt.purrytify.database.song.Song
import com.ynt.purrytify.databinding.FragmentLibraryBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class LibraryFragment : Fragment() {
    private var _binding: FragmentLibraryBinding? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    lateinit var libraryViewModel: LibraryViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        libraryViewModel =
            ViewModelProvider(this).get(LibraryViewModel::class.java)

        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textLibrary
//        libraryViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val libraryCompose: ComposeView = view.findViewById(R.id.library_compose_view)
        libraryCompose.setContent {
            LibraryLayout(libraryViewModel)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


@Composable
fun LibraryLayout(viewModel: LibraryViewModel){
    val showPopUpAddSong = remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val localContext = LocalContext.current
    if (showPopUpAddSong.value) {
        AddSong(setShowPopupSong = { showPopUpAddSong.value = it }, viewModel)
    }
    Scaffold(
        topBar = {
            LibraryTopBar(
                title = "Your Library",
                onAddClick = { showPopUpAddSong.value = true }
            )

        },
        containerColor = Color.Black
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ){
            AndroidView(
                modifier = Modifier,
                factory = {
                    val view = View.inflate(it, R.layout.recyclerview_library, null).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }

                    val rvSongs: RecyclerView = view.findViewById(R.id.rv_songs)
                    rvSongs.setHasFixedSize(true)
                    rvSongs.layoutManager = LinearLayoutManager(localContext)

                    val listSongAdapter = ListSongAdapter(emptyList())
                    rvSongs.adapter = listSongAdapter

                    viewModel.getAllSongs().observe(lifecycleOwner) { songList ->
                        if (songList != null) {
                            listSongAdapter.setListSongs(songList)
                        }
                    }

                    view
                },
//                update = { }
            )
        }
    }
}


@Composable
fun LibraryTopBar(
    title: String,
    onAddClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(
                top = 25.dp,
                start = 18.dp,
                end = 18.dp,
                bottom = 12.dp,
                )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
            )
            IconButton(
                onClick = onAddClick
                ,
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Upload Song",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        LibraryButtons()
    }
    HorizontalDivider(
        thickness = 2.dp,
        color = colorResource(R.color.dark_gray),
        modifier = Modifier
            .padding(top = 145.dp)
    )

}


@Composable
fun LibraryButtons(){
    val choices = remember {
        mutableStateListOf("All", "Liked")
    }
    val selectedChoiceIndex = remember {
        mutableIntStateOf(0)
    }
    SingleChoiceSegmentedButtonRow{
        choices.forEachIndexed { index, choice ->
            SegmentedButton(
                selected = selectedChoiceIndex.intValue == index,
                onClick = { selectedChoiceIndex.intValue = index },
                colors = SegmentedButtonDefaults.colors(
                    activeBorderColor = Color.Transparent,
                    activeContainerColor = colorResource(R.color.green),
                    activeContentColor = Color.Black,
                    inactiveContentColor = Color.White,
                    inactiveBorderColor = Color.Transparent,
                    inactiveContainerColor = colorResource(R.color.dark_gray)
                ),
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = choices.count(),

                ),
                icon = {}
            ) {
                Text(
                    text = choice,
                    fontSize = 14.sp
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSong(setShowPopupSong: (Boolean)->Unit,libraryViewModel: LibraryViewModel){
    val title = remember  { mutableStateOf("") }
    val artist = remember{ mutableStateOf("") }
    val selectedImageUri = remember { mutableStateOf<Uri?>(null) }
    val selectedSongUri = remember { mutableStateOf<Uri?>(null) }
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = {
            coroutineScope.launch {
                sheetState.hide()
                setShowPopupSong(false)
            }
        },
        sheetState = sheetState,
        containerColor = colorResource(R.color.dark_gray),
        contentColor = colorResource(R.color.dark_gray),
        ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ){
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
                ImagePicker() {
                    uri->
                    selectedImageUri.value = uri
                }
                Spacer(
                    modifier = Modifier
                        .width(24.dp)
                )
                SongPicker {
                    uri->
                    selectedSongUri.value = uri
                }
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
                ),
        ) {
            CancelButton(coroutineScope, sheetState, setShowPopupSong)
            SaveButton(
                title = title.value,
                artist = artist.value,
                libraryViewModel = libraryViewModel,
                imageUri = selectedImageUri.value,
                songUri = selectedSongUri.value)
        }
        }
    }
}


@Composable
fun SaveButton(
    title: String,
    artist: String,
    imageUri: Uri?,
    songUri: Uri?,
    libraryViewModel: LibraryViewModel
) {
    val context = LocalContext.current

    Button(
        colors = ButtonDefaults.buttonColors(colorResource(R.color.green)),
        onClick = {
            if (imageUri != null && songUri != null) {

                val savedSongUri = copyUriToExternalStorage(context,songUri,getFileNameFromUri(context, songUri))
                val savedImageUri = copyUriToStorage(context,imageUri)


                libraryViewModel.insert(
                    Song(
                        title = title,
                        artist = artist,
                        owner = "meong",
                        image = savedImageUri.toString(),
                        audio = savedSongUri.toString()
                    )
                )
            } else {
                libraryViewModel.insert(
                    Song(
                        title = title,
                        artist = artist,
                        owner = "meong"
                    )
                )
            }
        },
        modifier = Modifier
            .height(36.dp)
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    ) {
        Text(
            text = "Save",
            color = Color.White
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CancelButton(coroutineScope: CoroutineScope,sheetState: SheetState,setShowPopupSong: (Boolean) -> Unit){
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

@Composable
fun AddSongTextField(inputText: MutableState<String>,labelText:String,isLast:Boolean){
    var insertImeAction = ImeAction.Next
    if(isLast) insertImeAction = ImeAction.Done
    OutlinedTextField(
        singleLine = true,
        value = inputText.value,
        onValueChange = { inputText.value = it },
        label = { Text(text = labelText) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = colorResource(R.color.dark_gray),
            unfocusedContainerColor = colorResource(R.color.dark_gray),
            disabledContainerColor = colorResource(R.color.dark_gray),
            focusedLabelColor = colorResource(R.color.green),
            unfocusedLabelColor = colorResource(R.color.medium_dark_gray),
            focusedIndicatorColor = colorResource(R.color.green),
            unfocusedIndicatorColor = colorResource(R.color.medium_dark_gray),
            focusedTextColor = colorResource(R.color.white),
            unfocusedTextColor = colorResource(R.color.medium_dark_gray),
            cursorColor = colorResource(R.color.green)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        keyboardOptions = KeyboardOptions(
            imeAction = insertImeAction
        )
    )
}

@Composable
fun ImagePicker(onImagePicked: (Uri?) -> Unit) {
    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onImagePicked(uri)
        imageUri.value = uri
    }
    Box(
        modifier = Modifier
            .size(96.dp)
            .clickable { launcher.launch("image/*") }
    ){
        if (imageUri.value != null) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUri.value)
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

@Composable
fun SongPicker(
    onSongPicked: (Uri?) -> Unit
) {
    val context = LocalContext.current
    val songUri = remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        songUri.value = uri
        onSongPicked(uri)
    }

        Box(
            modifier = Modifier
                .size(96.dp)
                .clickable { launcher.launch("audio/*") }
        ){
        if (songUri.value != null) {
            val fileName = getFileNameFromUri(context, songUri.value!!)
            Text(
                text = "Selected: $fileName",
                color = Color.White,
                fontSize = 14.sp
            )
        } else {
            Image(
                    imageVector = ImageVector.vectorResource(R.drawable.choose_song),
                    contentDescription = "Choose Song",
                    modifier = Modifier
                        .size(96.dp)
                )
        }
    }
}

fun getFileNameFromUri(context: Context, uri: Uri): String {
    var name = "unknown_file"
    val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex != -1 && it.moveToFirst()) {
            name = it.getString(nameIndex)
        }
    }
    return name
}

fun copyUriToExternalStorage(context: Context, uri: Uri, filename: String): Uri? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val outputDir = context.getExternalFilesDir("songs")
        if (inputStream != null && outputDir != null) {
            val outFile = File(outputDir, filename)
            FileOutputStream(outFile).use { output ->
                inputStream.copyTo(output)
            }
            Uri.fromFile(outFile)
        } else {
            null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun copyUriToStorage(context: Context, uri: Uri): Uri? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val fileName = "${System.currentTimeMillis()}.jpg"
        val outputFile = File(context.filesDir, fileName)

        inputStream?.use { input ->
            FileOutputStream(outputFile).use { output ->
                input.copyTo(output)
            }
        }
        Uri.fromFile(outputFile)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}





//@Preview(
//    showBackground = true,
//    backgroundColor = 0x000000,
//)
//@Composable
//fun preview(viewModel: LibraryViewModel){
//    LibraryLayout(viewModel)
//}
