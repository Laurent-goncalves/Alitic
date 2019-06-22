package com.g.laurent.alitic

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.util.Half.toFloat
import android.view.View
import android.widget.TextView
import com.g.laurent.alitic.Controllers.Activities.StatType
import com.g.laurent.alitic.Controllers.ClassControllers.*
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet

fun createPieChart(listFoodTypes:List<FoodTypeStatEntry>, view: View, context:Context){

    val pieChart: PieChart = view.findViewById(R.id.piechart)

    // Create list of bar entries
    val valueSet = arrayListOf<PieEntry>()

    val listColors = mutableListOf<Int>()

    if(listFoodTypes.isNotEmpty()){
        for(i in 0 until listFoodTypes.size) {
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
    data.setValueTextSize(11f)

    pieChart.setEntryLabelColor(Color.BLACK)
    pieChart.setEntryLabelTextSize(14f)

    pieChart.data = data
    pieChart.legend.isEnabled = false
    pieChart.isRotationEnabled = false
    pieChart.description.isEnabled = false
    pieChart.invalidate()
}

fun configureBigPieChart(listFoodTypes:List<FoodTypeStatEntry>, view: View, context:Context){

    val pieChart: PieChart = view.findViewById(R.id.global_pie_chart)

    // Create list of bar entries
    val valueSet = arrayListOf<PieEntry>()

    val listColors = mutableListOf<Int>()

    if(listFoodTypes.isNotEmpty()){
        for(i in 0 until listFoodTypes.size) {
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
    data.setValueTextSize(11f)

    pieChart.setEntryLabelColor(Color.BLACK)
    pieChart.setEntryLabelTextSize(14f)

    //pieChart.setHoleColor(android.R.color.transparent)

    pieChart.data = data
    pieChart.legend.isEnabled = false
    pieChart.isRotationEnabled = false
    pieChart.description.isEnabled = false
    pieChart.invalidate()
}

fun configureSmallPieChart(statEntry: FoodStatEntry, statType: StatType, view: View, context:Context){

    val pieChart: PieChart = view.findViewById(R.id.piechart_around_circle)

    // Create list of bar entries
    val valueSet = listOf(PieEntry(1 - statEntry.ratio, ""), PieEntry(statEntry.ratio, ""))

    val listColors = if(statType == StatType.GLOBAL_ANALYSIS_NEG){
        listOf(android.R.color.transparent, android.R.color.holo_red_dark)
    } else {
        listOf(android.R.color.transparent, android.R.color.holo_green_dark)
    }

    val dataSet = PieDataSet(valueSet, null)

    dataSet.setColors(listColors.toIntArray(), context)

    val data = PieData(dataSet)
    data.dataSet = dataSet

    pieChart.data = data
    pieChart.legend.isEnabled = false
    pieChart.isRotationEnabled = false
    pieChart.description.isEnabled = false
    pieChart.invalidate()
}

fun getListUsedViewIndex(size:Int):List<Int>{
    return when(size){
        1 -> listOf(1)
        2 -> listOf(1,2)
        3 -> listOf(1,2,3)
        4 -> listOf(1,2,4,5)
        5 -> listOf(1,2,3,4,5)
        6 -> listOf(1,2,3,4,5,6)
        7 -> listOf(1,2,4,5,6,8,9)
        8 -> listOf(1,2,3,4,5,6,8,9)
        9 -> listOf(1,2,3,4,5,6,8,9,10)
        else -> listOf(1,2,3,4,5,6,7,8,9,10)
    }
}

fun getListUnusedViewIndex(size:Int):List<Int>{
    return when(size){
        1 -> listOf(2,3,4,5,6,7,8,9,10)
        2 -> listOf(3,4,5,6,7,8,9,10)
        3 -> listOf(4,5,6,7,8,9,10)
        4 -> listOf(3,6,7,8,9,10)
        5 -> listOf(6,7,8,9,10)
        6 -> listOf(7,8,9,10)
        7 -> listOf(3,7,10)
        8 -> listOf(7,10)
        9 -> listOf(7)
        else -> listOf()
    }
}

fun createBarChartTwoColumns(listStats:List<FoodStatEntry>, view: View, context:Context){



    val barChart:HorizontalBarChart = view.findViewById(R.id.barchart_detail_per_food)

    fun configureLegend(){
        barChart.legend.isEnabled = true
        val legend = barChart.legend
        legend.textSize = 14f
        val legendEntry1 = LegendEntry("Food nok", Legend.LegendForm.SQUARE, 10f, 2f, null, R.color.colorFoodNOK)
        val legendEntry2 = LegendEntry("Food OK", Legend.LegendForm.SQUARE, 10f, 2f, null, R.color.colorFoodOK)
        legend.setCustom(mutableListOf(legendEntry1, legendEntry2))
    }




    // Initialization
    val valueSet = arrayListOf<BarEntry>()
    val listFood = mutableListOf<String>()
    val listColors = mutableListOf<Int>()
    val mEntries = arrayListOf<Float>()

    // Create bar entries
    if(listStats.isNotEmpty()){

        barChart.visibility = View.VISIBLE
        view.findViewById<TextView>(R.id.title_detail_per_food).visibility = View.VISIBLE // show title graph
        view.findViewById<View>(R.id.line_separator_bottom).visibility = View.VISIBLE // show line separator

        val mEntriesCount = if(listStats.size >= 10) 9 else listStats.size - 1

        for(i in 0 .. mEntriesCount){
            valueSet.add(BarEntry(i.toFloat(), floatArrayOf(listStats[i].counter.countNOK.toFloat(), listStats[i].counter.countOK.toFloat())))
            listColors.add(R.color.colorFoodNOK)
            listColors.add(R.color.colorFoodOK)
            listFood.add(listStats[i].food.name!!)
            mEntries.add(i.toFloat())
        }

        // custom X-axis labels
        val xAxis = barChart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textSize = 14f
        xAxis.axisMinimum = -0.5f
        xAxis.axisMaximum = mEntriesCount.toFloat() + 0.5f
        xAxis.granularity = 1f
        xAxis.valueFormatter = MyXAxisValueFormatter(listFood.toTypedArray())

        // Data
        val barDataSet = BarDataSet(valueSet, null)
        barDataSet.setDrawValues(true)
        barDataSet.setColors(listColors.toIntArray(), context) // Set colors for each bar
        barDataSet.valueTextSize = 14f
        barDataSet.valueFormatter = MyYAxisValueFormatter()

        // hide Y-axis and gridlines
        val left = barChart.axisLeft
        left.setDrawLabels(false)
        left.setDrawGridLines(false)

        val right = barChart.axisRight
        right.setDrawLabels(false)
        right.setDrawGridLines(false)

        // Finalize bar chart
        val barData = BarData(barDataSet)
        barData.barWidth = 0.9f
        barChart.data = barData
        barChart.setFitBars(true)
        barChart.setDrawValueAboveBar(false)
        configureLegend()

        barChart.description.isEnabled = false
        barChart.setTouchEnabled(false)

        barChart.invalidate()

    } else {
        barChart.visibility = View.GONE // hide barchart
        view.findViewById<TextView>(R.id.title_detail_per_food).visibility = View.GONE // hide title graph
        view.findViewById<View>(R.id.line_separator_bottom).visibility = View.GONE // hide line separator
    }
}



/*
fun createBarChartOneColumn(listStats:List<StatEntry>, statType:StatType, view: View, context:Context){

    // Initialization
    val barChart: HorizontalBarChart = view.findViewById(R.id.barchart)
    val valueSet = arrayListOf<BarEntry>()
    val listFood = mutableListOf<String>()
    val listColors = mutableListOf<Int>()

    // Create bar entries
    if(listStats.isNotEmpty()){

        val limit = if(listStats.size >= 8) 8 else listStats.size

        for(i in 0 until limit){

            listFood.add(listStats[i].food!!)

            if(statType == StatType.GLOBAL_ANALYSIS_NEG) {
                valueSet.add(BarEntry(i.toFloat(), listStats[i].countNOK.toFloat()))
                listColors.add(R.color.colorFoodNOK)
            } else {
                valueSet.add(BarEntry(i.toFloat(), listStats[i].countOK.toFloat()))
                listColors.add(R.color.colorFoodOK)
            }
        }
    }

    val barDataSet = BarDataSet(valueSet, null)
    val dataSets = ArrayList<IBarDataSet>()
    dataSets.add(barDataSet)
    barDataSet.setDrawValues(true)

    // Set colors for each bar
    barDataSet.setColors(listColors.toIntArray(), context)

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
    xAxis.setDrawGridLines(false)

    // Finalize bar chart
    val barData = BarData(barDataSet)
    barChart.data = barData
    barChart.legend.isEnabled = false
    barChart.description.isEnabled = false
    barChart.setTouchEnabled(false)
    barChart.invalidate()
}


*/