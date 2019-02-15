package com.g.laurent.alitic.Controllers.ClassControllers

import android.content.Context
import com.g.laurent.alitic.*
import com.g.laurent.alitic.Models.EventType
import com.g.laurent.alitic.Models.Food
import com.g.laurent.alitic.Models.FoodType
import com.g.laurent.alitic.Models.Meal
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation
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

fun getListFoodForEventType(eventType: EventType, mode:Boolean = false, context:Context):List<StatEntry>{

    // Get the list of all meals
    val listMeals = getAllMeals(mode, context)

    // Get the list of all events corresponding to eventType and their limits of time (min and max) to take into account meals
    val eventTable = getEventTable(eventType, context)

    // Get the hashMap list of food from meals causing events and their occurrence
    val listFoodByOccur = getMealFittingEventTable(eventTable, mode, context, listMeals)

    // Sort the list in descending order
    val listSorted = getListInDescendingOrder(listFoodByOccur)

    return getOKmealForEachFood(listSorted, listMeals)
}

fun getEventTable(eventType: EventType, context:Context):List<EventTable> {

    val list : MutableList<EventTable> = mutableListOf()
    val listEvent = getAllEvents(context = context)

    if(listEvent!=null){
        for(e in listEvent){
            if(e.idEventType == eventType.id)
                list.add(
                    EventTable(
                        e.dateCode - eventType.maxTime,
                        e.dateCode - eventType.minTime
                    )
                )
        }
    }

    return list
}

fun shouldMealBeTakenIntoAccount(meal:Meal, eventTable:List<EventTable>):Boolean{
    for(e in eventTable){
        if(meal.dateCode in e.minTime .. e.maxTime)
            return true
    }
    return false
}

fun getMealFittingEventTable(eventTable:List<EventTable>, mode:Boolean=false, context: Context, listMeals:List<Meal>?):HashMap<Food, Int>{

    val list:HashMap<Food, Int> = hashMapOf()

    if(listMeals!=null){
        for(meal in listMeals){
            if(shouldMealBeTakenIntoAccount(meal, eventTable)){

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

fun getListInDescendingOrder(list:HashMap<Food, Int>):List<StatEntry>{

    val result : MutableList<StatEntry> = mutableListOf()

    if(list.size != 0){
        for (i in 0 until list.size) {
            val max = list.maxBy { it.value }
            if(max!=null){
                result.add(
                    StatEntry(
                        max.key.name,
                        max.key.id,
                        max.key.idFoodType,
                        0, max.value
                    )
                )
                list.remove(max.key)
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

/** ----------------------------------------------------------------------------------------------------------
 * ------------------------------------ EVOLUTION CALCUL -----------------------------------------------------
----------------------------------------------------------------------------------------------------------*/

enum class Evolution(val status:Int?, val icon:Int){
    NEGATIVE(-1, R.drawable.baseline_thumb_down_white_24),
    NEUTRAL(0, R.drawable.baseline_thumbs_up_down_white_24),
    POSITIVE(1, R.drawable.baseline_thumb_up_white_24),
    UNDEFINED(null,R.drawable.baseline_help_white_24);
}

fun getEvolution(listDates:List<Long>, mode:Boolean = false, context:Context):Evolution{

    var count:Int = 0

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

                val coeff = PearsonsCorrelation().correlation(dates,counts)

                return if (coeff > 0f) { // coefficient must be above 0.5 to have a good correlation

                    val slope = regression.slope

                    when {
                        slope > 0 -> Evolution.NEGATIVE
                        slope.equals(0) -> Evolution.NEUTRAL
                        else -> Evolution.POSITIVE
                    }

                } else {
                    Evolution.UNDEFINED
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

fun getListFoodTypesCounts(eventType: EventType, mode:Boolean = false, context:Context):List<FoodTypeStat>{

    val listFoodTypes = mutableListOf<FoodTypeStat>()

    // Get the list of all meals
    val listMeals = getAllMeals(mode, context)

    // Get the list of all events corresponding to eventType and their limits of time (min and max) to take into account meals
    val eventTable = getEventTable(eventType, context)

    // Get the hashMap list of food from meals causing events and their occurrence
    val listFoodByOccur = getMealFittingEventTable(eventTable, mode, context, listMeals)

    fun getListFoodTypesStat():Map<FoodType, Double>{

        val result = mutableMapOf<FoodType, Double>()

        if(listFoodByOccur.size > 0){

            for(food in listFoodByOccur){

                val foodType = getFoodType(food.key.idFoodType, context = context)

                if(result.containsKey(foodType) && foodType!=null){
                    if(result[foodType]!=null)
                        result[foodType] = food.value.toDouble() + result[foodType]!!
                    else
                        result[foodType] = food.value.toDouble()
                } else if(foodType!=null) {
                    result[foodType] = food.value.toDouble()
                }
            }
        }

        return result.toMap()
    }

    val mapFoodTypes = getListFoodTypesStat()
    val sumValues = mapFoodTypes.map { it.value }.sum()

    for(foodtype in mapFoodTypes){
        listFoodTypes.add(FoodTypeStat(foodtype.key, foodtype.value / sumValues))
    }

    return listFoodTypes
}


/*fun getChronoStatEventType(idEventType: Long?, perMonth:Boolean, mode:Boolean = false, context:Context):List<ChronoStat>{

    val listEvents = getListEventForOneEventType(idEventType, mode, context)

    fun getListEventsByMonthOrWeek():HashMap<Long, Int>{

        val hashmap = hashMapOf<Long, Int>()
        if(listEvents!=null){
            if(listEvents.isNotEmpty()){
                for(event in listEvents){

                    val key = if(perMonth){ // data per month
                        getFirstDayMonth(event.dateCode)
                    } else { // data per week
                        getFirstDayWeek(event.dateCode)
                    }

                    if(hashmap.containsKey(key))
                        hashmap[key] = hashmap[key]!! + 1
                    else
                        hashmap[key] =  1
                }
            }
        }

        return hashmap
    }

    fun createListChronoStat(hashmap:HashMap<Long, Int>):List<ChronoStat>{

        val result = mutableListOf<ChronoStat>()

        if(hashmap.isNotEmpty()){

            val minTime:Long? = hashmap.minBy { it.key }?.key
            val maxTime:Long? = hashmap.maxBy { it.key }?.key

            if(perMonth && minTime!=null && maxTime!=null){ // PER MONTH

                var count:Long = minTime

                while(count<=maxTime){
                    if(hashmap.containsKey(count))
                        result.add(ChronoStat(idEventType, count, hashmap[count]!!))
                    else
                        result.add(ChronoStat(idEventType, count, 0))

                    count = getFirstDayMonth(count + 33L * 24L * 60L * 60L * 1000L)
                }
            } else if(!perMonth && minTime!=null && maxTime!=null){ // PER WEEK

                var count:Long = minTime

                while(count<=maxTime){
                    if(hashmap.containsKey(count))
                        result.add(ChronoStat(idEventType, count, hashmap[count]!!))
                    else
                        result.add(ChronoStat(idEventType, count, 0))

                    count = getFirstDayWeek(count + 7L * 24L * 60L * 60L * 1000L)
                }
            }
        }

        return result.toList()
    }

    val listEventsByMonthOrWeek = getListEventsByMonthOrWeek()

    return createListChronoStat(listEventsByMonthOrWeek)
}*/
