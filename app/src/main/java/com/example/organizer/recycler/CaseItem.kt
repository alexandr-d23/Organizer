package com.example.organizer.recycler

import android.view.View
import com.example.organizer.entities.Case
import org.joda.time.DateTime

data class CaseItem(
    var havingInterval: HavingInterval,
    var type: ViewTypes
) {
    interface HavingInterval {
        fun getDateTimeStart(): DateTime
        fun getDateTimeFinish(): DateTime
    }
}