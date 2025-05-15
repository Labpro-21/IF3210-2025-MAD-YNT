package com.ynt.purrytify

import android.os.Build
import android.os.Bundle
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ynt.purrytify.models.Song
import com.ynt.purrytify.ui.component.BottomBar
import com.ynt.purrytify.ui.component.ConnectivityStatusBanner
import com.ynt.purrytify.ui.screen.homescreen.HomeScreen
import com.ynt.purrytify.ui.screen.libraryscreen.LibraryScreen
import com.ynt.purrytify.ui.screen.libraryscreen.LibraryViewModel
import com.ynt.purrytify.ui.screen.loginscreen.LoginScreen
import com.ynt.purrytify.ui.screen.audioroutingscreen.AudioRoutingScreen
import com.ynt.purrytify.ui.screen.player.SongPlayerSheet
import com.ynt.purrytify.ui.screen.editprofilescreen.EditProfileScreen
import com.ynt.purrytify.ui.screen.profilescreen.ProfileScreen
import com.ynt.purrytify.ui.theme.PurrytifyTheme
import com.ynt.purrytify.utils.auth.SessionManager
import com.ynt.purrytify.utils.mediaplayer.SongPlayerLiveData
import com.ynt.purrytify.utils.queue.QueueManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(applicationContext)
        enableEdgeToEdge()
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
                MainApp(sessionManager)
            }
        }
    }
}

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Library : Screen("library")
    data object Login : Screen("login")
    data object Profile: Screen("profile")
    data object  AudioRouting: Screen("audiorouting")
    data object EditProfile: Screen("editProfile")
}

enum class PlayerState {
    STARTED, PAUSED, PLAYING, STOPPED
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    sessionManager: SessionManager,
    songPlayerLiveData: SongPlayerLiveData = viewModel(),
    queueManager: QueueManager = remember { QueueManager() }
) {
    val context = LocalContext.current
    val navController: NavHostController = rememberNavController()
    val libraryViewModel: LibraryViewModel = viewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute != Screen.Login.route

    val isLoggedIn = rememberSaveable { mutableStateOf(false) }
    val didAutoLogin = rememberSaveable { mutableStateOf(false) }

    val refreshScope = rememberCoroutineScope()
    var refreshJob by remember { mutableStateOf<Job?>(null) }

    val currentSong = rememberSaveable{ mutableStateOf<Song?>(null) }
    val currentSongPosition = rememberSaveable { mutableIntStateOf(0) }
    val isPlaying = rememberSaveable { mutableStateOf(PlayerState.STOPPED) }

    val showSongPlayerSheet = rememberSaveable { mutableStateOf(false) }
    val showSongPlayerSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val configuration = LocalConfiguration.current

    LaunchedEffect(configuration.orientation){
        with(songPlayerLiveData.songPlayer.value){
            currentSongPosition.intValue = this?.getCurrentPosition()!!
        }
    }

    LaunchedEffect(isPlaying.value) {
        if (isPlaying.value == PlayerState.PLAYING) {
            while (isPlaying.value == PlayerState.PLAYING) {
                val position = songPlayerLiveData.songPlayer.value?.getCurrentPosition()
                if (position != null) {
                    currentSongPosition.intValue = position
                }
                delay(500)
            }
        }
    }

    LaunchedEffect(isPlaying.value) {
        with(songPlayerLiveData.songPlayer.value) {
            when (isPlaying.value) {
                PlayerState.STOPPED -> {
                }

                PlayerState.PLAYING -> {
                    this?.resumeAudio(currentSongPosition)
                }

                PlayerState.PAUSED -> {
                    this?.pauseAudio(currentSongPosition)
                }
                PlayerState.STARTED -> {
                    currentSong.value?.let { song ->
                        this?.playAudioFromUri(
                            context,
                            song.audio?.toUri() ?: "".toUri(),
                            isPlaying
                        )
                    }
                }
            }
        }
    }

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
                    isPlaying = isPlaying,
                    onSkip = {
                        val nextSong = queueManager.skipToNext()
                        currentSong.value = nextSong
                        val songCopy = nextSong?.copy(lastPlayed = System.currentTimeMillis())
                        if (songCopy != null) {
                            libraryViewModel.update(songCopy)
                        }
                        currentSongPosition.intValue = 0
                        if(queueManager.size.value==0){
                            isPlaying.value = PlayerState.STOPPED
                        }
                        else{
                            isPlaying.value = PlayerState.STARTED
                        }
                    },
                    onPlay = {
                        isPlaying.value = when (isPlaying.value){
                            PlayerState.STOPPED -> PlayerState.STARTED
                            PlayerState.PAUSED -> PlayerState.PLAYING
                            PlayerState.STARTED -> PlayerState.PAUSED
                            PlayerState.PLAYING -> PlayerState.PAUSED
                        }
                    },
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
                    sessionManager = sessionManager
                )
            }

            composable(Screen.Library.route) {
                LibraryScreen(
                    navController = navController,
                    sessionManager = sessionManager,
                    currentSong = currentSong,
                    currentSongPosition = currentSongPosition,
                    isPlaying = isPlaying,
                    queueManager = queueManager,
                    viewModel = libraryViewModel,
                    showSongPlayerSheet = showSongPlayerSheet
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    navController = navController,
                    sessionManager = sessionManager,
                )
            }

            composable(Screen.EditProfile.route) {
                EditProfileScreen(
                    navController = navController,
                    sessionManager = sessionManager
                )
            }

            composable(Screen.AudioRouting.route) {
                AudioRoutingScreen(
                    navController = navController,
                    songPlayerLiveData = songPlayerLiveData
                )
            }
        }
    }

    if (showSongPlayerSheet.value) {
        SongPlayerSheet(
            setShowPopupSong = {showSongPlayerSheet.value = it},
            queueManager = queueManager,
            sheetState = showSongPlayerSheetState,
            currentSong = currentSong,
            libraryViewModel = libraryViewModel,
            currentPosition = currentSongPosition,
            isPlaying = isPlaying,
            songPlayerLiveData = songPlayerLiveData
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
