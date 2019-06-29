package com.g.laurent.alitic.Controllers.Activities

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ProgressBar
import com.g.laurent.alitic.Controllers.Fragments.*
import com.g.laurent.alitic.R
import com.g.laurent.alitic.getDateAsLong
import com.g.laurent.alitic.getTextDate


class ChronoActivity : BaseActivity(), OnTimeLineDisplay, OnCalendarLoaded {


    private val chronoFragment = ChronoFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_chrono)
        super.onCreate(savedInstanceState)

        // Start progress Bar
        startProgressBar()

        movePicture(imageBackground, Loc.CENTER.position, Loc.BOTTOM_LEFT.position, matrix, this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_chrono, menu)
        configureToolbarWhenChronoFragment(toolbar, context.resources.getString(R.string.chronology), this)
        return super.onCreateOptionsMenu(menu)
    }

    fun configureChronoActivity() {
        showChronoFragment()
    }

    fun startProgressBar(){
        findViewById<ProgressBar>(R.id.progress_bar).visibility = View.VISIBLE
    }

    override fun calendarLoaded() {
        stopProgressBar()
    }

    fun stopProgressBar(){
        findViewById<ProgressBar>(R.id.progress_bar).visibility = View.GONE
    }

    fun showChronoFragment(){
        val fragmentManager = supportFragmentManager.beginTransaction()
        fragmentManager.replace(R.id.fragment_place, chronoFragment)
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
        fragmentManager.replace(R.id.fragment_place, timeLineFragment)
        fragmentManager.commit()

        configureToolbarWhenTimeLineFragment(toolbar, getTextDate(getDateAsLong(day,month,year,0,0)), this)
    }

    public override fun goToBackToMainPage(){
        super.goToBackToMainPage()

        // Remove fragments
        supportFragmentManager.popBackStack()

        // Move camera to the center of image in background
        movePicture(imageBackground, Loc.BOTTOM_LEFT.position,Loc.CENTER.position, matrix, this)
    }
}

interface OnTimeLineDisplay {
    fun displayTimeLineFragment(day:Int, month:Int, year:Int)
}

