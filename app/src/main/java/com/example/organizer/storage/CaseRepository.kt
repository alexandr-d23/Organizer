package com.example.organizer.storage

import android.util.Log
import com.example.organizer.entities.Case

object CaseRepository : RealTimeUpdatesListener {
    private var firebaseStorage: FirebaseStorage = FirebaseStorageImpl()
    private val realTimeUpdatesListeners: MutableCollection<RealTimeUpdatesListener> = ArrayList()
    private var casesList: MutableList<Case> = ArrayList()

    init {
        firebaseStorage.subscribeToRealtimeUpdates(this)
    }

    override fun onDataUpdated(list: List<Case>) {
        casesList = list.toMutableList()
        for (listener in realTimeUpdatesListeners) {
            listener.onDataUpdated(casesList)
        }
    }

    fun subscribeToRealtimeUpdates(listener: RealTimeUpdatesListener) {
        realTimeUpdatesListeners.add(listener)
    }

    fun getCases(): List<Case> = casesList

    fun getCases(year: Int, month: Int, day: Int): List<Case> = casesList.filter { case ->
        year == case.getDateTimeStart().year &&
                month == case.getDateTimeStart().monthOfYear
        day == case.getDateTimeStart().dayOfMonth
    }.also { Log.d("MYTAG", "${it.size}") }

    fun add(case: Case): Boolean {
        casesList.add(case)
        firebaseStorage.addCase(case)
        return false
    }

    fun getCaseById(id: String) = casesList.find {
        it.id == id
    }


}