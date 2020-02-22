package com.example.sdp_assistiverobot.dashboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sdp_assistiverobot.R
import com.example.sdp_assistiverobot.Util.Resident
import com.example.sdp_assistiverobot.patients.ResidentViewActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_residents.*

class ChoosePatientActivity : AppCompatActivity() {

    val TAG = "Choose Resident"
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private val residents: ArrayList<Resident> = ArrayList()
    private var pauseLoad = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_patient)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        var user_name = currentUser?.displayName

        getPatients(user_name)
    }

    private fun getPatients(user: String?){
        Log.d(TAG, user)
        db = FirebaseFirestore.getInstance()
        db.collection("Patients")
            .whereEqualTo("carer", user)
            .get()
            .addOnSuccessListener { documents ->
                Log.d(TAG, "results: ")
                for (document in documents) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    var first: String
                    var last: String
                    var medicalState: String
                    var location: String
                    document.apply {
                        first = get("first").toString()
                        last = get("last").toString()
                        medicalState = get("medicalState").toString()
                        location = get("location").toString()
                    }
                    val patient = Resident(
                        first,
                        last,
                        medicalState,
                        location
                    )
                    residents.add(patient)
                }

                Log.d(TAG, residents.toString())

                if (!pauseLoad) {
                    val viewManager = LinearLayoutManager(this)
                    val viewAdapter = MyAdapter(residents) { patient ->
                        //Toast.makeText(this.context, "click on $patientName", Toast.LENGTH_SHORT).show()
                        Intent(this, ResidentViewActivity::class.java).also {
                            it.putExtra("patient", patient)
                            startActivity(it)
                        }
                    }

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
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

    }

    private class MyAdapter constructor(val myDataset: ArrayList<Resident>, val clickListener: (Resident) -> Unit) :
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
                .inflate(R.layout.residents_list_row, parent, false)
            // set the view's size, margins, paddings and layout parameters
            // ...
            return MyViewHolder(view)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.name.text = "${myDataset[position].first} ${myDataset[position].last}"
            holder.itemView.setOnClickListener {
                clickListener(myDataset[position])
            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = myDataset.size
    }
}
