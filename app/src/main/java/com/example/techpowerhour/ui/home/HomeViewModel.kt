package com.example.techpowerhour.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.techpowerhour.data.repository.PowerHourRepository

class HomeViewModel(private val repository: PowerHourRepository) : ViewModel() {
    fun getTotalPointsEarnedTodayForCompany() : LiveData<Int> {
        return repository.getTotalPointsEarnedTodayForCompany()
    }

    fun getTotalPointsEarnedThisWeekForCompany() : LiveData<Int> {
        return repository.getTotalPointsEarnedThisWeekForCompany()
    }

    fun getTotalPointsEarnedThisMonthForCompany() : LiveData<Int> {
        return repository.getTotalPointsEarnedThisMonthForCompany()
    }

    fun getTotalPowerHoursCompletedTodayForCompany() : LiveData<Int> {
        return repository.getTotalPowerHoursCompletedTodayForCompany()
    }

    fun getTotalPowerHoursCompletedThisWeekForCompany() : LiveData<Int> {
        return repository.getTotalPowerHoursCompletedThisWeekForCompany()
    }

    fun getTotalPowerHoursCompletedThisMonthForCompany() : LiveData<Int> {
        return repository.getTotalPowerHoursCompletedThisMonthForCompany()
    }
}