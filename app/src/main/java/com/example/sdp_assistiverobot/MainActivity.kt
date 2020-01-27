package com.example.sdp_assistiverobot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.sdp_assistiverobot.patients.PatientsFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var selectedId: Int = 0
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        openFragment(DashboardFragment())

        bottom_navigation.selectedItemId = selectedId

        bottom_navigation.setOnNavigationItemSelectedListener {
            selectedId = it.itemId
            when (selectedId) {
                R.id.navigation_dashboard -> {
                    openFragment(DashboardFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_patients -> {
                    openFragment(PatientsFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_calendar -> {
                    openFragment(CalendarFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_map -> {
                    openFragment(MapFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_me -> {
                    openFragment(UserFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                else -> return@setOnNavigationItemSelectedListener false
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

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }


}
