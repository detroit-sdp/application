package com.example.sdp_assistiverobot.userpage

import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sdp_assistiverobot.R
import com.example.sdp_assistiverobot.util.DatabaseManager.authUser
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.android.synthetic.main.fragment_user.*

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    val TAG = "Change Password"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        setSupportActionBar(findViewById(R.id.change_password_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        auth = FirebaseAuth.getInstance()

        isEnabled(true)
        button_change_password.setOnClickListener{
            changePassword()
        }
    }

    private fun validate(): Boolean {
        val curPassword = current_password.text.toString()
        val newPassword = new_password.text.toString()
        val confirmPassword = confirm_password.text.toString()

        if (curPassword.isEmpty()) {
            current_password.error = "This is empty"
            return false
        } else {
            current_password.error = null
        }

        if (newPassword.length < 6) {
            new_password.error = "Password should be more than 6 characters"
            return false
        } else {
            new_password.error = null
        }

        if (newPassword != confirmPassword) {
            confirm_password.error = "New password does not match"
            return false
        } else {
            confirm_password.error = null
        }

        return true
    }

    private fun isEnabled(enable: Boolean) {
        current_password.isEnabled = enable
        new_password.isEnabled = enable
        confirm_password.isEnabled = enable
        if (enable) {
            progressBar3.visibility = ProgressBar.GONE
        } else {
            progressBar3.visibility = ProgressBar.VISIBLE
        }
    }

    private fun changePassword(){
        //check all fields have filled in
        isEnabled(false)
        if (!validate()) {
            isEnabled(true)
            return
        }

        val credential = EmailAuthProvider.getCredential(authUser!!.email.toString(), current_password.text.toString())

        // Prompt the user to re-provide their sign-in credentials
        authUser!!.reauthenticate(credential)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    authUser!!.updatePassword(new_password.text.toString())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this,"Password changed successfully",Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }
                }
                else{
                    current_password.error = "Current Password is incorrect"
                    isEnabled(true)
                }
            }
    }

}
