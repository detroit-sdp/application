package com.example.sdp_assistiverobot.dashboard


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.sdp_assistiverobot.R
import com.example.sdp_assistiverobot.util.Resident
import com.github.mikephil.charting.data.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_dashboard.*


/**
 * A simple [Fragment] subclass.
 */
class DashboardFragment : Fragment() {

    val TAG = "Dashboard Fragment"
    private lateinit var db: FirebaseFirestore
    private val lowPriorResidents: ArrayList<Resident> = ArrayList()
    private val medPriorResidents: ArrayList<Resident> = ArrayList()
    private val highPriorResidents: ArrayList<Resident> = ArrayList()

    private var deliveries = 0
    private val deliveryAmounts: IntArray = intArrayOf(10,20,30,40,50,60,deliveries)

    private var isPause = false

    val residentTypes = arrayOf("High Priority", "Medium Priority", "Low Priority")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getResidents()
        Log.d(TAG, "Getting residents data")
        setDeliveriesChart()
        val button: Button = button_sendTadashi
        button.setOnClickListener {
            // Choose resident to send Tadashi to
            Intent(this.context, ChooseResidentActivity::class.java).also {
                startActivity(it)
            }
        }
    }



    private fun getResidents(){
        db = FirebaseFirestore.getInstance()
        Log.d(TAG, "getResidents")
        val docRef = db.collection("Residents")
        docRef.get()
            .addOnSuccessListener { results ->
                for (document in results) {
//                    Log.d(TAG, "${document.id} => ${document.data}")
                    var first: String
                    var last: String
                    var priority: String
                    var location: String
                    document.apply {
                        first = get("first").toString()
                        last = get("last").toString()
                        priority = get("priority").toString()
                        location = get("location").toString()
                    }
                    val resident = Resident(
                        first,
                        last,
                        priority,
                        location
                    )
                    if (!isPause && pieChart1 != null) {
                        if (resident.priority == "Low"){
                            lowPriorResidents.add(resident)
                            Log.d(TAG, "added low")
                        }
                        else if(resident.priority == "Medium"){
                            medPriorResidents.add(resident)
                            Log.d(TAG, "added med")
                        }
                        else{
                            highPriorResidents.add(resident)
                            Log.d(TAG, "added high")
                        }
                        val highPriorNum = highPriorResidents.size.toFloat()
                        val medPriorNum = medPriorResidents.size.toFloat()
                        val lowPriorNum = lowPriorResidents.size.toFloat()

                        val residentTypesNum = arrayOf(highPriorNum, medPriorNum, lowPriorNum)
                        pieChart1.notifyDataSetChanged()
                        residentTypeChart(residentTypesNum)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    private fun residentTypeChart(residentTypesNum: Array<Float>) {
        Log.d(TAG, "residentTypeChart")
        val pieEntries: ArrayList<PieEntry> = ArrayList()
        for (i in residentTypes.indices){
            val pieEntry = PieEntry(residentTypesNum[i], residentTypes[i])
            pieEntries.add(pieEntry)
        }

        val dataSet1 = PieDataSet(pieEntries, "Residents Types")

        val colorFirst = context?.let { ContextCompat.getColor(it, R.color.colorPrimaryRed) }
        val colorSecond = context?.let { ContextCompat.getColor(it, R.color.colorPrimaryYellow) }
        val colorThird = context?.let { ContextCompat.getColor(it, R.color.colorPrimary) }
        dataSet1.colors = mutableListOf(colorFirst, colorSecond, colorThird)
        val data1 = PieData(dataSet1)

        //get Chart
        pieChart1.data = data1
        pieChart1.setDrawSliceText(false)
        pieChart1.description = null
        pieChart1.animateY(500)
        pieChart1.invalidate()
    }

    private fun setDeliveriesChart(){
        //TODO: stacked bar chart for types of deliveries (food, water, other)?
        val barEntries: ArrayList<BarEntry> = ArrayList()

        //populate with fake data
        barEntries.add(BarEntry(0f, deliveryAmounts[0].toFloat()))
        barEntries.add(BarEntry(1f, deliveryAmounts[1].toFloat()))
        barEntries.add(BarEntry(2f, deliveryAmounts[2].toFloat()))
        barEntries.add(BarEntry(3f, deliveryAmounts[3].toFloat()))
        barEntries.add(BarEntry(4f, deliveryAmounts[4].toFloat()))
        barEntries.add(BarEntry(5f, deliveryAmounts[5].toFloat()))

        barEntries.add(BarEntry(6f, deliveries.toFloat()))

        val deliveryDataSet = BarDataSet(barEntries, "Deliveries through the week")
        val deliveryData = BarData(deliveryDataSet)
        barChart1.setData(deliveryData)
//        barChart1.setDrawGridLines(false)
        barChart1.setDrawGridBackground(false)
        barChart1.setDrawValueAboveBar(false)
        barChart1.setDrawBorders(false)
        barChart1.setDescription(null)
        barChart1.animateY(500)
        barChart1.invalidate()
    }

    override fun onPause() {
        isPause = true
        super.onPause()
    }

}
