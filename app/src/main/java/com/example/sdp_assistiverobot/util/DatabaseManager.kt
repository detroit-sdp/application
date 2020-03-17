package com.example.sdp_assistiverobot.util

import android.util.Log
import com.example.sdp_assistiverobot.calendar.Delivery
import com.example.sdp_assistiverobot.residents.Resident
import com.example.sdp_assistiverobot.util.Util.todayToLong
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.ktx.Firebase

object DatabaseManager {

    private val TAG = "DatabaseManager"

    val DATABASE: FirebaseFirestore = FirebaseFirestore.getInstance()
    val robotStatusRef = Firebase.database.getReference("Status")
    val authUser = FirebaseAuth.getInstance().currentUser!!
    val residentsRef = DATABASE.collection("Residents")
    val eventsRef = DATABASE.collection("Delivery")

//    private val residents = ArrayList<Resident>()
    private val residents = HashMap<String, Resident>()
    private val todayUnsuccessfulDelivery = ArrayList<Delivery>()
    private lateinit var residentListener: ListenerRegistration
    private lateinit var eventsListener: ListenerRegistration

    fun getInstance(): DatabaseManager {
        return this
    }

    fun getResidents(): Map<String, Resident> {
        return residents
    }

    fun getTodayUnsuccessfulDelivery(): List<Delivery> {
        return todayUnsuccessfulDelivery
    }

    fun initializeDB() {
        Log.w(TAG, "Set listener")

        if (residents.isNotEmpty()) {
            return
        }

        residentListener = residentsRef.addSnapshotListener { snapshots, e ->
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
                        residents[dc.document.id] = resident
                    }
                    DocumentChange.Type.MODIFIED -> {
                        Log.d(TAG, "Modified Resident: ${dc.document.data}")
                        val resident =
                            newResident(
                                dc.document
                            )

                        residents[dc.document.id] = resident
                    }
                    DocumentChange.Type.REMOVED -> {
                        Log.d(TAG, "Removed Resident: ${dc.document.data}")
                        residents.remove(dc.document.id)
                    }
                }
            }
        }

//        eventsListener = eventsRef
//            .whereEqualTo("userId", authUser.email)
//            .whereEqualTo("deliverySuccessful", false)
//            .whereEqualTo("date", todayToLong())
//            .addSnapshotListener{ snapshots, e ->
//            if (e != null) {
//                Log.w(TAG, "listen:error", e)
//                return@addSnapshotListener
//            }
//
//            for (dc in snapshots!!.documentChanges) {
//                when (dc.type) {
//                    DocumentChange.Type.ADDED -> {
//                        Log.d(TAG, "Added Event: ${dc.document.data}")
//                        val event =
//                            newEvent(
//                                dc.document
//                            )
//                        todayUnsuccessfulDelivery.add(event)
//                    }
//                    DocumentChange.Type.MODIFIED -> {
//                        Log.d(TAG, "Modified Resident: ${dc.document.data}")
//                        val event =
//                            newEvent(
//                                dc.document
//                            )
//                        todayUnsuccessfulDelivery[dc.oldIndex] = event
//                    }
//                    DocumentChange.Type.REMOVED -> {
//                        Log.d(TAG, "Removed Event: ${dc.document.data}")
//                        todayUnsuccessfulDelivery.removeAt(dc.oldIndex)
//                    }
//                }
//            }
//        }
    }

    fun setUnsuccessfulDeliveryListener(time: Long) {
        val delivery = ArrayList<Delivery>()
        eventsRef
            .whereEqualTo("userId", authUser.email)
            .whereEqualTo("deliverySuccessful", false)
            .whereEqualTo("date", todayToLong())
            .whereGreaterThanOrEqualTo("time", Long)
            .get()
            .addOnSuccessListener {documents ->
                for (document in documents) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    delivery.add(DatabaseManager.newEvent(document))
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

    }

    fun detachListeners() {
        residentListener.remove()
        eventsListener.remove()
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

    fun newEvent(document: QueryDocumentSnapshot): Delivery {
        val userId: String
        val date: Long
        val residentId: String
        val time: Long
        val category: String
        val weightBefore: Double
        val weightAfter: Double
        val deliveryState: Int
        val note: String

        document.apply {
            userId = get("userId").toString()
            date = getLong("date")!!
            time = getLong("time")!!
            residentId = get("residentId").toString()
            category = get("category").toString()
            weightBefore = getDouble("weightBefore")!!
            weightAfter = getDouble("weightAfter")!!
            deliveryState = getLong("deliveryState")!!.toInt()
            note = get("note").toString()
        }
        return Delivery(
            userId,
            date,
            time,
            residentId,
            category,
            weightBefore,
            weightAfter,
            deliveryState,
            note
        )
    }

}