package com.g.laurent.alitic

import com.g.laurent.alitic.Controllers.ClassControllers.DAY
import com.g.laurent.alitic.Views.DayGrid
import java.util.*
import kotlin.collections.HashMap

fun isLastDayOfMonthTheLastWeekDay(month:Int, year:Int):Boolean{

    val ref = Calendar.getInstance()

    val lastDay = if(ref.firstDayOfWeek!=1)  ref.firstDayOfWeek - 1 else 1

    val cal = Calendar.getInstance()
    cal.set(Calendar.MONTH, month - 1)
    cal.set(Calendar.YEAR, year)
    cal.set(Calendar.DAY_OF_MONTH, getLastDayMonth(month, year))

    return cal[Calendar.DAY_OF_WEEK] == lastDay
}

fun getLevel(value:Int, max:Int): DayGrid {

    val palier:Int = max / 3

    if(value == 0)
        return DayGrid.NO_EVENT_DAY

    when (palier) {
        in 0..1 -> {
            when(value){
                1 -> return DayGrid.EVENT_LEV1
                2 -> return DayGrid.EVENT_LEV2
                3 -> return DayGrid.EVENT_LEV3
            }
        }
        2 -> when(value){
            in 1..2 -> return DayGrid.EVENT_LEV1
            in 3..4 -> return DayGrid.EVENT_LEV2
            in 5..6 -> return DayGrid.EVENT_LEV3
        }
        else -> {
            return when(value){
                in 1..(palier - 1) -> DayGrid.EVENT_LEV1
                in palier..(2*palier - 1) -> DayGrid.EVENT_LEV2
                in 2*palier..(3*palier) -> DayGrid.EVENT_LEV3
                else -> DayGrid.EVENT_LEV3
            }
        }
    }

    return DayGrid.DONT_EXISTS
}

fun getListDayGridForGridView(listDates:List<Long>, month:Int, year:Int): List<DayGrid> {

    val hashDates = getHashMapListDates(listDates)
    val result = mutableListOf<DayGrid>()

    val first = getFirstDateGridView(month,year)
    val last = getLastDateGridView(month, year)

    val nbCol = getNumberColumnsGridView(first, last)
    val table = Array(7) { Array(nbCol) { DayGrid.NO_EVENT_DAY} }

    val maxCountEventByDay = getMaxEventByDay(hashDates)

    // Fill array
    for(date in hashDates){
        if(date.key in first..last){

            val row = getRowDate(date.key)
            val col = getColumnDate(date.key, first, nbCol)

            table[row][col] = getLevel(date.value, maxCountEventByDay)
        }
    }

    // Transform array to List
    for(i in 0 .. 6)
        result.addAll(table[i].toList())

    return result
}

fun getMaxEventByDay(hashDates:HashMap<Long, Int>):Int{
    val max = hashDates.maxBy { it.value }
    return max?.value ?: 0
}

fun getHashMapListDates(listDates:List<Long>):HashMap<Long, Int>{

    val result = hashMapOf<Long, Int>()

    if(listDates.isNotEmpty()){
        for(date in listDates){
            val key = getBegDayDate(date)
            if(result.containsKey(key)){
                result[key] = result[key]!! + 1
            } else {
                result[key] = 1
            }
        }
    }

    return result
}

fun getRowDate(date:Long):Int{

    val cal = Calendar.getInstance()
    cal.timeInMillis = date

    return if(cal[Calendar.DAY_OF_WEEK]==cal.firstDayOfWeek)
        0
    else {
        if(cal[Calendar.DAY_OF_WEEK] - cal.firstDayOfWeek > 0)
            cal[Calendar.DAY_OF_WEEK] - cal.firstDayOfWeek
        else
            7 - cal[Calendar.DAY_OF_WEEK]
    }
}

fun getColumnDate(date:Long, first:Long, nbCol:Int):Int{

    for(i in 0 .. nbCol){
        if(date >= first + (i) * 7 * DAY && date < first + (i+1) * 7 * DAY){
            return i
        }
    }
    return 0
}

fun getFirstDateGridView(month:Int, year:Int):Long{

    val cal = Calendar.getInstance()
    val firstDayMonth = getDateAsLong(1, month, year, 0,0)
    cal.timeInMillis = firstDayMonth

    return if(cal[Calendar.DAY_OF_WEEK] == cal.firstDayOfWeek)
        firstDayMonth
    else {
        while(cal[Calendar.DAY_OF_WEEK] != cal.firstDayOfWeek){
            cal.timeInMillis -= DAY
        }
        cal.timeInMillis
    }
}

fun getLastDateGridView(month:Int, year:Int):Long{

    val cal = Calendar.getInstance()
    val lastDay = if(cal.firstDayOfWeek!=1)  cal.firstDayOfWeek - 1 else 1

    val lastDayMonth = getDateAsLong(getLastDayMonth(month, year), month, year,0,0)
    cal.timeInMillis = lastDayMonth

    return if(isLastDayOfMonthTheLastWeekDay(month, year))
        lastDayMonth
    else {
        while(cal[Calendar.DAY_OF_WEEK] != lastDay){
            cal.timeInMillis -= DAY
        }
        cal.timeInMillis
    }
}

fun getNumberColumnsGridView(first:Long, last:Long):Int{
    return if(last < first + 4 * 7 * DAY) 4 else 5
}

fun getNumberOfMonths(list:List<Long>):Int{

    val minDate = list.min()
    val maxDate = list.max()

    if(minDate!=null && maxDate!=null){
        if(getYear(maxDate) - getYear(minDate)==0){ // SAME YEAR
             return getMonth(maxDate) - getMonth(minDate) + 1
        } else { // DIFFERENT YEARS
            val monthsY1 = 12 - getMonth(minDate) + 1
            val monthsY2 = getMonth(maxDate)

            if(getYear(maxDate) - getYear(minDate)==1)
                return monthsY2 + monthsY1
            else
                 return monthsY2 + monthsY1 + (getYear(maxDate) - getYear(minDate))*12
        }
    }

    return 0
}

fun getMonthItem(list:List<Long>, position:Int):Int{

    val minDate = list.min()

    if(minDate!=null){
        val cal = Calendar.getInstance()
        cal.timeInMillis = minDate
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.add(Calendar.MONTH, position)
        return getMonth(cal.timeInMillis)
    }

    return Calendar.getInstance()[Calendar.MONTH]
}

fun getYearItem(list:List<Long>, position:Int):Int{

    val minDate = list.min()

    if(minDate!=null){
        val cal = Calendar.getInstance()
        cal.timeInMillis = minDate
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.add(Calendar.MONTH, position)
        return getYear(cal.timeInMillis)
    }

    return Calendar.getInstance()[Calendar.YEAR]
}