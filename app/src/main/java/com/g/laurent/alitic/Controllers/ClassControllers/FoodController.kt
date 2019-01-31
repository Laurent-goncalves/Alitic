package com.g.laurent.alitic.Controllers.ClassControllers

import android.content.Context
import com.g.laurent.alitic.Models.AppDataBase
import com.g.laurent.alitic.Models.Food
import com.g.laurent.alitic.Models.FoodType
import com.g.laurent.alitic.Models.Keyword

// ---------------------------------------------------------------------------------------------------------------
// ------------------------------------------- FOOD --------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------

fun saveNewFood(name:String, idFoodType:Long?, foodUrl:String?, mode:Boolean = false, context: Context):Long?{
    AppDataBase.TEST_MODE = mode
    val foodDao = AppDataBase.getInstance(context)?.foodDao()
    return foodDao?.insert(Food(null, name, idFoodType, 0, foodUrl))
}

fun getAllFood(mode:Boolean = false, context:Context):List<Food>?{
    AppDataBase.TEST_MODE = mode
    val foodDao = AppDataBase.getInstance(context)?.foodDao()
    return foodDao?.getAll()
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
    val search = "${word.toLowerCase()}%"

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

fun saveNewFoodType(name:String, foodTypeUrl:String?, mode:Boolean = false, context: Context):Long?{
    AppDataBase.TEST_MODE = mode
    val foodTypeDao = AppDataBase.getInstance(context)?.foodTypeDao()
    return foodTypeDao?.insert(FoodType(null, name, foodTypeUrl))
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
