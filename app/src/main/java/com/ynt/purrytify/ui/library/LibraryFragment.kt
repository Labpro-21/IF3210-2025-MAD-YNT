package com.ynt.purrytify.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryLayout(viewModel: LibraryViewModel){
    val showPopUpAddSong = remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val localContext = LocalContext.current
    val tokenStorage = remember { TokenStorage(localContext) }
    val token = tokenStorage.getAccessToken()
    val loggedInUser = viewModel.loggedInUser.observeAsState("")
    val selectedChoiceIndex = remember { mutableIntStateOf(0) }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

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
            context = localContext,
            sheetState = sheetState,
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
                    viewModel = viewModel,
                    choice = selectedChoiceIndex.intValue,
                    updateLikeSong = {
                        val songCopy = it.copy(isLiked = if(it.isLiked==1) 0 else 1)
                        viewModel.update(songCopy)
                    }
                )
            }
        }
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
