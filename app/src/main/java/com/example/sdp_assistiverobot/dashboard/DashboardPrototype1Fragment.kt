package com.example.sdp_assistiverobot.dashboard


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.sdp_assistiverobot.R
//import com.example.sdp_assistiverobot.Resident
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_dashboard_prototype1.*


/**
 * A simple [Fragment] subclass.
 */
class DashboardPrototype1Fragment : Fragment() {

    val TAG = "DashboardPrototype1"
    private lateinit var db: FirebaseFirestore
//    private val lowPriorResidents: ArrayList<Resident> = ArrayList()
//    private val medPriorResidents: ArrayList<Resident> = ArrayList()
//    private val highPriorResidents: ArrayList<Resident> = ArrayList()

    private var dailyVisits: IntArray = intArrayOf(1,1,5,8,3,2)

    private var foodDelivery = 0
    private val foodDeliveries: IntArray = intArrayOf(10,20,30,2,30,20,foodDelivery)

    private var waterDelivery = 0
    private val waterDeliveries: IntArray = intArrayOf(40,6,50,40,50,60,waterDelivery)

    private var isPause = false

    val residents = arrayOf("Resident 1", "Resident 2", "Resident 3", "Resident 4", "Resident 5")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard_prototype1, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        getResidents()
        Log.d(TAG, "Getting residents data")
        setDailyVisitChart()
        setFoodChart()
        setWaterChart()
    }



//    private fun getResidents(){
//        db = FirebaseFirestore.getInstance()
//        Log.d(TAG, "getResidents")
//        val docRef = db.collection("Residents")
//        docRef.get()
//            .addOnSuccessListener { results ->
//                for (document in results) {
////                    Log.d(TAG, "${document.id} => ${document.data}")
//                    var first: String
//                    var last: String
//                    var priority: String
//                    var location: String
//                    document.apply {
//                        first = get("first").toString()
//                        last = get("last").toString()
//                        priority = get("priority").toString()
//                        location = get("location").toString()
//                    }
//                    val resident = Resident(
//                        first,
//                        last,
//                        priority,
//                        location
//                    )
//                    if (!isPause && pieChart1 != null) {
//                        if (resident.priority == "Low"){
//                            lowPriorResidents.add(resident)
//                            Log.d(TAG, "added low")
//                        }
//                        else if(resident.priority == "Medium"){
//                            medPriorResidents.add(resident)
//                            Log.d(TAG, "added med")
//                        }
//                        else{
//                            highPriorResidents.add(resident)
//                            Log.d(TAG, "added high")
//                        }
//                        val highPriorNum = highPriorResidents.size.toFloat()
//                        val medPriorNum = medPriorResidents.size.toFloat()
//                        val lowPriorNum = lowPriorResidents.size.toFloat()
//
//                        val residentTypesNum = arrayOf(highPriorNum, medPriorNum, lowPriorNum)
//                        pieChart1.notifyDataSetChanged()
//                    }
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.d(TAG, "get failed with ", exception)
//            }
//    }


    private fun setDailyVisitChart(){
        //Horizontal bar chart for daily visits for each patient
        val barEntries: ArrayList<BarEntry> = ArrayList()

        //populate with fake data
        val stackedEntry = BarEntry(0f, floatArrayOf(dailyVisits[0].toFloat(), dailyVisits[1].toFloat(), dailyVisits[2].toFloat(),dailyVisits[3].toFloat(), dailyVisits[4].toFloat()))
        barEntries.add (stackedEntry)
//        barEntries.add(BarEntry(4f, dailyVisits[4].toFloat()))
//        barEntries.add(BarEntry(5f, dailyVisits[5].toFloat()))

        //x-axis formatting
        HbarChart1.xAxis.position = XAxis.XAxisPosition.BOTTOM
        HbarChart1.xAxis.setDrawGridLines(false)
        HbarChart1.xAxis.setDrawAxisLine(false)
        HbarChart1.xAxis.setDrawLabels(false)

        //y-axis formatting
        HbarChart1.getAxisRight().setEnabled(false)
        HbarChart1.axisLeft.setDrawAxisLine(false)
        HbarChart1.axisLeft.setDrawGridLines(false) // no grid lines
        HbarChart1.axisLeft.setAxisMinimum(0f)
        HbarChart1.axisLeft.setDrawLabels(false)

        val deliveryDataSet = BarDataSet(barEntries, "Daily visits for each resident")
        deliveryDataSet.colors = ColorTemplate.COLORFUL_COLORS.asList()

        deliveryDataSet.setStackLabels(residents)
        HbarChart1.getLegend().setWordWrapEnabled(true);
        val deliveryData = BarData(deliveryDataSet)
        HbarChart1.setData(deliveryData)
//        barChart1.setDrawGridLines(false)
        HbarChart1.setDrawGridBackground(false)
        HbarChart1.setDrawValueAboveBar(false)
        HbarChart1.setDrawBorders(false)
        HbarChart1.setDescription(null)
        HbarChart1.animateY(500)
        HbarChart1.invalidate()
    }

    private fun setFoodChart(){
        val barEntries: ArrayList<BarEntry> = ArrayList()

        //populate with fake data
        barEntries.add(BarEntry(0f, foodDeliveries[0].toFloat()))
        barEntries.add(BarEntry(1f, foodDeliveries[1].toFloat()))
        barEntries.add(BarEntry(2f, foodDeliveries[2].toFloat()))
        barEntries.add(BarEntry(3f, foodDeliveries[3].toFloat()))
        barEntries.add(BarEntry(4f, foodDeliveries[4].toFloat()))
        barEntries.add(BarEntry(5f, foodDeliveries[5].toFloat()))

        //x-axis formatting
        barChart2.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChart2.xAxis.setDrawGridLines(false)
        barChart2.xAxis.valueFormatter = MyXAxisFormatter()

        //y-axis formatting
        barChart2.getAxisRight().setEnabled(false)
        barChart2.axisLeft.setDrawGridLines(false) // no grid lines
        barChart2.axisLeft.setAxisMinimum(0f)

        val deliveryDataSet = BarDataSet(barEntries, "Food Deliveries for your residents")
        val deliveryData = BarData(deliveryDataSet)
        barChart2.setData(deliveryData)
//        barChart1.setDrawGridLines(false)
        barChart2.setDrawGridBackground(false)
        barChart2.setDrawValueAboveBar(true)
        barChart2.setDrawBorders(false)
        barChart2.setDescription(null)
        barChart2.animateY(500)
        barChart2.invalidate()
    }

    private fun setWaterChart(){
        //TODO: stacked bar chart for types of deliveries (food, water, other)?
        val barEntries: ArrayList<BarEntry> = ArrayList()

        //populate with fake data
        barEntries.add(BarEntry(0f, waterDeliveries[0].toFloat()))
        barEntries.add(BarEntry(1f, waterDeliveries[1].toFloat()))
        barEntries.add(BarEntry(2f, waterDeliveries[2].toFloat()))
        barEntries.add(BarEntry(3f, waterDeliveries[3].toFloat()))
        barEntries.add(BarEntry(4f, waterDeliveries[4].toFloat()))
        barEntries.add(BarEntry(5f, waterDeliveries[5].toFloat()))

        //x-axis formatting
        barChart3.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChart3.xAxis.setDrawGridLines(false)
        barChart3.xAxis.valueFormatter = MyXAxisFormatter()

        //y-axis formatting
        barChart3.getAxisRight().setEnabled(false)
        barChart3.axisLeft.setDrawGridLines(false) // no grid lines
        barChart3.axisLeft.setAxisMinimum(0f)

        val deliveryDataSet = BarDataSet(barEntries, "Water Deliveries for your residents")
        val deliveryData = BarData(deliveryDataSet)
        barChart3.setData(deliveryData)
//        barChart1.setDrawGridLines(false)
        barChart3.setDrawGridBackground(false)
        barChart3.setDrawValueAboveBar(true)
        barChart3.setDrawBorders(false)
        barChart3.setDescription(null)
        barChart3.animateY(500)
        barChart3.invalidate()
    }

    class MyXAxisFormatter : ValueFormatter() {
        private val days = arrayOf("Resident 1", "Resident 2", "Resident 3", "Resident 4", "Resident 5", "Resident 6")
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return days.getOrNull(value.toInt()) ?: value.toString()
        }
    }

    override fun onPause() {
        isPause = true
        super.onPause()
    }

}
