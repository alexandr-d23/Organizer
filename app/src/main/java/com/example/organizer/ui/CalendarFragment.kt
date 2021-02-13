package com.example.organizer.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.organizer.databinding.FragmentCalendarBinding
import com.example.organizer.recycler.CasesAdapter
import com.example.organizer.storage.CaseRepository
import org.joda.time.DateTime

class CalendarFragment : Fragment() {

    private lateinit var onFabClickListener: OnFabClickListener
    private lateinit var caseInformationShowing: CaseInformationShowing
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() : FragmentCalendarBinding = _binding!!
    private lateinit var adapter: CasesAdapter
    private var chosenDay = DateTime()

    interface OnFabClickListener {
        fun onFabClick(bundle: Bundle)
    }

    interface CaseInformationShowing {
        fun show(caseId: String)
    }

    companion object {
        const val DAY_ID = "DAY_ID_SERIALIZABLE"

        fun newInstance(): CalendarFragment {
            val args = Bundle()
            val fragment = CalendarFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onFabClickListener = context as OnFabClickListener
        caseInformationShowing = context as CaseInformationShowing
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (savedInstanceState?.getSerializable(DAY_ID) as? DateTime)?.let {
            chosenDay = it
        }
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        adapter = CasesAdapter {
            caseInformationShowing.show(it)
        }
        with(binding) {
            rvCases.adapter = adapter
            rvCases.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
            fab.setOnClickListener {
                val bundle = Bundle()
                bundle.putSerializable(DAY_ID, chosenDay)
                onFabClickListener.onFabClick(bundle)
            }
            calendarView.setDate(chosenDay.toDate())
            calendarView.setOnDayClickListener {
                chosenDay = DateTime(it.calendar)
                updateRecyclerList()
            }
        }
        updateRecyclerList()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(DAY_ID, chosenDay)
    }

    fun updateRecyclerList() {
        adapter.updateList(
            CaseRepository.getCases(
                chosenDay.year,
                chosenDay.monthOfYear,
                chosenDay.dayOfMonth
            )
        )
    }

}