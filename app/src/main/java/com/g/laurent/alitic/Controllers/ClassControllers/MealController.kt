package com.g.laurent.alitic.Controllers.ClassControllers

import android.content.Context
import com.g.laurent.alitic.Models.*
import com.g.laurent.alitic.getBegDayDate
import com.g.laurent.alitic.getEndDayDate
import kotlin.math.max

fun saveNewMeal(mealItems:List<MealItem>, dateCode:Long, mode:Boolean = false, context: Context):Long?{

    AppDataBase.TEST_MODE = mode
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

fun updateMeal(mealItems:List<MealItem>, dateCode:Long, mode:Boolean = false, context: Context):Long?{

    AppDataBase.TEST_MODE = mode
    val mealItemDao = AppDataBase.getInstance(context)?.mealItemDao()
    val mealDao = AppDataBase.getInstance(context)?.mealDao()

    // DELETE meal and mealitems
    deleteMeal(mealItems[0].id, mode, context)

    // INSERT NEW meal
    val idMeal = mealDao?.insert(Meal(null, dateCode, mealItems))

    // INSERT each mealItem
    for(item in mealItems){
        mealItemDao?.insert(MealItem(null, idMeal,item.idFood))
    }

    // RETURN idMeal
    return idMeal
}

fun getMealFromDatabase(idMeal:Long?, mode:Boolean = false, context: Context):Meal?{

    AppDataBase.TEST_MODE = mode
    val mealDao = AppDataBase.getInstance(context)?.mealDao()
    val mealItemDao = AppDataBase.getInstance(context)?.mealItemDao()

    val meal = mealDao?.getMeal(idMeal)
    val mealItems = mealItemDao?.getItemsFromMeal(idMeal)

    meal?.listMealItems = mealItems

    return meal
}

fun getMealItemsFromListFoods(listFood:List<Any>):List<MealItem>{

    val listMealItems = mutableListOf<MealItem>()
    for(any in listFood){
        val food = any as Food
        listMealItems.add(MealItem(null,null, food.id))
    }
    return listMealItems.toList()
}

fun getFoodsFromMeal(meal:Meal, mode:Boolean = false, context: Context):List<Food>{

    AppDataBase.TEST_MODE = mode
    val listItems = meal.listMealItems
    val listFood : MutableList<Food> = mutableListOf()
    val foodDao = AppDataBase.getInstance(context)?.foodDao()

    if(listItems!=null){
        for(item in listItems){
            val food = foodDao?.getFood(item.idFood)
            if(food!=null)
                listFood.add(food)
        }
    }

    return listFood.toList()
}

fun getOldestMeal(mode:Boolean = false, context: Context):Long?{
    AppDataBase.TEST_MODE = mode
    val mealDao = AppDataBase.getInstance(context)?.mealDao()
    return mealDao?.getOldestMealDate()
}

fun getLatestMeal(mode:Boolean = false, context: Context):Long?{
    AppDataBase.TEST_MODE = mode
    val mealDao = AppDataBase.getInstance(context)?.mealDao()
    return mealDao?.getLatestMealDate()
}

fun getAllMeals(mode:Boolean = false, context: Context):List<Meal>?{

    AppDataBase.TEST_MODE = mode
    val mealDao = AppDataBase.getInstance(context)?.mealDao()
    val mealItemDao = AppDataBase.getInstance(context)?.mealItemDao()

    val meals = mealDao?.getAll()

    if (meals != null) {
        for(meal in meals){
            meal.listMealItems = mealItemDao?.getItemsFromMeal(meal.id)
        }
    }

    return meals
}

fun getAllMealItems(mode:Boolean = false, context: Context):List<MealItem>?{
    AppDataBase.TEST_MODE = mode
    val mealItemDao = AppDataBase.getInstance(context)?.mealItemDao()
    return mealItemDao?.getAll()
}

fun getAllMealsFromDate(dateCode:Long, mode:Boolean = false, context: Context):List<Meal>?{

    AppDataBase.TEST_MODE = mode
    val mealDao = AppDataBase.getInstance(context)?.mealDao()
    val mealItemDao = AppDataBase.getInstance(context)?.mealItemDao()

    val minTime = getBegDayDate(dateCode)
    val maxTime = getEndDayDate(dateCode)

    val meals = mealDao?.getMealsDate(minTime, maxTime)

    if (meals != null) {
        for(meal in meals){
            meal.listMealItems = mealItemDao?.getItemsFromMeal(meal.id)
        }
    }

    return meals
}

fun deleteMeal(idMeal:Long?, mode:Boolean = false, context: Context){

    AppDataBase.TEST_MODE = mode
    val mealDao = AppDataBase.getInstance(context)?.mealDao()
    val mealItemDao = AppDataBase.getInstance(context)?.mealItemDao()

    mealItemDao?.deleteItemsFromMeal(idMeal)
    mealDao?.deleteMeal(idMeal)
}

fun deleteAllMeals(mode:Boolean = false, context: Context){

    AppDataBase.TEST_MODE = mode
    val mealDao = AppDataBase.getInstance(context)?.mealDao()
    val mealItemDao = AppDataBase.getInstance(context)?.mealItemDao()

    mealItemDao?.deleteAll()
    mealDao?.deleteAll()
}

// ------------------------------------------------------------------------------------------------------------
// --------------------------------------------- CONSTANTS ----------------------------------------------------
// ------------------------------------------------------------------------------------------------------------


