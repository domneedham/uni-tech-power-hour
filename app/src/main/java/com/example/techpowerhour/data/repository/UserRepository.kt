package com.example.techpowerhour.data.repository

import android.util.Log
import com.example.techpowerhour.data.model.User
import com.example.techpowerhour.data.service.UserService
import com.google.firebase.auth.FirebaseAuth

/**
 * The repository to handle all data for fetching and returning user information.
 * @param service The user service to fetch data from.
 */
class UserRepository(private val service: UserService) : BaseRepository() {
    /**
     * The instance of [FirebaseAuth] for references.
     */
    private val auth = FirebaseAuth.getInstance()

    /**
     * The current user logged in.
     * @see [User]
     */
    var currentUser: User? = null

    /**
     * A method for setting the current user of the application.
     * @param id The id of the user. Defaults to the uid from [auth].
     */
    suspend fun setCurrentUser(id: String = auth.uid!!) {
        try {
            var user: User?
            user = service.getById(id)
            if (user == null) {
                user = create(id)
            }
            currentUser = user
        } catch (e: Exception) {
            Log.d("User", "Failed to set user")
        }

    }

    /**
     * Create a new user in persistent storage.
     * @param id The id of the user to create.
     */
    suspend fun create(id: String): User {
        return service.create(id)
    }

    /**
     * Update a user in persistent storage.
     * @param user The user object to update.
     */
    fun update(user: User) {
        return service.update(user)
    }

    /**
     * Delete a user in persistent storage.
     * @param user The user to delete.
     */
    fun delete(user: User) {
        return service.delete(user)
    }

    /**
     * Fetch a [User] from persistent storage.
     * @param id The id of the user to fetch.
     */
    suspend fun getById(id: String): User? {
        return service.getById(id)
    }

    /**
     * Sign the user out of the application.
     */
    fun signOut() {
        auth.signOut()
        currentUser = null
    }
}