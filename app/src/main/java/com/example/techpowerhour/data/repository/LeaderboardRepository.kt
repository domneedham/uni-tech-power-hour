package com.example.techpowerhour.data.repository

import com.example.techpowerhour.data.model.LeaderboardUser
import com.example.techpowerhour.data.repository.enums.DatabaseCollectionPaths
import com.example.techpowerhour.data.repository.enums.PowerHourDatabaseDateType
import com.example.techpowerhour.util.DateHelper
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class LeaderboardRepository (private val userRepository: UserRepository) {
    private val db = Firebase.firestore
    private val leaderboardRef = db.collection(DatabaseCollectionPaths.Leaderboard.path)

    suspend fun getLeaderboardListForToday(): List<LeaderboardUser> {
        val tempList = ArrayList<LeaderboardUser>()
        val todayEpoch = DateHelper.todayEpoch.toString()

        val query = leaderboardRef
                .document(PowerHourDatabaseDateType.Day.type)
                .collection(todayEpoch)
                .orderBy("points", Query.Direction.DESCENDING)
        val documents = query.get().await().documents
        for (doc in documents) {
            var points = 0.0
            val value = doc.data?.get("points")?.toString()
            points += value?.toDouble()!!

            val user = userRepository.getById(doc.id)

            val leaderboardUser = LeaderboardUser(doc.id, user.name!!, points.toInt())
            tempList.add(leaderboardUser)
        }
        return tempList
    }

    suspend fun getLeaderboardListForWeek(): List<LeaderboardUser> {
        val tempList = ArrayList<LeaderboardUser>()
        val weekEpoch = DateHelper.startOfWeekEpoch.toString()

        val query = leaderboardRef
            .document(PowerHourDatabaseDateType.Week.type)
            .collection(weekEpoch)
            .orderBy("points", Query.Direction.DESCENDING)
        val documents = query.get().await().documents
        for (doc in documents) {
            var points = 0.0
            val value = doc.data?.get("points")?.toString()
            points += value?.toDouble()!!

            val user = userRepository.getById(doc.id)

            val leaderboardUser = LeaderboardUser(doc.id, user.name!!, points.toInt())
            tempList.add(leaderboardUser)
        }
        return tempList
    }

    suspend fun getLeaderboardListForMonth(): List<LeaderboardUser> {
        val tempList = ArrayList<LeaderboardUser>()
        val monthEpoch = DateHelper.startOfMonthEpoch.toString()

        val query = leaderboardRef
            .document(PowerHourDatabaseDateType.Month.type)
            .collection(monthEpoch)
            .orderBy("points", Query.Direction.DESCENDING)
        val documents = query.get().await().documents
        for (doc in documents) {
            var points = 0.0
            val value = doc.data?.get("points")?.toString()
            points += value?.toDouble()!!

            val user = userRepository.getById(doc.id)

            val leaderboardUser = LeaderboardUser(doc.id, user.name!!, points.toInt())
            tempList.add(leaderboardUser)
        }
        return tempList
    }
}