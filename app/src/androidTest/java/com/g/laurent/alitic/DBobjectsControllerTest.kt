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

    @Before
    fun setup() {
        AppDataBase.clearDatabase()
    }

    @After
    fun tearDown() {
        AppDataBase.clearDatabase()
    }

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

    fun saveEventsAndEventTypes(context: Context):Long?{

        val id1 = saveNewEventType("Reflux", 0,3*60*60*1000, true, context)
        val id2 = saveNewEventType("Mal au ventre", 0,6*60*60*1000, true, context)
        val id3 = saveNewEventType("Mal à la tête", 0,7*60*60*1000, true, context)

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

        return id1
    }

    // ----------------------------------------------------------------------------------------------------------
    // ------------------------------------ STAT CONTROLLER -----------------------------------------------------
    // ----------------------------------------------------------------------------------------------------------

    @Test
    fun test_stat() {

        // Initialize database and fill with values
        AppDataBase.clearDatabase()
        val context = InstrumentationRegistry.getTargetContext()
        val id1 = saveFullListMeal(context)
        val eventType:EventType? = getEventType(id1, true, context)
        var list:List<StatList> = mutableListOf()

        if(eventType!=null)
            list = getListFoodForEventType(eventType, context)

        Assert.assertEquals(list[0].food, "Abricot")
        Assert.assertEquals(list[1].food, "Salade")
        Assert.assertTrue(list[2].food == "Banane" || list[2].food == "Gateau")
        Assert.assertTrue(list[3].food == "Banane" || list[3].food == "Gateau")
    }

    fun saveFullListMeal(context: Context):Long?{

        val id1 = saveEventsAndEventTypes(context)

        val idFood1 = saveNewFood("Banane", "Fruit",true, context)
        val idFood2 = saveNewFood("Poulet", "Viande",true, context)
        val idFood3 = saveNewFood("Salade", "Crudités",true, context)
        val idFood4 = saveNewFood("Yaourt", "Laitage",true, context)
        val idFood5 = saveNewFood("Gateau", "Patisserie",true, context)
        val idFood6 = saveNewFood("Abricot", "Fruit",true, context)

        val time1 = getDateAsLong(2,12,2018,1,0)
        val time2 = getDateAsLong(12,12,2018,11,0)
        val time3 = getDateAsLong(2,12,2018,10,0)
        val time4 = getDateAsLong(14,1,2019,11,30)

        val list1 : MutableList<MealItem> = mutableListOf()
        list1.add(MealItem(null,0,idFood1))
        list1.add(MealItem(null,0,idFood2))
        list1.add(MealItem(null,0,idFood3))

        val list2 : MutableList<MealItem> = mutableListOf()
        list2.add(MealItem(null,0,idFood4))
        list2.add(MealItem(null,0,idFood1))
        list2.add(MealItem(null,0,idFood3))

        val list3 : MutableList<MealItem> = mutableListOf()
        list3.add(MealItem(null,0,idFood5))
        list3.add(MealItem(null,0,idFood3))
        list3.add(MealItem(null,0,idFood6))

        val list4 : MutableList<MealItem> = mutableListOf()
        list4.add(MealItem(null,0,idFood1))
        list4.add(MealItem(null,0,idFood3))
        list4.add(MealItem(null,0,idFood6))
        list4.add(MealItem(null,0,idFood6))

        saveNewMeal(list1,time1, context)
        saveNewMeal(list2,time2, context)
        saveNewMeal(list3,time3, context)
        saveNewMeal(list4,time4, context)

        return id1
    }
}