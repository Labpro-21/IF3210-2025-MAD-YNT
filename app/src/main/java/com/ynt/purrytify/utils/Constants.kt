package com.ynt.purrytify.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Person
import com.ynt.purrytify.Screen
import com.ynt.purrytify.models.BottomNavItem

object Constants {
    val BottomNavItems = listOf(
        BottomNavItem(
            label = "Home",
            icon = Icons.Filled.Home,
            route = Screen.Home.route,
        ),
        BottomNavItem(
            label = "Library",
            icon = Icons.Filled.LibraryMusic,
            route = Screen.Library.route,
        ),
        BottomNavItem(
            label = "Profile",
            icon = Icons.Filled.Person,
            route = Screen.Profile.route,
        ),
    )
}