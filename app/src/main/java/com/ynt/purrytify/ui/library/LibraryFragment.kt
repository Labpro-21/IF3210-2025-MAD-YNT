package com.ynt.purrytify.ui.library

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.ynt.purrytify.R
import com.ynt.purrytify.databinding.FragmentLibraryBinding
import com.ynt.purrytify.ui.library.ui.AddSong
import com.ynt.purrytify.ui.library.ui.LibraryTopBar
import com.ynt.purrytify.ui.library.ui.SongListRecyclerView
import com.ynt.purrytify.utils.TokenStorage
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class LibraryFragment : Fragment() {
    private var _binding: FragmentLibraryBinding? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var libraryViewModel: LibraryViewModel

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
    val tokenStorage = remember { TokenStorage(localContext) }
    val token = tokenStorage.getAccessToken()
    val loggedInUser = viewModel.loggedInUser.observeAsState("")
    val selectedChoiceIndex = remember { mutableIntStateOf(0) }

    LaunchedEffect(token) {
        if (!token.isNullOrEmpty()) {
            viewModel.loadUserProfile(token)
        }
    }


    if (showPopUpAddSong.value) {
        AddSong(
            setShowPopupSong = { showPopUpAddSong.value = it },
            libraryViewModel = viewModel,
            loggedInUser = loggedInUser.value,
            context = localContext
        )
    }
    Scaffold(
        topBar = {
            LibraryTopBar(
                title = "Your Library",
                onAddClick = { showPopUpAddSong.value = true },
                selectedChoiceIndex = selectedChoiceIndex.intValue,
                onChoiceSelected = {selectedChoiceIndex.intValue =  it}
            )

        },
        containerColor = Color.Black
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding),
        ){
            if (!loggedInUser.value.isNullOrEmpty()) {
                SongListRecyclerView(
                    localContext = localContext,
                    lifecycleOwner = lifecycleOwner,
                    loggedInUser = loggedInUser.value,
                    viewModel = viewModel
                )
            }
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
