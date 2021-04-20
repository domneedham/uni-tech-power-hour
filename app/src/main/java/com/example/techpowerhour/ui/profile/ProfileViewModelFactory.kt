package com.example.techpowerhour.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.techpowerhour.data.repository.PowerHourRepository
import com.example.techpowerhour.data.repository.UserRepository

@Suppress("UNCHECKED_CAST")
class ProfileViewModelFactory(
        private val phRepo: PowerHourRepository,
        private val userRepo: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(phRepo, userRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}