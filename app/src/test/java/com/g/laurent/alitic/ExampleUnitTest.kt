package com.g.laurent.alitic

import android.content.Context
import com.g.laurent.alitic.Controllers.getListInDescendingOrder
import com.g.laurent.alitic.Controllers.organizeEventsByTime
import com.g.laurent.alitic.Controllers.saveNewEvent
import com.g.laurent.alitic.Controllers.saveNewEventType
import com.g.laurent.alitic.Models.Event
import com.g.laurent.alitic.Models.Food
import com.g.laurent.alitic.Models.MealItem
import hirondelle.date4j.DateTime
import org.junit.Assert
import org.junit.Test

import org.junit.Assert.*
import java.sql.Date

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun time_today() {

        println(getDateAsLong(2,12,2018,1,0))
        println(getDateAsLong(2,12,2018,10,0))
        println(getDateAsLong(12,12,2018,11,0))
        println(getDateAsLong(14,1,2019,8,0))


        assertEquals(4, 2 + 2)
    }

    @Test
    fun test_organize_events_by_date(){

        val list = organizeEventsByTime(getListEventsForTest(), 1, 2019)

        val date1 = DateTime.forDateOnly(2019, 1, 5)
        val date2 = DateTime.forDateOnly(2019, 1, 15)
        val date3 = DateTime.forDateOnly(2019, 1, 2)

        assertEquals(3, list[date1]!![0].count)
        assertEquals(1, list[date2]!![0].count)
        assertTrue(list[date3]!![0].count == 1 && list[date3]!![1].count == 1)
    }

    @Test
    fun test_day_of_month() {

        var date = getDateAsLong(1,12,2018,0,0)
        assertEquals(1, getDayOfMonth(date))

        date = getDateAsLong(31,12,2018,23,59)
        assertEquals(31, getDayOfMonth(date))

        date = getDateAsLong(15,12,2018,10,0)
        assertEquals(15, getDayOfMonth(date))

        assertEquals(true, isRightMonth(date, 12, 2018))

        date = getDateAsLong(1,1,2018,0,0)
        assertEquals(true, isRightMonth(date, 1, 2018))

        date = getDateAsLong(1,2,2018,0,0)
        assertEquals(false, isRightMonth(date, 1, 2018))
    }

    @Test
    fun test_descending_order() {

        var list:HashMap<Food, Int> = hashMapOf()
        list[Food(0, "Banane", "Fruit")] = 2
        list[Food(0, "Abricot", "Fruit")] = 1
        list[Food(0, "Pomme", "Fruit")] = 4
        list[Food(0, "Avocat", "Fruit")] = 0
        list[Food(0, "Cerise", "Fruit")] = 0
        list[Food(0, "Poire", "Fruit")] = 1

        val result = getListInDescendingOrder(list)

        Assert.assertEquals("Pomme", result.get(0).food)
        Assert.assertEquals("Banane", result.get(1).food)
        Assert.assertEquals("Poire", result.get(2).food)
        Assert.assertEquals("Abricot", result.get(3).food)
        Assert.assertEquals("Avocat", result.get(4).food)
        Assert.assertEquals("Cerise", result.get(5).food)
    }

    fun getListEventsForTest():List<Event>?{

        val result : MutableList<Event> = mutableListOf()

        result.add(Event(0, 0, getDateAsLong(2,1,2019,12,0)))
        result.add(Event(1, 1, getDateAsLong(2,1,2019,12,0)))
        result.add(Event(2, 2, getDateAsLong(14,1,2019,12,0)))
        result.add(Event(3, 1, getDateAsLong(5,1,2019,12,0)))
        result.add(Event(4, 1, getDateAsLong(1,1,2019,12,0)))
        result.add(Event(5, 0, getDateAsLong(1,1,2019,12,0)))
        result.add(Event(6, 1, getDateAsLong(5,1,2019,12,0)))
        result.add(Event(7, 1, getDateAsLong(7,1,2019,12,0)))
        result.add(Event(8, 2, getDateAsLong(15,1,2019,12,0)))
        result.add(Event(9, 0, getDateAsLong(8,1,2019,12,0)))
        result.add(Event(10, 1, getDateAsLong(5,1,2019,12,0)))

        return result.toList()
    }
}
