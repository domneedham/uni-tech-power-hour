package com.example.techpowerhour.ui.login

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.techpowerhour.ui.main.MainActivity
import com.example.techpowerhour.R
import com.example.techpowerhour.databinding.ActivityLoginBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupViewModelBinding()

        if (viewModel.user != null) {
            displayNewActivity()
        } else {
            setContentView(R.layout.activity_main)
            binding = ActivityLoginBinding.inflate(layoutInflater)

            signInButtonBinding()

            setContentView(binding.root)
        }
    }

    private fun setupViewModelBinding() {
        val vm: LoginViewModel by viewModels()
        viewModel = vm
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
                    .setTheme(R.style.Theme_TechPowerHour)
                    .setAvailableProviders(providers)
                    .build(),
            RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) { //check this has returned from AuthUI intent
            val response = IdpResponse.fromResultIntent(data)
            //Check the exit from intent was successful
            if (resultCode == Activity.RESULT_OK) {
                // set the user variable in the viewmodel
                viewModel.resetUser()
                //Check email has been verified – if not
                if(!viewModel.user!!.isEmailVerified) {
                    viewModel.verifyEmail()

                    Snackbar.make(
                            findViewById(android.R.id.content),
                            getString(R.string.login_email_sent),
                            Snackbar.LENGTH_LONG
                    ).show()

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
}