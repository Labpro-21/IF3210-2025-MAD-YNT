package com.ynt.purrytify

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ynt.purrytify.ui.component.CustomNavBar
import com.ynt.purrytify.ui.screen.homescreen.HomeScreen
import com.ynt.purrytify.ui.screen.libraryscreen.LibraryScreen
import com.ynt.purrytify.ui.screen.loginscreen.LoginScreen
import com.ynt.purrytify.ui.screen.profilescreen.ProfileScreen
import com.ynt.purrytify.ui.theme.PurrytifyTheme
import com.ynt.purrytify.utils.SessionManager
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
}


@Composable
fun MainApp(sessionManager: SessionManager) {
    val context = LocalContext.current
    val navController: NavHostController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute != Screen.Login.route

    val isLoggedIn = remember { mutableStateOf(false) }

    val refreshScope = rememberCoroutineScope()
    var refreshJob by remember { mutableStateOf<Job?>(null) }

    fun startRefreshLoop() {
        refreshJob?.cancel()
        refreshJob = refreshScope.launch {
            while (isLoggedIn.value) {
                delay(240_000L)
                val success = sessionManager.refreshExpired()
//                Log.d("TOKEN", "Refresh: $success")
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

    LaunchedEffect(Unit) {
        val loggedIn = sessionManager.isLoggedIn()
        isLoggedIn.value = loggedIn

        if (loggedIn) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
            startRefreshLoop()
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
        bottomBar = {
            if (showBottomBar) {
                CustomNavBar(navController = navController)
            }
        },
        containerColor = Color(0xFF121212)
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
                    navController = navController
                )
            }

            composable(Screen.Library.route) {
                LibraryScreen(
                    navController = navController
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    navController = navController
                )
            }
        }
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