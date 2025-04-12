package com.ynt.purrytify.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ynt.purrytify.ui.login.LoginScreen
import com.ynt.purrytify.ui.home.HomeFragment
import com.ynt.purrytify.utils.TokenStorage

@Composable
fun AppNavigation(onNavigateToDashboard: () -> Unit){
    val navController = rememberNavController()
    val context = LocalContext.current
    val tokenStorage = remember { TokenStorage(context) }

    val startDestination = if (!tokenStorage.getAccessToken().isNullOrEmpty()) {
        // Go straight to Dashboard if token exists
        LaunchedEffect(Unit) {
            onNavigateToDashboard()
        }
        "login" // Won’t be shown but needed for NavHost
    } else {
        "login"
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    onNavigateToDashboard()
                }
            )
        }
    }
}