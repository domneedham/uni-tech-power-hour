package com.example.techpowerhour.ui.user_power_hour_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.techpowerhour.data.model.PowerHour
import com.example.techpowerhour.data.repository.PowerHourRepository

class UserPowerHourListViewModel(private val repository: PowerHourRepository) : ViewModel() {
    fun getAllPowerHours(): LiveData<List<PowerHour>> {
        return repository.powerHoursLD
    }
}