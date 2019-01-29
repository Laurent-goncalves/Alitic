package com.g.laurent.alitic.Controllers

import android.content.Context
import com.g.laurent.alitic.Models.EventType
import com.g.laurent.alitic.Models.Food
import com.g.laurent.alitic.Models.Meal

class EventTable(val minTime:Long, val maxTime:Long)

class StatList(val food:String, val type:Long?, val count:Int)

fun getListFoodForEventType(eventType: EventType, mode:Boolean = false, context:Context):List<StatList>{

    fun getEventTable(context:Context):List<EventTable> {

        val list : MutableList<EventTable> = mutableListOf()
        val listEvent = getAllEvents(context = context)

        if(listEvent!=null){
            for(e in listEvent){
                if(e.idEventType == eventType.id)
                    list.add(EventTable(e.dateCode-eventType.maxTime, e.dateCode-eventType.minTime))
            }
        }

        return list
    }

    fun shouldMealBeTakenIntoAccount(meal:Meal, eventTable:List<EventTable>):Boolean{
        for(e in eventTable){
            if(meal.dateCode in e.minTime .. e.maxTime)
                return true
        }
        return false
    }

    fun getMealFittingEventTable(eventTable:List<EventTable>, listMeals:List<Meal>?):HashMap<Food, Int>{

        val list:HashMap<Food, Int> = hashMapOf()

        if(listMeals!=null){
            for(meal in listMeals){
                if(shouldMealBeTakenIntoAccount(meal, eventTable)){

                    val listFood = getFoodsFromMeal(meal, mode, context)

                    for(food in listFood){
                        if(list[food] != null){
                            val count:Int = list[food]!!
                            list[food] = count + 1
                        } else
                            list[food] = 1
                    }
                }
            }
        }

        return list
    }

    // Get the list of all meals
    val listMeals = getAllMeals(mode, context)

    // Get the list of all events corresponding to eventType and their limits of time (min and max) to take into account meals
    val eventTable = getEventTable(context)

    // Get the hashMap list of food from meals causing events and their occurrence
    val listFoodByOccur = getMealFittingEventTable(eventTable, listMeals)

    // Return the list in descending order
    return getListInDescendingOrder(listFoodByOccur)
}

fun getListInDescendingOrder(list:HashMap<Food, Int>):List<StatList>{

    val result : MutableList<StatList> = mutableListOf()
    val listToCheck = list

    if(list.size != 0){
        for (i in 0 until list.size) {
            val max = listToCheck.maxBy { it.value }
            result.add(StatList(max!!.key.name, max.key.idFoodType, max.value))
            listToCheck.remove(max.key)
        }
    }

    return result.toList()
}