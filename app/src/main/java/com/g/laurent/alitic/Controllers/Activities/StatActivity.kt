package com.g.laurent.alitic.Controllers.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import com.g.laurent.alitic.Views.StatAdapter
import android.view.View
import com.g.laurent.alitic.R
import kotlinx.android.synthetic.main.activity_stat.*

class StatActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stat)

        clearDatabase(applicationContext)
        configureTabLayout()
    }

    /** ---------------------------------- TABS ---------------------------------------- **/

    private fun configureTabLayout(){

        /*tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                // Change fragment
                if(tab.tag!=null) {
                    when (tab.tag) {
                        R.string.menu_global_negative -> {
                            launchGlobalAnalysis(StatType.GLOBAL_ANALYSIS_NEG)
                        }
                        R.string.menu_global_positive -> {
                            launchGlobalAnalysis(StatType.GLOBAL_ANALYSIS_POS)
                        }
                        R.string.menu_detail -> {
                            launchDetailedAnalysis()
                        }
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })*/

        val tab0 = tabs.getTabAt(0)
        val tab1 = tabs.getTabAt(1)
        val tab2 = tabs.getTabAt(2)

        if(tab0!=null && tab1!=null && tab2!=null){
            tab0.icon = ContextCompat.getDrawable(applicationContext, R.drawable.baseline_cloud)
            tab1.icon = ContextCompat.getDrawable(applicationContext, R.drawable.baseline_wb_sunny_white_24)
            tab2.icon = ContextCompat.getDrawable(applicationContext, R.drawable.baseline_zoom)
        }

        stat_viewpager.visibility = View.VISIBLE


        tabs.tabMode = TabLayout.MODE_FIXED // Tabs have the same width
        stat_viewpager.adapter = StatAdapter(supportFragmentManager)
        tabs.setupWithViewPager(stat_viewpager)
    }

    /** --------------------------------- FRAGMENT -------------------------------------- **/

    /*private fun launchGlobalAnalysis(statType:StatType){

        // Show StatFragment
        val statGlobalFragment = StatGlobalFragment().newInstance(statType)
        val fragmentManager = supportFragmentManager.beginTransaction()
        fragmentManager.replace(R.id.fragment_place, statGlobalFragment, TAG_GLOBAL_ANALYSIS_FRAGMENT)
        fragmentManager.commit()
    }

    private fun launchDetailedAnalysis(){

        // Remove stat fragment (global analysis)
        val fragmentManager = supportFragmentManager.beginTransaction()
        val statFragment = supportFragmentManager.findFragmentByTag(TAG_GLOBAL_ANALYSIS_FRAGMENT)
        if(statFragment!=null)
            fragmentManager.remove(statFragment)
        fragmentManager.commit()

        // Configure viewpager
        val listEventTypes = getAllEventTypes(context = applicationContext)
        val listStats :MutableList<Long> = mutableListOf()

        if(listEventTypes!=null){
            for(eventType in listEventTypes){

                val list = getBarChartDataForDetailedAnalysis(eventType, context = applicationContext)

                if(list.isNotEmpty()){
                    val id = eventType.id
                    if(id!=null)
                        listStats.add(id)
                }
            }
            stat_viewpager.visibility = View.VISIBLE
            stat_viewpager.adapter = StatAdapter(supportFragmentManager)
        }
    }*/

    private fun displayInformations(){
        // TODO : to implement
    }
}

enum class StatType(val idMenuItem:Int, val title:Int){
    GLOBAL_ANALYSIS_POS(R.id.menu_global_positive, R.string.menu_global_positive),
    GLOBAL_ANALYSIS_NEG(R.id.menu_global_negative, R.string.menu_global_negative),
    DETAIL_ANALYSIS(R.id.menu_detail, R.string.menu_detail),
    INFORMATIONS(R.id.menu_info, R.string.menu_info);
}

const val TAG_GLOBAL_ANALYSIS_FRAGMENT = "global analysis fragment"


