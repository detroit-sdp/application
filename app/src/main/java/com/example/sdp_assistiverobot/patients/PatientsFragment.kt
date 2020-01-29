package com.example.sdp_assistiverobot.patients

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_patients.*
import com.example.sdp_assistiverobot.R
import kotlinx.android.synthetic.main.fragment_list_line.*

/**
 * A simple [Fragment] subclass.
 */
class PatientsFragment : Fragment() {

    val TAG = "PatientsFragment"
    private lateinit var db: FirebaseFirestore
    private lateinit var mInflater: LayoutInflater
    private val patients: ArrayList<String> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mInflater = inflater
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

        loadPatients()
    }

    private fun loadPatients() {
        db = FirebaseFirestore.getInstance()
        db.collection("Patients").get()
            .addOnSuccessListener { results ->
                for (document in results) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    val first = document.get("first").toString()
                    val last = document.get("last").toString()

                    patients.add("$first $last")
                }
                val viewManager = LinearLayoutManager(this.context)
                val viewAdapter = MyAdapter(patients)
                Log.d(TAG, patients.toString())
                patientsList.apply {
                    // use this setting to improve performance if you know that changes
                    // in content do not change the layout size of the RecyclerView
                    setHasFixedSize(true)

                    // use a linear layout manager
                    layoutManager = viewManager

                    adapter = viewAdapter
                }
            }
    }

    private class MyAdapter(val myDataset: ArrayList<String>) :
        RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder.
        // Each data item is just a string in this case that is shown in a TextView.
        class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView = view.findViewById(R.id.line_view)
        }

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): MyViewHolder {
            // create a new view
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_list_line, parent, false)
            // set the view's size, margins, paddings and layout parameters
            return MyViewHolder(view)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.name.text = myDataset[position]
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = myDataset.size
    }
}
