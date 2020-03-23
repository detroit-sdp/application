package com.example.sdp_assistiverobot.calendar

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import com.example.sdp_assistiverobot.R
import com.example.sdp_assistiverobot.util.DatabaseManager.eventsRef
import com.example.sdp_assistiverobot.util.Util.convertLongToDate
import com.example.sdp_assistiverobot.util.Util.convertLongToTime
import com.example.sdp_assistiverobot.util.Util.generateEventId
import kotlinx.android.synthetic.main.activity_edit_event.*

class EditEventActivity : AppCompatActivity() {

    private val TAG = "EditEventActivity"

    private lateinit var event: Delivery

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu_single, menu)
        menu?.getItem(0)?.setIcon(R.drawable.baseline_check_black_24)
        menu?.getItem(0)?.iconTintList = ContextCompat.getColorStateList(this, R.color.colorPrimary)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action0 -> {

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_event)

        event = intent.getSerializableExtra("event") as Delivery
        val eventId = generateEventId("${convertLongToDate(event.date)} ${convertLongToTime(event.time)}")

        button_delete.setOnClickListener {
            eventsRef.document(eventId).delete()
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully deleted!")
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }
                .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
        }
    }
}
