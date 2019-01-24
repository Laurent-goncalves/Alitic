package com.g.laurent.alitic.Controllers

import android.content.Context
import com.g.laurent.alitic.Models.*

fun saveNewMeal(mealItems:List<MealItem>, dateCode:Long, context: Context):Long?{

    AppDataBase.TEST_MODE = false
    val mealItemDao = AppDataBase.getInstance(context)?.mealItemDao()
    val mealDao = AppDataBase.getInstance(context)?.mealDao()

    // INSERT a new meal and get the id of the meal inserted
    val idMeal = mealDao?.insert(Meal(null, dateCode, mealItems))

    // INSERT each mealItem
    for(item in mealItems){
        mealItemDao?.insert(MealItem(null,idMeal,item.idFood))
    }

    // RETURN idMeal
    return idMeal
}

fun getMealFromDatabase(idMeal:Long?, context: Context):Meal?{

    AppDataBase.TEST_MODE = false
    val mealDao = AppDataBase.getInstance(context)?.mealDao()
    val mealItemDao = AppDataBase.getInstance(context)?.mealItemDao()

    val meal = mealDao?.getMeal(idMeal)
    val mealItems = mealItemDao?.getItemsFromMeal(idMeal)

    meal?.listMealItems = mealItems

    return meal
}

fun deleteMeal(){


}