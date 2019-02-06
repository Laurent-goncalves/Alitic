package com.g.laurent.alitic.Controllers.ClassControllers

import android.content.Context
import com.g.laurent.alitic.Models.Event
import com.g.laurent.alitic.Models.Meal
import com.g.laurent.alitic.getDateAsLong
import com.g.laurent.alitic.getHourAsInt
import com.g.laurent.alitic.getMinutesAsInt
import com.g.laurent.alitic.getTextTime
import java.util.*


class Chrono(val hour:String, val item:List<Any>)

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

        Collections.sort(result, object : Comparator<Any> {
            override fun compare(any1: Any, any2: Any): Int {

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

                return dateAny1.compareTo(dateAny2)
            }
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
                result.add(Chrono(getTextTime(getHourAsInt(any.dateCode), getMinutesAsInt(any.dateCode)), listOf(any)))
            }

            is Meal -> {
                val listFoods = getFoodsFromMeal(any, mode, context)
                result.add(Chrono(getTextTime(getHourAsInt(any.dateCode), getMinutesAsInt(any.dateCode)), listFoods))
            }
        }
    }

    return result
}


/*    fun findIndexChronoInList(idEventType: Long?, listChrono:List<ChronoItem>):Int?{
        for(i in 0 until listChrono.size){
            if(listChrono[i].idEventType == idEventType)
                return i
        }
        return null
    }*/
/*

// Get the list of all events to take into account
if(listEvents!=null){
    for(event in listEvents){
        if(isRightMonth(event.dateCode, month, year)){ // if this event should be taken into account
            val date = DateTime.forDateOnly(year, month, getDayOfMonth(event.dateCode))
            list.add(Chrono(event.idEventType, 0, date))
        }
    }
}

// Make a hashmap with dateTime and their events counter
if(list.size>0){
    for (i in 0 until list.size) {

        val listChrono : MutableList<Chrono>? = result[list[i].day]

        if(listChrono!= null){

            val index = findIndexChronoInList(list[i].idEventType, listChrono)

            if(index!=null){ // if idEventType already present in the listChrono of result, get the counter and add +1
                val count:Int = result[list[i].day]!![index].count
                result[list[i].day]!![index].count = count + 1
            } else { // if not,...
                result[list[i].day]!!.add(
                    Chrono(
                        list[i].idEventType,
                        1,
                        list[i].day
                    )
                )
            }

        } else {
            val newList : MutableList<Chrono> = mutableListOf()
            newList.add(
                Chrono(
                    list[i].idEventType,
                    1,
                    list[i].day
                )
            )
            result[list[i].day] = newList
        }
    }
}*/