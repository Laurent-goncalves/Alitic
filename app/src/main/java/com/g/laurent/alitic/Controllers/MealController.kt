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

fun getFoodsFromMeal(meal:Meal, context: Context):List<Food>{

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

fun getAllMeals(context: Context):List<Meal>?{
    AppDataBase.TEST_MODE = false
    val mealDao = AppDataBase.getInstance(context)?.mealDao()
    val mealItemDao = AppDataBase.getInstance(context)?.mealItemDao()

    var meals = mealDao?.getAll()

    if (meals != null) {
        for(meal in meals){
            meal.listMealItems = mealItemDao?.getItemsFromMeal(meal.id)
        }
    }

    return meals
}

fun deleteMeal(idMeal:Long?, context: Context){

    AppDataBase.TEST_MODE = false
    val mealDao = AppDataBase.getInstance(context)?.mealDao()
    val mealItemDao = AppDataBase.getInstance(context)?.mealItemDao()

    mealItemDao?.deleteItemsFromMeal(idMeal)
    mealDao?.deleteMeal(idMeal)
}

fun deleteAllMeals(context: Context){

    AppDataBase.TEST_MODE = false
    val mealDao = AppDataBase.getInstance(context)?.mealDao()
    val mealItemDao = AppDataBase.getInstance(context)?.mealItemDao()

    mealItemDao?.deleteAll()
    mealDao?.deleteAll()
}