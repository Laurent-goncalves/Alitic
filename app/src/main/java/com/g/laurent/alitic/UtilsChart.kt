package com.g.laurent.alitic

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.g.laurent.alitic.Controllers.Activities.StatType
import com.g.laurent.alitic.Controllers.ClassControllers.*
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.mikhaellopez.circularimageview.CircularImageView


fun configureBigPieChart(listFoodTypes:List<FoodTypeStatEntry>, view: View, context:Context){

    val pieChart: PieChart = view.findViewById(R.id.global_pie_chart)

    // Create list of bar entries
    val valueSet = arrayListOf<PieEntry>()

    val listColors = mutableListOf<Int>()

    if(listFoodTypes.isNotEmpty()){
        for(i in listFoodTypes.indices) {
            valueSet.add(PieEntry(listFoodTypes[i].ratio.toFloat() * 100, listFoodTypes[i].foodType.name))
            // Add foodtype color
            listColors.add(listFoodTypes[i].foodType.foodTypeColor)
        }
    }

    val dataSet = PieDataSet(valueSet, null)

    dataSet.setColors(listColors.toIntArray(), context)
    dataSet.sliceSpace = 3f
    dataSet.selectionShift = 5f

    val data = PieData(dataSet)
    data.dataSet = dataSet

    data.setValueFormatter(PercentFormatter())
    data.setValueTextSize(14f)

    pieChart.setEntryLabelColor(Color.BLACK)
    pieChart.setEntryLabelTextSize(14f)
    pieChart.setHoleColor(android.R.color.transparent)

    pieChart.data = data
    pieChart.legend.isEnabled = false
    pieChart.isRotationEnabled = false
    pieChart.description.isEnabled = false
    pieChart.setTouchEnabled(false)
    pieChart.invalidate()
}

fun configureSmallPieChart(statEntry: FoodStatEntry, statType: StatType, view: View, context:Context){

    val pieChart: PieChart = view.findViewById(R.id.piechart_around_circle)
    val foodImage = view.findViewById<ImageView>(R.id.bg_food_image)

    // Create list of bar entries
    val valueSet = listOf(PieEntry(1 - statEntry.ratio, ""), PieEntry(statEntry.ratio, ""))
    val listColors: List<Int>

    if (statType == StatType.GLOBAL_ANALYSIS_NEG) {
        listColors = listOf(android.R.color.transparent, android.R.color.holo_red_dark)
        val red = context.resources.getColor(R.color.colorSmallPieChartNok,null)
        foodImage.setColorFilter(red)
    } else {
        listColors = listOf(android.R.color.transparent, android.R.color.holo_green_dark)
        val green = context.resources.getColor(R.color.colorSmallPieChartOk,null)
        foodImage.setColorFilter(green)
    }

    val dataSet = PieDataSet(valueSet, null)
    dataSet.sliceSpace = 0f
    dataSet.setColors(listColors.toIntArray(), context)

    val data = PieData(dataSet)
    data.dataSet = dataSet
    pieChart.data = data
    pieChart.setDrawEntryLabels(false)
    pieChart.data.setDrawValues(false) // remove y-Values
    pieChart.isClickable = false
    pieChart.focusable = View.NOT_FOCUSABLE
    pieChart.setTouchEnabled(false)
    pieChart.legend.isEnabled = false
    pieChart.isRotationEnabled = false
    pieChart.description.isEnabled = false
    pieChart.invalidate()
}

fun createBarChartTwoColumns(listStats:List<FoodStatEntry>, view: View, context:Context){

    val barChart:HorizontalBarChart = view.findViewById(R.id.barchart_detail_per_food)

    // Initialization
    val valueSet = arrayListOf<BarEntry>()
    val listFood = mutableListOf<String>()
    val listColors = mutableListOf<Int>()
    val mEntriesCount = if(listStats.size >= 10) 9 else listStats.size-1

    fun configureBarChart(){

        // hide Y-axis and gridlines
        val left = barChart.axisLeft
        left.setDrawLabels(false)
        left.setDrawGridLines(false)

        val right = barChart.axisRight
        right.setDrawLabels(false)
        right.setDrawGridLines(false)

        barChart.setFitBars(true)
        barChart.setDrawValueAboveBar(false)

        barChart.legend.isEnabled = false
        barChart.description.isEnabled = false
        barChart.setTouchEnabled(false)
    }

    fun configureXAxis(){
        val xAxis = barChart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textSize = 14f
        xAxis.setCenterAxisLabels(false)
        xAxis.granularity = 1f // axis display values in multiple of 1
        xAxis.isGranularityEnabled=true
        xAxis.valueFormatter = IndexAxisValueFormatter(listFood.toTypedArray()) // show labels (foods) on xAxis
    }

    fun showBarChart(show:Boolean){

        val VIEW_VISIBILITY = if(show) View.VISIBLE else View.GONE

        barChart.visibility = VIEW_VISIBILITY // visibility barchart
        view.findViewById<TextView>(R.id.title_detail_per_food).visibility = VIEW_VISIBILITY // visibility title graph
        view.findViewById<View>(R.id.line_separator_bottom).visibility = VIEW_VISIBILITY // visibility line separator
        view.findViewById<RelativeLayout>(R.id.legend_barchart).visibility = VIEW_VISIBILITY // visibility barchart legend
    }

    // Create bar entries
    if(listStats.isNotEmpty()){

        showBarChart(true)
        configureBarChart()

        for(i in mEntriesCount downTo 0){
            valueSet.add(BarEntry(mEntriesCount - i.toFloat(), floatArrayOf(listStats[i].counter.countNOK.toFloat(), listStats[i].counter.countOK.toFloat())))
            listColors.add(R.color.colorFoodNOK)
            listColors.add(R.color.colorFoodOK)
            listFood.add(listStats[i].food.name!!)
        }

        // custom X-axis labels
        configureXAxis()

        // Data
        val barDataSet = BarDataSet(valueSet, null)
        barDataSet.setDrawValues(true)
        barDataSet.setColors(listColors.toIntArray(), context) // Set colors for each bar
        barDataSet.valueTextSize = 14f
        barDataSet.valueFormatter = MyYAxisValueFormatter()
        barChart.xAxis.labelCount = barDataSet.entryCount

        // Finalize bar chart
        val barData = BarData(barDataSet)
        barChart.data = barData
        barChart.invalidate()

    } else {
        showBarChart(false)
    }
}