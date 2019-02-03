package com.g.laurent.alitic.Controllers.ClassControllers

import android.content.Context
import com.g.laurent.alitic.Models.AppDataBase
import com.g.laurent.alitic.Models.Event
import com.g.laurent.alitic.Models.EventType
import com.g.laurent.alitic.Models.Food
import com.g.laurent.alitic.getBegDayDate
import com.g.laurent.alitic.getEndDayDate
import com.g.laurent.alitic.getTodayDate

// ---------------------------------------------------------------------------------------------------------------
// ------------------------------------------- EVENT -------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------

fun saveNewEvent(idEventType: Long?, dateCode:Long = getTodayDate(), mode:Boolean = false, context:Context):Long?{
    AppDataBase.TEST_MODE = mode
    val eventDao = AppDataBase.getInstance(context)?.eventDao()
    return eventDao?.insert(Event(null, idEventType, dateCode))
}

fun getListEventForOneEventType(idEventType:Long?, mode:Boolean = false, context:Context):List<Event>?{
    AppDataBase.TEST_MODE = mode
    val eventDao = AppDataBase.getInstance(context)?.eventDao()
    return eventDao?.getEventsByType(idEventType)
}

fun getEventsFromDate(dateCode:Long, mode:Boolean = false, context:Context):List<Event>? {
    AppDataBase.TEST_MODE = mode
    val eventDao = AppDataBase.getInstance(context)?.eventDao()

    val minTime = getBegDayDate(dateCode)
    val maxTime = getEndDayDate(dateCode)

    return eventDao?.getEventsDate(minTime, maxTime)
}

fun getAllEvents(mode:Boolean = false, context:Context):List<Event>? {
    AppDataBase.TEST_MODE = mode
    val eventDao = AppDataBase.getInstance(context)?.eventDao()
    return eventDao?.getAll()
}

fun updateEvent(event:Event, mode:Boolean = false, context:Context){
    AppDataBase.TEST_MODE = mode
    val eventDao = AppDataBase.getInstance(context)?.eventDao()
    eventDao?.update(event)
}

fun deleteEvent(idEvent:Long?, mode:Boolean = false, context:Context){
    AppDataBase.TEST_MODE = mode
    val eventDao = AppDataBase.getInstance(context)?.eventDao()
    eventDao?.deleteEvent(idEvent)
}

fun deleteAllEvents(mode:Boolean = false, context:Context){
    AppDataBase.TEST_MODE = mode
    val eventDao = AppDataBase.getInstance(context)?.eventDao()
    eventDao?.deleteAll()
}

// ---------------------------------------------------------------------------------------------------------------
// ----------------------------------------- EVENTTYPE -----------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------

fun saveNewEventType(name:String, eventPic:String?, minTime:Long, maxTime:Long, mode:Boolean = false, context:Context):Long?{
    AppDataBase.TEST_MODE = mode
    val eventTypeDao = AppDataBase.getInstance(context)?.eventTypeDao()
    return eventTypeDao?.insert(EventType(null, name, eventPic, minTime, maxTime))
}

fun getEventType(idEventType:Long?, mode:Boolean = false, context:Context): EventType?{
    AppDataBase.TEST_MODE = mode
    val eventTypeDao = AppDataBase.getInstance(context)?.eventTypeDao()
    return eventTypeDao?.getEventType(idEventType)
}

fun getAllEventTypes(mode:Boolean = false, context:Context): List<EventType>?{
    AppDataBase.TEST_MODE = mode
    val eventTypeDao = AppDataBase.getInstance(context)?.eventTypeDao()
    return eventTypeDao?.getAll()
}

fun updateEventType(eventType:EventType?, mode:Boolean = false, context:Context){
    AppDataBase.TEST_MODE = mode
    val eventTypeDao = AppDataBase.getInstance(context)?.eventTypeDao()
    if (eventType != null) {
        eventTypeDao?.update(eventType)
    }
}

fun deleteEventType(idEventType:Long?, mode:Boolean = false, context:Context){
    AppDataBase.TEST_MODE = mode
    val eventTypeDao = AppDataBase.getInstance(context)?.eventTypeDao()
    eventTypeDao?.deleteEventType(idEventType)
}

fun deleteAllEventTypes(mode:Boolean = false, context:Context){
    AppDataBase.TEST_MODE = mode
    val eventTypeDao = AppDataBase.getInstance(context)?.eventTypeDao()
    eventTypeDao?.deleteAll()
}

/*fun getListEventType(search:String, mode:Boolean = false, context:Context):List<EventType>?{

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
}*/