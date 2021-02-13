package com.example.organizer.storage

import com.example.organizer.entities.Case

public interface RealTimeUpdatesListener {
    fun onDataUpdated(list: List<Case>)
}