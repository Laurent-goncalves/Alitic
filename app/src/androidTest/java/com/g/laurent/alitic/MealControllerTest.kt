package com.g.laurent.alitic

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.g.laurent.alitic.Controllers.getMealFromDatabase
import com.g.laurent.alitic.Controllers.saveNewMeal
import com.g.laurent.alitic.Models.*
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MealControllerTest {

    var foodDao: FoodDao?= null

    @Before
    fun setup() {
        AppDataBase.TEST_MODE = true
        foodDao = AppDataBase.getInstance(InstrumentationRegistry.getTargetContext())?.foodDao()
    }

    @After
    fun tearDown() {

    }

    @Test
    fun testSaveAndGetMeal() {
        val idMeal = saveNewMeal(getListMealItems(),1548273609584, InstrumentationRegistry.getContext())
        val meal = getMealFromDatabase(idMeal, InstrumentationRegistry.getContext())
        Assert.assertEquals(3, meal?.listMealItems?.size)
    }

    fun getListMealItems():List<MealItem>{

        val idFood1 = foodDao?.insert(Food(null, "Banane", "Fruit"))
        val idFood2 = foodDao?.insert(Food(null, "Cerise", "Fruit"))
        val idFood3 = foodDao?.insert(Food(null, "Salade", "Crudités"))

        val list : MutableList<MealItem> = mutableListOf()
        list.add(MealItem(null,0,idFood1))
        list.add(MealItem(null,0,idFood2))
        list.add(MealItem(null,0,idFood3))

        return list
    }
}