package com.example.sdp_assistiverobot.userpage

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sdp_assistiverobot.R
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

        auth = FirebaseAuth.getInstance()

        button_change_password.setOnClickListener{
            changePassword()
        }
    }

    private fun changePassword(){
        //check all fields have filled in
        if(current_password.text.isNotEmpty() && new_password.text.isNotEmpty() && confirm_password.text.isNotEmpty()){
            //check if new password is same as the confirmation
            if(new_password.text.toString().equals(confirm_password.text.toString())){
                val user = auth.currentUser
                if (user != null && user.email != null){
                    val credential = EmailAuthProvider
                        .getCredential(user.email.toString(), current_password.text.toString())

                    // Prompt the user to re-provide their sign-in credentials
                    user?.reauthenticate(credential)
                        ?.addOnCompleteListener {
                            if(it.isSuccessful){
                                user?.updatePassword(new_password.text.toString())
                                    ?.addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(this,"Password changed successfully",Toast.LENGTH_SHORT).show()
                                            finish()
                                        }
                                    }
                            }
                            else{
                                Toast.makeText(this,"Current Password incorrect",Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
            else{
                Toast.makeText(this,"New passwords doesn't match", Toast.LENGTH_SHORT).show()
            }
        }
        else{
            Toast.makeText(this,"Required field not filled in", Toast.LENGTH_SHORT).show()
        }
    }

}
