package com.g.laurent.alitic.Controllers.Activities

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ProgressBar
import com.g.laurent.alitic.Controllers.Fragments.*
import com.g.laurent.alitic.Models.Loc
import com.g.laurent.alitic.Models.Position
import com.g.laurent.alitic.R
import com.g.laurent.alitic.Views.LegendCalendarDialog
import com.g.laurent.alitic.getDateAsLong
import com.g.laurent.alitic.getTextDate


class ChronoActivity : BaseActivity(), OnTimeLineDisplay, OnCalendarLoaded {

    private val chronoFragment = ChronoFragment()
    private val timeLineFragment = TimeLineFragment()
    private var dateSelected:Long?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_chrono)
        super.onCreate(savedInstanceState)

        // Start progress Bar
        findViewById<ProgressBar>(R.id.progress_bar).visibility = View.VISIBLE

        movePicture(imageBackground, Loc.CENTER.position, Loc.BOTTOM_LEFT.position, matrix)
    }

    override fun onMenuItemClick() {
        val fm = supportFragmentManager
        val legendCalendarDialog = LegendCalendarDialog().newInstance()
        legendCalendarDialog.show(fm, "legendCalendarDialog")
    }

    override fun onClickBackButtonToolbar(){
        if(chronoFragment.isVisible){
            goToBackToMainPage()
        } else if(timeLineFragment.isVisible){
            showChronoFragment()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_chrono, menu)

        configureToolbar(toolbar,
            title = context.resources.getString(R.string.chronology),
            homeButtonNeeded = true,
            infoIconNeeded = true
        )

        return super.onCreateOptionsMenu(menu)
    }

    private fun configureChronoActivity() {
        showChronoFragment()
    }

    override fun calendarLoaded() {
        findViewById<ProgressBar>(R.id.progress_bar).visibility = View.GONE
    }

    private fun showChronoFragment(){

        chronoFragment.updateData(dateSelected)

        val fragmentManager = supportFragmentManager.beginTransaction()
        fragmentManager.replace(R.id.fragment_place, chronoFragment, CHRONO_FRAGMENT)
        fragmentManager.commit()

        // Configure Toolbar
        configureToolbar(toolbar,
            title = context.resources.getString(R.string.chronology),
            homeButtonNeeded = true,
            infoIconNeeded = true
        )
    }

    override fun displayTimeLineFragment(day: Int, month: Int, year: Int) {

        // Update dateSelected
        dateSelected = getDateAsLong(day, month, year,0,0)

        val bundle=Bundle()
        bundle.putInt(DAY_ARGS, day)
        bundle.putInt(MONTH_ARGS, month)
        bundle.putInt(YEAR_ARGS, year)
        timeLineFragment.arguments = bundle

        val fragmentManager = supportFragmentManager.beginTransaction()
        fragmentManager.replace(R.id.fragment_place, timeLineFragment, TIMELINE_FRAGMENT)
        fragmentManager.commit()

        configureToolbar(toolbar,
            title = getTextDate(getDateAsLong(day,month,year,0,0)),
            homeButtonNeeded = true,
            infoIconNeeded = false
        )
    }

    public override fun goToBackToMainPage(){
        super.goToBackToMainPage()

        // Remove fragments
        val fragmentManager = supportFragmentManager.beginTransaction()
        fragmentManager.remove(chronoFragment)
        fragmentManager.commit()

        // Move camera to the center of image in background
        movePicture(imageBackground, Loc.BOTTOM_LEFT.position,Loc.CENTER.position, matrix)
    }

    override fun doWhenAnimationIsFinished(toPosition: Position) {
        if(toPosition.equals(Loc.CENTER.position)){ // if picture move to center
            finishActivity()
        } else { // if picture move to bottom left corner
            configureChronoActivity()
        }
    }

    override fun onBackPressed() {
        onClickBackButtonToolbar()
    }
}

interface OnTimeLineDisplay {
    fun displayTimeLineFragment(day:Int, month:Int, year:Int)
}

const val TIMELINE_FRAGMENT = "Timeline_fragment"
const val CHRONO_FRAGMENT = "Chrono_fragment"
