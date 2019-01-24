package com.g.laurent.alitic

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.g.laurent.alitic.Controllers.*
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class EventControllerTest {

    @Test
    fun test_save_event(){

        val context = InstrumentationRegistry.getTargetContext()

        saveEventsAndEventTypes(context)

        val list = getEventsFromDate(getDateAsLong(14,1,2019,12,0), true, context)

        Assert.assertEquals(2, list?.size)
    }

    @Test
    fun test_update_event_type(){

        val context = InstrumentationRegistry.getTargetContext()

        val id1 = saveNewEventType("Reflux", 0,23, true, context)

        updateEventType(id1, "Reflux gastrique", 1, 555, true, context)

        var list = getListEventType("reflux", true, context)
        Assert.assertEquals(1, list?.size)

        list = getListEventType("gast", true, context)
        Assert.assertEquals(1, list?.size)

        list = getListEventType("Refli", true, context)
        Assert.assertEquals(0, list?.size)
    }

    fun saveEventsAndEventTypes(context: Context){

        val id1 = saveNewEventType("Reflux", 0,23, true, context)
        val id2 = saveNewEventType("Mal au ventre", 6,12, true, context)
        val id3 = saveNewEventType("Mal à la tête", 7,24, true, context)

        saveNewEvent(idEventType = id1,
            dateCode = getDateAsLong(2,12,2018,12,0),
            context = context)

        saveNewEvent(idEventType = id2,
            dateCode = getDateAsLong(12,12,2018,12,0),
            context = context)

        saveNewEvent(idEventType = id3,
            dateCode = getDateAsLong(14,1,2019,11,0),
            context = context)

        saveNewEvent(idEventType = id1,
            dateCode = getDateAsLong(14,1,2019,14,0),
            context = context)

        saveNewEvent(idEventType = id3,
            dateCode = getDateAsLong(24,1,2019,12,0),
            context = context)
    }
}