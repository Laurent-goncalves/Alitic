package com.g.laurent.alitic.Controllers.ClassControllers

import android.content.Context
import com.g.laurent.alitic.*
import com.g.laurent.alitic.Controllers.Activities.StatType
import com.g.laurent.alitic.Models.*
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import org.apache.commons.math3.stat.regression.SimpleRegression
import java.text.DecimalFormat
import java.util.*
import kotlin.math.absoluteValue

class FoodStatEntry(val food:Food, val ratio:Float, val counter:Counter)

class Counter(var countOK:Int, var countNOK:Int)

enum class Evolution(val icon:Int, val colorId:Int, val background:Int){
    NEGATIVE(R.drawable.baseline_thumb_down_white_24, android.R.color.holo_red_dark, R.drawable.background_spinner_stat_red),
    NEUTRAL(R.drawable.baseline_thumbs_up_down_white_24, android.R.color.holo_blue_dark, R.drawable.background_spinner_stat_blue),
    POSITIVE(R.drawable.baseline_thumb_up_white_24, android.R.color.holo_green_dark, R.drawable.background_spinner_stat_green),
    UNDEFINED(R.drawable.baseline_help_white_24, android.R.color.darker_gray, R.drawable.background_spinner_stat_gray);
}

class FoodTypeStatEntry(val foodType:FoodType, val ratio:Double)

class MyYAxisValueFormatter : IValueFormatter {

    private val mFormat = DecimalFormat("###,###,##0")

    override fun getFormattedValue(
        value: Float,
        entry: Entry?,
        dataSetIndex: Int,
        viewPortHandler: ViewPortHandler?
    ): String {
        return if(value!=0f)
            mFormat.format(value)
        else
            ""
    }
}

/** ----------------------------------------------------------------------------------------------------------
 * --------------------------------------- FOOD STAT ---------------------------------------------------------
   ----------------------------------------------------------------------------------------------------------*/

fun getFoodStat(statType:StatType, eventType:EventType, mode:Boolean = false, context:Context):List<FoodStatEntry>{

    fun getListOf10Food(statType:StatType, eventType:EventType, mode:Boolean = false, context:Context):List<FoodStatEntry>{

        fun getTop10(list:MutableList<FoodStatEntry>):List<FoodStatEntry>{
            return list.sortedWith(compareBy({it.ratio}, {it.counter.countNOK})).reversed()
        }

        fun getListStatEntryWithRatio(statType:StatType, hashmap:HashMap<Food, Counter>):MutableList<FoodStatEntry>{

            val result:MutableList<FoodStatEntry> = mutableListOf()

            if(statType == StatType.GLOBAL_ANALYSIS_NEG || statType == StatType.DETAIL_ANALYSIS){
                for(food in hashmap){
                    val ratio = food.value.countNOK.toFloat() / (food.value.countNOK.toFloat() + food.value.countOK.toFloat())
                    if(ratio > 0)
                        result.add(FoodStatEntry(food.key, ratio, food.value))
                }
            } else {
                for(food in hashmap){
                    val ratio = food.value.countOK.toFloat() / (food.value.countNOK.toFloat()+food.value.countOK.toFloat())
                    if(ratio > 0)
                        result.add(FoodStatEntry(food.key, ratio, food.value))
                }
            }

            return result
        }

        val listFoodWithCounters = getListFoodsWithCounters(statType, eventType, mode, context)

        // Calculate ratio for each food
        val listStatEntry = getListStatEntryWithRatio(statType, listFoodWithCounters)

        // Get and return the top 10
        return getTop10(listStatEntry)
    }

    return getListOf10Food(statType, eventType, mode, context)
}

fun getListFoodsWithCounters(statType:StatType, eventType:EventType, mode:Boolean = false, context:Context):HashMap<Food, Counter> {

    fun getListFoodWithCountersRelatedToListEvents(listEvents:List<Event>, mode:Boolean=false, context: Context):HashMap<Food, Counter>{

        fun getListFoodOccurrenceInAllMeals(listMeals: List<Meal>, mode:Boolean=false, context: Context):HashMap<Food, Int>{

            val result:HashMap<Food, Int> = hashMapOf()

            for(meal in listMeals){
                val listFood = getFoodsFromMeal(meal, mode, context)

                for(food in listFood) {
                    if(food.forAnalysis){
                        if (result.containsKey(food)) {
                            val count:Int = result[food]!!.absoluteValue
                            result[food] = count + 1
                        } else {
                            result[food] = 1
                        }
                    }
                }
            }

            return result
        }

        fun getListMealsInTimeOrder(mode:Boolean=false, context: Context):List<Meal>?{
            val list = getAllMeals(mode, context)?.toMutableList()
            list?.sortByDescending { it.dateCode }
            return list
        }

        fun findMealsWhichCausedEvent(event: Event, listMeals:List<Meal>, mode:Boolean=false, context: Context):List<Meal>{

            fun didMealCauseEvent(meal:Meal, event:Event, eventType: EventType):Boolean{

                val emin = eventType.minTime
                val emax = eventType.maxTime

                if(emin!=null && emax!=null) {
                    val min = event.dateCode - emax
                    val max = event.dateCode - emin
                    return meal.dateCode in min..max
                }
                return false
            }

            fun findLastMeal(event: Event, listMeals:List<Meal>):Meal?{
                for(i in 0 until listMeals.size){
                    if(listMeals[i].dateCode <= event.dateCode)
                        return listMeals[i]
                }
                return null
            }

            val result:MutableList<Meal> = mutableListOf()
            val eType = getEventType(event.idEventType, mode, context) ?: return result

            for(meal in listMeals){

                val forLastMeal = eType.forLastMeal

                if(forLastMeal != null && forLastMeal){ // if last meal must be taken into account
                    val lastMeal = findLastMeal(event, listMeals)
                    if(lastMeal!=null)
                        result.add(lastMeal)
                    return result

                } else { // if event related to specific period of time
                    if(didMealCauseEvent(meal, event, eType)){
                        result.add(meal)
                    }
                }
            }
            return result
        }

        // INIT
        val result:HashMap<Food, Counter> = hashMapOf()
        val listMeals = getListMealsInTimeOrder(mode, context)
        val listMealsNOK = mutableListOf<Meal>()

        // START
        if(listMeals!=null){

            // For each event, get the list of meals impacting event
            for(event in listEvents) {
                val list = findMealsWhichCausedEvent(event, listMeals, mode, context)
                if(list.isNotEmpty()){
                    for(meal in list){
                        if(listMealsNOK.find { it.id == meal.id }==null){
                            listMealsNOK.add(meal)
                        }
                    }
                }
            }

            // For each meal of the list, get the foods and calculate NOK counter
            for(meal in listMealsNOK){

                val listFood = getFoodsFromMeal(meal, mode, context)

                for(food in listFood) {
                    if(food.forAnalysis){
                        if (result.containsKey(food)) {
                            val countNOK:Int = result[food]!!.countNOK
                            result[food]!!.countNOK = countNOK + 1
                        } else {
                            result[food] = Counter(0,1)
                        }
                    }
                }
            }

            // For each food, calculate OK counter
            val listFoodsCountTot = getListFoodOccurrenceInAllMeals(listMeals, mode, context)

            for(food in listFoodsCountTot.keys) {
                if (result.containsKey(food)) {
                    val countTot:Int = listFoodsCountTot[food]!!
                    result[food]!!.countOK = countTot - result[food]!!.countNOK
                } else {
                    result[food] = Counter(1,0)
                }
            }
        }

        return result
    }

    if (statType == StatType.DETAIL_ANALYSIS) {

        // Get the list of events related to eventType
        val listEvents = if(eventType.id==null){
            getAllEvents(mode, context)
        } else {
            getListEventForOneEventType(eventType.id, mode, context)
        }

        // Get list of foods with their counters
        if(listEvents!=null)
            return getListFoodWithCountersRelatedToListEvents(listEvents, mode, context)
    } else {

        // Get the list of all events
        val listEvents = getAllEvents(mode, context)

        // Get list of foods with their counters
        if(listEvents!=null)
            return getListFoodWithCountersRelatedToListEvents(listEvents, mode, context)
    }

    return hashMapOf()
}

/** ----------------------------------------------------------------------------------------------------------
 * -------------------------------------- CHRONOLOGY EVENTS --------------------------------------------------
----------------------------------------------------------------------------------------------------------*/

fun getChronologyEvents(eventType:EventType?, mode:Boolean = false, context:Context):List<Long>{

    val result = mutableListOf<Long>()

    val listEvents = if(eventType!=null){
        getListEventForOneEventType(eventType.id, mode, context)
    } else {
        getAllEvents(mode, context)
    }

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
            val nbweeks = (gap / DAY).toFloat()

            if (nbweeks < DURATION_MIN_ANALYSIS_IN_DAYS) {
                return Evolution.UNDEFINED
            } else {
                val period: Long = (maxTime + DAY - minTime) / 10
                val regression = SimpleRegression()
                val counts = getCountsList(minTime, period)

                for (i in 0 until dates.size)
                    regression.addData(dates[i],counts[i])

                val slope = regression.slope

                return when {
                    slope > 0 -> Evolution.NEGATIVE
                    slope.equals(0) -> Evolution.NEUTRAL
                    else -> Evolution.POSITIVE
                }
            }
        }
    }

    return Evolution.UNDEFINED
}

/** ----------------------------------------------------------------------------------------------------------
 * --------------------------------------- FOODTYPE STAT -----------------------------------------------------
------------------------------------------------------------------------------------------------------------*/

fun getListFoodTypesStats(statType: StatType, eventType: EventType, mode:Boolean = false, context:Context):List<FoodTypeStatEntry>{

    val listFoodCounters = getListFoodsWithCounters(statType, eventType, mode, context)

    fun getListFoodTypes(statType: StatType):Map<FoodType, Int>{

        fun getCount(counter:Counter):Int{
        return when (statType) {
            StatType.DETAIL_ANALYSIS -> counter.countNOK
            StatType.GLOBAL_ANALYSIS_NEG -> counter.countNOK
            else -> counter.countOK
        }
    }

        val result = mutableMapOf<FoodType, Int>()

        if(listFoodCounters.isNotEmpty()){

            for(food in listFoodCounters){

                val foodType = getFoodType(food.key.idFoodType, mode, context)

                if(result.containsKey(foodType) && foodType!=null){
                    if(result[foodType]!=null)
                        result[foodType] = getCount(food.value) + result[foodType]!!
                    else
                        result[foodType] = getCount(food.value)
                } else if(foodType!=null) {
                    result[foodType] = getCount(food.value)
                }
            }
        }

        return result.toMap()
    }

    fun calculateRatioForEachFoodType(listFoodTypes:Map<FoodType, Int>):List<FoodTypeStatEntry>{

        val result = mutableListOf<FoodTypeStatEntry>()

        val sumValues = listFoodTypes.map { it.value }.sum()

        for(foodType in listFoodTypes){
            if(foodType.value > 0){
                result.add(FoodTypeStatEntry(foodType.key, foodType.value.toDouble() / sumValues))
            }
        }
        return result.toList()
    }

    return calculateRatioForEachFoodType(getListFoodTypes(statType))
}