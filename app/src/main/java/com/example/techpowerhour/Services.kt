package com.example.techpowerhour

import com.example.techpowerhour.data.service.LeaderboardService
import com.example.techpowerhour.data.service.PowerHourService
import com.example.techpowerhour.data.service.StatisticsService
import com.example.techpowerhour.data.service.UserService

object Services {
    val user by lazy { UserService() }
    val powerHour by lazy { PowerHourService() }
    val leaderboard by lazy { LeaderboardService(Repositories.user.value) }
    val statistics by lazy { StatisticsService() }
}