package com.example.sdp_assistiverobot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.ProgressBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val REQUEST_SIGNUP = 0
    private var LOGIN_STATUS = 0

    private var usr = null as String?
    private var pwd = null as String?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (savedInstanceState?.getInt("LOGIN_STATUS") == 1) {
            usr = savedInstanceState?.getString("usr")
            pwd = savedInstanceState?.getString("pwd")
            login()
        }

        button_login.setOnClickListener {
            usr = username.text.toString()
            pwd = password.text.toString()
            login()
        }
        button_signup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivityForResult(intent, REQUEST_SIGNUP)
        }

        isEnabledButtons(true)
    }

    private fun login() {
        LOGIN_STATUS = 1
        isEnabledButtons(false)

        if (!validate()) {
            onLoginFailed()
            return
        }

        loading.visibility = ProgressBar.VISIBLE
        loading.isIndeterminate = true

        // TODO: Implement your own authentication logic here.

    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putInt("LOGIN_STATUS", LOGIN_STATUS)
        outState.putString("usr", usr)
        outState.putString("pwd", pwd)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                //TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish()
            }
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    private fun onLoginFailed() {
        LOGIN_STATUS = 0
        Toast.makeText(baseContext, "Login failed", Toast.LENGTH_LONG).show()
        isEnabledButtons(true)
        loading.visibility = ProgressBar.GONE
    }

    private fun isEnabledButtons(enable : Boolean) {
        button_login.isEnabled = enable
        button_signup.isEnabled = enable
    }

    private fun onLoginSuccess() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun validate() : Boolean {
        if (usr == null || !android.util.Patterns.EMAIL_ADDRESS.matcher(usr).matches()) {
            Toast.makeText(this,usr,Toast.LENGTH_SHORT).show()
            username.error = "enter a valid email address"
            return false
        } else {
            username.error = null
        }

        if (pwd == null) {
            password.error = "password is incorrect"
            return false
        } else {
            password.error = null
        }

        return true
    }
}
