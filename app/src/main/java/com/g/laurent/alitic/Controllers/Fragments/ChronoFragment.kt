package com.g.laurent.alitic.Controllers.Fragments

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.g.laurent.alitic.Controllers.Activities.OnTimeLineDisplay
import com.g.laurent.alitic.R
import com.g.laurent.alitic.Views.CalendarAdapter
import com.g.laurent.alitic.getLastDayMonth
import com.roomorama.caldroid.CaldroidFragment
import hirondelle.date4j.DateTime
import kotlinx.android.synthetic.main.activity_chrono.view.*
import java.util.*


class ChronoFragment : CaldroidFragment(), OnCalendarLoaded {


    private lateinit var contextFragment:Context
    private lateinit var onTimeLineDisplay:OnTimeLineDisplay
    private lateinit var onCalendarLoaded:OnCalendarLoaded

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val cal = Calendar.getInstance()
        this.month = cal.get(Calendar.MONTH) + 1
        this.year = cal.get(Calendar.YEAR)

        // Set Caldroid fragment custom theme
        val args = Bundle()
        args.putInt(THEME_RESOURCE, com.caldroid.R.style.CaldroidDefaultDark)
        this.arguments = args

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun getNewDatesGridAdapter(month: Int, year: Int): CalendarAdapter {
        return CalendarAdapter(contextFragment, onTimeLineDisplay, onCalendarLoaded, month, year, false, getCaldroidData(), HashMap())
    }

    override fun calendarLoaded() {
        onCalendarLoaded.calendarLoaded()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context!=null)
            contextFragment = context
        if(context is OnTimeLineDisplay)
            onTimeLineDisplay = context
        if(context is OnCalendarLoaded)
            onCalendarLoaded = context
    }

}

interface OnCalendarLoaded {
    fun calendarLoaded()
}
