package com.ynt.purrytify

import android.os.Build
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ynt.purrytify.utils.downloadmanager.DownloadHelper
import com.ynt.purrytify.models.Song
import com.ynt.purrytify.ui.component.BottomBar
import com.ynt.purrytify.ui.component.ConnectivityStatusBanner
import com.ynt.purrytify.ui.screen.audioroutingscreen.AudioRoutingScreen
import com.ynt.purrytify.ui.screen.homescreen.HomeScreen
import com.ynt.purrytify.ui.screen.libraryscreen.LibraryScreen
import com.ynt.purrytify.ui.screen.libraryscreen.LibraryViewModel
import com.ynt.purrytify.ui.screen.loginscreen.LoginScreen
import com.ynt.purrytify.ui.screen.audioroutingscreen.AudioRoutingScreen
import com.ynt.purrytify.ui.screen.player.SongPlayerSheet
import com.ynt.purrytify.ui.screen.editprofilescreen.EditProfileScreen
import com.ynt.purrytify.ui.screen.player.SongPlayerSheet
import com.ynt.purrytify.ui.screen.profilescreen.ProfileScreen
import com.ynt.purrytify.ui.screen.topchartscreen.TopSongScreen
import com.ynt.purrytify.ui.theme.PurrytifyTheme
import com.ynt.purrytify.utils.auth.SessionManager
import com.ynt.purrytify.utils.mediaplayer.MediaPlayerService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var sessionManager: SessionManager

    private val xisPlaying = MutableStateFlow(false)
    private val xcurrentDuration = MutableStateFlow(0f)
    private val xcurrentSong = MutableStateFlow(Song())
    private val _mediaBinder = mutableStateOf<MediaPlayerService.MediaBinder?>(null)
    private val mediaBinder get() = _mediaBinder.value
    private var service: MediaPlayerService? = null

    private var isBound = false
    private val isServiceReady = MutableStateFlow(false)
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            try {
                _mediaBinder.value = p1 as? MediaPlayerService.MediaBinder
                if (mediaBinder == null) {
                    Log.e("MainActivity", "Failed to cast binder to MediaBinder")
                    return
                }
                service = mediaBinder!!.getService()
                lifecycleScope.launch {
                    try {
                        mediaBinder?.isPlaying()?.collectLatest {
                            xisPlaying.value = it
                        }
                    } catch (e: Exception) {
                        Log.e("MainActivity", "Error collecting isPlaying", e)
                    }
                }
                lifecycleScope.launch {
                    try {
                        mediaBinder?.currentDuration()?.collectLatest {
                            xcurrentDuration.value = it
                        }
                    } catch (e: Exception) {
                        Log.e("MainActivity", "Error collecting currentDuration", e)
                    }
                }
                lifecycleScope.launch {
                    try {
                        mediaBinder?.getCurrentSong()?.collectLatest {
                            xcurrentSong.value = it
                        }
                    } catch (e: Exception) {
                        Log.e("MainActivity", "Error collecting currentSong", e)
                    }
                }
                isBound = true
                isServiceReady.value = true
            } catch (e: Exception) {
                Log.e("MainActivity", "Error in onServiceConnected", e)
            }
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isBound = false
            _mediaBinder.value = null
            service = null
            isServiceReady.value = false
        }
    }

    private lateinit var downloadHelper : DownloadHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(applicationContext)
        downloadHelper = DownloadHelper(this)
        enableEdgeToEdge()
        val serviceIntent = Intent(this, MediaPlayerService::class.java)
        startService(serviceIntent)

        setContent {
            val view = window.decorView
            val darkIcons = false
            SideEffect {
                window.statusBarColor = Color(0xFF121212).toArgb()
                window.navigationBarColor = Color.Black.toArgb()
                val controller = WindowInsetsControllerCompat(window, view)
                controller.isAppearanceLightStatusBars = darkIcons
                controller.isAppearanceLightNavigationBars = darkIcons
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .safeDrawingPadding()
            ) {
                val play: (song: Song) -> Unit = { mediaBinder?.setCurrentSong(it) }
                val seek: (pos: Float) -> Unit = {service?.seekTo(it)}
                val onPlayPause: ()->Unit = { service?.playPause() }
                val onNext: ()->Unit = { service?.next() }
                val onPrevious: ()->Unit = { service?.previous() }
                MainApp(
                    sessionManager = sessionManager,
                    xcurrentSong = xcurrentSong,
                    xcurrentDuration = xcurrentDuration,
                    xisPlaying = xisPlaying,
                    onSongsLoaded = { songs ->
                        if (!songs.isNullOrEmpty()) {
                            mediaBinder?.let { binder ->
                                try {
                                    binder.setSongList(songs)
                                } catch (e: Exception) {
                                    Log.e("MainActivity", "Error setting song list", e)
                                }
                            }
                        }
                    },
                    onPlayPause = onPlayPause,
                    onNext = onNext,
                    onPrevious = onPrevious,
                    onPlay = play,
                    onSeek = seek,
                    downloadHelper = downloadHelper,
                    mediaBinder = _mediaBinder.value,
                    isServiceReady = isServiceReady,
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, MediaPlayerService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }

}

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Library : Screen("library")
    data object Login : Screen("login")
    data object Profile: Screen("profile")
    data object EditProfile: Screen("editProfile")
    data object TopGlobalCharts : Screen("topGlobalCharts")
    data object TopRegionCharts : Screen("topRegionCharts")
    data object AudioRouting : Screen("audioRouting")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    sessionManager: SessionManager,
    xcurrentSong: MutableStateFlow<Song>,
    xcurrentDuration: MutableStateFlow<Float>,
    xisPlaying: MutableStateFlow<Boolean>,
    onSongsLoaded: (List<Song>?) -> Unit = {},
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onPlay: (song: Song) -> Unit,
    onSeek: (pos: Float) -> Unit,
    downloadHelper: DownloadHelper,
    mediaBinder: MediaPlayerService.MediaBinder?,
    isServiceReady: MutableStateFlow<Boolean>,
) {
    val currentSong by xcurrentSong.collectAsState()
    val currentDuration by xcurrentDuration.collectAsState()
    val isPlaying by xisPlaying.collectAsState()

//    val context = LocalContext.current
    val navController: NavHostController = rememberNavController()
    val libraryViewModel: LibraryViewModel = viewModel()
//    val lifecycleOwner = LocalLifecycleOwner.current
//    val topChartViewModel: TopChartViewModel = viewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute != Screen.Login.route

    val isLoggedIn = rememberSaveable { mutableStateOf(false) }
    val didAutoLogin = rememberSaveable { mutableStateOf(false) }

    val refreshScope = rememberCoroutineScope()
    var refreshJob by remember { mutableStateOf<Job?>(null) }

    val showSongPlayerSheet = rememberSaveable { mutableStateOf(false) }
    val showSongPlayerSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

//    val username = remember(isLoggedIn.value) {
//        if (isLoggedIn.value) sessionManager.getUser() else null
//    }

//    LaunchedEffect(username) {
//        libraryViewModel.getAllSongs(username ?: "").observe(lifecycleOwner) { songList ->
//            if (songList != null) {
//                onSongsLoaded(songList)
//            }
//        }
//    }

    fun startRefreshLoop() {
        refreshJob?.cancel()
        refreshJob = refreshScope.launch {
            while (isLoggedIn.value) {
                delay(240_000L)
                val success = sessionManager.refreshExpired()
                if (!success) {
                    isLoggedIn.value = false
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                    break
                }
            }
        }
    }

    fun stopRefreshLoop() {
        refreshJob?.cancel()
        refreshJob = null
    }

    LaunchedEffect(isLoggedIn.value, didAutoLogin.value) {
        if (isLoggedIn.value && !didAutoLogin.value) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
                launchSingleTop = true
            }
            startRefreshLoop()
            didAutoLogin.value = true
        }
    }

    LaunchedEffect(isLoggedIn.value) {
        if (isLoggedIn.value) {
            startRefreshLoop()
        } else {
            stopRefreshLoop()
        }
    }

    Scaffold(
        topBar = {
            ConnectivityStatusBanner()
        },
        bottomBar = {
            if (showBottomBar) {
                BottomBar(
                    navController = navController,
                    currentSong = currentSong,
                    xcurrentDuration = xcurrentDuration,
                    isPlaying = isPlaying,
                    onSkip = onNext,
                    onPlay = onPlayPause,
                    onClick = {
                        showSongPlayerSheet.value = true
                    }
                )
            }
        },
        containerColor = Color.Black
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(tween(300)) },
            exitTransition = { fadeOut(tween(300)) },
            popEnterTransition = { fadeIn(tween(300)) },
            popExitTransition = { fadeOut(tween(300)) }

        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        isLoggedIn.value = true
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    sessionManager = sessionManager
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    navController = navController,
                    sessionManager = sessionManager,
                    onSongsLoaded = onSongsLoaded,
                    showSongPlayerSheet = showSongPlayerSheet,
                    onPlay = onPlay,
                    currentSong = xcurrentSong
                )
            }

            composable(Screen.Library.route) {
                LibraryScreen(
                    navController = navController,
                    sessionManager = sessionManager,
                    viewModel = libraryViewModel,
                    showSongPlayerSheet = showSongPlayerSheet,
                    onPlay = onPlay,
                    currentSong = xcurrentSong,
                    onSongsLoaded = onSongsLoaded
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    navController = navController,
                    sessionManager = sessionManager,

                )
            }

            composable(Screen.TopGlobalCharts.route) {
                TopSongScreen(
                    navController = navController,
                    isRegion = false,
                    sessionManager = sessionManager,
                    downloadHelper = downloadHelper,
                    showSongPlayerSheet = showSongPlayerSheet,
                    onPlay = onPlay,
                    currentSong = xcurrentSong,
                    onSongsLoaded = onSongsLoaded
                )
            }

            composable(Screen.TopRegionCharts.route) {
                TopSongScreen(
                    navController = navController,
                    isRegion = true,
                    sessionManager = sessionManager,
                    downloadHelper = downloadHelper,
                    showSongPlayerSheet = showSongPlayerSheet,
                    onPlay = onPlay,
                    currentSong = xcurrentSong,
                    onSongsLoaded = onSongsLoaded
                )
            }

            composable(Screen.EditProfile.route) {
                EditProfileScreen(
                    navController = navController,
                    sessionManager = sessionManager
                )
            }

            composable(Screen.AudioRouting.route) {
                val ready by isServiceReady.collectAsState()
                if (ready && mediaBinder != null) {
                    AudioRoutingScreen(
                        mediaBinder = mediaBinder,
                        navController = navController
                    )
                } else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }

    if (showSongPlayerSheet.value) {
        SongPlayerSheet(
            setShowPopupSong = { showSongPlayerSheet.value = it },
            sheetState = showSongPlayerSheetState,
            xcurrentSong = xcurrentSong,
            libraryViewModel = libraryViewModel,
            currentPosition = currentDuration,
            isPlaying = isPlaying,
            onPlayPause = onPlayPause,
            onNext = onNext,
            onPrevious = onPrevious,
            onSeek = onSeek
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            stopRefreshLoop()
        }
    }

}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PurrytifyTheme {
    }
}
