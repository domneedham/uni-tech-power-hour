package com.example.techpowerhour.ui.add_power_hour

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techpowerhour.data.model.PowerHour
import com.example.techpowerhour.data.model.enums.PowerHourType
import com.example.techpowerhour.data.repository.PowerHourRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDate

class AddPowerHourViewModel(private val repository: PowerHourRepository) : ViewModel() {
    fun createNewPourHour(name: String, duration: Double, type: PowerHourType, date: LocalDate): PowerHour {
        val powerHour = PowerHour(
            name,
            duration,
            type,
            date.toEpochDay()
        )
        repository.insert(powerHour)
        return powerHour
    }
}