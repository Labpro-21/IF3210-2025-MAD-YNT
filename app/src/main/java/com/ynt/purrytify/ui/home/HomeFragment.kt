package com.ynt.purrytify.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ynt.purrytify.R
import com.ynt.purrytify.databinding.FragmentHomeBinding
import com.ynt.purrytify.ui.profile.ProfileViewModel
import com.ynt.purrytify.utils.TokenStorage

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val homeViewModel =
//            ViewModelProvider(this).get(HomeViewModel::class.java)
        val homeViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[HomeViewModel::class.java]
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val homeCompose: ComposeView = view.findViewById(R.id.home_compose_view)
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]
        homeCompose.setContent {
            HomeScreen(homeViewModel)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val context = LocalContext.current
    val token = remember { TokenStorage(context).getAccessToken() }

    Column (
        modifier = Modifier
        .padding(30.dp),
        horizontalAlignment = Alignment.Start
    ) {
        NewSongs()
        RecentlyPlayed()
    }
}