package com.example.sdp_assistiverobot.util

import android.util.Log
import com.example.sdp_assistiverobot.util.Constants.currentUser
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QueryDocumentSnapshot

object DatabaseManager {

    private val TAG = "DatabaseManager"
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val residents: ArrayList<Resident> = ArrayList()
    private lateinit var residentListener: ListenerRegistration
    private lateinit var deliveryListener: ListenerRegistration

    fun getInstance(): DatabaseManager {
        return this
    }

    fun getResidents(): ArrayList<Resident> {
        return residents
    }

    fun initializeDB() {
        Log.w(TAG, "Set listener")

        if (residents.isNotEmpty()) {
            return
        }

        val query = db.collection("Residents")
        residentListener = query.addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.w(TAG, "listen:error", e)
                return@addSnapshotListener
            }

            for (dc in snapshots!!.documentChanges) {
                when (dc.type) {
                    DocumentChange.Type.ADDED -> {
                        Log.d(TAG, "Added Resident: ${dc.document.data}")
                        val resident =
                            newResident(
                                dc.document
                            )
                        residents.add(resident)
                    }
                    DocumentChange.Type.MODIFIED -> {
                        Log.d(TAG, "Modified Resident: ${dc.document.data}")
                        val resident =
                            newResident(
                                dc.document
                            )
                        residents[dc.oldIndex] = resident
                    }
                    DocumentChange.Type.REMOVED -> {
                        Log.d(TAG, "Removed Resident: ${dc.document.data}")
                        residents.removeAt(dc.oldIndex)
                    }
                }
            }
        }
    }

    fun detachListener() {
        residentListener.remove()
    }

    private fun newResident(document: QueryDocumentSnapshot): Resident {
        var carer: String
        var first: String
        var last: String
        var priority: String
        var location: String
        document.apply {
            carer = get("carer").toString()
            first = get("first").toString()
            last = get("last").toString()
            priority = get("priority").toString()
            location = get("location").toString()
        }
        return Resident(
            carer,
            first,
            last,
            priority,
            location
        )
    }

}