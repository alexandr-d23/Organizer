package com.example.organizer.storage

import com.example.organizer.entities.Case

interface FirebaseStorage {
    fun subscribeToRealtimeUpdates(listener: RealTimeUpdatesListener)
    suspend fun retrieveCases(): List<Case>
    fun addCase(case: Case)
}