package com.example.techpowerhour

import com.example.techpowerhour.data.repository.LeaderboardRepository
import com.example.techpowerhour.data.repository.PowerHourRepository
import com.example.techpowerhour.data.repository.StatisticsRepository
import com.example.techpowerhour.data.repository.UserRepository

object Repositories {
    val user = lazy {
        val ur = UserRepository(Services.user)
        ur.onInit()
        ur
    }
    val powerHour = lazy {
        val phr = PowerHourRepository(Services.powerHour)
        phr.onInit()
        phr
    }
    val leaderboard = lazy {
        val lr = LeaderboardRepository(Services.leaderboard)
        lr.onInit()
        lr
    }
    val statistics = lazy {
        val sr = StatisticsRepository(Services.statistics)
        sr.onInit()
        sr
    }

    fun onInit() {
        if (user.isInitialized()) user.value.onInit()
        if (powerHour.isInitialized()) powerHour.value.onInit()
        if (leaderboard.isInitialized()) leaderboard.value.onInit()
        if (statistics.isInitialized()) statistics.value.onInit()
    }

    fun onDestroy() {
        if (user.isInitialized()) user.value.onDestroy()
        if (powerHour.isInitialized()) powerHour.value.onDestroy()
        if (leaderboard.isInitialized()) leaderboard.value.onDestroy()
        if (statistics.isInitialized()) statistics.value.onDestroy()
    }
}