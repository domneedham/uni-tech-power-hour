package com.example.techpowerhour.ui.add_power_hour

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.techpowerhour.data.repository.PowerHourRepository

@Suppress("UNCHECKED_CAST")
class AddPowerHourViewModelFactory(private val repository: PowerHourRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddPowerHourViewModel::class.java)) {
            return AddPowerHourViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}