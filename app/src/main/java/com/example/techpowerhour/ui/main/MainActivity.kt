package com.example.techpowerhour.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.techpowerhour.R
import com.example.techpowerhour.Repositories
import com.example.techpowerhour.ui.leaderboard.LeaderboardViewModel
import com.example.techpowerhour.ui.leaderboard.LeaderboardViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Repositories.onInit()

        setupViewModelBinding()

        setContentView(R.layout.activity_main)

        // Bottom navigation
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_leaderboard, R.id.navigation_profile))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        lifecycleScope.launch {
            viewModel.setUser()
        }
    }

    private fun setupViewModelBinding() {
        val viewModelFactory = MainActivityViewModelFactory(Repositories.user.value)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainActivityViewModel::class.java)
    }

    override fun onDestroy() {
        super.onDestroy()
        Repositories.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}