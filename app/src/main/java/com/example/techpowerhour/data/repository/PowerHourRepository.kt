package com.example.techpowerhour.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.techpowerhour.data.model.PowerHour
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.math.ceil
import java.util.*

class PowerHourRepository() {
    private val database: FirebaseDatabase = Firebase.database
    private val powerHours: DatabaseReference = database.reference

    val powerHoursLD = MutableLiveData<List<PowerHour>>()

    init {
//        getAll()
        getTest()
    }

    private fun getTest() {
        powerHours.child("contacts").get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }

    fun insert(newPourHour: PowerHour) {
        val id = 1
//        Log.v("data", newPourHour.toString())
//        powerHours.child(id.toString()).setValue(newPourHour)
        powerHours.child("hello").setValue("titan")
    }


    fun insertTest() {
        powerHours.child("hello").setValue("world")
    }

    fun delete(pourHour: PowerHour) {
        powerHours.child(pourHour.id.toString()).removeValue()
    }

    private fun getAll() {
        powerHours.addValueEventListener(object : ValueEventListener {
            val powerHoursArray = ArrayList<PowerHour>()
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                powerHoursArray.clear()
                for (childSnapshot in dataSnapshot.children) {
                    val powerHour = childSnapshot.getValue(PowerHour::class.java)
                    Log.v(dataSnapshot.key, powerHour.toString())
                    powerHour!!.id = childSnapshot.key?.toInt()
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