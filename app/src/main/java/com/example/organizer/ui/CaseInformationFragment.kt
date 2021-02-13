package com.example.organizer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.organizer.databinding.FragmentCaseInformationBinding
import com.example.organizer.entities.Case
import org.joda.time.format.DateTimeFormat

class CaseInformationFragment : Fragment() {

    private lateinit var case: Case
    private var _binding: FragmentCaseInformationBinding? = null
    private val binding get() : FragmentCaseInformationBinding = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCaseInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        private const val CASE_ID = "CASE_SERIALIZABLE_ID"
        fun newInstance(case: Case): CaseInformationFragment {
            val args = Bundle()
            args.putSerializable(CASE_ID, case)
            val fragment = CaseInformationFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onStart() {
        super.onStart()
        case = arguments?.getSerializable(CASE_ID) as Case
        binding.btnBack.setOnClickListener {
            activity?.onBackPressed()
        }
        with(binding) {
            tvName.text = case.name
            tvDescription.text = case.description
            tvDate.text = case.getDateTimeStart().toString(DateTimeFormat.shortDate())
            tvTimeStart.text = case.getDateTimeStart().toString(DateTimeFormat.shortTime())
            tvTimeFinish.text = case.getDateTimeFinish().toString(DateTimeFormat.shortTime())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}