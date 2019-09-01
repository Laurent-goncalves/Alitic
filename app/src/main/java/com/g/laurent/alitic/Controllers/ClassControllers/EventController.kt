package com.g.laurent.alitic.Controllers.ClassControllers

import android.content.Context
import com.g.laurent.alitic.*
import com.g.laurent.alitic.Models.AppDataBase
import com.g.laurent.alitic.Models.Event
import com.g.laurent.alitic.Models.EventType
import hirondelle.date4j.DateTime

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

fun getOldestEvent(mode:Boolean = false, context: Context):Long?{
    AppDataBase.TEST_MODE = mode
    val eventDao = AppDataBase.getInstance(context)?.eventDao()
    return eventDao?.getOldestEventDate()
}

fun getLatestEvent(mode:Boolean = false, context: Context):Long?{
    AppDataBase.TEST_MODE = mode
    val eventDao = AppDataBase.getInstance(context)?.eventDao()
    return eventDao?.getLatestEventDate()
}

fun getAllEvents(mode:Boolean = false, context:Context):List<Event>? {
    AppDataBase.TEST_MODE = mode
    val eventDao = AppDataBase.getInstance(context)?.eventDao()
    return eventDao?.getAll()
}

fun getAllEventData(mode:Boolean = false, context: Context):List<EventData>?{
    AppDataBase.TEST_MODE = mode
    val eventDao = AppDataBase.getInstance(context)?.eventDao()
    return eventDao?.getAllEventDatas()
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

fun saveNewEventType(name:String?, eventPic:String?, minTime:Long?, maxTime:Long?, forLastMeal:Boolean?, mode:Boolean = false, context:Context):Long?{
    AppDataBase.TEST_MODE = mode
    val eventTypeDao = AppDataBase.getInstance(context)?.eventTypeDao()
    return eventTypeDao?.insert(EventType(null, name, eventPic, minTime, maxTime, forLastMeal))
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

fun getListEventTypesForStatDetailFragment(context:Context):List<EventType>{

    val result = mutableListOf<EventType>()
    val listTotEventTypes = getAllEventTypes(context = context)
    val listEvents = getAllEvents(context = context)

    if(listTotEventTypes!=null && listEvents!=null){
        for(event in listEvents){

            val eventType = getEventType(event.idEventType, context = context)
            val title = eventType?.name

            if(title != null && listTotEventTypes.find { it.id == event.idEventType } != null
                && result.find { it.name.equals(eventType.name)} == null)
                result.add(eventType)
        }
    }

    result.add(0, EventType(null,context.resources.getString(R.string.all_event_types),null, 0,0,false))

    return result
}

fun getChronoEvents(mode:Boolean=false, context: Context):List<DateTime>{

    val chronoEvents = mutableListOf<DateTime>()

    val events = getAllEvents(mode, context)

    if(events!=null && events.isNotEmpty()){

        for(event in events){
            val dateTime = getDateTimeFromLong(event.dateCode)
            if(!chronoEvents.contains(dateTime))
                chronoEvents.add(dateTime)
        }
    }

    return chronoEvents
}