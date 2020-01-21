package com.example.sdp_assistiverobot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*
import java.net.Authenticator

class LoginActivity : AppCompatActivity() {

    private val REQUEST_SIGNUP = 0
    private var LOGIN_STATUS = 0
    private val TAG = "LoginActivity"

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        button_login.setOnClickListener {
            val usr = username.text.toString()
            val pwd = password.text.toString()
            login(usr, pwd)
        }
        button_signup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivityForResult(intent, REQUEST_SIGNUP)
        }

        isEnabledAll(true)
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser == null) {

        }
    }

    private fun login(username: String, password: String) {
        LOGIN_STATUS = 1
        isEnabledAll(false)

        if (!validate()) {
            return
        }

        loading.visibility = ProgressBar.VISIBLE
        loading.isIndeterminate = true

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
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    updateUI(null)
                }
            }


//        if (usr == "niu123456@163.com" && pwd == "123456") {
//            onLoginSuccess(null)
//        } else {
//            onLoginFailed()
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                //TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                finish()
            }
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    private fun isEnabledAll(enable : Boolean) {
        button_login.isEnabled = enable
        button_signup.isEnabled = enable
        username.isEnabled = enable
        password.isEnabled = enable
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            LOGIN_STATUS = 0
            Toast.makeText(baseContext, "Login failed", Toast.LENGTH_LONG).show()
            isEnabledAll(true)
            loading.visibility = ProgressBar.GONE
        }
    }

    private fun validate() : Boolean {
        val usr = username.text.toString()
        val pwd = password.text.toString()

        if (usr.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(usr).matches()) {
            username.error = "enter a valid email address"
            return false
        } else {
            username.error = null
        }

        if (pwd.isEmpty()) {
            password.error = "password is incorrect"
            return false
        } else {
            password.error = null
        }

        return true
    }
}
