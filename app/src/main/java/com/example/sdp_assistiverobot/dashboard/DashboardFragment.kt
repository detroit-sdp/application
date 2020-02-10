package com.example.sdp_assistiverobot.dashboard


import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sdp_assistiverobot.R
import com.example.sdp_assistiverobot.patients.Patient
import com.google.firebase.firestore.FirebaseFirestore

/**
 * A simple [Fragment] subclass.
 */
class DashboardFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private val happyPatients: ArrayList<Patient> = ArrayList()
    private val neutralPatients: ArrayList<Patient> = ArrayList()
    private val priorityPatients: ArrayList<Patient> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    private fun getPatients(){
        db = FirebaseFirestore.getInstance()
        val docRef = db.collection("Patients")
        docRef.get()
            .addOnSuccessListener { results ->
                for (document in results) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    var first: String
                    var last: String
                    var dob: String
                    var gender: String
                    var medicalState: String
                    var location: String
                    var note: String
                    document.apply {
                        first = get("first").toString()
                        last = get("last").toString()
                        dob = get("dob").toString()
                        gender = get("gender").toString()
                        medicalState = get("medicalState").toString()
                        location = get("location").toString()
                        note = get("note").toString()
                    }
                    val patient = Patient(first,last,dob,gender,medicalState,note,location)
                    if (patient.medicalState == "Satisfactory"){
                        happyPatients.add(patient)
                    }
                    else if(patient.medicalState == "Stable"){
                        neutralPatients.add(patient)
                    }
                    else{
                        priorityPatients.add(patient)
                    }

                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

    }


}
