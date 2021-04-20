package com.example.techpowerhour.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    var user: FirebaseUser? = auth.currentUser

    fun verifyEmail() {
        user?.sendEmailVerification()
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.w("Login", "Verification email sent to " + user!!.email)
                    } else {
                        Log.e("Login", "sendEmailVerification", task.exception)
                    }
                }
    }

    fun resetUser() {
        user = auth.currentUser
    }
}