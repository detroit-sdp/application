package com.example.sdp_assistiverobot


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

        user_name.setOnClickListener {
            updateName()
        }
        user_email.setOnClickListener {
            updateEmail()
        }
        user_phone.setOnClickListener {
            updatePhoneNo()
        }
        logout.setOnClickListener {
            signOut()
        }
    }

    private fun updateName() {
//        val profileUpdates = UserProfileChangeRequest.Builder()
//            .setDisplayName("test")
//            .build()
//        val currentUser = auth.currentUser
//        currentUser?.updateProfile(profileUpdates)
//            ?.addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Log.d(TAG, "User profile updated.")
//                    user_name.text = currentUser.displayName
//                }
//            }
        Toast.makeText(this.context, "update username", Toast.LENGTH_SHORT).show()
    }

    private fun updateEmail() {
//        val currentUser = auth.currentUser
//        currentUser?.updateEmail("niu123456@123.com")
//            ?.addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Log.d(TAG, "User email address updated.")
//                    user_email.text = currentUser.email
//                }
//            }
        Toast.makeText(this.context, "update email", Toast.LENGTH_SHORT).show()
    }
    private fun updatePhoneNo(){
        Toast.makeText(this.context, "update phone", Toast.LENGTH_SHORT).show()
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        Intent(this.context, LoginActivity::class.java).also {
            startActivity(it)
        }
    }
}
