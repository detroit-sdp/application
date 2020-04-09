package com.example.sdp_assistiverobot.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import com.example.sdp_assistiverobot.MainActivity
import com.example.sdp_assistiverobot.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val REQUEST_SIGNUP = 0
    private val TAG = "LoginActivity"

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        // Check if the user has already logged in.
        if (auth.currentUser != null) {
            Log.d(TAG, "user signed in")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        setContentView(R.layout.activity_login)
        isEnabledAll(true)
        // Set up listeners for buttons
        button_login.setOnClickListener {
            val usr = username.text.toString()
            val pwd = password.text.toString()
            login(usr, pwd)
        }
        button_signup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivityForResult(intent, REQUEST_SIGNUP)
        }
    }

    /**
     * Connect to Firebase to check the authentication
     */
    private fun login(username: String, password: String) {
        Log.d(TAG, "login")
        isEnabledAll(false)

        if (!validate()) {
            updateUI(null)
            return
        }

        // Implement your own authentication logic here.
        auth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Login failed", Toast.LENGTH_LONG).show()
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    updateUI(null)
                }
            }

    }

    /**
     * When user successfully signs up, remove the login activity from the stack
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                finish()
            }
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    /**
     * Control all frames on the screen
     */
    private fun isEnabledAll(enable : Boolean) {
        button_login.isEnabled = enable
        button_signup.isEnabled = enable
        username.isEnabled = enable
        password.isEnabled = enable
        if (enable) {
            loading.visibility = ProgressBar.GONE
        } else {
            loading.visibility = ProgressBar.VISIBLE
        }
    }

    /**
     * Update the UI based on whether the user is signed in correctly or not
     */
    private fun updateUI(user: FirebaseUser?) {
        Log.d(TAG, "updateUI")
        if (user != null) {
            Log.d(TAG, "user signed in")
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("email", user.email)
            }
            startActivity(intent)
        } else {
            Log.d(TAG, "user not signed in")
            isEnabledAll(true)
        }
    }

    /**
     * Check the validation of user input
     */
    private fun validate() : Boolean {
        val usr = username.text.toString()
        val pwd = password.text.toString()

        if (usr.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(usr).matches()) {
            username.error = "enter a valid email address"
            return false
        } else {
            username.error = null
        }

        if (pwd.isEmpty() || pwd.length < 6) {
            password.error = "password is incorrect"
            return false
        } else {
            password.error = null
        }

        return true
    }
}
