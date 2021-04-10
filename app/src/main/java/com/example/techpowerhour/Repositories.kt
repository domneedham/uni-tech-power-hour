package com.example.techpowerhour

import com.example.techpowerhour.data.repository.LeaderboardRepository
import com.example.techpowerhour.data.repository.PowerHourRepository
import com.example.techpowerhour.data.repository.StatisticsRepository
import com.example.techpowerhour.data.repository.UserRepository

object Repositories {
    val user by lazy {
        val ur = UserRepository(Services.user)
        ur
    }
    val powerHour by lazy {
        val phr = PowerHourRepository(Services.powerHour)
        phr.onInit()
        phr
    }
    val leaderboard by lazy {
        val lr = LeaderboardRepository(Services.leaderboard)
        lr
    }
    val statistics by lazy {
        val sr = StatisticsRepository(Services.statistics)
        sr
    }
}