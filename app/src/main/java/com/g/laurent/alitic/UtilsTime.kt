package com.g.laurent.alitic

import java.util.*

fun getTodayDate(): Long {
    val date = Calendar.getInstance()
    return date.timeInMillis
}

fun getDateAsLong(day:Int, month:Int, year:Int, hrs:Int, min:Int):Long {
    val date = Calendar.getInstance()
    date.set(year, month-1, day, hrs, min)
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