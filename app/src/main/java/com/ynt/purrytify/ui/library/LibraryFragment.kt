package com.ynt.purrytify.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ynt.purrytify.R
import com.ynt.purrytify.databinding.FragmentLibraryBinding

class LibraryFragment : Fragment() {
    private var _binding: FragmentLibraryBinding? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val libraryViewModel =
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
            LibraryLayout()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

@Composable
fun LibraryLayout(){
    val context = LocalContext.current
    val showPopUpAddSong = remember { mutableStateOf(false) }
    if (showPopUpAddSong.value) {
        AddSong(setShowPopupSong = { showPopUpAddSong.value = it })
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
fun AddSong(setShowPopupSong: (Boolean)->Unit){
    val title = remember { mutableStateOf("") }
    val artist = remember { mutableStateOf("") }
    ModalBottomSheet(
        onDismissRequest = { setShowPopupSong(false)},
        containerColor = colorResource(R.color.dark_gray),
        contentColor = colorResource(R.color.dark_gray)
        ){
        Column(
            modifier = Modifier.fillMaxWidth()
        ){
            Text(
                text = "Upload Song",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
//            Row {
//            }
            //
//            TextField(
//                value = title.value,
//                onValueChange = { title.value = it },
//                label = { Text("Title") },
//            )
//            TextField(
//                value = artist.value,
//                onValueChange = { artist.value = it },
//                label = { Text("Artist") },
//            )
            //
        }
    }
}
