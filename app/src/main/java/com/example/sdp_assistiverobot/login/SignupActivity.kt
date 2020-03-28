package com.example.sdp_assistiverobot.login

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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_signup.*
import com.example.sdp_assistiverobot.MainActivity
import com.example.sdp_assistiverobot.R
import com.example.sdp_assistiverobot.userpage.User
import com.example.sdp_assistiverobot.util.Util
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val TAG = "SignupActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        setSupportActionBar(findViewById(R.id.main_toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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
            username.error = "Enter a valid username"
            return false
        } else {
            username.error = null
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(usr).matches()) {
            useremail.error = "Enter a valid email address"
            return false
        } else {
            useremail.error = null
        }

        if (pwd.length < 6) {
            password.error = "Password should be more than 6 characters"
            return false
        } else {
            password.error = null
        }

        if (pwd != pwd2) {
            confirmPassword.error = "Password does not match"
            return false
        } else {
            confirmPassword.error = null
        }

        if (!Util.isInternetAvailable(baseContext)) {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show()
            return false
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
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        useremail.error = "The email address is already in use by another account."
                    }
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
            loading.visibility = ProgressBar.GONE
            isEnabledAll(true)
        }


    }
}
