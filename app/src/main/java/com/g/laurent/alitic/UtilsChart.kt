package com.g.laurent.alitic

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.view.View
import com.g.laurent.alitic.Controllers.Activities.StatType
import com.g.laurent.alitic.Controllers.ClassControllers.FoodTypeStat
import com.g.laurent.alitic.Controllers.ClassControllers.MyXAxisValueFormatter
import com.g.laurent.alitic.Controllers.ClassControllers.StatEntry
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet

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

fun createBarChartTwoColumns(listStats:List<StatEntry>, view: View, context:Context){

    // Initialization
    val barChart: HorizontalBarChart = view.findViewById(R.id.barchart)
    val valueSet1 = arrayListOf<BarEntry>()
    val valueSet2 = arrayListOf<BarEntry>()
    val listFood = mutableListOf<String>()
    val listColors1 = mutableListOf<Int>()
    val listColors2 = mutableListOf<Int>()

    // Create bar entries
    if(listStats.isNotEmpty()){

        val limit = if(listStats.size >= 8) 8 else listStats.size

        for(i in 0 until limit){

            valueSet1.add(BarEntry(i.toFloat(), listStats[i].countNOK.toFloat()))
            listColors1.add(R.color.colorFoodNOK)

            valueSet2.add(BarEntry(i.toFloat(), listStats[i].countOK.toFloat()))
            listColors2.add(R.color.colorFoodOK)

            listFood.add(listStats[i].food!!)
        }
    }

    // Data for NOK count
    val barDataSet1 = BarDataSet(valueSet1, null)
    barDataSet1.setDrawValues(true)
    barDataSet1.setColors(listColors1.toIntArray(), context) // Set colors for each bar

    // Data for OK count
    val barDataSet2 = BarDataSet(valueSet2, null)
    barDataSet2.setDrawValues(true)
    barDataSet2.setColors(listColors2.toIntArray(), context) // Set colors for each bar

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
    val barData = BarData(listOf(barDataSet1, barDataSet2))
    barChart.data = barData
    barChart.legend.isEnabled = false
    barChart.description.isEnabled = false
    barChart.setTouchEnabled(false)
    barChart.invalidate()
}

fun createPieChart(listFoodTypes:List<FoodTypeStat>, view: View, context:Context){

    val pieChart: PieChart = view.findViewById(R.id.piechart)

    // Create list of bar entries
    val valueSet = arrayListOf<PieEntry>()

    val listColors = mutableListOf<Int>()

    if(listFoodTypes.isNotEmpty()){
        for(i in 0 until listFoodTypes.size) {
            valueSet.add(PieEntry(listFoodTypes[i].percent.toFloat() * 100, listFoodTypes[i].foodType.name))
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