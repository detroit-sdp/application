package com.example.sdp_assistiverobot.userpage


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.sdp_assistiverobot.R
import com.example.sdp_assistiverobot.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.fragment_user.*

class UserFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private val TAG = "UserFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        user_name.text = currentUser?.displayName
        user_email.text = currentUser?.email
        user_phone.text = currentUser?.phoneNumber

        user_email.setOnClickListener {
            updateEmail()
        }
        user_phone.setOnClickListener {
            updatePhoneNo()
        }
        changePassword.setOnClickListener{
            Intent(this.context, ChangePasswordActivity::class.java).also {
                startActivity(it)
            }
        }
        logout.setOnClickListener {
            signOut()
        }
    }
    
    private fun updateEmail() {
        // TODO
    }
    private fun updatePhoneNo(){
        // TODO
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        Intent(this.context, LoginActivity::class.java).also {
            startActivity(it)
        }
    }
}
