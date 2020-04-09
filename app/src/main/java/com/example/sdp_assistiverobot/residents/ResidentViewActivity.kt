package com.example.sdp_assistiverobot.residents

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.sdp_assistiverobot.R
//import com.example.sdp_assistiverobot.util.Resident
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.example.sdp_assistiverobot.map.ConfirmDialogFragment
import kotlinx.android.synthetic.main.activity_resident_view.*

class ResidentViewActivity : AppCompatActivity() {

    private val TAG = "ResidentViewActivity"
    lateinit var resident: Resident
    lateinit var id: String
    private val EDIT_PROFILE = 1

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu_single, menu)
        menu?.getItem(0)?.setIcon(R.drawable.baseline_edit_black_24)
        menu?.getItem(0)?.iconTintList = ContextCompat.getColorStateList(this, R.color.colorPrimary)
        return super.onCreateOptionsMenu(menu)
    }

    private var  dayVisitsNum = 0
    private var  weekVisitsNum = 5

    private var foodDelivery = 0
    private val foodDeliveries: IntArray = intArrayOf(5,6,2,4,5,7,foodDelivery)

    private var waterDelivery = 0
    private val waterDeliveries: IntArray = intArrayOf(4,6,5,4,5,6,waterDelivery)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resident_view)
        setSupportActionBar(findViewById(R.id.resident_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onStart() {
        super.onStart()
        setResidentInfo()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action0 -> {
                startActivityForResult(Intent(this, EditResidentActivity::class.java).apply {
                    putExtra("id", id)
                    putExtra("resident", resident)
                }, EDIT_PROFILE)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * If the info is changed, update the activity
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == EDIT_PROFILE) {
            if (resultCode == Activity.RESULT_OK) {
                finish()
                startActivity(data)
            } else if (resultCode == Activity.RESULT_CANCELED) {
                finish()
                startActivity(intent)
            } else if (resultCode == 1) {
                finish()
            }
        }
    }

    private fun setResidentInfo() {
        resident = intent.getSerializableExtra("resident") as Resident
        id = intent.getStringExtra("id")
        name.text = "${resident.first} ${resident.last}"
        location.text = "${resident.location}"
        priority.text = "${resident.priority}"
        dayVisits.text = "${dayVisitsNum}"
        weekVisits.text = "${weekVisitsNum}"
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

        barEntries.add(BarEntry(6f, foodDelivery.toFloat()))

        //x-axis formatting
        foodChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        foodChart.xAxis.setDrawGridLines(false)
        foodChart.xAxis.valueFormatter = MyXAxisFormatter()

        //y-axis formatting
        foodChart.getAxisRight().setEnabled(false)
        foodChart.axisLeft.setDrawGridLines(false) // no grid lines
        foodChart.axisLeft.setAxisMinimum(0f)

        val deliveryDataSet = BarDataSet(barEntries, "No. of food deliveries")
        val deliveryData = BarData(deliveryDataSet)
        foodChart.setData(deliveryData)
//        foodChart1.setDrawGridLines(false)
        foodChart.setDrawGridBackground(false)
        foodChart.setDrawValueAboveBar(true)
        foodChart.setDrawBorders(false)
        foodChart.setDescription(null)
        foodChart.animateY(500)
        foodChart.invalidate()
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
        waterChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        waterChart.xAxis.setDrawGridLines(false)
        waterChart.xAxis.valueFormatter = MyXAxisFormatter()

        //y-axis formatting
        waterChart.getAxisRight().setEnabled(false)
        waterChart.axisLeft.setDrawGridLines(false) // no grid lines
        waterChart.axisLeft.setAxisMinimum(0f)

        val deliveryDataSet = BarDataSet(barEntries, "No. of water deliveries")
        val deliveryData = BarData(deliveryDataSet)
        waterChart.setData(deliveryData)
//        barChart1.setDrawGridLines(false)
        waterChart.setDrawGridBackground(false)
        waterChart.setDrawValueAboveBar(true)
        waterChart.setDrawBorders(false)
        waterChart.setDescription(null)
        waterChart.animateY(500)
        waterChart.invalidate()
    }

    class MyXAxisFormatter : ValueFormatter() {
        private val days = arrayOf("Mon", "Tues", "Weds", "Thurs", "Fri", "Sat", "Sun")
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return days.getOrNull(value.toInt()) ?: value.toString()
        }
    }
}
