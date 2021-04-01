package com.example.techpowerhour.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.techpowerhour.data.model.PowerHour
import com.example.techpowerhour.util.DateHelper
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.DayOfWeek
import java.time.LocalDate
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

    fun update(powerHour: PowerHour) {
        powerHours.child(powerHour.id!!).setValue(powerHour)
    }

    fun delete(pourHour: PowerHour) {
        powerHours.child(pourHour.id!!).removeValue()
    }

    fun deleteAll() {
        powerHours.removeValue()
    }

    fun getTotalPointsEarned(): Int {
        return powerHoursLD.value?.sumOf { powerHour: PowerHour -> powerHour.points!! } ?: 0
    }

    fun getTotalPowerHoursCreated(): Int {
        return powerHoursLD.value?.size ?: 0
    }

    fun getTotalPointsEarnedForUser(userId: String): Int {
        // no need for live data as no way of possible updates when on user page
        // therefore called every refresh anyway
        val points = powerHoursLD.value
            ?.filter { powerHour: PowerHour -> powerHour.userId!! == userId }
            ?.sumOf { powerHour: PowerHour -> powerHour.points!! }

        return points ?: 0
    }

    fun getTotalPowerHoursCompletedForUser(userId: String): Int {
        // no need for live data as no way of possible updates when on user page
        // therefore called every refresh anyway
        val points = powerHoursLD.value
            ?.filter { powerHour: PowerHour -> powerHour.userId!! == userId }
            ?.count()

        return points ?: 0
    }

    fun getTotalPointsEarnedTodayForCompany(): LiveData<Int> {
        val points: MutableLiveData<Int> = MutableLiveData(0)
        val todayEpoch = DateHelper.todayEpoch
        powerHoursLD.observeForever {
            val observePoints = it
                    .filter { powerHour: PowerHour -> powerHour.epochDate!! >= todayEpoch }
                    .sumOf { powerHour: PowerHour -> powerHour.points!! }
            points.value = observePoints
        }
        return points
    }

    fun getTotalPointsEarnedThisWeekForCompany(): LiveData<Int> {
        val points: MutableLiveData<Int> = MutableLiveData(0)
        val weekEpoch = DateHelper.startOfWeekEpoch
        powerHoursLD.observeForever {
            val observePoints = it
                    .filter { powerHour: PowerHour -> powerHour.epochDate!! >= weekEpoch }
                    .sumOf { powerHour: PowerHour -> powerHour.points!! }
            points.value = observePoints
        }
        return points
    }

    fun getTotalPointsEarnedThisMonthForCompany(): LiveData<Int> {
        val points: MutableLiveData<Int> = MutableLiveData(0)
        // get difference between current date and first day of the month
        val monthEpoch = DateHelper.startOfMonthEpoch
        powerHoursLD.observeForever {
            val observePoints = it
                    .filter { powerHour: PowerHour -> powerHour.epochDate!! >= monthEpoch }
                    .sumOf { powerHour: PowerHour -> powerHour.points!! }
            points.value = observePoints
        }
        return points
    }

    fun getTotalPowerHoursCompletedTodayForCompany(): LiveData<Int> {
        val points: MutableLiveData<Int> = MutableLiveData(0)
        val todayEpoch = DateHelper.todayEpoch
        powerHoursLD.observeForever {
            val observePoints = it
                    .filter { powerHour: PowerHour -> powerHour.epochDate!! >= todayEpoch }
                    .count()
            points.value = observePoints
        }
        return points
    }

    fun getTotalPowerHoursCompletedThisWeekForCompany(): LiveData<Int> {
        val points: MutableLiveData<Int> = MutableLiveData(0)
        val weekEpoch = DateHelper.startOfWeekEpoch
        powerHoursLD.observeForever {
            val observePoints = it
                    .filter { powerHour: PowerHour -> powerHour.epochDate!! >= weekEpoch }
                    .count()
            points.value = observePoints
        }
        return points
    }

    fun getTotalPowerHoursCompletedThisMonthForCompany(): LiveData<Int> {
        val points: MutableLiveData<Int> = MutableLiveData(0)
        val monthEpoch = DateHelper.startOfMonthEpoch
        powerHoursLD.observeForever {
            val observePoints = it
                    .filter { powerHour: PowerHour -> powerHour.epochDate!! >= monthEpoch }
                    .count()
            points.value = observePoints
        }
        return points
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

    fun getPowerHourById(id: String): PowerHour? {
        return powerHoursLD.value?.find { ph -> ph.id == id }
    }
}