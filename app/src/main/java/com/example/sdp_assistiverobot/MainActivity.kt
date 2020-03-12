package com.example.sdp_assistiverobot

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.example.sdp_assistiverobot.util.Constants
import com.example.sdp_assistiverobot.util.DatabaseManager
import com.example.sdp_assistiverobot.calendar.CalendarFragment
import com.example.sdp_assistiverobot.dashboard.DashboardPrototype1Fragment
import com.example.sdp_assistiverobot.map.GuardService
import com.example.sdp_assistiverobot.map.MapFragment
import com.example.sdp_assistiverobot.map.NetworkCommService
import com.example.sdp_assistiverobot.userpage.UserFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var selectedId: Int = R.id.navigation_dashboard
    private val TAG = "MainActivity"
    private lateinit var db: DatabaseManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.main_toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Initialise database
        db = DatabaseManager.getInstance()
        db.initializeDB()

        // Get menu list
        val navIds = arrayListOf<Int>()
        for (i in 0 until bottom_navigation.menu.size()) {
            navIds.add(bottom_navigation.menu[i].itemId)
        }

        // Initialise the view, set navigation item selected listener
        selectedId = intent.getIntExtra("mapId", R.id.navigation_dashboard)

        if (savedInstanceState != null) {
            selectedId = savedInstanceState.getInt("selectedId")
        }
        bottom_navigation.selectedItemId = selectedId
        chooseFragment()

        bottom_navigation.setOnNavigationItemSelectedListener {
            selectedId = it.itemId
            if (navIds.contains(selectedId)) {
                chooseFragment()
                return@setOnNavigationItemSelectedListener true
            } else {
                return@setOnNavigationItemSelectedListener false
            }
        }

        // Start listener port for receiving UDP packet.
        val serviceIntent = Intent(this.baseContext, GuardService::class.java)
        startService(serviceIntent)
    }

    private fun startRecursiveNetworkService() {

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        Constants.HAS_MAIN_FOCUSED = hasFocus
        if (hasFocus) {
            Log.d(TAG,"Main activity has focus")
        } else {
            createNotificationChannel()
            Log.d(TAG,"Main activity has no focus")
        }
    }

    private fun chooseFragment() {
        when (selectedId) {
            R.id.navigation_dashboard -> {
                toolbar_title.text = "Dashboard"
//                openFragment(DashboardFragment())
                openFragment(DashboardPrototype1Fragment())
//                openFragment(DashboardPrototype2Fragment())
//                openFragment(DashboardPrototype3Fragment())
            }

            R.id.navigation_calendar -> {
                toolbar_title.text = "Calendar"
                openFragment(CalendarFragment())
            }
            R.id.navigation_map -> {
                toolbar_title.text = "Map"
                openFragment(MapFragment())
            }
            R.id.navigation_me -> {
                toolbar_title.text = "Me"
                openFragment(UserFragment())
            }
        }
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, fragment)
            addToBackStack(null)
            commit()
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "SDPRobot"
            val descriptionText = "Notifications from robot"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(Constants.CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt("selectedId", selectedId)
    }

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        db.detachListeners()
    }

    override fun onResume() {
        super.onResume()
        chooseFragment()
    }
}
