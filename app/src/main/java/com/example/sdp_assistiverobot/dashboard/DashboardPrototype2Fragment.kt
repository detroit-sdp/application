package com.example.sdp_assistiverobot.dashboard


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
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
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_dashboard_prototype2.*
import kotlinx.android.synthetic.main.fragment_resident_object.*


/**
 * A simple [Fragment] subclass.
 */
class DashboardPrototype2Fragment : Fragment() {

    val TAG = "DashboardPrototype2"

    private lateinit var demoCollectionPagerAdapter: DemoCollectionPagerAdapter
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
        return inflater.inflate(R.layout.fragment_dashboard_prototype2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        demoCollectionPagerAdapter = DemoCollectionPagerAdapter(childFragmentManager)
        viewPager = view.findViewById(R.id.pager)
        viewPager.adapter = demoCollectionPagerAdapter
        tab_layout.setupWithViewPager(viewPager)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getResidents()
        Log.d(TAG, "Getting residents data")
        setFoodChart()
        setWaterChart()
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
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    private fun dailyVisitsChart(residentTypesNum: Array<Float>) {
        Log.d(TAG, "DailyVisits")

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

        barEntries.add(BarEntry(6f, foodDelivery.toFloat()))

        val deliveryDataSet = BarDataSet(barEntries, "Food Deliveries for your resident")
        val deliveryData = BarData(deliveryDataSet)
//        foodChart2.setData(deliveryData)
////        foodChart1.setDrawGridLines(false)
//        foodChart2.setDrawGridBackground(false)
//        foodChart2.setDrawValueAboveBar(false)
//        foodChart2.setDrawBorders(false)
//        foodChart2.setDescription(null)
//        foodChart2.animateY(500)
//        foodChart2.invalidate()
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

        barEntries.add(BarEntry(6f, waterDelivery.toFloat()))

        val deliveryDataSet = BarDataSet(barEntries, "No. of water deliveries")
        val deliveryData = BarData(deliveryDataSet)
//        waterChart2.setData(deliveryData)
////        barChart1.setDrawGridLines(false)
//        waterChart2.setDrawGridBackground(false)
//        waterChart2.setDrawValueAboveBar(false)
//        waterChart2.setDrawBorders(false)
//        waterChart2.setDescription(null)
//        waterChart2.animateY(500)
//        waterChart2.invalidate()
    }

    override fun onPause() {
        isPause = true
        super.onPause()
    }

}

class DemoCollectionPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getCount(): Int = 4

    override fun getPageTitle(position: Int): CharSequence {
        return "Resident ${(position + 1)}"
    }

    override fun getItem(i: Int): Fragment {
        val fragment = ResidentObjectFragment()
        fragment.arguments = Bundle().apply {
            // Our object is just an integer :-P
            putInt("Resident", i + 1)
        }
        return fragment
    }
}


private const val ARG_OBJECT = "object"

// Instances of this class are fragments representing a single
// object in our collection.
class ResidentObjectFragment : Fragment() {

    val TAG = "DashboardPrototype2"
    private var dayVisitsNum = 3
    private var weekVisitsNum = 25


    private var foodDelivery = 0
    private val foodDeliveries: IntArray = intArrayOf(5,6,2,4,5,7,foodDelivery)

    private var waterDelivery = 0
    private val waterDeliveries: IntArray = intArrayOf(4,6,5,4,5,6,waterDelivery)

    val daysOfWeek = arrayOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_resident_object, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(ARG_OBJECT) }?.apply {
            val textView: TextView = view.findViewById(android.R.id.text1)
            textView.text = getInt(ARG_OBJECT).toString()
        }
        setResidentInfo()
        setFoodChart()
        setWaterChart()
    }


    fun setResidentInfo(){
        Log.d(TAG, "setting info")
        dayVisits.text = "No. of visits today: ${dayVisitsNum}"
        weekVisits.text = "No. of visits this week: ${weekVisitsNum}"
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

        barEntries.add(BarEntry(6f, foodDelivery.toFloat()))

        //x-axis formatting
        foodChart2.xAxis.position = XAxis.XAxisPosition.BOTTOM
        foodChart2.xAxis.setDrawGridLines(false)
        foodChart2.xAxis.valueFormatter = MyXAxisFormatter()

        //y-axis formatting
        foodChart2.getAxisRight().setEnabled(false)
        foodChart2.axisLeft.setDrawGridLines(false) // no grid lines
        foodChart2.axisLeft.setAxisMinimum(0f)

        val deliveryDataSet = BarDataSet(barEntries, "No. of food deliveries")
        val deliveryData = BarData(deliveryDataSet)
        foodChart2.setData(deliveryData)
//        foodChart1.setDrawGridLines(false)
        foodChart2.setDrawGridBackground(false)
        foodChart2.setDrawValueAboveBar(true)
        foodChart2.setDrawBorders(false)
        foodChart2.setDescription(null)
        foodChart2.animateY(500)
        foodChart2.invalidate()
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

        barEntries.add(BarEntry(6f, waterDelivery.toFloat()))

        //x-axis formatting
        waterChart2.xAxis.position = XAxis.XAxisPosition.BOTTOM
        waterChart2.xAxis.setDrawGridLines(false)
        waterChart2.xAxis.valueFormatter = MyXAxisFormatter()

        //y-axis formatting
        waterChart2.getAxisRight().setEnabled(false)
        waterChart2.axisLeft.setDrawGridLines(false) // no grid lines
        waterChart2.axisLeft.setAxisMinimum(0f)

        val deliveryDataSet = BarDataSet(barEntries, "No. of water deliveries")
        val deliveryData = BarData(deliveryDataSet)
        waterChart2.setData(deliveryData)
//        barChart1.setDrawGridLines(false)
        waterChart2.setDrawGridBackground(false)
        waterChart2.setDrawValueAboveBar(true)
        waterChart2.setDrawBorders(false)
        waterChart2.setDescription(null)
        waterChart2.animateY(500)
        waterChart2.invalidate()
    }

    class MyXAxisFormatter : ValueFormatter() {
        private val days = arrayOf("Mon", "Tues", "Weds", "Thurs", "Fri", "Sat", "Sun")
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return days.getOrNull(value.toInt()) ?: value.toString()
        }
    }
}

