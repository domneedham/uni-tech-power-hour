package com.example.techpowerhour

import com.example.techpowerhour.data.repository.LeaderboardRepository
import com.example.techpowerhour.data.repository.PowerHourRepository
import com.example.techpowerhour.data.repository.StatisticsRepository
import com.example.techpowerhour.data.repository.UserRepository

/**
 * Singleton for holding a reference to all of the repositories for the application.
 * Each repository is lazy loaded.
 */
object Repositories {
    /**
     * Holds the instance of the [UserRepository] class.
     * The value is lazy loaded and will call the [UserRepository.onInit] method on first request.
     */
    val user = lazy {
        val ur = UserRepository(Services.user)
        ur.onInit()
        ur
    }

    /**
     * Holds the instance of the [PowerHourRepository] class.
     * The value is lazy loaded and will call the [PowerHourRepository.onInit] method on first request.
     */
    val powerHour = lazy {
        val phr = PowerHourRepository(Services.powerHour)
        phr.onInit()
        phr
    }

    /**
     * Holds the instance of the [LeaderboardRepository] class.
     * The value is lazy loaded and will call the [LeaderboardRepository.onInit] method on first request.
     */
    val leaderboard = lazy {
        val lr = LeaderboardRepository(Services.leaderboard)
        lr.onInit()
        lr
    }

    /**
     * Holds the instance of the [StatisticsRepository] class.
     * The value is lazy loaded and will call the [StatisticsRepository.onInit] method on first request.
     */
    val statistics = lazy {
        val sr = StatisticsRepository(Services.statistics)
        sr.onInit()
        sr
    }

    /**
     * To be called when the [MainActivity] is loaded.
     * On first load of the Activity, this method will do nothing as none of the repositories are initialised.
     * On subsequent loads (e.g. if a user does a logout and login), it will recall the onInit methods of the repositories.
     * This ensures cache values and variables are appropriately set for the user
     */
    fun onInit() {
        if (user.isInitialized()) user.value.onInit()
        if (powerHour.isInitialized()) powerHour.value.onInit()
        if (leaderboard.isInitialized()) leaderboard.value.onInit()
        if (statistics.isInitialized()) statistics.value.onInit()
    }

    /**
     * To be called when the [MainActivity] is destroyed.
     * This will call the onDestroy method of each repository.
     * The Activity is currently only destroyed if a user does a logout.
     * This method will do nothing to the repository if it has never been initialised thanks to lazy loading.
     */
    fun onDestroy() {
        if (user.isInitialized()) user.value.onDestroy()
        if (powerHour.isInitialized()) powerHour.value.onDestroy()
        if (leaderboard.isInitialized()) leaderboard.value.onDestroy()
        if (statistics.isInitialized()) statistics.value.onDestroy()
    }
}