package com.example.techpowerhour.data.repository

import android.util.Log
import com.example.techpowerhour.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class UserRepository() {
    private val auth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = Firebase.database
    private val users: DatabaseReference = database.getReference("user")

    var currentUser: User? = null

    fun setCurrentUser(id: String) {
        val snapshot = users.child(id).get()
        snapshot.addOnCompleteListener {
            // if no user is found, create them
            if (it.result?.value == null) {
                create()
            } else {
                currentUser = it.result?.getValue(User::class.java)
            }
        }
        snapshot.addOnFailureListener {
            it.printStackTrace()
        }

    }

    fun create() {
        val user = User(auth.currentUser!!.displayName!!)
        users.child(auth.uid!!).setValue(user)
        setCurrentUser(auth.uid!!)
    }

    fun update(user: User) {
        users.child(user.id!!).setValue(user)
    }

    fun delete(user: User) {
        users.child(user.id!!).removeValue()
    }

    fun deleteAll() {
        users.removeValue()
    }

    fun getAll(): List<User> {
        val userList = ArrayList<User>()

        val snapshot = users.get()
        snapshot.addOnSuccessListener {
            Log.v("snapshot,", it.value.toString())
            for (childSnapshot in it.children) {
                val user = childSnapshot.getValue(User::class.java)
                user!!.id = childSnapshot.key
                userList.add(user)
            }
        }
        snapshot.addOnFailureListener {
            it.printStackTrace()
        }

        return userList
    }
}