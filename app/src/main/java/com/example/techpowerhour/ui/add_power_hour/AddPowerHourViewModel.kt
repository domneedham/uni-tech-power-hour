package com.example.techpowerhour.ui.add_power_hour

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techpowerhour.data.model.PowerHour
import com.example.techpowerhour.data.model.enums.PowerHourType
import com.example.techpowerhour.data.repository.PowerHourRepository
import kotlinx.coroutines.launch
import java.time.LocalDate

class AddPowerHourViewModel(val repository: PowerHourRepository) : ViewModel() {
    fun createNewPourHour(name: String, duration: Double, type: PowerHourType, date: LocalDate) = viewModelScope.launch {
        val powerHour = PowerHour(
            name,
            duration,
            type,
            date.toEpochDay()
        )
        repository.insert(powerHour)
    }
}