package com.g.laurent.alitic.Controllers.ClassControllers

import android.content.Context
import com.g.laurent.alitic.Models.Event
import com.g.laurent.alitic.Models.Meal
import com.g.laurent.alitic.Models.TypeDisplay
import com.g.laurent.alitic.getDateAsLong
import com.g.laurent.alitic.getHourAsInt
import com.g.laurent.alitic.getMinutesAsInt
import com.g.laurent.alitic.getTextTime
import java.util.*


class Chrono(val hour:String, val id:Long?, val typeDisplay: TypeDisplay, val item:List<Any>)

fun getChronology(day:Int, month:Int, year:Int, mode:Boolean = false, context: Context):List<Chrono>{

    // Get the list of all events
    val listEvents = getEventsFromDate(getDateAsLong(day,month,year,0,0),mode, context)

    // Get the list of all meals
    val listMeals = getAllMealsFromDate(getDateAsLong(day,month,year,0,0),mode, context)

    // Get the list of all events
    return organizeEventsByTime(listEvents, listMeals, mode, context)
}

fun organizeEventsByTime(listEvents:List<Event>?, listMeals:List<Meal>?, mode:Boolean = false, context: Context):List<Chrono>{

    fun sortListInChronologicalOrder():MutableList<Any>{

        val result:MutableList<Any> = mutableListOf()

        if(listEvents!=null)
            result.addAll(listEvents.toMutableList())

        if(listMeals!=null)
            result.addAll(listMeals.toMutableList())

        result.sortWith(Comparator { any1, any2 ->
            var dateAny1:Long = 0
            var dateAny2:Long = 0

            when(any1){
                is Event -> { dateAny1 = any1.dateCode }
                is Meal -> { dateAny1 = any1.dateCode }
            }

            when(any2){
                is Event -> { dateAny2 = any2.dateCode }
                is Meal -> { dateAny2 = any2.dateCode }
            }

            dateAny1.compareTo(dateAny2)
        })

        return result
    }

    // Get list of meals and events in chronological order
    val list:MutableList<Any> = sortListInChronologicalOrder()

    // Create chrono for each row of list
    val result : MutableList<Chrono> = mutableListOf()

    for(any in list){

        when(any){
            is Event -> {
                result.add(Chrono(getTextTime(getHourAsInt(any.dateCode), getMinutesAsInt(any.dateCode)), any.id, TypeDisplay.EVENT, listOf(any)))
            }

            is Meal -> {
                val listFoods = getFoodsFromMeal(any, mode, context)
                result.add(Chrono(getTextTime(getHourAsInt(any.dateCode), getMinutesAsInt(any.dateCode)), any.id, TypeDisplay.MEAL, listFoods))
            }
        }
    }

    return result
}