package com.example.techpowerhour.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.techpowerhour.data.model.PowerHour
import com.example.techpowerhour.data.repository.PowerHourRepository
import com.google.firebase.auth.FirebaseAuth

class ProfileViewModel(private val repository: PowerHourRepository) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    val username = MutableLiveData<String>()

    init {
        username.value = auth.currentUser!!.displayName
    }

    /**
     * Fetch the user Power Hour list.
     */
    fun getPowerHours(): LiveData<List<PowerHour>> {
        return repository.userPowerHoursLD
    }

    /**
     * Signout from the app.
     */
    fun signOut() {
        auth.signOut()
    }
}