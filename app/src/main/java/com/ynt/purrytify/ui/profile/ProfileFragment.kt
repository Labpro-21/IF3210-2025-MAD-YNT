package com.ynt.purrytify.ui.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil.compose.rememberAsyncImagePainter
import coil.load
import com.ynt.purrytify.R
import com.ynt.purrytify.databinding.FragmentProfileBinding
import com.ynt.purrytify.repository.SongRepository
import com.ynt.purrytify.utils.TokenStorage

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val profileViewModel =
//            ViewModelProvider(this)[ProfileViewModel::class.java]
        val profileViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[ProfileViewModel::class.java]
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.usernameText
//        profileViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val profileCompose: ComposeView = view.findViewById(R.id.profile_compose_view)
        val profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        profileCompose.setContent {
            ProfileScreen(profileViewModel)
        }
    }
}

@Composable
fun ProfileScreen(viewModel: ProfileViewModel) {
    val context = LocalContext.current
    val token = remember { TokenStorage(context).getAccessToken() }

    LaunchedEffect(token) {
        viewModel.loadProfile(token)
    }

    val resultState = viewModel.data.observeAsState()
    val result = resultState.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        result?.onSuccess { data ->

            Spacer(modifier = Modifier.height(100.dp))

            Image(
                painter = rememberAsyncImagePainter(data?.photoURL),
                contentDescription = "Profile Image",
                modifier = Modifier.size(150.dp).clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = data?.username ?: "No username",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = data?.location ?: "No location",
                fontSize = 16.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(50.dp))

            SongProfileDetail(viewModel)

        } ?: run {
            Text(
                text = "Memuat data...",
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun SongProfileDetail(viewModel: ProfileViewModel) {
//    val songCountState = viewModel.countSong.observeAsState()
//    val songCount = songCountState.value ?: 0
    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("0",
                color = Color.White,
                fontSize = 20.sp,
            )
            Text("Songs",
                color = Color.White,
                fontSize = 15.sp,
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("0",
                color = Color.White,
                fontSize = 20.sp,)
            Text("Liked",
                color = Color.White,
                fontSize = 15.sp
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("0",
                color = Color.White,
                fontSize = 20.sp
            )
            Text("Listened",
                color = Color.White,
                fontSize = 15.sp
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    LogoutButton()
}

