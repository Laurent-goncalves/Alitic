package com.g.laurent.alitic.Controllers.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.view.Menu
import com.g.laurent.alitic.Views.StatAdapter
import android.view.View
import com.g.laurent.alitic.Controllers.ClassControllers.getListEventTypesForStatDetailFragment
import com.g.laurent.alitic.Controllers.Fragments.StatDetailFragment
import com.g.laurent.alitic.Models.EventType
import com.g.laurent.alitic.R
import kotlinx.android.synthetic.main.activity_stat.*

class StatActivity : AppCompatActivity(), OnEventTypeChangeListener{

    var idEventType:Long? = null
    private var listEventTypes:List<EventType> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stat)

        clearDatabase(applicationContext)

        val toolbar: Toolbar = findViewById(R.id.activity_stat_toolbar)
        setSupportActionBar(toolbar)

        listEventTypes = getListEventTypesForStatDetailFragment(applicationContext)
        configureTabLayout()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_stats, menu)
        val toolbar: Toolbar = findViewById(R.id.activity_stat_toolbar)
        configureToolbar(toolbar, this, applicationContext)
        return super.onCreateOptionsMenu(menu)
    }

    /** ---------------------------------- TABS ---------------------------------------- **/

    private fun configureTabLayout(){

        stat_viewpager.visibility = View.VISIBLE
        stat_viewpager.adapter = StatAdapter(supportFragmentManager, applicationContext, listEventTypes[0], getTitlesFromListEventTypes(listEventTypes))

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

        tab2?.select() // TODO : to delete
    }

    override fun changeEventType(position: Int) {

        stat_viewpager.adapter = StatAdapter(supportFragmentManager, applicationContext, listEventTypes[position], getTitlesFromListEventTypes(listEventTypes))

        stat_viewpager.currentItem = 2
    }

    fun displayInformations(){
        // TODO : to implement
    }


    fun getTitlesFromListEventTypes(list:List<EventType>):ArrayList<String>{

        val result = arrayListOf<String>()

        if(list.isNotEmpty()){
            for(e in list){
                val title = e.name
                if(title!=null){
                    result.add(title)
                }
            }
        }
        return result
    }
}

enum class StatType(val idMenuItem:Int, val titleTab:Int, val titleFood:Int?, val titlePieChart:Int?){
    GLOBAL_ANALYSIS_POS(R.id.menu_global_positive, R.string.menu_global_positive, R.string.title_foods_list_positive,R.string.title_piechart_positive),
    GLOBAL_ANALYSIS_NEG(R.id.menu_global_negative, R.string.menu_global_negative, R.string.title_foods_list_negative,R.string.title_piechart_negative),
    DETAIL_ANALYSIS(R.id.menu_detail, R.string.menu_detail, null,null),
    INFORMATIONS(R.id.menu_info, R.string.menu_info, null,null);
}

interface OnEventTypeChangeListener {
    fun changeEventType(position:Int)
}
