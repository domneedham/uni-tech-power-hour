package com.example.techpowerhour.data.repository

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.techpowerhour.data.repository.enums.DatabaseCollectionPaths
import com.example.techpowerhour.data.repository.enums.DatabaseStatisticsDocumentPaths
import com.example.techpowerhour.data.repository.enums.PowerHourDatabaseDateType
import com.example.techpowerhour.util.DateHelper
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class StatisticsRepository : BaseRepository() {
    private val db = Firebase.firestore
    private val statisticsRef = db.collection(DatabaseCollectionPaths.Statistics.path)

    fun getTotalPointsEarnedTodayForCompany(): LiveData<Int> {
        val totalPoints: MutableLiveData<Int> = MutableLiveData(0)
        val todayEpoch = DateHelper.todayEpoch.toString()

        val query = statisticsRef
                .document(PowerHourDatabaseDateType.Day.type)
                .collection(todayEpoch)
                .document(DatabaseStatisticsDocumentPaths.Points.path)
        query.get()
                .addOnSuccessListener { document ->
                    val value = document?.data?.get("total").toString()
                    val total = value.toDoubleOrNull()
                    totalPoints.value = total?.toInt() ?: 0
                }
                .addOnFailureListener { error ->
                    Log.d(ContentValues.TAG, "Error: $error")
                }

        return totalPoints
    }

    fun getTotalPointsEarnedThisWeekForCompany(): LiveData<Int> {
        val totalPoints: MutableLiveData<Int> = MutableLiveData(0)
        val weekEpoch = DateHelper.startOfWeekEpoch.toString()

        val query = statisticsRef
                .document(PowerHourDatabaseDateType.Week.type)
                .collection(weekEpoch)
                .document(DatabaseStatisticsDocumentPaths.Points.path)
        query.get()
                .addOnSuccessListener { document ->
                    val value = document?.data?.get("total").toString()
                    val total = value.toDoubleOrNull()
                    totalPoints.value = total?.toInt() ?: 0
                }
                .addOnFailureListener { error ->
                    Log.d(ContentValues.TAG, "Error: $error")
                }

        return totalPoints
    }

    fun getTotalPointsEarnedThisMonthForCompany(): LiveData<Int> {
        val totalPoints: MutableLiveData<Int> = MutableLiveData(0)
        val monthEpoch = DateHelper.startOfMonthEpoch.toString()

        val query = statisticsRef
                .document(PowerHourDatabaseDateType.Month.type)
                .collection(monthEpoch)
                .document(DatabaseStatisticsDocumentPaths.Points.path)
        query.get()
                .addOnSuccessListener { document ->
                    val value = document?.data?.get("total").toString()
                    val total = value.toDoubleOrNull()
                    totalPoints.value = total?.toInt() ?: 0
                }
                .addOnFailureListener { error ->
                    Log.d(ContentValues.TAG, "Error: $error")
                }

        return totalPoints
    }

    fun getTotalPowerHoursCompletedTodayForCompany(): LiveData<Int> {
        val totalPowerHours: MutableLiveData<Int> = MutableLiveData(0)
        val todayEpoch = DateHelper.todayEpoch.toString()

        val query = statisticsRef
                .document(PowerHourDatabaseDateType.Day.type)
                .collection(todayEpoch)
                .document(DatabaseStatisticsDocumentPaths.PowerHours.path)
        query.get()
                .addOnSuccessListener { document ->
                    val value = document?.data?.get("count").toString()
                    val count = value.toDoubleOrNull()
                    totalPowerHours.value = count?.toInt() ?: 0
                }
                .addOnFailureListener { error ->
                    Log.d(ContentValues.TAG, "Error: $error")
                }

        return totalPowerHours
    }

    fun getTotalPowerHoursCompletedThisWeekForCompany(): LiveData<Int> {
        val totalPowerHours: MutableLiveData<Int> = MutableLiveData(0)
        val weekEpoch = DateHelper.startOfWeekEpoch.toString()

        val query = statisticsRef
                .document(PowerHourDatabaseDateType.Week.type)
                .collection(weekEpoch)
                .document(DatabaseStatisticsDocumentPaths.PowerHours.path)
        query.get()
                .addOnSuccessListener { document ->
                    val value = document?.data?.get("count").toString()
                    val count = value.toDoubleOrNull()
                    totalPowerHours.value = count?.toInt() ?: 0
                }
                .addOnFailureListener { error ->
                    Log.d(ContentValues.TAG, "Error: $error")
                }

        return totalPowerHours
    }

    fun getTotalPowerHoursCompletedThisMonthForCompany(): LiveData<Int> {
        val totalPowerHours: MutableLiveData<Int> = MutableLiveData(0)
        val monthEpoch = DateHelper.startOfMonthEpoch.toString()

        val query = statisticsRef
                .document(PowerHourDatabaseDateType.Month.type)
                .collection(monthEpoch)
                .document(DatabaseStatisticsDocumentPaths.PowerHours.path)
        query.get()
                .addOnSuccessListener { document ->
                    val value = document?.data?.get("count").toString()
                    val count = value.toDoubleOrNull()
                    totalPowerHours.value = count?.toInt() ?: 0
                }
                .addOnFailureListener { error ->
                    Log.d(ContentValues.TAG, "Error: $error")
                }

        return totalPowerHours
    }
}