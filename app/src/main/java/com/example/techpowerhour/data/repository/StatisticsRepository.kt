package com.example.techpowerhour.data.repository

import androidx.lifecycle.LiveData
import com.example.techpowerhour.data.service.StatisticsService

class StatisticsRepository(private val service: StatisticsService) : BaseRepository() {
    fun getTotalPointsEarnedTodayForCompany(): LiveData<Int> {
        return service.getTotalPointsEarnedTodayForCompany()
    }

    fun getTotalPointsEarnedThisWeekForCompany(): LiveData<Int> {
        return service.getTotalPointsEarnedThisWeekForCompany()
    }

    fun getTotalPointsEarnedThisMonthForCompany(): LiveData<Int> {
        return service.getTotalPointsEarnedThisMonthForCompany()
    }

    fun getTotalPowerHoursCompletedTodayForCompany(): LiveData<Int> {
        return service.getTotalPowerHoursCompletedTodayForCompany()
    }

    fun getTotalPowerHoursCompletedThisWeekForCompany(): LiveData<Int> {
        return service.getTotalPowerHoursCompletedThisWeekForCompany()
    }

    fun getTotalPowerHoursCompletedThisMonthForCompany(): LiveData<Int> {
        return service.getTotalPowerHoursCompletedThisMonthForCompany()
    }
}