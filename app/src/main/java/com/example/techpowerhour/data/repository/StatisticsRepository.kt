package com.example.techpowerhour.data.repository

import androidx.lifecycle.LiveData
import com.example.techpowerhour.data.service.StatisticsService

/**
 * The repository to handle fetching and returning company statistics information.
 * @param service The statistics service to fetch data from.
 */
class StatisticsRepository(private val service: StatisticsService) : BaseRepository() {
    /**
     * Gets and subscribes to the total points earned by the company for the current day.
     */
    fun getTotalPointsEarnedTodayForCompany(): LiveData<Int> {
        return service.getTotalPointsEarnedTodayForCompany()
    }

    /**
     * Gets and subscribes to the total points earned by the company for the current week.
     */
    fun getTotalPointsEarnedThisWeekForCompany(): LiveData<Int> {
        return service.getTotalPointsEarnedThisWeekForCompany()
    }

    /**
     * Gets and subscribes to the total points earned by the company for the current month.
     */
    fun getTotalPointsEarnedThisMonthForCompany(): LiveData<Int> {
        return service.getTotalPointsEarnedThisMonthForCompany()
    }

    /**
     * Gets and subscribes to the total Power Hours completed the company for the current day.
     */
    fun getTotalPowerHoursCompletedTodayForCompany(): LiveData<Int> {
        return service.getTotalPowerHoursCompletedTodayForCompany()
    }

    /**
     * Gets and subscribes to the total Power Hours completed the company for the current week.
     */
    fun getTotalPowerHoursCompletedThisWeekForCompany(): LiveData<Int> {
        return service.getTotalPowerHoursCompletedThisWeekForCompany()
    }

    /**
     * Gets and subscribes to the total Power Hours completed the company for the current month.
     */
    fun getTotalPowerHoursCompletedThisMonthForCompany(): LiveData<Int> {
        return service.getTotalPowerHoursCompletedThisMonthForCompany()
    }
}