package com.g.laurent.alitic.Controllers

import android.content.Context
import com.g.laurent.alitic.Models.AppDataBase
import com.g.laurent.alitic.Models.Event
import com.g.laurent.alitic.Models.EventType
import com.g.laurent.alitic.getBegDayDate
import com.g.laurent.alitic.getEndDayDate
import com.g.laurent.alitic.getTodayDate


fun saveNewEvent(idEventType: Long?, dateCode:Long = getTodayDate(), mode:Boolean = false, context:Context):Long?{
    AppDataBase.TEST_MODE = mode
    val eventDao = AppDataBase.getInstance(context)?.eventDao()
    return eventDao?.insert(Event(null, idEventType, dateCode))
}

fun saveNewEventType(name:String, minTime:Long, maxTime:Long, mode:Boolean = false, context:Context):Long?{
    AppDataBase.TEST_MODE = mode
    val eventTypeDao = AppDataBase.getInstance(context)?.eventTypeDao()
    return eventTypeDao?.insert(EventType(null, name, minTime, maxTime))
}

fun updateEventType(idEventType:Long?, name:String, minTime:Long, maxTime:Long, mode:Boolean = false, context:Context){
    AppDataBase.TEST_MODE = mode
    val eventTypeDao = AppDataBase.getInstance(context)?.eventTypeDao()
    eventTypeDao?.update(EventType(idEventType, name, minTime, maxTime))
}

fun getEventsFromDate(dateCode:Long, mode:Boolean = false, context:Context):List<Event>? {
    AppDataBase.TEST_MODE = mode
    val eventDao = AppDataBase.getInstance(context)?.eventDao()

    val minTime = getBegDayDate(dateCode)
    val maxTime = getEndDayDate(dateCode)

    return eventDao?.getEventsDate(minTime, maxTime)
}

fun getListEventType(search:String, mode:Boolean = false, context:Context):List<EventType>?{

    AppDataBase.TEST_MODE = mode
    val eventTypeDao = AppDataBase.getInstance(context)?.eventTypeDao()
    val listEventType = eventTypeDao?.getAll()
    val result : MutableList<EventType> = mutableListOf()

    return if(listEventType!=null){
        for(eventType in listEventType){
            if(eventType.name.toLowerCase().contains(search.toLowerCase())) // if the name contains the search word, add eventType to the list
                result.add(eventType)
        }
        result
    } else
        null
}