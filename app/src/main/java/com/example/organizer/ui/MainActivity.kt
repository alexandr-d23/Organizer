package com.example.organizer.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.organizer.R
import com.example.organizer.databinding.ActivityMainBinding
import com.example.organizer.entities.Case
import com.example.organizer.storage.RealTimeUpdatesListener
import com.example.organizer.storage.CaseRepository
import com.example.organizer.storage.FirebaseStorageImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), RealTimeUpdatesListener,
    CalendarFragment.OnFabClickListener, CalendarFragment.CaseInformationShowing,
    AddCaseFragment.Adding {

    private lateinit var binding: ActivityMainBinding
    private var calendarFragment: CalendarFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        calendarFragment = CalendarFragment.newInstance().also {
            supportFragmentManager.beginTransaction().replace(R.id.fl_container, it).commit()
        }
        CaseRepository.subscribeToRealtimeUpdates(this)
    }

    override fun onFabClick(bundle: Bundle) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_container, AddCaseFragment.newInstance(bundle)).addToBackStack(null)
            .commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        supportFragmentManager.popBackStack()
        if (supportFragmentManager.backStackEntryCount > 0) finish()
    }

    override fun addCase(case: Case) {
        CaseRepository.add(case)
    }

    override fun onDataUpdated(list: List<Case>) {
        CoroutineScope(Dispatchers.Main).launch {
            calendarFragment?.updateRecyclerList()
        }
    }

    override fun show(caseId: String) {
        CaseRepository.getCaseById(caseId)?.let { case ->
            supportFragmentManager.beginTransaction()
                .replace(R.id.fl_container, CaseInformationFragment.newInstance(case))
                .addToBackStack(null).commit()
        }

    }


}