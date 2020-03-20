package com.example.sdp_assistiverobot.calendar

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import com.example.sdp_assistiverobot.R
import com.example.sdp_assistiverobot.util.DatabaseManager.getResidents
import com.example.sdp_assistiverobot.util.Util.convertLongToDate
import com.example.sdp_assistiverobot.util.Util.convertLongToTime
import kotlinx.android.synthetic.main.activity_event_view.*

class EventViewActivity : AppCompatActivity() {

    private lateinit var event: Delivery
    private val EDIT_DELIVERY = 0

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu_single, menu)
        menu?.getItem(0)?.setIcon(R.drawable.baseline_edit_black_24)
        menu?.getItem(0)?.iconTintList = ContextCompat.getColorStateList(this, R.color.colorPrimary)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_view)
        setSupportActionBar(findViewById(R.id.event_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        event = intent.getSerializableExtra("event") as Delivery

        state.text = "${event.deliveryState}"
        time.text = "${convertLongToDate(event.date)} ${convertLongToTime(event.time)}"
        note.text = "${event.note}"

        val residents = getResidents()
        resident.text = "${residents[event.residentId]?.first} ${residents[event.residentId]?.last}"
        location.text = "${residents[event.residentId]?.location}"
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action0 -> {

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == EDIT_DELIVERY) {
            if (resultCode == Activity.RESULT_OK) {
                finish()
                startActivity(data)
            } else if (resultCode == Activity.RESULT_CANCELED) {
                finish()
                startActivity(intent)
            }
        }
    }
}
