package com.example.sdp_assistiverobot.util

import android.util.Log
import com.example.sdp_assistiverobot.calendar.Event
import com.example.sdp_assistiverobot.residents.Resident
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QueryDocumentSnapshot

object DatabaseManager {

    private val TAG = "DatabaseManager"
    val DATABASE: FirebaseFirestore = FirebaseFirestore.getInstance()
    val AuthUser = FirebaseAuth.getInstance().currentUser
    private val residents: ArrayList<Resident> = ArrayList()
    private val events = ArrayList<Event>()
    private val locationToResident = HashMap<String, Resident>()
    private lateinit var residentListener: ListenerRegistration
    private lateinit var eventsListener: ListenerRegistration

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

        val residentRf = DATABASE.collection("Residents")
        residentListener = residentRf.addSnapshotListener { snapshots, e ->
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
                        locationToResident[resident.location] = resident
                    }
                    DocumentChange.Type.MODIFIED -> {
                        Log.d(TAG, "Modified Resident: ${dc.document.data}")
                        val resident =
                            newResident(
                                dc.document
                            )
                        locationToResident[resident.location] = resident
                        residents[dc.oldIndex] = resident
                    }
                    DocumentChange.Type.REMOVED -> {
                        Log.d(TAG, "Removed Resident: ${dc.document.data}")
                        val location = dc.document.get("location").toString()
                        locationToResident.remove(location)
                        residents.removeAt(dc.oldIndex)
                    }
                }
            }
        }

        val eventQuery = DATABASE.collection("Events").whereEqualTo(AuthUser!!.email.toString(),true)
        eventsListener = eventQuery.addSnapshotListener{ snapshots, e ->
            if (e != null) {
                Log.w(TAG, "listen:error", e)
                return@addSnapshotListener
            }

            for (dc in snapshots!!.documentChanges) {
                when (dc.type) {
                    DocumentChange.Type.ADDED -> {
                        Log.d(TAG, "Added Event: ${dc.document.data}")
                        val event =
                            newEvent(
                                dc.document
                            )
                        events.add(event)
                    }
                    DocumentChange.Type.MODIFIED -> {
                        Log.d(TAG, "Modified Resident: ${dc.document.data}")
                        val resident =
                            newResident(
                                dc.document
                            )
                        locationToResident[resident.location] = resident
                        residents[dc.oldIndex] = resident
                    }
                    DocumentChange.Type.REMOVED -> {
                        Log.d(TAG, "Removed Event: ${dc.document.data}")
                        events.removeAt(dc.oldIndex)
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

    private fun newEvent(document: QueryDocumentSnapshot): Event {
        var date: String
        var resident: String
        var minute: Int
        val hour: Int

        document.apply {
            date = get("date").toString()
            minute = get("minute") as Int
            hour = get("hour") as Int
            resident = get("resident").toString()
        }
        return Event(
            date,
            hour,
            minute,
            locationToResident[resident]!!
        )
    }

}