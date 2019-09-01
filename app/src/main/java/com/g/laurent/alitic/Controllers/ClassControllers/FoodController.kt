package com.g.laurent.alitic.Controllers.ClassControllers

import android.content.Context
import com.g.laurent.alitic.Models.*
import java.util.*

// ---------------------------------------------------------------------------------------------------------------
// ------------------------------------------- FOOD --------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------

fun saveNewFood(name:String?, idFoodType:Long?, foodUrl:String?, forAnalysis:Boolean, mode:Boolean = false, context: Context):Long?{
    AppDataBase.TEST_MODE = mode
    val foodDao = AppDataBase.getInstance(context)?.foodDao()
    return foodDao?.insert(Food(null, name, idFoodType, 0, foodUrl, forAnalysis))
}

fun getAllFood(mode:Boolean = false, context:Context):List<Food>?{
    AppDataBase.TEST_MODE = mode
    val foodDao = AppDataBase.getInstance(context)?.foodDao()
    return foodDao?.getAll()
}

fun getFood(idFood:Long?, mode:Boolean = false, context: Context):Food?{
    AppDataBase.TEST_MODE = mode
    val foodDao = AppDataBase.getInstance(context)?.foodDao()
    return foodDao?.getFood(idFood)
}

fun getListFoodByType(idFoodType:Long?, mode:Boolean = false, context:Context):List<Food>?{
    AppDataBase.TEST_MODE = mode
    val foodDao = AppDataBase.getInstance(context)?.foodDao()
    return foodDao?.getFoodByType(idFoodType)
}

fun getListFood(word:String, mode:Boolean = false, context:Context):List<Food>?{

    AppDataBase.TEST_MODE = mode
    val foodDao = AppDataBase.getInstance(context)?.foodDao()
    val keywordDao = AppDataBase.getInstance(context)?.keywordDao()
    val search = "${word.toLowerCase(Locale.FRANCE)}%"

    if(foodDao!=null) {

        val result: MutableList<Food> = foodDao.getListFoodSearch(search).toMutableList()

        val listKeyword = keywordDao?.getKeywords(search)

        // get all foods for each keyword
        if (listKeyword != null) {
            for (key in listKeyword) {
                val food = foodDao.getFood(key.idFood)
                if (food != null) {
                    if (result.indexOf(food)==-1)
                        result.add(food)
                }
            }
        }

        return result.toList()
    } else
        return null
}

fun ignoreFood(idFood: Long?, mode:Boolean = false, context: Context){
    AppDataBase.TEST_MODE = mode
    val foodDao = AppDataBase.getInstance(context)?.foodDao()
    val food = foodDao?.getFood(idFood)
    if(food!=null){
        food.forAnalysis = false
        updateFood(food, mode, context)
    }
}

fun updateFood(food:Food, mode:Boolean = false, context: Context){
    AppDataBase.TEST_MODE = mode
    val foodDao = AppDataBase.getInstance(context)?.foodDao()
    foodDao?.update(food)
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

// ---------------------------------------------------------------------------------------------------------------
// ----------------------------------------- FOODTYPE ------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------

fun saveNewFoodType(name:String, foodTypeUrl:String?, foodColor:Int, mode:Boolean = false, context: Context):Long?{
    AppDataBase.TEST_MODE = mode
    val foodTypeDao = AppDataBase.getInstance(context)?.foodTypeDao()
    return foodTypeDao?.insert(FoodType(null, name, foodTypeUrl, foodColor))
}

fun getAllFoodTypes(mode:Boolean = false, context:Context):List<FoodType>?{
    AppDataBase.TEST_MODE = mode
    val foodTypeDao = AppDataBase.getInstance(context)?.foodTypeDao()
    return foodTypeDao?.getAll()
}

fun getFoodType(idFoodType:Long?, mode:Boolean = false, context:Context):FoodType?{
    AppDataBase.TEST_MODE = mode
    val foodTypeDao = AppDataBase.getInstance(context)?.foodTypeDao()
    return foodTypeDao?.getFoodType(idFoodType)
}

fun getFoodType(nameFoodType:String?, mode:Boolean = false, context:Context):FoodType?{
    AppDataBase.TEST_MODE = mode
    val foodTypeDao = AppDataBase.getInstance(context)?.foodTypeDao()
    return foodTypeDao?.getFoodType(nameFoodType)
}

fun deleteFoodType(idFoodType:Long?, mode:Boolean = false, context: Context){
    AppDataBase.TEST_MODE = mode
    val foodTypeDao = AppDataBase.getInstance(context)?.foodTypeDao()
    foodTypeDao?.deleteFoodType(idFoodType)
}

fun deleteAllFoodType(mode:Boolean = false, context: Context){
    AppDataBase.TEST_MODE = mode
    val foodTypeDao = AppDataBase.getInstance(context)?.foodTypeDao()
    foodTypeDao?.deleteAll()
}

// ---------------------------------------------------------------------------------------------------------------
// ----------------------------------------- KEYWORDS ------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------

fun saveKeyword(name:String, idFood:Long?, mode:Boolean = false, context: Context):Long?{
    AppDataBase.TEST_MODE = mode
    val keywordDao = AppDataBase.getInstance(context)?.keywordDao()
    return keywordDao?.insert(Keyword(null, name, idFood))
}

fun deleteAllKeywords(mode:Boolean = false, context: Context){
    AppDataBase.TEST_MODE = mode
    val keywordDao = AppDataBase.getInstance(context)?.keywordDao()
    keywordDao?.deleteAll()
}
