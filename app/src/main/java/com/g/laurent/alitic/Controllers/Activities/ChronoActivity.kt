package com.g.laurent.alitic.Controllers.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.g.laurent.alitic.Controllers.ClassControllers.saveNewEvent
import com.g.laurent.alitic.Controllers.ClassControllers.saveNewEventType
import com.g.laurent.alitic.Controllers.Fragments.*
import com.g.laurent.alitic.R
import com.g.laurent.alitic.getDateAsLong


class ChronoActivity : AppCompatActivity(), OnTimeLineDisplay {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chrono)

        clearDatabase(applicationContext)

        val chronoFragment = ChronoFragment()
        val fragmentManager = supportFragmentManager.beginTransaction()
        fragmentManager.replace(R.id.calendar_fragment, chronoFragment)
        fragmentManager.commit()
    }

    override fun displayTimeLineFragment(day: Int, month: Int, year: Int) {

        val timeLineFragment = TimeLineFragment()

        val bundle=Bundle()
        bundle.putInt(DAY_ARGS, day)
        bundle.putInt(MONTH_ARGS, month)
        bundle.putInt(YEAR_ARGS, year)
        timeLineFragment.arguments = bundle

        val fragmentManager = supportFragmentManager.beginTransaction()
        fragmentManager.replace(R.id.calendar_fragment, timeLineFragment)
        fragmentManager.commit()
    }

}

interface OnTimeLineDisplay {
    fun displayTimeLineFragment(day:Int, month:Int, year:Int)
}