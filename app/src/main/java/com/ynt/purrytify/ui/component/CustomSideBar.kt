package com.ynt.purrytify.ui.component

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ynt.purrytify.utils.Constants

@Composable
fun CustomSideBar(navController: NavController, modifier: Modifier){
    NavigationRail(
        containerColor = Color.Black,
        modifier = modifier
    ) {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry.value?.destination?.route
        Constants.BottomNavItems.forEach {
            NavigationRailItem(
                selected = currentRoute == it.route,
                icon = {
                    Icon(imageVector = it.icon, contentDescription = it.label)
                },
                label = {
                    Text(text = it.label)
                },
                onClick = {
                    navController.navigate(it.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                alwaysShowLabel = false,
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = Color.White,
                    indicatorColor = Color(0xFF1BB452)
                )
            )
        }
    }
}