package com.g.laurent.alitic.Controllers.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.g.laurent.alitic.Controllers.Activities.OnTimeLineDisplay
import com.g.laurent.alitic.Controllers.ClassControllers.*
import com.g.laurent.alitic.Views.CalendarAdapter
import com.g.laurent.alitic.getMonth
import com.g.laurent.alitic.getYear
import com.roomorama.caldroid.CaldroidFragment
import hirondelle.date4j.DateTime
import java.util.*


class ChronoFragment : CaldroidFragment() {

    private lateinit var contextFragment:Context
    private lateinit var onTimeLineDisplay:OnTimeLineDisplay
    private lateinit var onCalendarLoaded:OnCalendarLoaded
    lateinit var calendarAdapter:CalendarAdapter
    lateinit var chronoEvents:List<DateTime>
    lateinit var chronoMeals:List<DateTime>
    var dateSelected:Long? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Set the month to display
        val dateCal = dateSelected
        if(dateCal!=null){
            this.month = getMonth(dateCal)
            this.year = getYear(dateCal)
        } else {
            val cal = Calendar.getInstance()
            this.month = cal.get(Calendar.MONTH) + 1
            this.year = cal.get(Calendar.YEAR)

            chronoEvents = getChronoEvents(context = contextFragment)
            chronoMeals = getChonoMeals(context = contextFragment)
        }

        // Set Caldroid fragment custom theme
        val args = Bundle()
        args.putInt(START_DAY_OF_WEEK, MONDAY) // start day of the week
        args.putInt(THEME_RESOURCE, com.caldroid.R.style.CaldroidDefaultDark)
        args.putBoolean(DISABLE_DATES, false)
        this.arguments = args

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    fun updateData(dateSelected:Long?){
        if(::calendarAdapter.isInitialized && dateSelected!=null){
            chronoEvents = updateChronoEvents(chronoEvents.toMutableList(), dateSelected, context = contextFragment)
            chronoMeals = updateChonoMeals(chronoMeals.toMutableList(), dateSelected, context = contextFragment)
            this.dateSelected = dateSelected
            calendarAdapter.notifyDataSetChanged()
        }
    }

    override fun getNewDatesGridAdapter(month: Int, year: Int): CalendarAdapter {

        calendarAdapter = CalendarAdapter(this,
            contextFragment,
            onTimeLineDisplay,
            this.month,
            this.year,
            false,
            getCaldroidData(),
            HashMap()
        )

        onCalendarLoaded.calendarLoaded()

        return calendarAdapter
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