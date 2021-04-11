package com.example.techpowerhour.data.service

import com.example.techpowerhour.data.model.User
import com.example.techpowerhour.data.service.enums.DatabaseCollectionPaths
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

/**
 * The service class for the User persistent storage in Firestore.
 */
class UserService {
    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    /**
     * The reference to the users collection.
     */
    private val usersRef = db.collection(DatabaseCollectionPaths.User.path)

    /**
     * Perform a query on the [usersRef] to get user details.
     * @param id The user id.
     */
    suspend fun getById(id: String): User {
        val query = usersRef.document(id)
        return query.get().await().toObject<User>()!!
    }

    /**
     * Perform a query on the [usersRef] to create a new user.
     * @param id The user id.
     */
    fun create(id: String) {
        val user = User(auth.currentUser!!.displayName!!)
        usersRef.document(id).set(user)
    }

    /**
     * Perform a query on the [usersRef] to update an existing user.
     * @param user The user object to update.
     */
    fun update(user: User) {
        usersRef.document(user.id!!).set(user)
    }

    /**
     * Perform a query on the [usersRef] to delete an existing user.
     * @param user The user object to delete.
     */
    fun delete(user: User) {
        usersRef.document(user.id!!).delete()
    }
}