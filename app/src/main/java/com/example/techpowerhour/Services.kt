package com.example.techpowerhour

import com.example.techpowerhour.data.service.LeaderboardService
import com.example.techpowerhour.data.service.PowerHourService
import com.example.techpowerhour.data.service.StatisticsService
import com.example.techpowerhour.data.service.UserService

/**
 * Singleton for holding a reference to all of the services for the application.
 * Each service is lazy loaded.
 */
object Services {
    /**
     * Holds the instance of the [UserService] class. The value is lazy loaded.
     */
    val user by lazy { UserService() }

    /**
     * Holds the instance of the [PowerHourService] class. The value is lazy loaded.
     */
    val powerHour by lazy { PowerHourService() }

    /**
     * Holds the instance of the [LeaderboardService] class. The value is lazy loaded.
     */
    val leaderboard by lazy { LeaderboardService(Repositories.user.value) }

    /**
     * Holds the instance of the [StatisticsService] class. The value is lazy loaded.
     */
    val statistics by lazy { StatisticsService() }
}