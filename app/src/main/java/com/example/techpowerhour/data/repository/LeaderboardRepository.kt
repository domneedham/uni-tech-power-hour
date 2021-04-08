package com.example.techpowerhour.data.repository

import android.content.ContentValues.TAG
import android.nfc.Tag
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.techpowerhour.data.model.LeaderboardUser
import com.example.techpowerhour.util.DateHelper
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LeaderboardRepository {
    private val db = Firebase.firestore
    private val leaderboardRef = db.collection("leaderboard")

    fun getLeaderboardListForToday(): MutableLiveData<List<LeaderboardUser>> {
        val leaderboardList = MutableLiveData<List<LeaderboardUser>>()
        val todayEpoch = DateHelper.todayEpoch.toString()

        val query = leaderboardRef.document("days").collection(todayEpoch)
        query.get()
            .addOnSuccessListener { documents ->
                val tempList = ArrayList<LeaderboardUser>()
                for (doc in documents) {
                    var points = 0.0
                    val value = doc.data["points"].toString()
                    points += value.toDouble()

                    val leaderboardUser = LeaderboardUser(doc.id, doc.id, points.toInt())
                    tempList.add(leaderboardUser)
                }
                leaderboardList.value = tempList.sortedByDescending { lu -> lu.points }
            }
            .addOnFailureListener { error ->
                Log.d(TAG, "Error: $error")
            }

        return leaderboardList
    }

    fun getLeaderboardListForWeek(): MutableLiveData<List<LeaderboardUser>> {
        val leaderboardList = MutableLiveData<List<LeaderboardUser>>()
        val weekEpoch = DateHelper.startOfWeekEpoch.toString()

        val query = leaderboardRef.document("weeks").collection(weekEpoch)
        query.get()
            .addOnSuccessListener { documents ->
                val tempList = ArrayList<LeaderboardUser>()
                for (doc in documents) {
                    var points = 0.0
                    val value = doc.data["points"].toString()
                    points += value.toDouble()

                    val leaderboardUser = LeaderboardUser(doc.id, doc.id, points.toInt())
                    tempList.add(leaderboardUser)
                }
                leaderboardList.value = tempList.sortedByDescending { lu -> lu.points }
            }
            .addOnFailureListener { error ->
                Log.d(TAG, "Error: $error")
            }

        return leaderboardList
    }

    fun getLeaderboardListForMonth(): MutableLiveData<List<LeaderboardUser>> {
        val leaderboardList = MutableLiveData<List<LeaderboardUser>>()
        val monthEpoch = DateHelper.startOfMonthEpoch.toString()

        val query = leaderboardRef.document("months").collection(monthEpoch)
        query.get()
            .addOnSuccessListener { documents ->
                val tempList = ArrayList<LeaderboardUser>()
                for (doc in documents) {
                    var points = 0.0
                    val value = doc.data["points"].toString()
                    points += value.toDouble()

                    val leaderboardUser = LeaderboardUser(doc.id, doc.id, points.toInt())
                    tempList.add(leaderboardUser)
                }
                leaderboardList.value = tempList.sortedByDescending { lu -> lu.points }
            }
            .addOnFailureListener { error ->
                Log.d(TAG, "Error: $error")
            }

        return leaderboardList
    }
}