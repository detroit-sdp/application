package com.example.sdp_assistiverobot

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val TAG = "SignupActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()

        button_signup.setOnClickListener {
            createNewAccount()
        }

        isEnabledAll(true)
    }

    private fun validate() : Boolean {
        val usr = useremail.text.toString()
        val pwd = password.text.toString()
        val pwd2 = confirmPassword.text.toString()
        val name = username.text.toString()

        if (name.isEmpty()) {
            username.error = "enter a valid username"
            return false
        } else {
            username.error = null
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(usr).matches()) {
            useremail.error = "enter a valid email address"
            return false
        } else {
            useremail.error = null
        }

        if (pwd.length < 6) {
            password.error = "password should be more than 6 characters"
            return false
        } else {
            password.error = null
        }

        if (pwd != pwd2) {
            confirmPassword.error = "password does not match"
            return false
        } else {
            confirmPassword.error = null
        }

        return true
    }

    private fun createNewAccount() {
        isEnabledAll(false)

        if (!validate()) {
            updateUI(null)
            return
        }

        loading.visibility = ProgressBar.VISIBLE
        loading.isIndeterminate = true

        val pwd = password.text.toString()
        val usr = useremail.text.toString()
        auth.createUserWithEmailAndPassword(usr, pwd)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun isEnabledAll(enable : Boolean) {
        button_signup.isEnabled = enable
        username.isEnabled = enable
        useremail.isEnabled = enable
        password.isEnabled = enable
        confirmPassword.isEnabled = enable
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(username.text.toString())
                .build()

            user.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User profile updated.")
                    }
                }

            setResult(Activity.RESULT_OK)
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("email", user.email)
            }
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(baseContext, "Login failed", Toast.LENGTH_LONG).show()
            loading.visibility = ProgressBar.GONE
            isEnabledAll(true)
        }
    }
}
