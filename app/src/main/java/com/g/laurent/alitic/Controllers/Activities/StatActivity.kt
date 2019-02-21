package com.g.laurent.alitic.Controllers.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.g.laurent.alitic.Controllers.ClassControllers.getAllEventTypes
import com.g.laurent.alitic.Views.StatAdapter
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import com.g.laurent.alitic.Controllers.ClassControllers.getBarChartDataForDetailedAnalysis
import com.g.laurent.alitic.Controllers.Fragments.StatFragment
import com.g.laurent.alitic.R
import kotlinx.android.synthetic.main.activity_stat.*


class StatActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener {

    lateinit var statType:StatType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stat)

        clearDatabase(applicationContext)

        statType =StatType.GLOBAL_ANALYSIS_NEG

        // Launch global analysis (for events) at creation of activity
        launchGlobalAnalysis()
    }

    /** --------------------------------- FRAGMENT -------------------------------------- **/

    private fun launchGlobalAnalysis(){

        // Show StatFragment
        val statFragment = StatFragment().newInstance(-1, statType)
        val fragmentManager = supportFragmentManager.beginTransaction()
        fragmentManager.replace(R.id.fragment_place, statFragment, TAG_GLOBAL_ANALYSIS_FRAGMENT)
        fragmentManager.commit()

        // Hide viewpager
        stat_viewpager.visibility = View.GONE
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
            stat_viewpager.adapter = StatAdapter(supportFragmentManager, listStats)
        }
    }

    private fun displayInformations(){
        // TODO : to implement
    }

    /** --------------------------------- POP MENU -------------------------------------- **/

    override fun onMenuItemClick(item: MenuItem): Boolean {

        when(item.itemId){

            StatType.GLOBAL_ANALYSIS_NEG.idMenuItem -> {
                statType = StatType.GLOBAL_ANALYSIS_NEG
                launchGlobalAnalysis()
            }
            StatType.GLOBAL_ANALYSIS_POS.idMenuItem -> {
                statType = StatType.GLOBAL_ANALYSIS_POS
                launchGlobalAnalysis()
            }
            StatType.DETAIL_ANALYSIS.idMenuItem -> {
                statType = StatType.DETAIL_ANALYSIS
                launchDetailedAnalysis()
            }
            StatType.INFORMATIONS.idMenuItem -> {
                statType = StatType.INFORMATIONS
                displayInformations()
            }
        }
        return true
    }
}

enum class StatType(val idMenuItem:Int, val title:Int){
    GLOBAL_ANALYSIS_POS(R.id.menu_global_positive, R.string.menu_global_positive),
    GLOBAL_ANALYSIS_NEG(R.id.menu_global_negative, R.string.menu_global_negative),
    DETAIL_ANALYSIS(R.id.menu_detail, R.string.menu_detail),
    INFORMATIONS(R.id.menu_info, R.string.menu_info);
}

const val TAG_GLOBAL_ANALYSIS_FRAGMENT = "global analysis fragment"


