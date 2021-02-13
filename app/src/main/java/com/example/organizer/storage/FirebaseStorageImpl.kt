package com.example.organizer.storage

import android.util.Log
import com.example.organizer.entities.Case
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FirebaseStorageImpl : FirebaseStorage {

    data class FirebaseJson(
        val json: String = ""
    )

    private val caseCollectionRef = Firebase.firestore.collection("cases")
    private val realTimeUpdatesListeners: MutableCollection<RealTimeUpdatesListener> = ArrayList()

    init {
        subscribeToRealtimeUpdates()
    }

    override fun addCase(case: Case) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val gson = Gson()
                val doc = caseCollectionRef.document()
                Log.d("MYTAG", "doc id  ${doc.id}")
                case.id = doc.id
                doc.set(FirebaseJson(gson.toJson(case)))
            } catch (e: Exception) {
                Log.d("MYTAG", "Exception in FirebaseStorageImpl.addCase() " + e.message)
            }
        }
    }


    override suspend fun retrieveCases(): List<Case> {
        val resList: MutableList<Case> = ArrayList()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val snapshot = caseCollectionRef.get().await()
                val gson = Gson()
                for (document in snapshot.documents) {
                    val json = document.toObject(FirebaseJson::class.java)
                    val case = gson.fromJson(json?.json, Case::class.java)
                    case?.let {
                        resList.add(it)
                    }
                }
            } catch (e: Exception) {
                e.message?.let {
                    Log.d("MYTAG", "Exception in FirebaseStorageImpl.retrieveCases() " + it)
                }
            }
        }.join()
        Log.d("MYTAG", "INITIALIZED")
        return resList
    }

    override fun subscribeToRealtimeUpdates(listener: RealTimeUpdatesListener) {
        realTimeUpdatesListeners.add(listener)
    }

    private fun subscribeToRealtimeUpdates() {
        caseCollectionRef.addSnapshotListener { snapshot, error ->
            error?.let {
                return@addSnapshotListener
                TODO()
            }
            snapshot?.let {
                CoroutineScope(Dispatchers.IO).launch {
                    val resList: MutableList<Case> = ArrayList()
                    val gson = Gson()
                    for (document in snapshot.documents) {
                        val json = document.toObject(FirebaseJson::class.java)
                        val case = gson.fromJson(json?.json, Case::class.java)
                        case?.let {
                            resList.add(it)
                        }
                    }
                    for (listener in realTimeUpdatesListeners) {
                        listener.onDataUpdated(resList)
                    }
                }
            }
        }
    }


}
