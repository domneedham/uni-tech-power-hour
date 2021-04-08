package com.example.techpowerhour.ui.leaderboard

import androidx.lifecycle.ViewModel
import com.example.techpowerhour.data.model.LeaderboardUser
import com.example.techpowerhour.data.repository.LeaderboardRepository

class LeaderboardViewModel(private val repository: LeaderboardRepository) : ViewModel() {
    suspend fun leaderboardToday() : List<LeaderboardUser> {
        return repository.getLeaderboardListForToday()
    }

    suspend fun leaderboardWeek(): List<LeaderboardUser> {
        return repository.getLeaderboardListForWeek()
    }

    suspend fun leaderboardMonth(): List<LeaderboardUser> {
        return repository.getLeaderboardListForMonth()
    }
}