package com.example.techpowerhour

import com.example.techpowerhour.data.repository.LeaderboardRepository
import com.example.techpowerhour.data.repository.PowerHourRepository
import com.example.techpowerhour.data.repository.UserRepository

object Repositories {
    val powerHour by lazy { PowerHourRepository() }
    val user by lazy { UserRepository() }
    val leaderboard by lazy { LeaderboardRepository() }
}