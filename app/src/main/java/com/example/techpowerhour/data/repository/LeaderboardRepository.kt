package com.example.techpowerhour.data.repository

import com.example.techpowerhour.data.model.LeaderboardUser
import com.example.techpowerhour.data.service.LeaderboardService

class LeaderboardRepository(private val service: LeaderboardService) : BaseRepository() {
    suspend fun getLeaderboardListForToday(): List<LeaderboardUser> {
        return service.getLeaderboardListForToday()
    }

    suspend fun getLeaderboardListForWeek(): List<LeaderboardUser> {
        return service.getLeaderboardListForWeek()
    }

    suspend fun getLeaderboardListForMonth(): List<LeaderboardUser> {
        return service.getLeaderboardListForMonth()
    }
}