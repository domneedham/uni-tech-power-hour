package com.example.techpowerhour.data.service

import com.example.techpowerhour.data.model.User
import com.example.techpowerhour.data.service.enums.DatabaseCollectionPaths
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class UserService {
    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore
    private val usersRef = db.collection(DatabaseCollectionPaths.User.path)

    suspend fun getById(id: String): User {
        val query = usersRef.document(id)
        return query.get().await().toObject<User>()!!
    }

    fun create(id: String) {
        val user = User(auth.currentUser!!.displayName!!)
        usersRef.document(id).set(user)
    }

    fun update(user: User) {
        usersRef.document(user.id!!).set(user)
    }

    fun delete(user: User) {
        usersRef.document(user.id!!).delete()
    }
}