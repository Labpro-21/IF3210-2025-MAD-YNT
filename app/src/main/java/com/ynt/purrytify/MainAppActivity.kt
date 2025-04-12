package com.ynt.purrytify

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ynt.purrytify.databinding.ActivityMainBinding
import com.ynt.purrytify.utils.isUserAuthorized
import com.ynt.purrytify.utils.logout
import androidx.lifecycle.lifecycleScope
import com.ynt.purrytify.utils.isTokenValid
import kotlinx.coroutines.launch
class MainAppActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val tokenValid = isTokenValid(this@MainAppActivity)
            if (!tokenValid) {
                logout(this@MainAppActivity)
                return@launch
            }

            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            val navView: BottomNavigationView = binding.navView
            val navController = findNavController(R.id.nav_host_fragment_activity_main)

            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_home, R.id.navigation_library, R.id.navigation_profile
                )
            )

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            navView.setupWithNavController(navController)
//            val logoutButton = Button(this@MainAppActivity).apply {
//                text = "Logout"
//                setOnClickListener {
//                    logout(this@MainAppActivity)
//                }
//            }
//            binding.root.addView(logoutButton)
        }
    }

}