package com.g.laurent.alitic.Controllers

import android.content.Context
import com.g.laurent.alitic.Models.AppDataBase
import com.g.laurent.alitic.Models.Food

fun saveNewFood(name:String, type:String, mode:Boolean = false, context: Context):Long?{
    AppDataBase.TEST_MODE = mode
    val foodDao = AppDataBase.getInstance(context)?.foodDao()
    return foodDao?.insert(Food(null, name, type))
}

fun getAllFood(mode:Boolean = false, context:Context):List<Food>?{
    AppDataBase.TEST_MODE = mode
    val foodDao = AppDataBase.getInstance(context)?.foodDao()
    return foodDao?.getAll()
}


fun getListFood(search:String, mode:Boolean = false, context:Context):List<Food>?{

    AppDataBase.TEST_MODE = mode
    val foodDao = AppDataBase.getInstance(context)?.foodDao()
    val listFood = foodDao?.getAll()
    val result : MutableList<Food> = mutableListOf()

    return if(listFood!=null){
        for(food in listFood){
            if(food.name.toLowerCase().contains(search.toLowerCase())) // if the name contains the search word, add food to the list
                result.add(food)
        }
        result
    } else
        null
}

fun getListFoodByType(type:String, mode:Boolean = false, context:Context):List<Food>?{
    AppDataBase.TEST_MODE = mode
    val foodDao = AppDataBase.getInstance(context)?.foodDao()
    return foodDao?.getFoodByType(type)
}

fun deleteFood(idFood:Long?, mode:Boolean = false, context: Context){
    AppDataBase.TEST_MODE = mode
    val foodDao = AppDataBase.getInstance(context)?.foodDao()
    foodDao?.deleteFood(idFood)
}

fun deleteAllFood(mode:Boolean = false, context: Context){
    AppDataBase.TEST_MODE = mode
    val foodDao = AppDataBase.getInstance(context)?.foodDao()
    foodDao?.deleteAll()
}