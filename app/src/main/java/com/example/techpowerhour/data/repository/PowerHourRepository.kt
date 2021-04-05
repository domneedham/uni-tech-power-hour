package com.example.techpowerhour.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.techpowerhour.data.model.PowerHour
import com.example.techpowerhour.util.DateHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class PowerHourRepository() {
    private val loggedInUserId = FirebaseAuth.getInstance().uid!!

    private val database: FirebaseDatabase = Firebase.database
    private val powerHoursRef: DatabaseReference = database.getReference("power_hour")

    val userPowerHoursLD = MutableLiveData<List<PowerHour>>()

    init {
        getPowerHoursForUser()
    }

    fun insert(newPourHour: PowerHour) {
        val id = UUID.randomUUID().toString()
        powerHoursRef.child(id).setValue(newPourHour)
    }

    fun update(powerHour: PowerHour) {
        powerHoursRef.child(powerHour.id!!).setValue(powerHour)
    }

    fun delete(pourHour: PowerHour) {
        powerHoursRef.child(pourHour.id!!).removeValue()
    }

    fun deleteAll() {
        powerHoursRef.removeValue()
    }

    private fun getPowerHoursForUser() {
        val query = powerHoursRef.orderByChild("userId").equalTo(loggedInUserId)

        query.addValueEventListener(object : ValueEventListener {
            val powerHoursArray = ArrayList<PowerHour>()
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                powerHoursArray.clear()
                for (childSnapshot in dataSnapshot.children) {
                    val powerHour = childSnapshot.getValue(PowerHour::class.java)
                    powerHour!!.id = childSnapshot.key
                    powerHoursArray.add(powerHour)
                }
                userPowerHoursLD.value = powerHoursArray
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "onCancelled", error.toException())
            }
        })
    }

    fun getTotalPointsEarnedForUser(): Int {
        // no need for live data as no way of possible updates when on user page
        // therefore called every refresh anyway
        val points = userPowerHoursLD.value
            ?.sumOf { powerHour: PowerHour -> powerHour.points!! }

        return points ?: 0
    }

    fun getTotalPowerHoursCompletedForUser(): Int {
        // no need for live data as no way of possible updates when on user page
        // therefore called every refresh anyway
        val points = userPowerHoursLD.value?.count()

        return points ?: 0
    }

    fun getTotalPointsEarnedTodayForCompany(): LiveData<Int> {
        val totalPoints: MutableLiveData<Int> = MutableLiveData(0)
        val todayEpoch = DateHelper.todayEpoch

        val query = powerHoursRef.orderByChild("epochDate").equalTo(todayEpoch.toDouble())
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var points = 0
                for (childSnapshot in dataSnapshot.children) {
                    val snapshotPoints = childSnapshot.child("points").getValue(Int::class.java)
                    points += snapshotPoints!!
                }
                totalPoints.value = points
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "onCancelled", error.toException())
            }
        })

        return totalPoints
    }

    fun getTotalPointsEarnedThisWeekForCompany(): LiveData<Int> {
        val totalPoints: MutableLiveData<Int> = MutableLiveData(0)
        val weekEpoch = DateHelper.startOfWeekEpoch
        val query = powerHoursRef.orderByChild("epochDate").startAt(weekEpoch.toDouble())
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var points = 0
                for (childSnapshot in dataSnapshot.children) {
                    val snapshotPoints = childSnapshot.child("points").getValue(Int::class.java)
                    points += snapshotPoints!!
                }
                totalPoints.value = points
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "onCancelled", error.toException())
            }
        })
        return totalPoints
    }

    fun getTotalPointsEarnedThisMonthForCompany(): LiveData<Int> {
        val totalPoints: MutableLiveData<Int> = MutableLiveData(0)
        // get difference between current date and first day of the month
        val monthEpoch = DateHelper.startOfMonthEpoch
        val query = powerHoursRef.orderByChild("epochDate").startAt(monthEpoch.toDouble())
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var points = 0
                for (childSnapshot in dataSnapshot.children) {
                    val snapshotPoints = childSnapshot.child("points").getValue(Int::class.java)
                    points += snapshotPoints!!
                }
                totalPoints.value = points
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "onCancelled", error.toException())
            }
        })
        return totalPoints
    }

    fun getTotalPowerHoursCompletedTodayForCompany(): LiveData<Int> {
        val totalPowerHours: MutableLiveData<Int> = MutableLiveData(0)
        val todayEpoch = DateHelper.todayEpoch
        val query = powerHoursRef.orderByChild("epochDate").equalTo(todayEpoch.toDouble())
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                totalPowerHours.value = dataSnapshot.childrenCount.toInt()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "onCancelled", error.toException())
            }
        })
        return totalPowerHours
    }

    fun getTotalPowerHoursCompletedThisWeekForCompany(): LiveData<Int> {
        val totalPowerHours: MutableLiveData<Int> = MutableLiveData(0)
        val weekEpoch = DateHelper.startOfWeekEpoch
        val query = powerHoursRef.orderByChild("epochDate").startAt(weekEpoch.toDouble())
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                totalPowerHours.value = dataSnapshot.childrenCount.toInt()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "onCancelled", error.toException())
            }
        })
        return totalPowerHours
    }

    fun getTotalPowerHoursCompletedThisMonthForCompany(): LiveData<Int> {
        val totalPowerHours: MutableLiveData<Int> = MutableLiveData(0)
        val monthEpoch = DateHelper.startOfMonthEpoch
        val query = powerHoursRef.orderByChild("epochDate").startAt(monthEpoch.toDouble())
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                totalPowerHours.value = dataSnapshot.childrenCount.toInt()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "onCancelled", error.toException())
            }
        })
        return totalPowerHours
    }

    fun getUserPowerHourById(id: String) : PowerHour? {
        return userPowerHoursLD.value?.find { ph -> ph.id == id }
    }

    fun getPowerHourById(id: String): LiveData<PowerHour> {
        val powerHour = MutableLiveData<PowerHour>()
        powerHoursRef.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                powerHour.value = snapshot.getValue(PowerHour::class.java)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "onCancelled", error.toException())
            }
        })
        return powerHour
    }
}