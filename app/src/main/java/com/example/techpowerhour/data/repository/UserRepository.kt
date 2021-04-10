package com.example.techpowerhour.data.repository

import com.example.techpowerhour.data.model.User
import com.example.techpowerhour.data.service.UserService
import com.google.firebase.auth.FirebaseAuth

class UserRepository(private val service: UserService) : BaseRepository() {
    private val auth = FirebaseAuth.getInstance()
    var currentUser: User? = null

    suspend fun setCurrentUser(id: String = auth.uid!!) {
        currentUser = service.getById(id)
    }

    fun create(id: String) {
        return service.create(id)
    }

    fun update(user: User) {
        return service.update(user)
    }

    fun delete(user: User) {
        return service.delete(user)
    }

    suspend fun getById(id: String): User {
        return service.getById(id)
    }
}