package com.g.laurent.alitic.Controllers.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.g.laurent.alitic.Controllers.Activities.OnTimeLineDisplay
import com.g.laurent.alitic.Views.CalendarAdapter
import com.roomorama.caldroid.CaldroidFragment
import java.util.*


class ChronoFragment : CaldroidFragment() {

    private lateinit var contextFragment:Context
    private lateinit var onTimeLineDisplay:OnTimeLineDisplay

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val cal = Calendar.getInstance()
        this.month = cal.get(Calendar.MONTH) + 1
        this.year = cal.get(Calendar.YEAR)

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun getNewDatesGridAdapter(month: Int, year: Int): CalendarAdapter {
        return CalendarAdapter(contextFragment, onTimeLineDisplay, month, year, false, getCaldroidData(), HashMap())
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context!=null)
            contextFragment = context
        if(context is OnTimeLineDisplay)
            onTimeLineDisplay = context
    }

}
