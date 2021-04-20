package com.example.techpowerhour.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.techpowerhour.data.model.PowerHour
import com.example.techpowerhour.data.repository.PowerHourRepository
import com.example.techpowerhour.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth

class ProfileViewModel(
        private val phRepo: PowerHourRepository,
        private val userRepo: UserRepository
) : ViewModel() {
    val username = MutableLiveData<String>()

    init {
        username.value = userRepo.currentUser!!.name!!
    }

    /**
     * Fetch the user Power Hour list.
     */
    fun getPowerHours(): LiveData<List<PowerHour>> {
        return phRepo.userPowerHoursLD
    }

    /**
     * Signout from the app.
     */
    fun signOut() {
        userRepo.signOut()
    }
}