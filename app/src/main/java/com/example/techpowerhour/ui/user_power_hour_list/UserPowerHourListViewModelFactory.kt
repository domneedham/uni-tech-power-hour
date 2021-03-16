package com.example.techpowerhour.ui.user_power_hour_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.techpowerhour.data.repository.PowerHourRepository

@Suppress("UNCHECKED_CAST")
class UserPowerHourListViewModelFactory(private val repository: PowerHourRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserPowerHourListViewModel::class.java)) {
            return UserPowerHourListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}