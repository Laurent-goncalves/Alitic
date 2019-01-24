package com.g.laurent.alitic

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.g.laurent.alitic.Models.*
import org.junit.After
import org.junit.Assert
import org.junit.runner.RunWith
import org.junit.Before
import org.junit.Test


@RunWith(AndroidJUnit4::class)
class DaoTest {

    var mealDao: MealDao?= null
    var eventDao: EventDao?= null
    var mealItemDao: MealItemDao?= null
    var foodDao: FoodDao?= null
    var eventTypeDao: EventTypeDao?= null

    @Before
    fun setup() {
        AppDataBase.TEST_MODE = true
        foodDao = AppDataBase.getInstance(InstrumentationRegistry.getTargetContext())?.foodDao()
        eventTypeDao = AppDataBase.getInstance(InstrumentationRegistry.getTargetContext())?.eventTypeDao()
        eventDao = AppDataBase.getInstance(InstrumentationRegistry.getTargetContext())?.eventDao()
        mealItemDao = AppDataBase.getInstance(InstrumentationRegistry.getTargetContext())?.mealItemDao()
        mealDao = AppDataBase.getInstance(InstrumentationRegistry.getTargetContext())?.mealDao()
    }

    @After
    fun tearDown() {

    }

    // ---------------------------------------- TESTS MEALS

    @Test
    fun testFoodDAO(){

        var fruit = Food(0, "Banane", "Fruit")

        // ---------- INSERT
        foodDao?.insert(fruit)

        fruit.name = ""

        // ---------- GET
        var list:List<Food>? = foodDao?.getAll()

        if(list?.get(0)?.name == "Banane")
            fruit = list[0]

        Assert.assertEquals(fruit.name, "Banane")

        // ---------- UPDATE

        fruit.name = "Abricot"

        foodDao?.update(fruit)

        fruit.name = ""

        fruit = foodDao?.getFood(fruit.id)!!

        Assert.assertEquals(fruit.name, "Abricot")

        // ---------- DELETE
        foodDao?.deleteAll()

        list = foodDao?.getAll()
        Assert.assertEquals(list?.size, 0)
    }

    @Test
    fun testMealDAO(){

        var dateCode:Long = 222333444
        var meal = Meal(0, dateCode, null)

        // ---------- INSERT
        mealDao?.insert(meal)
        meal.dateCode = 0

        // ---------- GET
        var list:List<Meal>? = mealDao?.getAll()

        if (list != null) {
            for(m in list){
                if(m.dateCode == dateCode)
                    meal = m
            }
        }

        Assert.assertEquals(meal.dateCode, 222333444)

        // ---------- UPDATE

        dateCode= 111555222
        meal.dateCode = dateCode

        mealDao?.update(meal)
        meal.dateCode = 0

        list = mealDao?.getAll()

        if (list != null) {
            for(e in list)
                if(e.dateCode==dateCode)
                    meal = e
        }

        Assert.assertEquals(meal.dateCode, 111555222)

        // ---------- DELETE

        mealDao?.deleteAll()

        list = mealDao?.getAll()

        Assert.assertEquals(list?.size, 0)
    }

    @Test
    fun testMealItemDAO(){

        val idMeal = getIdMeal()
        val idFood = getIdFood()

        val mealItem = MealItem(0, idMeal, idFood)

        // ---------- INSERT
        mealItemDao?.insert(mealItem)

        // ---------- GET
        var list = mealItemDao?.getItemsFromMeal(idMeal)
        Assert.assertEquals(list?.size, 1)

        // ---------- DELETE

        mealItemDao?.deleteItemsFromMeal(idMeal)

        list = mealItemDao?.getItemsFromMeal(idMeal)

        Assert.assertEquals(list?.size, 0)
    }

    // ---------------------------------------- TESTS EVENTS

    @Test
    fun testEventNameDAO(){

        var eventName = EventType(0, "Reflux gastrique", 0,0)

        // ---------- INSERT
        eventTypeDao?.insert(eventName)
        eventName.name = ""

        // ---------- GET
        var list:List<EventType>? = eventTypeDao?.getAll()

        if(list?.get(0)?.name == "Reflux gastrique")
            eventName = list[0]

        Assert.assertEquals(eventName.name, "Reflux gastrique")

        // ---------- UPDATE

        eventName.name = "Digestion difficile"

        eventTypeDao?.update(eventName)
        eventName.name = ""

        eventName = eventTypeDao?.getEventType(eventName.id)!!

        Assert.assertEquals(eventName.name, "Digestion difficile")

        // ---------- DELETE

        eventTypeDao?.deleteEventType(eventName.id)

        list = eventTypeDao?.getAll()
        Assert.assertEquals(list?.size, 0)
    }

    @Test
    fun testEventDAO(){

        var event = Event(0, getIdEventName(), 2222255555)

        // ---------- INSERT
        eventDao?.insert(event)
        event.dateCode = 0

        // ---------- GET
        var list:List<Event>? = eventDao?.getAll()

        if (list != null) {
            for(e in list)
                if(e.idEventType == getIdEventName())
                    event = e
        }

        Assert.assertEquals(event.dateCode, 2222255555)

        // ---------- UPDATE

        event.dateCode = 11111444444

        eventDao?.update(event)
        event.dateCode = 0

        list = eventDao?.getEventsDate(11111444441,11111444445)!!

        for(e in list)
            if(e.idEventType == getIdEventName())
                event = e

        Assert.assertEquals(event.dateCode, 11111444444)

        // ---------- DELETE

        eventDao?.deleteEvent(event.id)

        list = eventDao?.getEventsDate(11111444441,11111444445)!!

        Assert.assertEquals(list.size, 0)
    }

    // ---------------------------------------- UTILS

    fun getIdEventName(): Long? {

        val eventName = EventType(0, "Reflux gastrique",0,0)
        eventTypeDao?.insert(eventName)
        val list:List<EventType>? = eventTypeDao?.getAll()

        if (list != null) {
            for(e in list){
                if(e.name == "Reflux gastrique")
                    return e.id
            }
        }
        return null
    }

    fun getIdMeal(): Long? {

        val idMeal:Long = 222333555
        val meal = Meal(0, idMeal, null)

        mealDao?.insert(meal)
        val list:List<Meal>? = mealDao?.getAll()

        if (list != null) {
            for(m in list){
                if(m.dateCode.equals(idMeal))
                    return m.id
            }
        }
        return null
    }

    fun getIdFood(): Long? {

        val food = Food(0, "Banane", "Fruit")
        foodDao?.insert(food)
        val list:List<Food>? = foodDao?.getAll()

        if (list != null) {
            for(fruit in list){
                if(fruit.name == "Banane")
                    return fruit.id
            }
        }
        return null
    }
}
