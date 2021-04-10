package com.example.techpowerhour

import com.example.techpowerhour.data.repository.LeaderboardRepository
import com.example.techpowerhour.data.repository.PowerHourRepository
import com.example.techpowerhour.data.repository.StatisticsRepository
import com.example.techpowerhour.data.repository.UserRepository

object Repositories {
    val user by lazy {
        val ur = UserRepository()
        ur
    }
    val powerHour by lazy {
        val phr = PowerHourRepository()
        phr
    }
    val leaderboard by lazy {
        val lr = LeaderboardRepository(user)
        lr
    }
    val statistics by lazy {
        val sr = StatisticsRepository()
        sr
    }

    fun onInit() {
        user.onInit()
        powerHour.onInit()
        leaderboard.onInit()
        statistics.onInit()
    }

    fun onDestroy() {
        user.onDestroy()
        powerHour.onDestroy()
        leaderboard.onDestroy()
        statistics.onDestroy()
    }
}