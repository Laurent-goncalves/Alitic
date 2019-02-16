package com.g.laurent.alitic.Controllers.Fragments

import android.content.Context
import android.graphics.Color
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
import com.g.laurent.alitic.Controllers.ClassControllers.*
import com.g.laurent.alitic.Models.EventType
import com.g.laurent.alitic.R
import com.g.laurent.alitic.Views.FoodTypeAdapter
import com.g.laurent.alitic.Views.StatChronoAdapter
import com.g.laurent.alitic.getListDayGridForGridView
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import kotlinx.android.synthetic.main.pick_meal_layout.*


class StatFragment : Fragment() {

    lateinit var contextFrag: Context

    fun newInstance(idEventType:Long?):StatFragment{

        val frag = StatFragment()

        if(idEventType!=null){
            val args = Bundle()
            args.putLong(ID_EVENTTYPE, idEventType)
            frag.arguments = args
        }
        return frag
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val result = inflater.inflate(R.layout.barchart_layout, container, false)
        val args = arguments

        if(args!=null){

            val idEventType:Long = args.getLong(ID_EVENTTYPE)
            val eventType = getEventType(idEventType, context = contextFrag)

            if(eventType!=null){
                val listDates = getDatesEvents(idEventType, context = contextFrag)

                // Configure header panel
                configureHeaderPanel(result, eventType, listDates)

                // Configure bar chart
                configureBarChart(result, eventType)

                // Configure chronology
                configureChronology(result, listDates)

                // Configure pie chart
                configurePieChart(result, eventType)
            }
        }
        return result
    }

    /** --------------------- HEADER PANEL -------------------------------------------  **/

    private fun configureHeaderPanel(result:View, eventType:EventType, listDates:List<Long>){

        val statPanel:View = result.findViewById(R.id.stat_panel_layout)
        val titleView = statPanel.findViewById<TextView>(R.id.title_eventtype)
        val imageView = statPanel.findViewById<ImageView>(R.id.image_eventtype)
        val nbEventsView = statPanel.findViewById<TextView>(R.id.value_nb_events)
        val evolutionView = statPanel.findViewById<ImageView>(R.id.evolution_view)

        // Configure title
        titleView.text = eventType.name

        // Configure image
        // TODO imageView.drawable

        // Configure nb events
        val nbEvents = getListEventForOneEventType(eventType.id, context = contextFrag)
        nbEventsView.text = nbEvents?.size.toString()

        // Configure evolution
        val evolution = getEvolution(listDates, context = contextFrag)
        evolutionView.setImageDrawable(ContextCompat.getDrawable(contextFrag, evolution.icon)) // get icon evolution
        evolutionView.background = ContextCompat.getDrawable(contextFrag, evolution.colorId) // color background
    }

    /** --------------------- BAR CHART ----------------------------------------------  **/
    private fun configureBarChart(result:View, eventType: EventType){

        val barChart: HorizontalBarChart = result.findViewById(R.id.barchart)
        val valueSet = arrayListOf<BarEntry>()

        val listStats = getListFoodForEventType(eventType, context = contextFrag)
        val listFood = mutableListOf<String>()

        // Create bar entries
        if(listStats.isNotEmpty()){
            for(i in 0 until listStats.size){
                valueSet.add(BarEntry(i.toFloat(), listStats[i].countNOK.toFloat()))
                listFood.add(listStats[i].food!!)
            }
        }

        val barDataSet = BarDataSet(valueSet, eventType.name)
        val dataSets = ArrayList<IBarDataSet>()
        dataSets.add(barDataSet)

        // hide Y-axis and gridlines
        val left = barChart.axisLeft
        left.setDrawLabels(false)
        left.setDrawGridLines(false)

        val right = barChart.axisRight
        right.setDrawLabels(false)
        right.setDrawGridLines(false)

        // custom X-axis labels
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textSize = 14f
        xAxis.granularity = 1f // restrict the minimum interval of your axis to "1"
        xAxis.valueFormatter = MyXAxisValueFormatter(listFood.toTypedArray())

        // Finalize bar chart
        val barData = BarData(barDataSet)
        barData.barWidth = 0.3f
        barChart.data = barData
        barChart.legend.isEnabled = false
        barChart.description.isEnabled = false
        barChart.setTouchEnabled(false)
        barChart.invalidate()

    }

    /** --------------------- CHRONOLOGY -------------------------------------------  **/
    private fun configureChronology(result:View, listDates:List<Long>){

        val chronology: RecyclerView = result.findViewById(R.id.chronology)
        val mLayoutManager = LinearLayoutManager(contextFrag, RecyclerView.HORIZONTAL, false)
        chronology.layoutManager = mLayoutManager
        val adapter = StatChronoAdapter(listDates, context = contextFrag)
        chronology.adapter = adapter

    }

    /** ----------------------- PIE CHART -------------------------------------------  **/
    private fun configurePieChart(result:View, eventType:EventType){

        val pieChart: PieChart = result.findViewById(R.id.piechart)

        // Create list of bar entries
        val valueSet = arrayListOf<PieEntry>()
        val listFoodTypes = getListFoodTypesCounts(eventType, context = contextFrag)

        if(listFoodTypes.isNotEmpty()){
            for(i in 0 until listFoodTypes.size)
                valueSet.add(PieEntry(listFoodTypes[i].percent.toFloat() * 100, listFoodTypes[i].foodType.name))
        }

        val dataSet = PieDataSet(valueSet, "")

        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f

        val data = PieData(dataSet)
        data.dataSet = dataSet

        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(11f)

        pieChart.setEntryLabelColor(Color.BLACK)

        pieChart.data = data
        pieChart.legend.isEnabled = false
        pieChart.isRotationEnabled = false
        pieChart.description.isEnabled = false
        pieChart.invalidate()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context!=null)
            contextFrag = context
    }

}



const val ID_EVENTTYPE = "id_eventType"