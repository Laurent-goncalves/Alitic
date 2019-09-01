package com.g.laurent.alitic

import android.content.Context
import com.g.laurent.alitic.Models.EventType
import hirondelle.date4j.DateTime
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max
import kotlin.math.min

fun getTodayDate(): Long {
    val date = Calendar.getInstance()
    return date.timeInMillis
}

fun getDateAsLong(day:Int, month:Int, year:Int, hrs:Int, min:Int):Long {
    val date = Calendar.getInstance()
    date.set(year, month-1, day, hrs, min)
    date.set(Calendar.MILLISECOND, 0)
    date.set(Calendar.SECOND, 0)
    return date.timeInMillis
}

fun getBegDayDate(dateCode:Long):Long{
    val date = Calendar.getInstance()
    date.timeInMillis = dateCode
    date.set(Calendar.HOUR_OF_DAY, 0)
    date.set(Calendar.MINUTE, 0)
    date.set(Calendar.SECOND, 0)
    date.set(Calendar.MILLISECOND, 0)
    return date.timeInMillis
}

fun getEndDayDate(dateCode:Long):Long{
    return getBegDayDate(dateCode) + 24*60*60*999
}

fun isRightMonth(dateCode:Long, month:Int, year:Int):Boolean{
    val date = Calendar.getInstance()
    date.timeInMillis = dateCode
    return date.get(Calendar.MONTH)+1==month && date.get(Calendar.YEAR)==year
}

fun getDayOfMonth(dateCode:Long):Int{
    val date = Calendar.getInstance()
    date.timeInMillis = dateCode
    return date.get(Calendar.DAY_OF_MONTH)
}

fun getFirstDayWeek(dateCode: Long): Long{

    val dateCal = Calendar.getInstance()
    dateCal.timeInMillis = dateCode

    dateCal.set(Calendar.HOUR_OF_DAY, 0)
    dateCal.set(Calendar.MINUTE, 0)
    dateCal.set(Calendar.SECOND, 0)
    dateCal.set(Calendar.MILLISECOND, 0)

    val day = dateCal.get(Calendar.DAY_OF_WEEK)
    val n = day - dateCal.firstDayOfWeek

    if(day > dateCal.firstDayOfWeek){ // if the date is not the first day of the week, change it
        dateCal.timeInMillis -= n * 24 * 60 * 60 * 1000
    } else if (day < dateCal.firstDayOfWeek){
        dateCal.timeInMillis -= (n + 7) * 24 * 60 * 60 * 1000
    }
    return dateCal.timeInMillis
}

fun getTextDate(date:Long):String{
    val dateFormat= SimpleDateFormat("EEE d MMM", Locale.FRENCH)
    val dateCal = Calendar.getInstance()
    dateCal.timeInMillis = date
    val dateToConvert = dateCal.time
    return dateFormat.format(dateToConvert)
}

fun getTextDate(date:Long?, context: Context):String{

    return if(date!=null){
        val dateFormat= SimpleDateFormat("EEE d MMM", Locale.FRENCH)
        val dateCal = Calendar.getInstance()
        dateCal.timeInMillis = date
        val dateToConvert = dateCal.time
        dateFormat.format(dateToConvert)

    } else {
        context.resources.getString(R.string.unavailable_date)
    }
}

fun getTextTime(hourOfDay: Int, minute: Int): String {
    return if (minute < 10)
        "$hourOfDay:0$minute"
    else
        "$hourOfDay:$minute"
}

fun getMonthText(month:Int, year:Int):String{
    val dateFormat= SimpleDateFormat("MMM", Locale.FRENCH)
    val dateCal = Calendar.getInstance()
    dateCal.timeInMillis = getDateAsLong(1,month,2000,0,0)
    val dateToConvert = dateCal.time
    var monthText = dateFormat.format(dateToConvert)
    monthText = monthText.replace(".", "")
    monthText = monthText.substring(0,1).toUpperCase(Locale.FRANCE) + monthText.substring(1,monthText.length)

    return if(month == 1 || month == 12)
        "$monthText ${year.toString().substring(2,4)}"
    else
        monthText
}

fun getMonth(dateCode:Long):Int{
    val date = Calendar.getInstance()
    date.timeInMillis = dateCode
    return date.get(Calendar.MONTH) + 1
}

fun getYear(dateCode:Long):Int{
    val date = Calendar.getInstance()
    date.timeInMillis = dateCode
    return date.get(Calendar.YEAR)
}

fun getTimeAsLong(hour:Int, min:Int):Long {
    return ((min + hour * 60) * 60 * 1000).toLong()
}

fun getDateTimeFromLong(date:Long):DateTime{

    val calendar = Calendar.getInstance()
    calendar.timeInMillis = date

    return DateTime(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), 0,0,0,0)
}

fun getLastDayMonth(month:Int, year:Int ):Int{
    val cal = Calendar.getInstance()
    cal.set(Calendar.MONTH, month-1)
    cal.set(Calendar.YEAR, year)
    cal.set(Calendar.DAY_OF_MONTH, 1)

    return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
}

fun getHourAsInt(dateCode:Long):Int {
    val date = Calendar.getInstance()
    date.timeInMillis = dateCode
    return date.get(Calendar.HOUR_OF_DAY)
}

fun getMinutesAsInt(dateCode:Long):Int {
    val date = Calendar.getInstance()
    date.timeInMillis = dateCode
    return date.get(Calendar.MINUTE)
}

fun getMinTime(minEvent:Long?, minMeal:Long?):Long?{
    return if(minEvent==null && minMeal == null)
        null
    else if(minEvent==null)
        minMeal
    else if(minMeal == null)
        minEvent
    else min(minEvent, minMeal)
}

fun getMaxTime(maxEvent:Long?, maxMeal:Long?):Long?{
    return if(maxEvent==null && maxMeal == null)
        null
    else if(maxEvent==null)
        maxMeal
    else if(maxMeal == null)
        maxEvent
    else max(maxEvent, maxMeal)
}

fun getMinTimeFromEventType(eventType: EventType):Int?{

    val minTime:Long? = eventType.minTime

    return if(minTime!=null){
        (minTime / (60 * 60 * 1000)).toInt()
    } else
        null
}

fun getMaxTimeFromEventType(eventType: EventType):Int?{

    val maxTime:Long? = eventType.maxTime

    return if(maxTime!=null){
        (maxTime / (60 * 60 * 1000)).toInt()
    } else
        null
}