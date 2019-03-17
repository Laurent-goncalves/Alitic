package com.g.laurent.alitic.Controllers.Fragments

import android.content.Context
import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.g.laurent.alitic.*
import com.g.laurent.alitic.Controllers.Activities.StatType
import com.g.laurent.alitic.Controllers.ClassControllers.*
import com.g.laurent.alitic.Models.EventType
import com.g.laurent.alitic.Views.StatChronoAdapter

class StatFragment : Fragment() {

    lateinit var contextFrag: Context
    lateinit var statType:StatType
    lateinit var listDates:List<Long>
    private var eventType:EventType = EventType(null,null,null, 0,0, false)

    fun newInstance(idEventType:Long, statType:StatType):StatFragment{

        val frag = StatFragment()
        val args = Bundle()
        args.putLong(ID_EVENTTYPE, idEventType)
        args.putString(STAT_TYPE, statType.name)
        frag.arguments = args

        return frag
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.barchart_layout, container, false)

        val args = arguments

        fun initializeVariables() {
            val statTypeName = args!!.getString(STAT_TYPE)
            val idEventType: Long? = args.getLong(ID_EVENTTYPE)

            if(statTypeName!=null) {
                statType = StatType.valueOf(statTypeName)
            }

            if(statType == StatType.DETAIL_ANALYSIS){ // --------------- DETAIL ANALYSIS ------------------

                val e = getEventType(idEventType, context = contextFrag)
                if(e!=null)
                    eventType = e

                listDates = getChronologyEvents(eventType, context = contextFrag)

            } else { // --------------------- GLOBAL ANALYSIS ----------------------------
                listDates = getChronologyEvents(null, context = contextFrag)
            }
        }

        if(args != null){

            initializeVariables()

            // Configure big pie chart
            configurePieChart(view)
        }

        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context!=null)
            contextFrag = context
    }

    /** ----------------------- PIE CHART -------------------------------------------  **/
    private fun configurePieChart(view:View){

        //val listFoodTypes = getListFoodTypesCounts(statType, eventType, context = contextFrag)

        // Create pie chart
        //createPieChart(listFoodTypes, view, contextFrag)
    }

}

const val ID_EVENTTYPE = "id_eventType"
const val STAT_TYPE = "stat_type"

/*

    /** --------------------- HEADER PANEL -------------------------------------------  **/

    private fun configureHeaderPanel(view:View){

        val statPanel:View = view.findViewById(R.id.stat_panel_layout)
        val titleView = statPanel.findViewById<TextView>(R.id.title_eventtype)
        val imageView = statPanel.findViewById<ImageView>(R.id.image_eventtype)
        val evolutionView = statPanel.findViewById<ImageView>(R.id.evolution_view)

        if(statType==StatType.DETAIL_ANALYSIS){ // ------------------------- DETAILED ANALYSIS -------------------------
            // Configure title
            titleView.text = eventType.name

            // Configure image
            setImageResource(eventType.eventPic, imageView, contextFrag)

            // Configure evolution
            val evolution = getEvolution(listDates, context = contextFrag)
            evolutionView.setImageDrawable(ContextCompat.getDrawable(contextFrag, evolution.icon)) // get icon evolution
            evolutionView.background = ContextCompat.getDrawable(contextFrag, evolution.colorId) // color background

        } else { // ------------------------------------- GLOBAL ANALYSIS --------------------------
            // Add title
            titleView.text = contextFrag.resources.getString(statType.title)

            // hide image and evolution
            imageView.visibility = View.GONE
            evolutionView.visibility = View.GONE
        }
    }

    /** --------------------- BAR CHART ----------------------------------------------  **/
    private fun configureBarChart(view:View){

        if(statType==StatType.DETAIL_ANALYSIS) { // ------------- DETAILED ANALYSIS --------------------

            val listStats = getBarChartDataForDetailedAnalysis(eventType, context = contextFrag)

            // Create bar chart 2 columns (for detailed analysis)
            createBarChartTwoColumns(listStats, view, contextFrag)

        } else { // ------------------------------------- GLOBAL ANALYSIS --------------------------

            val listStats = getBarChartDataForGlobalAnalysis(statType, context = contextFrag)

            // Create bar chart 1 column (for global analysis)
            createBarChartOneColumn(listStats, statType, view, contextFrag)
        }
    }

    /** --------------------- CHRONOLOGY -------------------------------------------  **/
    private fun configureChronology(view:View){

        val chronology: RecyclerView = view.findViewById(R.id.chronology)

        if(statType==StatType.DETAIL_ANALYSIS){ // --------- DETAILED ANALYSIS -------------------------
            val mLayoutManager = LinearLayoutManager(contextFrag, RecyclerView.HORIZONTAL, false)
            chronology.layoutManager = mLayoutManager
            val adapter = StatChronoAdapter(listDates, context = contextFrag)
            chronology.adapter = adapter

        } else { // ------------------------------------- GLOBAL ANALYSIS --------------------------
            chronology.visibility = View.GONE
        }
    }

    /** ----------------------- PIE CHART -------------------------------------------  **/
    private fun configurePieChart(view:View){

        val listFoodTypes = getListFoodTypesCounts(statType, eventType, context = contextFrag)

        // Create pie chart
        createPieChart(listFoodTypes, view, contextFrag)
    }

 */