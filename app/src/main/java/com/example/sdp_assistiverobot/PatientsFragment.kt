package com.example.sdp_assistiverobot


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_patients.*

/**
 * A simple [Fragment] subclass.
 */
class PatientsFragment : Fragment() {

    val TAG = "PatientsFragment"
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_patients, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        database = FirebaseDatabase.getInstance().reference
        addPatient.setOnClickListener {
            // Start add patient activity
            Intent(this.context, AddPatientActivity::class.java).also {
                startActivity((it))
            }
        }
    }


}
