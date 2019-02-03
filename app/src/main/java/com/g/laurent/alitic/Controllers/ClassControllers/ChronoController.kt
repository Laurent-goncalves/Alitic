package com.g.laurent.alitic.Controllers.ClassControllers

import android.content.Context
import com.g.laurent.alitic.Models.Event
import com.g.laurent.alitic.getDayOfMonth
import com.g.laurent.alitic.isRightMonth
import hirondelle.date4j.DateTime

class ChronoItem(val hour:String, val item:List<Any>)

class Chrono(val idEventType:Long?, var count:Int, val day:DateTime)

fun getChronoForOneEventType(idEventType:Long?, month:Int, year:Int, mode:Boolean = false, context: Context):HashMap<DateTime, MutableList<Chrono>>{

    // Get the list of all events related to idEventType
    val listEventByType =
        getListEventForOneEventType(idEventType, mode, context)

    // Get the list of all events related to idEventType
    return organizeEventsByTime(listEventByType, month, year)
}

fun getChronoForAllEventType(month:Int, year:Int, mode:Boolean = false, context: Context):HashMap<DateTime, MutableList<Chrono>>{

    // Get the list of all events
    val listEvents = getAllEvents(mode, context)

    // Get the list of all events
    return organizeEventsByTime(listEvents, month, year)
}

fun organizeEventsByTime(listEvents:List<Event>?, month:Int, year:Int):HashMap<DateTime, MutableList<Chrono>>{

    val list: MutableList<Chrono> = mutableListOf()
    val result: HashMap<DateTime, MutableList<Chrono>> = hashMapOf()

    fun findIndexChronoInList(idEventType: Long?, listChrono:List<Chrono>):Int?{
        for(i in 0 until listChrono.size){
            if(listChrono[i].idEventType == idEventType)
                return i
        }
        return null
    }

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
    }

    return result
}