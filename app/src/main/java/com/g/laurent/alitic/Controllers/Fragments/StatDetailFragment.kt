package com.g.laurent.alitic.Controllers.Fragments

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.g.laurent.alitic.Controllers.Activities.StatType
import com.g.laurent.alitic.Controllers.ClassControllers.*
import com.g.laurent.alitic.LIST_EVENT_TYPES
import com.g.laurent.alitic.Views.StatChronoAdapter
import com.g.laurent.alitic.createBarChartTwoColumns
import com.g.laurent.alitic.Models.EventType
import com.g.laurent.alitic.R
import com.github.mikephil.charting.charts.HorizontalBarChart
import de.hdodenhof.circleimageview.CircleImageView


class StatDetailFragment : StatFragment(), AdapterView.OnItemSelectedListener {

    private lateinit var eventType : EventType
    private lateinit var listDates:List<Long>
    private lateinit var listTitles:ArrayList<String>

    fun newInstance(listTitles:ArrayList<String>):StatDetailFragment{

        val frag = StatDetailFragment()
        val args = Bundle()

        args.putStringArrayList(LIST_EVENT_TYPES, listTitles)
        frag.arguments = args

        return frag
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_stat_detail, container, false)

        // Initialize variables and configure StatDetailFragment
        val args = arguments
        if(args != null) {
            initializeVariables(args)
            configureChoiceEventType(view)
            configureViews(view)
        }

        return view
    }

    override fun initializeVariables(args:Bundle) {
        super.initializeVariables(args)

        statType = StatType.DETAIL_ANALYSIS

        getEventTypeAndChronologyFromId(null)

        val list = args.getStringArrayList(LIST_EVENT_TYPES)
        if(list!=null)
            listTitles = list
    }

    private fun getEventTypeAndChronologyFromId(id:Long?){

        eventType = getEventType(id, context = contextFrag) ?: EventType()

        listDates = if(eventType.id!=null)
            getChronologyEvents(eventType, context = contextFrag)
        else
            getChronologyEvents(null, context = contextFrag)
    }

    /**
     *  ---------------------------------- FRAGMENT UPDATE ---------------------------------------------------
     */

    private fun updateFragment(position:Int){

        // Find eventType
        var idEventType:Long? = null
        val listEventTypes = getListEventTypesForStatDetailFragment(contextFrag)
        for(e in listEventTypes){
            if(listTitles[position].equals(e.name)){
                idEventType = e.id
                break
            }
        }

        // Update eventType and list Dates
        getEventTypeAndChronologyFromId(idEventType)

        // Configure views
        val view = this.view
        if(view !=null)
            configureViews(view)
    }

    /**
     *  ------------------------------- CONFIGURATION FRAGMENT -----------------------------------------------
     */

    private fun configureViews(view:View){

        // Configure evolution
        configureEvolution(view)

        // Configure chronology
        configureChronology(view)

        // Configure Bar chart
        configureBarChart(view)
    }

    /** SELECT EVENT TYPE **/
    private fun configureChoiceEventType(view:View) {

        val title = view.findViewById<Spinner>(R.id.select_event_type)

        // Spinner click listener
        title.onItemSelectedListener = this

        // Creating adapter for spinner
        val dataAdapter = ArrayAdapter(contextFrag, android.R.layout.simple_spinner_item, listTitles)

        // Drop down layout style
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // attaching data adapter to spinner
        title.adapter = dataAdapter
    }

    private var init : Boolean = false
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if(init) {
            updateFragment(position)
        } else
            init = true
    }
    override fun onNothingSelected(parent: AdapterView<*>?) {}

    /** EVOLUTION **/
    private fun configureEvolution(view:View){
        val evolutionView = view.findViewById<CircleImageView>(R.id.symbol_evolution)
        val evolution = getEvolution(listDates, context = contextFrag)
        evolutionView.setImageDrawable(ContextCompat.getDrawable(contextFrag, evolution.icon)) // get icon evolution
        evolutionView.background = ContextCompat.getDrawable(contextFrag, evolution.colorId) // color background
        val evolutionLayout = view.findViewById<LinearLayout>(R.id.evolution_layout)
        evolutionLayout.background = ContextCompat.getDrawable(contextFrag, evolution.colorId)
        view.findViewById<Spinner>(R.id.select_event_type).background = contextFrag.resources.getDrawable(evolution.background, null)
        val arrowSpinner = view.findViewById<ImageView>(R.id.arrow_spinner)
        DrawableCompat.setTint(arrowSpinner.drawable, ContextCompat.getColor(contextFrag, evolution.colorId))
    }

    /** CHRONOLOGY **/
    private fun configureChronology(view:View){

        fun showGraph(show:Boolean){
            view.findViewById<TextView>(R.id.not_enough_data_chrono).visibility = if(show) View.GONE else View.VISIBLE
            view.findViewById<LinearLayout>(R.id.chronology_layout).visibility = if(show) View.VISIBLE else View.GONE
            view.findViewById<View>(R.id.legend_chrono).visibility = if(show) View.VISIBLE else View.GONE
        }

        showGraph(listDates.isNotEmpty())

        if(listDates.isNotEmpty()){

            val chronology = view.findViewById<RecyclerView>(R.id.chronology)
            val mLayoutManager = LinearLayoutManager(contextFrag, RecyclerView.HORIZONTAL, false)
            chronology.layoutManager = mLayoutManager
            val adapter = StatChronoAdapter(listDates, context = contextFrag)
            chronology.adapter = adapter
        }
    }

    /** HORIZONTAL BAR CHART **/
    private fun configureBarChart(view:View){

        val listStats = getFoodStat(statType, eventType, context = contextFrag)

        fun showGraph(show:Boolean){
            view.findViewById<TextView>(R.id.not_enough_data_barchart).visibility = if(show) View.GONE else View.VISIBLE
            view.findViewById<HorizontalBarChart>(R.id.barchart_detail_per_food).visibility = if(show) View.VISIBLE else View.GONE
            view.findViewById<View>(R.id.legend_barchart).visibility = if(show) View.VISIBLE else View.GONE
        }

        if(listStats.isNotEmpty()){
            // Create bar chart 2 columns (for detailed analysis)
            showGraph(true)
            createBarChartTwoColumns(listStats, view, contextFrag)
        } else {
            showGraph(false)
        }
    }
}
