package com.example.sdp_assistiverobot

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        button_signup.isEnabled = true
        updateUI(currentUser)
    }

    private fun createNewAccount() {
        loading.visibility = ProgressBar.VISIBLE
        loading.isIndeterminate = true
        button_signup.isEnabled = false
        username.isEnabled = false
        password.isEnabled = false

        val pwd = password.text.toString()
        val usr = username.text.toString()

        if (pwd == confirmPassword.text.toString()
            && android.util.Patterns.EMAIL_ADDRESS.matcher(usr).matches()) {
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
        } else {
            username.error = "enter a valid email address"
        }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        Toast.makeText(baseContext, "Authentication Success.",
            Toast.LENGTH_SHORT).show()
    }
}
