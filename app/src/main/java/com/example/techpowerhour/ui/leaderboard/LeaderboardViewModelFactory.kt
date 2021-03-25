package com.example.techpowerhour.ui.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.techpowerhour.data.repository.PowerHourRepository

@Suppress("UNCHECKED_CAST")
class LeaderboardViewModelFactory(private val repository: PowerHourRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LeaderboardViewModel::class.java)) {
            return LeaderboardViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}