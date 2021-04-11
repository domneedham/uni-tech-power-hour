package com.example.techpowerhour

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.techpowerhour.databinding.ActivityLoginBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (auth.currentUser != null) {
            displayNewActivity()
        } else {
            setContentView(R.layout.activity_main)
            binding = ActivityLoginBinding.inflate(layoutInflater)

            signInButtonBinding()

            setContentView(binding.root)
        }
    }

    companion object {
        private const val RC_SIGN_IN = 123 //Request code for sign in
    }

    private fun signInButtonBinding() {
        binding.signInButton.setOnClickListener { displaySignIn() }
    }

    private fun displaySignIn() {
        val providers = arrayListOf( // Choose authentication providers
            AuthUI.IdpConfig.EmailBuilder().build())
        startActivityForResult( // Create and launch sign-in intent
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) { //check this has returned from AuthUI intent
            val response = IdpResponse.fromResultIntent(data)
            //Check the exit from intent was successful
            if (resultCode == Activity.RESULT_OK) {
                //Check email has been verified – if not
                if(!auth.currentUser!!.isEmailVerified) {
                    verifyEmail()

                    //display the sign in page again
                    displaySignIn()
                }
                else { //if user has had their email address verified
                    displayNewActivity()
                }
            }
        }
    }

    private fun displayNewActivity() {
        val nextIntent = Intent(this, MainActivity::class.java)
        startActivity(nextIntent)
        finish()
    }

    private fun verifyEmail() {
        val user = auth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.w("Login", "Verification email sent to " + user.email)
                } else {
                    Log.e("Login", "sendEmailVerification", task.exception)
                }
            }
    }

}