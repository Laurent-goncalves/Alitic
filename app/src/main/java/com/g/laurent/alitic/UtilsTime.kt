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

fun transformHourInLong(hour:Double):Long{
    return 60 * 60 * (1000 * hour).toLong()
}

fun transformLongInHour(hour:Long):Double{
    return (hour / 100000).toDouble() / 36
}