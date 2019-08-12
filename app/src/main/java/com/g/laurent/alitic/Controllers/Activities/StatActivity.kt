package com.g.laurent.alitic.Controllers.Activities

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import android.view.Menu
import com.g.laurent.alitic.Views.StatAdapter
import android.view.View
import android.widget.ProgressBar
import com.g.laurent.alitic.Controllers.ClassControllers.getListEventTypesForStatDetailFragment
import com.g.laurent.alitic.Models.EventType
import com.g.laurent.alitic.Models.Loc
import com.g.laurent.alitic.Models.Position
import com.g.laurent.alitic.R
import com.g.laurent.alitic.Views.StatInfoDialog
import kotlinx.android.synthetic.main.activity_stat.*


class StatActivity : BaseActivity(), OnEventTypeChangeListener{

    var idEventType:Long? = null
    private var listEventTypes:List<EventType> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_stat)
        super.onCreate(savedInstanceState)

        // Start progress Bar
        findViewById<ProgressBar>(R.id.progress_bar).visibility= View.VISIBLE

        listEventTypes = getListEventTypesForStatDetailFragment(applicationContext)

        movePicture(imageBackground, Loc.CENTER.position, Loc.BOTTOM_RIGHT.position, matrix)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_stats, menu)

        configureToolbar(toolbar,
            title = context.getString(R.string.title_toolbar_stat),
            homeButtonNeeded = true,
            infoIconNeeded = true)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onClickBackButtonToolbar() {
        goToBackToMainPage()
    }

    override fun onMenuItemClick() {
        val fm = supportFragmentManager
        val statInfoDialog = StatInfoDialog().newInstance()
        statInfoDialog.show(fm, "statInfoDialog")
    }

    /** ---------------------------------- TABS ---------------------------------------- **/

    private fun configureStatActivity(){

        // Stop progress Bar
        findViewById<ProgressBar>(R.id.progress_bar).visibility= View.GONE

        // Configure StatAdapter
        stat_viewpager.visibility = View.VISIBLE
        stat_viewpager.adapter = StatAdapter(supportFragmentManager, applicationContext, 0, listEventTypes)

        // Configure tab layout
        tabs.visibility = View.VISIBLE
        tabs.setupWithViewPager(stat_viewpager)
        tabs.tabMode = TabLayout.MODE_FIXED // Tabs have the same width

        val tab0 = tabs.getTabAt(0)
        val tab1 = tabs.getTabAt(1)
        val tab2 = tabs.getTabAt(2)

        if(tab0!=null && tab1!=null && tab2!=null){
            tab0.icon = ContextCompat.getDrawable(applicationContext, R.drawable.baseline_cloud)
            tab1.icon = ContextCompat.getDrawable(applicationContext, R.drawable.baseline_wb_sunny_white_24)
            tab2.icon = ContextCompat.getDrawable(applicationContext, R.drawable.baseline_zoom)
        }
    }

    override fun changeEventType(position: Int) {
        stat_viewpager.adapter = StatAdapter(supportFragmentManager, applicationContext, position, listEventTypes)
        stat_viewpager.currentItem = 2
    }

    override fun doWhenAnimationIsFinished(toPosition: Position) {
        if(toPosition.equals(Loc.CENTER.position)){ // if picture move to center
            finishActivity()
        } else { // if picture move to bottom left corner
            configureStatActivity()
        }
    }

    public override fun goToBackToMainPage(){
        super.goToBackToMainPage()

        // Hide stat_viewpager and tablayout
        tabs.visibility = View.INVISIBLE
        stat_viewpager.visibility = View.GONE
        // Move camera to the center of image in background
        movePicture(imageBackground, Loc.BOTTOM_RIGHT.position,Loc.CENTER.position, matrix)
    }
}

enum class StatType(val titleTab:Int, val titleFood:Int?, val titlePieChart:Int?){
    GLOBAL_ANALYSIS_POS(R.string.menu_global_positive, R.string.title_foods_list_positive,R.string.title_piechart_positive),
    GLOBAL_ANALYSIS_NEG(R.string.menu_global_negative, R.string.title_foods_list_negative,R.string.title_piechart_negative),
    DETAIL_ANALYSIS(R.string.menu_detail, null,null);
}

interface OnEventTypeChangeListener {
    fun changeEventType(position:Int)
}
