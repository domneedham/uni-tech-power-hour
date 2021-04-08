package com.example.techpowerhour.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.example.techpowerhour.data.model.User
import com.example.techpowerhour.data.repository.enums.DatabaseCollectionPaths
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore
    private val usersRef = db.collection(DatabaseCollectionPaths.User.path)

    var currentUser: User? = null

    fun setCurrentUser(id: String) {
        val query = usersRef.document(id)
        query.get()
                .addOnSuccessListener { document ->
                    // if no user is found, create them
                    if (document.data == null) {
                        create(id)
                    } else {
                        currentUser = document.toObject<User>()
                    }
                }
                .addOnFailureListener { error ->
                    Log.d(TAG, "Error: $error")
                }
    }

    private fun create(id: String) {
        val user = User(auth.currentUser!!.displayName!!)

        val statement = usersRef.document(id).set(user)
        statement.addOnSuccessListener {
            setCurrentUser(id)
        }.addOnFailureListener { error ->
            Log.d(TAG, "Error: $error")
        }

    }

    fun update(user: User) {
        usersRef.document(user.id!!).set(user)
    }

    fun delete(user: User) {
        usersRef.document(user.id!!).delete()
    }

    fun getAll(): List<User> {
        val userList = ArrayList<User>()

        val snapshot = usersRef.get()
        snapshot.addOnSuccessListener { documents ->
            for (doc in documents.documents) {
                val user = doc.toObject<User>()
                user!!.id = doc.id
                userList.add(user)
            }
        }
        snapshot.addOnFailureListener { error ->
            Log.d(TAG, "Error: $error")
        }

        return userList
    }
}