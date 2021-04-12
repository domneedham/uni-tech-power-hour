package com.example.techpowerhour.ui.leaderboard

import androidx.lifecycle.ViewModel
import com.example.techpowerhour.data.model.LeaderboardUser
import com.example.techpowerhour.data.repository.LeaderboardRepository

class LeaderboardViewModel(private val repository: LeaderboardRepository) : ViewModel() {
    /**
     * Fetch and return the leaderboard list for the current day.
     */
    suspend fun leaderboardToday() : List<LeaderboardUser> {
        return repository.getLeaderboardListForToday()
    }

    /**
     * Fetch and return the leaderboard list for the current week.
     */
    suspend fun leaderboardWeek(): List<LeaderboardUser> {
        return repository.getLeaderboardListForWeek()
    }

    /**
     * Fetch and return the leaderboard list for the current month.
     */
    suspend fun leaderboardMonth(): List<LeaderboardUser> {
        return repository.getLeaderboardListForMonth()
    }
}