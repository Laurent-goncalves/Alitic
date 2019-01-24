package com.g.laurent.alitic

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.g.laurent.alitic.Controllers.*
import com.g.laurent.alitic.Models.*
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DBobjectsControllerTest {

    // ----------------------------------------------------------------------------------------------------------
    // ------------------------------------ FOOD CONTROLLER -----------------------------------------------------
    // ----------------------------------------------------------------------------------------------------------

    @Test
    fun testSaveGetDeleteFood() {

        // Check insertion and query
        val context = InstrumentationRegistry.getContext()
        deleteAllFood(true, context)

        val idFood1 = saveNewFood("Banane", "Fruit",true, context)
        saveNewFood("Cerise", "Fruit",true, context)
        saveNewFood("Salade", "Crudités",true, context)

        // Check search fruit by key word
        var food = getListFood("ba", true, context)
        Assert.assertEquals(1, food?.size)

        food = getListFood("ca", true, context)
        Assert.assertEquals(0, food?.size)

        // Check search fruit by type
        food = getListFoodByType("Fruit", true, context)
        Assert.assertEquals(2, food?.size)

        food = getListFoodByType("Vegetable", true, context)
        Assert.assertEquals(0, food?.size)

        // Check deletion 1 food
        deleteFood(idFood1, true, context)
        food = getAllFood(true, context)
        Assert.assertEquals(2, food?.size)

        // Check deletion all foods
        deleteAllFood( true, context)
        food = getAllFood(true, context)
        Assert.assertEquals(0, food?.size)
    }

    // ----------------------------------------------------------------------------------------------------------
    // ------------------------------------ MEAL CONTROLLER -----------------------------------------------------
    // ----------------------------------------------------------------------------------------------------------

    @Test
    fun testSaveGetDeleteMeal() {

        // Check insertion and query
        val context = InstrumentationRegistry.getContext()
        val idMeal = saveNewMeal(getListMealItems(context),1548273609584, context)
        var meal = getMealFromDatabase(idMeal, context)
        Assert.assertEquals(3, meal?.listMealItems?.size)

        // Check deletion
        deleteMeal(idMeal, context)
        meal = getMealFromDatabase(idMeal, context)
        Assert.assertEquals(null, meal?.listMealItems?.size)
    }

    fun getListMealItems(context: Context):List<MealItem>{

        val idFood1 = saveNewFood("Banane", "Fruit",true, context)
        val idFood2 = saveNewFood("Cerise", "Fruit",true, context)
        val idFood3 = saveNewFood("Salade", "Crudités",true, context)

        val list : MutableList<MealItem> = mutableListOf()
        list.add(MealItem(null,0,idFood1))
        list.add(MealItem(null,0,idFood2))
        list.add(MealItem(null,0,idFood3))

        return list
    }

    // ----------------------------------------------------------------------------------------------------------
    // ----------------------------------- EVENT CONTROLLER -----------------------------------------------------
    // ----------------------------------------------------------------------------------------------------------

    @Test
    fun test_save_event(){

        // Check insertion and query of event
        val context = InstrumentationRegistry.getTargetContext()
        saveEventsAndEventTypes(context)

        val list = getEventsFromDate(getDateAsLong(14,1,2019,12,0), true, context)
        Assert.assertEquals(2, list?.size)
    }

    @Test
    fun test_update_event_type(){

        val context = InstrumentationRegistry.getTargetContext()

        val eventTypeDao = AppDataBase.getInstance(InstrumentationRegistry.getTargetContext())?.eventTypeDao()
        eventTypeDao?.deleteAll()

        val id1 = saveNewEventType("Reflux", 0,23, true, context)

        updateEventType(id1, "Reflux gastrique", 1, 555, true, context)

        var list = getListEventType("reflux", true, context)
        Assert.assertEquals(1, list?.size)

        list = getListEventType("gast", true, context)
        Assert.assertEquals(1, list?.size)

        list = getListEventType("Refli", true, context)

        Assert.assertEquals(0, list?.size)
    }

    fun saveEventsAndEventTypes(context: Context){

        val id1 = saveNewEventType("Reflux", 0,23, true, context)
        val id2 = saveNewEventType("Mal au ventre", 6,12, true, context)
        val id3 = saveNewEventType("Mal à la tête", 7,24, true, context)

        saveNewEvent(idEventType = id1,
            dateCode = getDateAsLong(2,12,2018,12,0),
            mode = true, context = context)

        saveNewEvent(idEventType = id2,
            dateCode = getDateAsLong(12,12,2018,12,0),
            mode = true, context = context)

        saveNewEvent(idEventType = id3,
            dateCode = getDateAsLong(14,1,2019,11,0),
            mode = true, context = context)

        saveNewEvent(idEventType = id1,
            dateCode = getDateAsLong(14,1,2019,14,0),
            mode = true, context = context)

        saveNewEvent(idEventType = id3,
            dateCode = getDateAsLong(24,1,2019,12,0),
            mode = true, context = context)
    }
}