package com.g.laurent.alitic

import com.g.laurent.alitic.Controllers.ClassControllers.getListInDescendingOrder
import com.g.laurent.alitic.Controllers.ClassControllers.organizeEventsByTime
import com.g.laurent.alitic.Models.Event
import com.g.laurent.alitic.Models.Food
import hirondelle.date4j.DateTime
import org.junit.Assert
import org.junit.Test
import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun test_organize_events_by_date(){

        val list =
            organizeEventsByTime(getListEventsForTest(), 1, 2019)

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
    fun test_get_day_month_year() {
        val date = getDateAsLong(1,12,2018,14,55)
        assertEquals(14, getHourAsInt(date))
        assertEquals(55, getMinutesAsInt(date))
        assertEquals(11, getMonth(date))
        assertEquals(2018, getYear(date))
    }

    @Test
    fun test_get_date_in_text_format(){
        val date = getDateAsLong(1,12,2018,4,55)
        assertEquals("sam. 1 déc.", getTextDate(date))
        assertEquals("4:55", getTextTime(4, 55))
        assertEquals("4:09", getTextTime(4, 9))
    }

    @Test
    fun test_descending_order() {

        val list:HashMap<Food, Int> = hashMapOf()
        list[Food(0, "Banane", 0,0,null, false)] = 4
        list[Food(0, "Abricot", 0,0,null, false)] = 2
        list[Food(0, "Pomme", 0,0,null, false)] = 5
        list[Food(0, "Avocat", 0,0,null, false)] = 1
        list[Food(0, "Cerise", 0,0,null, false)] = 0
        list[Food(0, "Poire", 0,0,null, false)] = 3

        val result = getListInDescendingOrder(list)

        Assert.assertEquals("Pomme", result[0].food)
        Assert.assertEquals("Banane", result[1].food)
        Assert.assertEquals("Poire", result[2].food)
        Assert.assertEquals("Abricot", result[3].food)
        Assert.assertEquals("Avocat", result[4].food)
        Assert.assertEquals("Cerise", result[5].food)
    }

    fun getListEventsForTest():List<Event>?{

        val result : MutableList<Event> = mutableListOf()

        result.add(Event(0, 0, getDateAsLong(2,1,2019,12,0), false))
        result.add(Event(1, 1, getDateAsLong(2,1,2019,12,0), false))
        result.add(Event(2, 2, getDateAsLong(14,1,2019,12,0), false))
        result.add(Event(3, 1, getDateAsLong(5,1,2019,12,0), false))
        result.add(Event(4, 1, getDateAsLong(1,1,2019,12,0), false))
        result.add(Event(5, 0, getDateAsLong(1,1,2019,12,0), false))
        result.add(Event(6, 1, getDateAsLong(5,1,2019,12,0), false))
        result.add(Event(7, 1, getDateAsLong(7,1,2019,12,0), false))
        result.add(Event(8, 2, getDateAsLong(15,1,2019,12,0), false))
        result.add(Event(9, 0, getDateAsLong(8,1,2019,12,0), false))
        result.add(Event(10, 1, getDateAsLong(5,1,2019,12,0), false))

        return result.toList()
    }
}
