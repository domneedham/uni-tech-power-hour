package com.example.techpowerhour.ui.home

import androidx.lifecycle.*
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

    fun addPowerHourRun() {
        val newPowerHour = PowerHour("Run", 30.0, PowerHourType.Run, LocalDate.now().toEpochDay())
        repository.insertTest()
    }

    fun deleteAllPowerHours() = viewModelScope.launch {
//        repository.deleteAll()
    }

    fun getAllPowerHours(): LiveData<List<PowerHour>> {
        return repository.powerHoursLD
    }
}