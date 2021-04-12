package com.example.techpowerhour.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.techpowerhour.data.repository.StatisticsRepository

class HomeViewModel(private val repository: StatisticsRepository) : ViewModel() {
    /**
     * Gets and returns the total points earned for the company for the current day.
     */
    fun getTotalPointsEarnedTodayForCompany() : LiveData<Int> {
        return repository.getTotalPointsEarnedTodayForCompany()
    }

    /**
     * Gets and returns the total points earned for the company for the current week.
     */
    fun getTotalPointsEarnedThisWeekForCompany() : LiveData<Int> {
        return repository.getTotalPointsEarnedThisWeekForCompany()
    }

    /**
     * Gets and returns the total points earned for the company for the current month.
     */
    fun getTotalPointsEarnedThisMonthForCompany() : LiveData<Int> {
        return repository.getTotalPointsEarnedThisMonthForCompany()
    }

    /**
     * Gets and returns the total Power Hours completed for the company for the current day.
     */
    fun getTotalPowerHoursCompletedTodayForCompany() : LiveData<Int> {
        return repository.getTotalPowerHoursCompletedTodayForCompany()
    }

    /**
     * Gets and returns the total Power Hours completed for the company for the current week.
     */
    fun getTotalPowerHoursCompletedThisWeekForCompany() : LiveData<Int> {
        return repository.getTotalPowerHoursCompletedThisWeekForCompany()
    }

    /**
     * Gets and returns the total Power Hours completed for the company for the current month.
     */
    fun getTotalPowerHoursCompletedThisMonthForCompany() : LiveData<Int> {
        return repository.getTotalPowerHoursCompletedThisMonthForCompany()
    }
}