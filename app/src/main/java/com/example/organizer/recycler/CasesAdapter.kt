package com.example.organizer.recycler

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.organizer.entities.Case
import org.joda.time.DateTime
import org.joda.time.MutableDateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class CasesAdapter(
    private val itemClick: (String) -> (Unit)
) : ListAdapter<CaseItem, CaseHolder>(
    object : DiffUtil.ItemCallback<CaseItem>() {
        override fun areItemsTheSame(oldItem: CaseItem, newItem: CaseItem): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: CaseItem, newItem: CaseItem): Boolean =
            oldItem == newItem
    }
) {

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type.value
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CaseHolder =
        CaseHolder.create(parent, itemClick, viewType)

    override fun onBindViewHolder(holder: CaseHolder, position: Int) =
        holder.bind(getItem(position))

    fun updateList(list: List<Case>) {
        submitList(
            getListWithTimeElements(list)
        )
    }

    private fun getListWithTimeElements(list: List<Case>): List<CaseItem> {
        val array: BooleanArray = BooleanArray(24)
        for (case in list) {
            for (i in case.getDateTimeStart().hourOfDay..case.getDateTimeFinish().hourOfDay) {
                array[i] = true
            }
        }
        var resList = ArrayList<CaseItem>()
        for (i in array.indices) {
            if (!array[i]) {
                resList.add(CaseItem(Time(i, (i + 1) % 24), ViewTypes.TIME))
            }
        }
        list.forEach { case -> resList.add(CaseItem(case, ViewTypes.CASE)) }
        return resList.sortedWith { case1, case2 ->
            (case1.havingInterval.getDateTimeStart().hourOfDay * 60 + case1.havingInterval.getDateTimeStart().minuteOfHour)
                .compareTo((case2.havingInterval.getDateTimeStart().hourOfDay * 60 + case2.havingInterval.getDateTimeStart().minuteOfHour))
                .also {
                    Log.d(
                        "MYTAG",
                        "$it ${
                            case1.havingInterval.getDateTimeStart()
                                .toString(DateTimeFormat.shortTime())
                        } ${
                            case2.havingInterval.getDateTimeStart()
                                .toString(DateTimeFormat.shortTime())
                        }"
                    )
                }
        }.onEach {
            Log.d("TAG", it.havingInterval.getDateTimeStart().toString(DateTimeFormat.shortTime()))
        }
    }

    inner class Time(
        private val timeStart: DateTime,
        private val timeFinish: DateTime
    ) : CaseItem.HavingInterval {

        constructor(
            hourOfDayStart: Int,
            hourOfDayFinish: Int
        ) : this(
            MutableDateTime().apply {
                hourOfDay = hourOfDayStart
                minuteOfHour = 0
            }.toDateTime(),
            MutableDateTime().apply {
                hourOfDay = hourOfDayFinish
                minuteOfHour = 0
            }.toDateTime()
        )

        override fun getDateTimeStart(): DateTime = timeStart
        override fun getDateTimeFinish(): DateTime = timeFinish
    }

}
