package com.example.techpowerhour.data.service

import com.example.techpowerhour.data.model.LeaderboardUser
import com.example.techpowerhour.data.repository.BaseRepository
import com.example.techpowerhour.data.repository.UserRepository
import com.example.techpowerhour.data.service.enums.DatabaseCollectionPaths
import com.example.techpowerhour.data.service.enums.PowerHourDatabaseDateType
import com.example.techpowerhour.util.DateHelper
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

/**
 * The service class for the Leaderboard persistent storage in Firestore.
 * @param userRepository The instance of the [UserRepository] for access to user credentials.
 */
class LeaderboardService(private val userRepository: UserRepository) : BaseRepository() {
    private val db = Firebase.firestore

    /**
     * The reference to the leaderboard collection.
     */
    private val leaderboardRef = db.collection(DatabaseCollectionPaths.Leaderboard.path)

    /**
     * Get the 25 top users based on points from Firestore for the current day.
     */
    suspend fun getLeaderboardListForToday(): List<LeaderboardUser> {
        val tempList = ArrayList<LeaderboardUser>()
        val todayEpoch = DateHelper.todayEpoch.toString()

        val query = leaderboardRef
            .document(PowerHourDatabaseDateType.Day.type)
            .collection(todayEpoch)
            .orderBy("points", Query.Direction.DESCENDING)
            .limit(25)
        val documents = query.get().await().documents
        for (doc in documents) {
            val user = makeUserFromDocument(doc)
            tempList.add(user)
        }
        return tempList
    }

    /**
     * Get the 25 top users based on points from Firestore for the current week.
     */
    suspend fun getLeaderboardListForWeek(): List<LeaderboardUser> {
        val tempList = ArrayList<LeaderboardUser>()
        val weekEpoch = DateHelper.startOfWeekEpoch.toString()

        val query = leaderboardRef
            .document(PowerHourDatabaseDateType.Week.type)
            .collection(weekEpoch)
            .orderBy("points", Query.Direction.DESCENDING)
            .limit(25)
        val documents = query.get().await().documents
        for (doc in documents) {
            val user = makeUserFromDocument(doc)
            tempList.add(user)
        }
        return tempList
    }

    /**
     * Get the 25 top users based on points from Firestore for the current month.
     */
    suspend fun getLeaderboardListForMonth(): List<LeaderboardUser> {
        val tempList = ArrayList<LeaderboardUser>()
        val monthEpoch = DateHelper.startOfMonthEpoch.toString()

        val query = leaderboardRef
            .document(PowerHourDatabaseDateType.Month.type)
            .collection(monthEpoch)
            .orderBy("points", Query.Direction.DESCENDING)
            .limit(25)
        val documents = query.get().await().documents
        for (doc in documents) {
            val user = makeUserFromDocument(doc)
            tempList.add(user)
        }
        return tempList
    }

    /**
     * Create a [LeaderboardUser] object from the [document] and a fetch of the user details from the [UserRepository].
     * @param document The document retrieved from Firestore.
     */
    private suspend fun makeUserFromDocument(document: DocumentSnapshot): LeaderboardUser {
        // ran into issues converting straight away from the get, due to long/double
        // so convert to string first then into a double as no issues found this way
        var points = 0.0
        val value = document.data?.get("points")?.toString()
        points += value?.toDouble()!!

        val user = userRepository.getById(document.id)!!

        return LeaderboardUser(document.id, user.name!!, points.toInt())
    }
}