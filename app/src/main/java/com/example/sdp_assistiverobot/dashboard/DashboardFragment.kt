package com.example.sdp_assistiverobot.dashboard


import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.sdp_assistiverobot.R
import com.example.sdp_assistiverobot.patients.Patient
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_dashboard.view.*

/**
 * A simple [Fragment] subclass.
 */
class DashboardFragment : Fragment() {

    val TAG = "Dashboard Fragment"
    private lateinit var db: FirebaseFirestore
    private val happyPatients: ArrayList<Patient> = ArrayList()
    private val neutralPatients: ArrayList<Patient> = ArrayList()
    private val priorityPatients: ArrayList<Patient> = ArrayList()
    val patientTypes = arrayOf("High Priority", "Medium Priority", "Low Priority")
//    private val highPriorHappy: ArrayList<>()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getPatients()
        Log.d(TAG, "Getting patients data")
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
                        Log.d(TAG, "added low")
                    }
                    else if(patient.medicalState == "Stable"){
                        neutralPatients.add(patient)
                        Log.d(TAG, "added med")
                    }
                    else{
                        priorityPatients.add(patient)
                        Log.d(TAG, "added high")
                    }

                }

                val highPriorNum = priorityPatients.size.toFloat()
                val medPriorNum = neutralPatients.size.toFloat()
                val lowPriorNum = happyPatients.size.toFloat()

                Log.d(TAG, "high")
                Log.d(TAG, highPriorNum.toString())
                Log.d(TAG, "med")
                Log.d(TAG, medPriorNum.toString())
                Log.d(TAG, "low")
                Log.d(TAG, lowPriorNum.toString())

                val patientTypesNum = arrayOf(highPriorNum, medPriorNum, lowPriorNum)
                setupPieChart(patientTypesNum)

            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

    }

    private fun setupPieChart(patientTypesNum: Array<Float>) {
        val pieEntries: ArrayList<PieEntry> = ArrayList()
        for (i in patientTypes.indices){
            val pieEntry = PieEntry(patientTypesNum[i], patientTypes[i])
            pieEntries.add(pieEntry)
        }

        val dataSet1 = PieDataSet(pieEntries, "Patient Types")

        val colorFirst = context?.let { ContextCompat.getColor(it, R.color.colorPrimaryRed) }
        val colorSecond = context?.let { ContextCompat.getColor(it, R.color.colorPrimaryYellow) }
        val colorThird = context?.let { ContextCompat.getColor(it, R.color.colorPrimary) }
        dataSet1.colors = mutableListOf(colorFirst, colorSecond, colorThird)
//        val colourSet = intArrayOf(R.color.colorPrimaryRed, R.color.colorPrimaryYellow, R.color.colorPrimary))
//        dataSet1.colors(colourSet)
        val data1 = PieData(dataSet1)

        //get Chart
//        pieChart1 = view?.findViewById(R.id.pieChart1) as PieChart
        pieChart1.setData(data1)
        pieChart1.animateY(500)
        pieChart1.invalidate()

    }


}
