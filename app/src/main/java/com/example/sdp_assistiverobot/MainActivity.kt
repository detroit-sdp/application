package com.example.sdp_assistiverobot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // By default, load the patients fragment
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, PatientsFragment())
            addToBackStack(null)
            commit()
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_patients -> {
                    openFragment(PatientsFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_analytics -> {
                    openFragment(AnalyticsFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_addPatient -> {
                    openFragment(AddPatientFragment())
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

    fun openFragment(fragment: Fragment) {
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
