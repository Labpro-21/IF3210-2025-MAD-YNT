package com.ynt.purrytify

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
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
import com.ynt.purrytify.ui.screen.player.SongPlayerSheet
import com.ynt.purrytify.ui.screen.editprofilescreen.EditProfileScreen
import com.ynt.purrytify.ui.screen.player.PlaybackViewModel
import com.ynt.purrytify.ui.screen.profilescreen.ProfileScreen
import com.ynt.purrytify.ui.screen.topchartscreen.TopSongScreen
import com.ynt.purrytify.ui.theme.PurrytifyTheme
import com.ynt.purrytify.utils.auth.SessionManager
import com.ynt.purrytify.utils.mediaplayer.PlaybackService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var downloadHelper : DownloadHelper
    private val playbackViewModel: PlaybackViewModel by viewModels()

    @androidx.annotation.OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(applicationContext)
        downloadHelper = DownloadHelper(this)
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
                MainApp(
                    sessionManager = sessionManager,
                    downloadHelper = downloadHelper,
                    playbackViewModel = playbackViewModel
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playbackViewModel.disconnect()
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

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    sessionManager: SessionManager,
    downloadHelper: DownloadHelper,
    playbackViewModel: PlaybackViewModel
) {
    val navController: NavHostController = rememberNavController()
    val libraryViewModel: LibraryViewModel = viewModel()
    val context =  LocalContext.current

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

    LaunchedEffect(Unit) {
        playbackViewModel.connect()
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
            playbackViewModel.sendUser(sessionManager.getUser())
            startRefreshLoop()
            didAutoLogin.value = true
        }
    }

    LaunchedEffect(isLoggedIn.value) {
        if (isLoggedIn.value) {
            playbackViewModel.sendUser(sessionManager.getUser())
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
                    playbackViewModel = playbackViewModel,
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
                    showSongPlayerSheet = showSongPlayerSheet,
                    playbackViewModel = playbackViewModel,
                    libraryViewModel = libraryViewModel
                )
            }

            composable(Screen.Library.route) {
                LibraryScreen(
                    navController = navController,
                    sessionManager = sessionManager,
                    viewModel = libraryViewModel,
                    playbackViewModel = playbackViewModel,
                    showSongPlayerSheet = showSongPlayerSheet,
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
                    playbackViewModel = playbackViewModel,
                )
            }

            composable(Screen.TopRegionCharts.route) {
                TopSongScreen(
                    navController = navController,
                    isRegion = true,
                    sessionManager = sessionManager,
                    downloadHelper = downloadHelper,
                    showSongPlayerSheet = showSongPlayerSheet,
                    playbackViewModel = playbackViewModel
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
                    playbackViewModel = playbackViewModel,
                    navController = navController
                )
            }
        }
    }

    if (showSongPlayerSheet.value) {
        SongPlayerSheet(
            setShowPopupSong = { showSongPlayerSheet.value = it },
            sheetState = showSongPlayerSheetState,
            libraryViewModel = libraryViewModel,
            playbackViewModel = playbackViewModel,
            sessionManager = sessionManager
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            stopRefreshLoop()
        }
    }

}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PurrytifyTheme {
    }
}
