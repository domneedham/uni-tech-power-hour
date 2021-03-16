package com.example.techpowerhour.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.techpowerhour.data.model.PowerHour
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class PowerHourRepository() {
    private val database: FirebaseDatabase = Firebase.database
    private val powerHours: DatabaseReference = database.getReference("power_hour")

    val powerHoursLD = MutableLiveData<List<PowerHour>>()

    init {
        getAll()
    }

    fun insert(newPourHour: PowerHour) {
        val id = UUID.randomUUID().toString()
        powerHours.child(id).setValue(newPourHour)
    }


    fun delete(pourHour: PowerHour) {
        powerHours.child(pourHour.id!!).removeValue()
    }

    fun deleteAll() {
        powerHours.removeValue()
    }

    fun getTotalPointsEarned(): Int? {
        return powerHoursLD.value?.sumOf { powerHour: PowerHour -> powerHour.points!! }
    }

    fun getTotalPowerHoursCreated(): Int? {
        return powerHoursLD.value?.size
    }

    private fun getAll() {
        powerHours.addValueEventListener(object : ValueEventListener {
            val powerHoursArray = ArrayList<PowerHour>()
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                powerHoursArray.clear()
                for (childSnapshot in dataSnapshot.children) {
                    val powerHour = childSnapshot.getValue(PowerHour::class.java)
                    powerHour!!.id = childSnapshot.key
                    powerHoursArray.add(powerHour)
                }
                powerHoursLD.value = powerHoursArray
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("TAG", "onCancelled", databaseError.toException())
            }
        })
    }
}