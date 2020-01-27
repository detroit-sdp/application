package com.example.sdp_assistiverobot.patients


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sdp_assistiverobot.ListLineFragment
import com.example.sdp_assistiverobot.R
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_list_line.*
import kotlinx.android.synthetic.main.fragment_patients.*

/**
 * A simple [Fragment] subclass.
 */
class PatientsFragment : Fragment() {

    val TAG = "PatientsFragment"
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_patients, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        addPatient.setOnClickListener {
            // Start add patient activity
            Intent(this.context, AddPatientActivity::class.java).also {
                startActivity((it))
            }
        }

        db = FirebaseFirestore.getInstance()
        db.collection("Patients").get()
            .addOnSuccessListener {results ->
                for (document in results) {
                    Log.d(TAG, "${document.id} => ${document.data}")

                    // Inflate list line
                    childFragmentManager.beginTransaction().apply {
                        add(
                            patientsList.id,
                            ListLineFragment.newInstance(
                                document.get("first").toString(),
                                document.get("last").toString()
                            )
                        )
                        commit()
                    }
                }
            }

    }


}
