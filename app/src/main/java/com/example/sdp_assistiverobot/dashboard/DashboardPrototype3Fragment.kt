package com.example.sdp_assistiverobot.dashboard

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager

import com.example.sdp_assistiverobot.R
import com.example.sdp_assistiverobot.Resident
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_dashboard_prototype3.*
import kotlinx.android.synthetic.main.fragment_priority_residents_object.*

/**
 * A simple [Fragment] subclass.
 */
class DashboardPrototype3Fragment : Fragment() {

    val TAG = "DashboardPrototype3"

    private lateinit var residentsPagerAdapter: ResidentsPagerAdapter
    private lateinit var viewPager: ViewPager

    private lateinit var db: FirebaseFirestore
    private val lowPriorResidents: ArrayList<Resident> = ArrayList()
    private val medPriorResidents: ArrayList<Resident> = ArrayList()
    private val highPriorResidents: ArrayList<Resident> = ArrayList()

    private var dailyVisits: IntArray = intArrayOf(0,1,5,8,3,2)

    private var foodDelivery = 0
    private val foodDeliveries: IntArray = intArrayOf(10,20,30,40,50,60,foodDelivery)

    private var waterDelivery = 0
    private val waterDeliveries: IntArray = intArrayOf(40,6,50,40,50,60,foodDelivery)

    private var isPause = false

    val residents = arrayOf("Resident 1", "Resident 2", "Resident 3", "Resident 4")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard_prototype3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        residentsPagerAdapter = ResidentsPagerAdapter(childFragmentManager)
        viewPager = view.findViewById(R.id.pager3)
        viewPager.adapter = residentsPagerAdapter
        tab_layout3.setupWithViewPager(viewPager)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getResidents()
        Log.d(TAG, "Getting residents data")
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
                    if (!isPause) {
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
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    override fun onPause() {
        isPause = true
        super.onPause()
    }

}

class ResidentsPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getCount(): Int = 3

    override fun getPageTitle(position: Int): CharSequence {
        if(position == 0){
            return "Low Priority"
        }
        else if(position == 1){
            return "Medium Priority"
        }
        else{
            return "High Priority"
        }
    }

    override fun getItem(i: Int): Fragment {
        val fragment = PriorityResidentsObjectFragment()
        fragment.arguments = Bundle().apply {
            // Our object is just an integer :-P
            putInt("Priority", i + 1)
        }
        return fragment
    }
}


private const val ARG_OBJECT = "object"

// Instances of this class are fragments representing a single
// object in our collection.
class PriorityResidentsObjectFragment : Fragment() {

    val TAG = "DashboardPrototype3"

    private var foodDelivery = 0
    private val foodDeliveries: IntArray = intArrayOf(6,2,9,5,5,6,foodDelivery)

    private var waterDelivery = 0
    private val waterDeliveries: IntArray = intArrayOf(4,1,5,4,5,6,waterDelivery)

    val daysOfWeek = arrayOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_priority_residents_object, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(ARG_OBJECT) }?.apply {
            val textView: TextView = view.findViewById(android.R.id.text1)
            textView.text = getInt(ARG_OBJECT).toString()
        }
        setFoodChart()
        setWaterChart()
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
        foodChart3.xAxis.position = XAxis.XAxisPosition.BOTTOM
        foodChart3.xAxis.setDrawGridLines(false)
        foodChart3.xAxis.valueFormatter = MyXAxisFormatter()

        //y-axis formatting
        foodChart3.getAxisRight().setEnabled(false)
        foodChart3.axisLeft.setDrawGridLines(false) // no grid lines
        foodChart3.axisLeft.setAxisMinimum(0f)

        val deliveryDataSet = BarDataSet(barEntries, "Today's Food Deliveries for your residents")
        val deliveryData = BarData(deliveryDataSet)

        val xAxis: XAxis = foodChart3.xAxis

        foodChart3.setData(deliveryData)
//        foodChart1.setDrawGridLines(false)
        foodChart3.setDrawGridBackground(false)
        foodChart3.setDrawValueAboveBar(true)
        foodChart3.setDrawBorders(false)
        foodChart3.setDescription(null)
        foodChart3.animateY(500)
        foodChart3.invalidate()
    }

    private fun setWaterChart(){
        val barEntries: ArrayList<BarEntry> = ArrayList()

        //populate with fake data
        barEntries.add(BarEntry(0f, waterDeliveries[0].toFloat()))
        barEntries.add(BarEntry(1f, waterDeliveries[1].toFloat()))
        barEntries.add(BarEntry(2f, waterDeliveries[2].toFloat()))
        barEntries.add(BarEntry(3f, waterDeliveries[3].toFloat()))
        barEntries.add(BarEntry(4f, waterDeliveries[4].toFloat()))
        barEntries.add(BarEntry(5f, waterDeliveries[5].toFloat()))

        //x-axis formatting
        waterChart3.xAxis.position = XAxis.XAxisPosition.BOTTOM
        waterChart3.xAxis.setDrawGridLines(false)
        waterChart3.xAxis.valueFormatter = MyXAxisFormatter()

        //y-axis formatting
        waterChart3.getAxisRight().setEnabled(false)
        waterChart3.axisLeft.setDrawGridLines(false) // no grid lines
        waterChart3.axisLeft.setAxisMinimum(0f)

        val deliveryDataSet = BarDataSet(barEntries, "Today's water deliveries for your residents")
        val deliveryData = BarData(deliveryDataSet)
        waterChart3.setData(deliveryData)
        waterChart3.setDrawGridBackground(false)
        waterChart3.setDrawValueAboveBar(true)
        waterChart3.setDrawBorders(false)
        waterChart3.setDescription(null)
        waterChart3.animateY(500)
        waterChart3.invalidate()
    }

    class MyXAxisFormatter : ValueFormatter() {
        private val days = arrayOf("Resident 1", "Resident 2", "Resident 3", "Resident 4", "Resident 5", "Resident 6")
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return days.getOrNull(value.toInt()) ?: value.toString()
        }
    }
}