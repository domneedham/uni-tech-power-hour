package com.example.techpowerhour.ui.main

import androidx.lifecycle.ViewModel
import com.example.techpowerhour.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth

class MainActivityViewModel(private val userRepo: UserRepository) : ViewModel() {
    val auth = FirebaseAuth.getInstance()

    suspend fun setUser() {
        userRepo.setCurrentUser(auth.uid!!)
    }
}