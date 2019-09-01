package com.g.laurent.alitic

import com.g.laurent.alitic.Models.Event
import com.g.laurent.alitic.Models.Meal
import com.g.laurent.alitic.Views.DayGrid
import org.junit.Assert
import org.junit.Test
import org.junit.Assert.*
import kotlin.math.ceil

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {



    @Test
    fun test_last_day_month(){
        Assert.assertFalse(isLastDayOfMonthTheLastWeekDay(2,2019))
        Assert.assertTrue(isLastDayOfMonthTheLastWeekDay(3,2019))
        Assert.assertFalse(isLastDayOfMonthTheLastWeekDay(5,2019))
        Assert.assertTrue(isLastDayOfMonthTheLastWeekDay(6,2019))
    }




    @Test
    fun test_hashmap_dates_stats(){

        val listDates = listOf(
            getDateAsLong(1,2,2019,12,0),
            getDateAsLong(1,2,2019,14,0),
            getDateAsLong(3,2,2019,1,0),
            getDateAsLong(8,2,2019,23,0),
            getDateAsLong(9,2,2019,18,0),
            getDateAsLong(11,2,2019,0,0),
            getDateAsLong(11,2,2019,1,0),
            getDateAsLong(1,2,2019,16,0),
            getDateAsLong(19,2,2019,15,0),
            getDateAsLong(3,3,2019,12,0),
            getDateAsLong(20,3,2019,11,0))

        val hashdates = getHashMapListDates(listDates)

        Assert.assertEquals(3, hashdates[getBegDayDate(getDateAsLong(1,2,2019,12,0))])
        Assert.assertEquals(2, hashdates[getBegDayDate(getDateAsLong(11,2,2019,12,0))])
        Assert.assertEquals(1, hashdates[getBegDayDate(getDateAsLong(3,3,2019,12,0))])
        Assert.assertEquals(3,getMaxEventByDay(hashdates))
    }

    @Test
    fun test_gridview_stats(){

        // FIRST DAY GRIDVIEW
        Assert.assertEquals(getDateAsLong(31,12,2018,0,0), getFirstDateGridView(1,2019))
        Assert.assertEquals(getDateAsLong(28,1,2019,0,0), getFirstDateGridView(2,2019))
        Assert.assertEquals(getDateAsLong(25,2,2019,0,0), getFirstDateGridView(3,2019))
        Assert.assertEquals(getDateAsLong(1,4,2019,0,0), getFirstDateGridView(4,2019))
        Assert.assertEquals(getDateAsLong(29,4,2019,0,0), getFirstDateGridView(5,2019))
        Assert.assertEquals(getDateAsLong(27,5,2019,0,0), getFirstDateGridView(6,2019))

        // LAST DAY GRIDVIEW
        Assert.assertEquals(getDateAsLong(27,1,2019,0,0), getLastDateGridView(1,2019))
        Assert.assertEquals(getDateAsLong(24,2,2019,0,0), getLastDateGridView(2,2019))
        Assert.assertEquals(getDateAsLong(31,3,2019,0,0), getLastDateGridView(3,2019))
        Assert.assertEquals(getDateAsLong(28,4,2019,0,0), getLastDateGridView(4,2019))
        Assert.assertEquals(getDateAsLong(26,5,2019,0,0), getLastDateGridView(5,2019))
        Assert.assertEquals(getDateAsLong(30,6,2019,0,0), getLastDateGridView(6,2019))

        // ROW
        Assert.assertEquals(0, getRowDate(getDateAsLong(11,2,2019,12,0)))
        Assert.assertEquals(1, getRowDate(getDateAsLong(12,2,2019,12,0)))
        Assert.assertEquals(2, getRowDate(getDateAsLong(13,2,2019,12,0)))
        Assert.assertEquals(3, getRowDate(getDateAsLong(14,2,2019,12,0)))
        Assert.assertEquals(4, getRowDate(getDateAsLong(15,2,2019,12,0)))
        Assert.assertEquals(5, getRowDate(getDateAsLong(16,2,2019,12,0)))
        Assert.assertEquals(6, getRowDate(getDateAsLong(17,2,2019,12,0)))

        // NUMBER OF COLUMNS
        var first = getFirstDateGridView(3,2019)
        var last = getLastDateGridView(3, 2019)
        var nbCol = getNumberColumnsGridView(first, last)

        Assert.assertEquals(5, nbCol)

        first = getFirstDateGridView(2,2019)
        last = getLastDateGridView(2, 2019)
        nbCol = getNumberColumnsGridView(first, last)

        Assert.assertEquals(4, nbCol)

        // COLUMN
        Assert.assertEquals(0, getColumnDate(getDateAsLong(28,1,2019,0,0), first, nbCol))
        Assert.assertEquals(1, getColumnDate(getDateAsLong(6,2,2019,0,0), first, nbCol))
        Assert.assertEquals(2, getColumnDate(getDateAsLong(11,2,2019,0,0), first, nbCol))
        Assert.assertEquals(3, getColumnDate(getDateAsLong(24,2,2019,0,0), first, nbCol))
        Assert.assertEquals(4, getColumnDate(getDateAsLong(2,3,2019,0,0), first, nbCol))

        // GRIDVIEW
        val listDates = listOf(
            getDateAsLong(1,2,2019,12,0),
            getDateAsLong(1,2,2019,14,0),
            getDateAsLong(3,2,2019,1,0),
            getDateAsLong(8,2,2019,23,0),
            getDateAsLong(9,2,2019,18,0),
            getDateAsLong(11,2,2019,0,0),
            getDateAsLong(11,2,2019,1,0),
            getDateAsLong(1,2,2019,16,0),
            getDateAsLong(19,2,2019,15,0),
            getDateAsLong(21,2,2019,12,0),
            getDateAsLong(15,2,2019,11,0),
            getDateAsLong(14,2,2019,11,0),
            getDateAsLong(12,2,2019,11,0),
            getDateAsLong(22,2,2019,11,0),
            getDateAsLong(21,2,2019,11,0),
            getDateAsLong(29,2,2019,11,0),
            getDateAsLong(3,2,2019,11,0),
            getDateAsLong(5,2,2019,11,0)
        )

        val grid = getListDayGridForGridView(listDates, 2, 2019)

        Assert.assertEquals(listOf(DayGrid.NO_EVENT_DAY,DayGrid.NO_EVENT_DAY,DayGrid.EVENT_LEV2,DayGrid.NO_EVENT_DAY,
            DayGrid.NO_EVENT_DAY,DayGrid.EVENT_LEV1,DayGrid.EVENT_LEV1,DayGrid.EVENT_LEV1,
            DayGrid.NO_EVENT_DAY,DayGrid.NO_EVENT_DAY,DayGrid.NO_EVENT_DAY,DayGrid.NO_EVENT_DAY,
        DayGrid.NO_EVENT_DAY,DayGrid.NO_EVENT_DAY,DayGrid.EVENT_LEV1,DayGrid.EVENT_LEV2,
        DayGrid.EVENT_LEV3,DayGrid.EVENT_LEV1,DayGrid.EVENT_LEV1,DayGrid.EVENT_LEV1,
        DayGrid.NO_EVENT_DAY,DayGrid.EVENT_LEV1,DayGrid.NO_EVENT_DAY,DayGrid.NO_EVENT_DAY,
        DayGrid.EVENT_LEV2,DayGrid.NO_EVENT_DAY,DayGrid.NO_EVENT_DAY,DayGrid.NO_EVENT_DAY), grid)
    }

    @Test
    fun test_first_day_and_end_day_week(){

        var date = getDateAsLong(2,2,2019,12,15)

        var firstDay = getFirstDayWeek(date)

        assertEquals("lun. 28 janv.", getTextDate(firstDay))

        date = getDateAsLong(3,2,2019,23,15)
        firstDay = getFirstDayWeek(date)

        assertEquals("lun. 28 janv.", getTextDate(firstDay))
    }

    @Test
    fun checkFactorCalculation(){

        var size = 4
        var factor = ceil(size.toDouble() / 4.toDouble()).toInt()
        assertEquals(1, factor)

        size = 10
        factor = ceil(size.toDouble() / 4.toDouble()).toInt()
        assertEquals(3, factor)

        size = 12
        factor = ceil(size.toDouble() / 4.toDouble()).toInt()
        assertEquals(3, factor)

        size = 13
        factor = ceil(size.toDouble() / 4.toDouble()).toInt()
        assertEquals(4, factor)
    }

    @Test
    fun test_month(){

        val list = mutableListOf(getDateAsLong(15,3,2018,0,0),
            getDateAsLong(12, 8, 2018, 0,0))
        assertEquals(6, getNumberOfMonths(list))

        list.add(getDateAsLong(31,1,2019,0,0))
        assertEquals(11, getNumberOfMonths(list))

        list.add(getDateAsLong(1,9,2019,0,0))
        assertEquals(19, getNumberOfMonths(list))

        assertEquals("Janv 19", getMonthText(1, 2019))
        assertEquals("Févr", getMonthText(2, 2019))
        assertEquals("Sept", getMonthText(9, 2019))
        assertEquals("Déc 19", getMonthText(12, 2019))
    }

    @Test
    fun test_month_and_year_stat(){

        val list = mutableListOf(getDateAsLong(15,3,2018,0,0),
            getDateAsLong(12, 8, 2021, 0,0))

        assertEquals(3, getMonthItem(list, 0))
        assertEquals(5, getMonthItem(list, 2))
        assertEquals(3, getMonthItem(list, 12))
        assertEquals(9, getMonthItem(list, 18))
        assertEquals(4, getMonthItem(list, 25))

        assertEquals(2018, getYearItem(list, 0))
        assertEquals(2018, getYearItem(list, 2))
        assertEquals(2019, getYearItem(list, 12))
        assertEquals(2019, getYearItem(list, 18))
        assertEquals(2020, getYearItem(list, 25))
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
        assertEquals(12, getMonth(date))
        assertEquals(2018, getYear(date))
    }

    @Test
    fun test_get_last_day_month() {
        assertEquals(28, getLastDayMonth(2,2019))
        assertEquals(31, getLastDayMonth(3,2019))
        assertEquals(30, getLastDayMonth(4,2019))
    }

    @Test
    fun test_get_date_in_text_format(){
        val date = getDateAsLong(1,12,2018,4,55)
        assertEquals("sam. 1 déc.", getTextDate(date))
        assertEquals("4:55", getTextTime(4, 55))
        assertEquals("4:09", getTextTime(4, 9))
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
    @Test
    fun test(){

        val list = mutableListOf<Meal>()
        list.add(Meal(null, 1500, null))
        list.add(Meal(null, 1400, null))
        list.add(Meal(null, 1800, null))
        list.add(Meal(null, 1700, null))
        list.add(Meal(null, 1600, null))
        list.add(Meal(null, 1900, null))

        list.sortByDescending { it.dateCode }

        val meal = findLastMeal(Event(null, null, 110, false), list)
println("eee " + meal?.dateCode)
    }

    fun findLastMeal(event: Event, listMeals:List<Meal>):Meal?{
        for(i in 0 until listMeals.size){
            if(listMeals[i].dateCode <= event.dateCode)
                return listMeals[i]
        }
        return null
    }
}
