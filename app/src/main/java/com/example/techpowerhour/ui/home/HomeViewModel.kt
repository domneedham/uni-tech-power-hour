package com.example.techpowerhour.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techpowerhour.data.model.PowerHour
import com.example.techpowerhour.data.model.enums.PowerHourType
import com.example.techpowerhour.data.repository.PowerHourRepository
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeViewModel(private val repository: PowerHourRepository) : ViewModel() {
    fun addPowerHourWalk() = viewModelScope.launch {
        val newPowerHour = PowerHour("Walk", 30.0, PowerHourType.Walk, LocalDate.now().toEpochDay())
        repository.insert(newPowerHour)
    }

    fun addPowerHourRun() = viewModelScope.launch {
        val newPowerHour = PowerHour("Run", 30.0, PowerHourType.Run, LocalDate.now().toEpochDay())
        repository.insert(newPowerHour)
    }

    fun deleteAllPowerHours() = viewModelScope.launch {
        repository.deleteAll()
    }

    fun getAllPowerHours(): LiveData<List<PowerHour>> {
        return repository.powerHoursLD
    }

    fun getTotalPointsEarnedTodayForCompany() : LiveData<Int> {
        return repository.getTotalPointsEarnedTodayForCompany()
    }

    fun getTotalPointsEarnedThisWeekForCompany() : LiveData<Int> {
        return repository.getTotalPointsEarnedThisWeekForCompany()
    }

    fun getTotalPointsEarnedThisMonthForCompany() : LiveData<Int> {
        return repository.getTotalPowerHoursCompletedThisMonthForCompany()
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