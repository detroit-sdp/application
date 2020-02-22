package com.example.sdp_assistiverobot

import android.util.Log
import android.view.View
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QueryDocumentSnapshot

object DatabaseManager {

    private val TAG = "DatabaseManager"
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val residents: ArrayList<Resident> = ArrayList()
    private lateinit var listener: ListenerRegistration

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

        db.collection("Residents").apply {
            listener = addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "listen:error", e)
                    return@addSnapshotListener
                }

                for (dc in snapshots!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            val resident = newResident(dc.document)
                            residents.add(resident)
                        }
                        DocumentChange.Type.MODIFIED -> {
                            Log.d(TAG, "Modified Resident: ${dc.document.data}")
                            val resident = newResident(dc.document)
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
    }

    fun detachListener() {
        listener.remove()
    }

    private fun newResident(document: QueryDocumentSnapshot): Resident {
//        Log.d(TAG, "New Resident: ${document.data}")
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
        return Resident(first, last, priority, location)
    }

}