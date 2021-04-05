package com.example.techpowerhour.ui.add_power_hour

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techpowerhour.data.model.PowerHour
import com.example.techpowerhour.data.model.enums.PowerHourType
import com.example.techpowerhour.data.repository.PowerHourRepository
import com.example.techpowerhour.util.DateHelper
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AddPowerHourViewModel(private val repository: PowerHourRepository) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    fun createNewPourHour(name: String, duration: String, type: String, date: String): PowerHour {
        val powerHour = PowerHour(
                name,
                duration.toDouble(),
                PowerHourType.valueOf(type),
                DateHelper.parseDateToEpoch(date),
                auth.uid
        )
        repository.insert(powerHour)
        return powerHour
    }

    fun updatePowerHour(powerHour: PowerHour, name: String, duration: String, type: String, date: String): PowerHour {
        powerHour.name = name
        powerHour.minutes = duration.toDouble()
        powerHour.type = PowerHourType.valueOf(type)
        powerHour.epochDate = DateHelper.parseDateToEpoch(date)

        repository.update(powerHour)

        return powerHour
    }

    fun getPowerHourById(id: String): PowerHour? {
        return repository.getUserPowerHourById(id)
    }
}