package com.example.organizer.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.organizer.R
import com.example.organizer.databinding.ItemCaseBinding
import com.example.organizer.databinding.ItemTimeBinding
import com.example.organizer.entities.Case
import org.joda.time.format.DateTimeFormat

class CaseHolder(
    itemView: View,
    private val itemClick: (String) -> (Unit)
) : RecyclerView.ViewHolder(itemView) {

    constructor(
        itemCaseBinding: ItemCaseBinding,
        itemClick: (String) -> (Unit)
    ) : this(itemCaseBinding.root, itemClick) {
        this.itemCaseBinding = itemCaseBinding
    }

    constructor(
        itemTimeBinding: ItemTimeBinding,
        itemClick: (String) -> (Unit)
    ) : this(itemTimeBinding.root, itemClick) {
        this.itemTimeBinding = itemTimeBinding
    }

    private var itemCaseBinding: ItemCaseBinding? = null
    private var itemTimeBinding: ItemTimeBinding? = null

    companion object {
        fun create(parent: ViewGroup, itemClick: (String) -> (Unit), viewType: Int): CaseHolder {
            when (viewType) {
                ViewTypes.CASE.value -> {
                    val holderBinding = ItemCaseBinding.bind(
                        LayoutInflater.from(parent.context).inflate(
                            R.layout.item_case,
                            parent,
                            false
                        )
                    )
                    return CaseHolder(holderBinding, itemClick)
                }
                ViewTypes.TIME.value -> {
                    val holderBinding = ItemTimeBinding.bind(
                        LayoutInflater.from(parent.context).inflate(
                            R.layout.item_time,
                            parent,
                            false
                        )
                    )
                    return CaseHolder(holderBinding, itemClick)
                }
                else -> return CaseHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.item_time, parent, false),
                    itemClick
                )
            }

        }
    }

    fun bind(caseItem: CaseItem) {
        when (caseItem.type) {
            ViewTypes.CASE -> {
                val case = caseItem.havingInterval as Case
                itemCaseBinding?.run {
                    root.setOnClickListener {
                        itemClick.invoke(case.id)
                    }
                    tvName.text = case.name
                    if (case.description.isEmpty()) {
                        tvDescription.visibility = View.GONE
                    } else {
                        tvDescription.text = case.description
                    }
                    tvTimeStart.text = case.getDateTimeStart().toString(DateTimeFormat.shortTime())
                    tvTimeFinish.text =
                        case.getDateTimeFinish().toString(DateTimeFormat.shortTime())
                }
            }
            ViewTypes.TIME -> {
                itemTimeBinding?.run {
                    tvTimeStart.text = caseItem.havingInterval.getDateTimeStart()
                        .toString(DateTimeFormat.shortTime())
                    tvTimeFinish.text = caseItem.havingInterval.getDateTimeFinish()
                        .toString(DateTimeFormat.shortTime())
                }
            }
        }

    }
}