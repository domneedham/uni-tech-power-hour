package com.example.techpowerhour.data.repository

import com.example.techpowerhour.data.model.LeaderboardUser
import com.example.techpowerhour.data.service.LeaderboardService

/**
 * The repository to handle all data regarding the Leaderboard functionality.
 * @param service The leaderboard service to fetch data from.
 */
class LeaderboardRepository(private val service: LeaderboardService) : BaseRepository() {
    /**
     * Gets the leaderboard user list for the current day.
     */
    suspend fun getLeaderboardListForToday(): List<LeaderboardUser> {
        return service.getLeaderboardListForToday()
    }

    /**
     * Gets the leaderboard user list for the current week.
     */
    suspend fun getLeaderboardListForWeek(): List<LeaderboardUser> {
        return service.getLeaderboardListForWeek()
    }

    /**
     * Gets the leaderboard user list for the current month.
     */
    suspend fun getLeaderboardListForMonth(): List<LeaderboardUser> {
        return service.getLeaderboardListForMonth()
    }
}