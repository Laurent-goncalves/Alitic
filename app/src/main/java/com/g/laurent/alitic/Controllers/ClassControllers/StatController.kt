package com.g.laurent.alitic.Controllers.ClassControllers

import android.content.Context
import com.g.laurent.alitic.*
import com.g.laurent.alitic.Controllers.Activities.StatType
import com.g.laurent.alitic.Models.EventType
import com.g.laurent.alitic.Models.Food
import com.g.laurent.alitic.Models.FoodType
import com.g.laurent.alitic.Models.Meal
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import org.apache.commons.math3.stat.regression.SimpleRegression

const val DAY:Long = 24*60*60*1000

class EventTable(val minTime:Long, val maxTime:Long)

class StatList(val idEventType:Long?, val listEntries:List<StatEntry>){

    // TODO : to remove
    override fun toString(): String {
        println("eee    eventType=$idEventType")
        for(stat in listEntries)
            println("eee    food=${stat.food}     countOK=${stat.countOK}        countNOK=${stat.countNOK}")
        println("eee  -----------------------------------------------------------")
        return ""
    }
}

class StatEntry(val food:String?, val idFood:Long?, val type:Long?, val countOK:Int, val countNOK:Int)

class MyXAxisValueFormatter(private val mValues: Array<String>) : IAxisValueFormatter {

    override fun getFormattedValue(value: Float, axis: AxisBase): String {
        // "value" represents the position of the label on the axis
        return mValues[value.toInt()]
    }
}

/** ----------------------------------------------------------------------------------------------------------
 * --------------------------------------- FOOD STAT ---------------------------------------------------------
   ----------------------------------------------------------------------------------------------------------*/

fun getBarChartDataForDetailedAnalysis(eventType: EventType, mode:Boolean = false, context:Context):List<StatEntry>{

    // Get the list of all meals
    val listMeals = getAllMeals(mode, context)

    // Get the list of all events corresponding to eventType and their limits of time (min and max) to take into account meals
    val eventTable = getEventTable(StatType.DETAIL_ANALYSIS, eventType, context)

    // Get the hashMap list of food from meals causing events and their occurrence
    val listFoodByOccur = getMealFittingEventTable(StatType.DETAIL_ANALYSIS, eventTable, mode, context, listMeals)

    // Sort the list in ascending order
    val listSorted = getListInAscendingOrder(listFoodByOccur)

    return getOKmealForEachFood(listSorted, listMeals) // get "countOK" for each food
}

fun getBarChartDataForGlobalAnalysis(statType:StatType, mode:Boolean = false, context: Context): List<StatEntry> {

    // Get the list of all meals
    val listMeals = getAllMeals(mode, context)

    // Get the list of all events and their limits of time (min and max) to take into account meals
    val eventTable = getEventTable(statType, null, context)

    // Get the hashMap list of food from meals causing events and their occurrence
    val listFoodByOccur = getMealFittingEventTable(statType, eventTable, mode, context, listMeals)

    // Sort the list in ascending order
    return getListInAscendingOrder(listFoodByOccur)
}

fun getEventTable(statType:StatType, eventType: EventType?, context:Context):List<EventTable> {

    val list : MutableList<EventTable> = mutableListOf()
    val listEvent = getAllEvents(context = context)

    if(listEvent!=null){
        for(e in listEvent){
            if(statType == StatType.DETAIL_ANALYSIS) {
                if (e.idEventType == eventType!!.id)
                    list.add(EventTable(
                            e.dateCode - eventType.maxTime,
                            e.dateCode - eventType.minTime))
            } else {
                val eventype = getEventType(e.idEventType, context = context)
                if(eventype!=null){
                    list.add(EventTable(
                        e.dateCode - eventype.maxTime,
                        e.dateCode - eventype.minTime))
                }
            }
        }
    }

    return list
}

fun shouldMealBeTakenIntoAccount(statType:StatType, meal:Meal, eventTable:List<EventTable>):Boolean{

    fun isMealRelatedToOneEvent():Boolean {
        for(e in eventTable){
            if(meal.dateCode in e.minTime .. e.maxTime)
                return true
        }
        return false
    }

    return when (statType) {
        StatType.DETAIL_ANALYSIS -> isMealRelatedToOneEvent()
        StatType.GLOBAL_ANALYSIS_NEG -> isMealRelatedToOneEvent()
        else -> !isMealRelatedToOneEvent()
    }
}

fun getMealFittingEventTable(statType:StatType, eventTable:List<EventTable>, mode:Boolean=false, context: Context, listMeals:List<Meal>?):HashMap<Food, Int>{

    val list:HashMap<Food, Int> = hashMapOf()

    if(listMeals!=null){
        for(meal in listMeals){
            if(shouldMealBeTakenIntoAccount(statType, meal, eventTable)){

                val listFood =
                    getFoodsFromMeal(meal, mode, context)

                for(food in listFood){
                    if(list[food] != null){
                        val count:Int = list[food]!!
                        list[food] = count + 1
                    } else
                        list[food] = 1
                }
            }
        }
    }

    return list
}

fun getOKmealForEachFood(list:List<StatEntry>, listMeals:List<Meal>?):List<StatEntry>{

    fun getNumberFoodForAllMeal(idFood:Long?, listMeals:List<Meal>):Int{
        return if(listMeals.isNotEmpty()){

            var count = 0

            for(meal in listMeals){
                if(meal.listMealItems!=null){
                    for(mealItem in meal.listMealItems!!){
                        if(mealItem.idFood == idFood)
                            count++
                    }
                }
            }
            count
        } else
            0
    }

    val result = mutableListOf<StatEntry>()

    if(list.isNotEmpty() && listMeals!=null){
        for(i in 0 until list.size){
            val countTotal = getNumberFoodForAllMeal(list[i].idFood, listMeals)
            result.add(StatEntry(list[i].food, list[i].idFood, list[i].type, countTotal - list[i].countNOK, list[i].countNOK))
        }
    }

    return result.toList()
}

fun getListInAscendingOrder(list:HashMap<Food, Int>):List<StatEntry>{

    val result : MutableList<StatEntry> = mutableListOf()

    if(list.size != 0){
        for (i in 0 until list.size) {
            val min = list.minBy { it.value }
            if(min!=null){
                result.add(
                    StatEntry(
                        min.key.name,
                        min.key.id,
                        min.key.idFoodType,
                        min.value, min.value
                    )
                )
                list.remove(min.key)
            }
        }
    }

    return result.toList()
}

/** ----------------------------------------------------------------------------------------------------------
 * --------------------------------------- DATES EVENTS ------------------------------------------------------
----------------------------------------------------------------------------------------------------------*/

fun getDatesEvents(idEventType: Long?, mode:Boolean = false, context:Context):List<Long>{

    val listEvents = getListEventForOneEventType(idEventType, mode, context)

    val result = mutableListOf<Long>()

    if(listEvents!=null){
        if(listEvents.isNotEmpty()){
            for(event in listEvents){
                result.add(event.dateCode)
            }
        }
    }

    return result.toList()
}

fun getDatesAllEvents(mode:Boolean = false, context:Context):List<Long>{

    val listEvents = getAllEvents(mode, context)

    val result = mutableListOf<Long>()

    if(listEvents!=null){
        if(listEvents.isNotEmpty()){
            for(event in listEvents){
                result.add(event.dateCode)
            }
        }
    }

    return result.toList()
}

/** ----------------------------------------------------------------------------------------------------------
 * ------------------------------------ EVOLUTION CALCUL -----------------------------------------------------
----------------------------------------------------------------------------------------------------------*/

enum class Evolution(val status:Int?, val icon:Int, val colorId:Int){
    NEGATIVE(-1, R.drawable.baseline_thumb_down_white_24, android.R.color.holo_red_dark),
    NEUTRAL(0, R.drawable.baseline_thumbs_up_down_white_24, android.R.color.holo_blue_dark),
    POSITIVE(1, R.drawable.baseline_thumb_up_white_24, android.R.color.holo_green_dark),
    UNDEFINED(null,R.drawable.baseline_help_white_24, android.R.color.darker_gray);
}

fun getEvolution(listDates:List<Long>, mode:Boolean = false, context:Context):Evolution{

    var count = 0

    fun getCountsList(minTime:Long, period:Long):DoubleArray{

        fun getIndexDate(dateCode:Long, minTime:Long, period:Long):Int?{
            for(i in 0 .. 9){
                if(dateCode >= (minTime + (i)*period) && dateCode < (minTime + (i+1)*period)) {
                    return i
                }
            }
            return null
        }

        val result = mutableListOf(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0)

        for(stat in listDates){
            val index = getIndexDate(stat, minTime, period)
            count++
            if(index!=null )
                result[index] = result[index] + 1
        }

        return result.toDoubleArray()
    }

    if(listDates.isNotEmpty()){

        val minTime = getMinTime(getOldestEvent(mode, context), getOldestMeal(mode, context))
        val maxTime = getMaxTime(getLatestEvent(mode, context), getLatestMeal(mode, context))
        val dates = listOf(0.0,1.0,2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0).toDoubleArray()

        if(minTime==null || maxTime==null){
            return Evolution.UNDEFINED // No value available
        } else {
            val gap = maxTime - minTime
            val nbweeks = (gap / (7 * DAY)).toFloat()

            if (nbweeks < 3) {
                return Evolution.UNDEFINED
            } else {
                val period: Long = (maxTime + DAY - minTime) / 10
                val regression = SimpleRegression()
                val counts = getCountsList(minTime, period)

                for (i in 0 until dates.size)
                    regression.addData(dates[i],counts[i])

                val slope = regression.slope

                when {
                    slope > 0 -> return Evolution.NEGATIVE
                    slope.equals(0) -> return Evolution.NEUTRAL
                    else -> return Evolution.POSITIVE
                }
            }
        }
    }

    return Evolution.UNDEFINED
}

/** ----------------------------------------------------------------------------------------------------------
 * --------------------------------------- FOODTYPE STAT -----------------------------------------------------
------------------------------------------------------------------------------------------------------------*/

class FoodTypeStat(val foodType:FoodType, val percent:Double)

fun getListFoodTypesCounts(statType: StatType, eventType: EventType, mode:Boolean = false, context:Context):List<FoodTypeStat>{

    val listFoodTypes = mutableListOf<FoodTypeStat>()

    // Get the hashMap list of food from meals causing events and their occurrence
    val listFoodByOccur = if(statType==StatType.DETAIL_ANALYSIS)
        getBarChartDataForDetailedAnalysis(eventType, mode, context)
    else
        getBarChartDataForGlobalAnalysis(statType, mode, context)

    fun getListFoodTypesStat(statType: StatType):Map<FoodType, Double>{

        fun getCount(statEntry:StatEntry):Double{
            return if(statType == StatType.DETAIL_ANALYSIS)
                statEntry.countNOK.toDouble()
            else if(statType == StatType.GLOBAL_ANALYSIS_NEG)
                statEntry.countNOK.toDouble()
            else
                statEntry.countOK.toDouble()
        }

        val result = mutableMapOf<FoodType, Double>()

        if(listFoodByOccur.isNotEmpty()){

            for(food in listFoodByOccur){

                val foodItem = getFood(food.idFood, mode, context)
                val foodType = getFoodType(foodItem?.idFoodType, mode, context)

                if(result.containsKey(foodType) && foodType!=null){
                    if(result[foodType]!=null)
                        result[foodType] = getCount(food) + result[foodType]!!
                    else
                        result[foodType] = getCount(food)
                } else if(foodType!=null) {
                    result[foodType] = getCount(food)
                }
            }
        }

        return result.toMap()
    }

    val mapFoodTypes = getListFoodTypesStat(statType)
    val sumValues = mapFoodTypes.map { it.value }.sum()

    for(foodtype in mapFoodTypes){
        listFoodTypes.add(FoodTypeStat(foodtype.key, foodtype.value / sumValues))
    }

    return listFoodTypes
}
