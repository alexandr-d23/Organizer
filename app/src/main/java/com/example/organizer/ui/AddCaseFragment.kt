package com.example.organizer.ui

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.organizer.R
import com.example.organizer.databinding.FragmentAddCaseBinding
import com.example.organizer.entities.Case
import org.joda.time.DateTime
import org.joda.time.MutableDateTime
import org.joda.time.format.DateTimeFormat

class AddCaseFragment : Fragment() {

    private var _binding: FragmentAddCaseBinding? = null
    private val binding get(): FragmentAddCaseBinding = _binding!!
    private val dateTimeStart: MutableDateTime = MutableDateTime()
    private val dateTimeFinish: MutableDateTime = MutableDateTime().apply { addHours(1) }
    private lateinit var adding: Adding

    interface Adding {
        fun addCase(case: Case)
    }

    companion object {
        fun newInstance(bundle: Bundle = Bundle()): AddCaseFragment {
            val fragment = AddCaseFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        adding = context as Adding
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddCaseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            tvTimeStart.also {
                setTextWithPrefix(
                    it,
                    getString(R.string.start_from),
                    dateTimeStart.toString(DateTimeFormat.shortTime())
                )
                it.setOnClickListener {
                    showDialog(dateTimeStart, R.string.start_from, binding.tvTimeStart, true)
                }
            }
            tvTimeFinish.also {
                setTextWithPrefix(
                    it,
                    getString(R.string.to),
                    dateTimeFinish.toString(DateTimeFormat.shortTime())
                )
                it.setOnClickListener {
                    showDialog(dateTimeFinish, R.string.to, binding.tvTimeFinish, false)
                }
            }
            btnAdd.setOnClickListener {
                if (isCorrectly()) {
                    adding.addCase(
                        Case(
                            dateTimeStart.toDateTime(),
                            dateTimeFinish.toDateTime(),
                            binding.etName.text.toString(),
                            binding.etDescription.text.toString()
                        )
                    )
                    activity?.onBackPressed()
                }
            }
        }
        val date: DateTime? = arguments?.getSerializable(CalendarFragment.DAY_ID) as? DateTime
        date?.let {
            binding.dpDate.init(it.year, it.monthOfYear - 1, it.dayOfMonth) { _, i, i2, i3 ->
                dateTimeStart.setDate(i, i2 + 1, i3)
                dateTimeFinish.setDate(i, i2 + 1, i3)
            }
            dateTimeStart.setDate(date.year, date.monthOfYear, date.dayOfMonth)
            dateTimeFinish.setDate(date.year, date.monthOfYear, date.dayOfMonth)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setTextWithPrefix(textView: TextView, prefix: String, text: String) {
        "$prefix $text".also { textView.text = it }
    }

    private fun isCorrectly(): Boolean {
        var checkRes = true
        if (binding.etName.text.toString().isEmpty()) {
            binding.tiName.error = "Case must have name"
            checkRes = false
        }
        if (dateTimeFinish.isBefore(dateTimeStart)) {
            binding.tvTimeFinish.error = "Finish time must be after start time"
        }
        return checkRes
    }

    private fun showDialog(
        dateTime: MutableDateTime,
        resId: Int,
        textView: TextView,
        isStart: Boolean
    ) {
        val dateTimeForDialog = if (isStart) dateTimeStart else dateTimeFinish
        val dialog = TimePickerDialog(
            requireContext(),
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                dateTime.setTime(hourOfDay, minute, 0, 0)
                setTextWithPrefix(
                    textView,
                    getString(resId),
                    dateTime.toString(DateTimeFormat.shortTime())
                )
            },
            dateTimeForDialog.hourOfDay,
            dateTimeForDialog.minuteOfHour,
            true
        )
        dialog.show()
    }


}