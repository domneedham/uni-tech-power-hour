package com.example.techpowerhour

import com.example.techpowerhour.data.service.LeaderboardService
import com.example.techpowerhour.data.service.PowerHourService
import com.example.techpowerhour.data.service.StatisticsService
import com.example.techpowerhour.data.service.UserService

object Services {
    val user by lazy {
        val ur = UserService()
        ur
    }
    val powerHour by lazy {
        val phr = PowerHourService()
        phr
    }
    val leaderboard by lazy {
        val lr = LeaderboardService(Repositories.user.value)
        lr
    }
    val statistics by lazy {
        val sr = StatisticsService()
        sr
    }
}