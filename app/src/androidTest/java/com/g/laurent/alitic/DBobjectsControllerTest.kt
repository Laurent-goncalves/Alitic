package com.g.laurent.alitic

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.g.laurent.alitic.Controllers.ClassControllers.*
import com.g.laurent.alitic.Models.*
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DBobjectsControllerTest {

    @After
    fun tearDown() {
        AppDataBase.clearDatabase()
    }

    // ----------------------------------------------------------------------------------------------------------
    // ------------------------------------ FOOD CONTROLLER -----------------------------------------------------
    // ----------------------------------------------------------------------------------------------------------

    @Test
    fun testSaveGetDeleteFood() {

        // Check insertion
        val context = InstrumentationRegistry.getContext()
        AppDataBase.clearDatabase()
        val listIdFoods = getIds("food", context)

        var food = getAllFood(true, context)
        Assert.assertEquals(6, food?.size)

        // Test get food by type
        food = getListFoodByType(
            getFoodType(
                "Fruits",
                true,
                context
            )?.id, true, context
        )
        Assert.assertEquals(2, food?.size)

        food = getListFoodByType(
            getFoodType(
                "Légumes",
                true,
                context
            )?.id, true, context
        )
        Assert.assertEquals(2, food?.size)

        food = getListFoodByType(
            getFoodType(
                "Legumes",
                true,
                context
            )?.id, true, context
        )
        Assert.assertEquals(0, food?.size)

        // Check deletion 1 food
        deleteAllMeals(true, context)
        deleteAllKeywords(true, context)

        deleteFood(listIdFoods!![0], true, context)
        deleteFood(listIdFoods[4], true, context)
        food = getAllFood(true, context)
        Assert.assertEquals(4, food?.size)

        // Check deletion all foods
        deleteAllFood(true, context)
        food = getAllFood(true, context)
        Assert.assertEquals(0, food?.size)
    }

    @Test
    fun testSaveGetDeleteFoodTypes() {

        // Check insertion
        val context = InstrumentationRegistry.getContext()
        AppDataBase.clearDatabase()
        val listIdFoods = getIds("foodtype", context)

        var foodtypes = getAllFoodTypes(true, context)
        Assert.assertEquals(4, foodtypes?.size)

        // Test get foodtype by id
        var foodtype = getFoodType(listIdFoods!![0], true, context)
        Assert.assertEquals("Fruits", foodtype?.name)

        // Test get foodtype by name
        foodtype = getFoodType("Légumes", true, context)
        Assert.assertEquals(listIdFoods[2], foodtype?.id)

        // Check deletion 1 foodtype
        deleteFoodType(listIdFoods[0], true, context)
        deleteFoodType(listIdFoods[1], true, context)
        foodtypes = getAllFoodTypes(true, context)
        Assert.assertEquals(2, foodtypes?.size)

        // Check deletion all foodtypes
        deleteAllFoodType(true, context)
        foodtypes = getAllFoodTypes(true, context)
        Assert.assertEquals(0, foodtypes?.size)
    }

    @Test
    fun testSearchFood() {

        // Check insertion
        val context = InstrumentationRegistry.getContext()
        AppDataBase.clearDatabase()
        getIds("food", context) // save food and keywords

        var list = getListFood("banane", true, context)
        Assert.assertEquals(1, list?.size)

        list = getListFood("laitue", true, context)
        Assert.assertEquals("Salade", list!![0].name)
        Assert.assertEquals(1, list.size)

        list = getListFood("yao", true, context)
        Assert.assertEquals("Yaourt", list!![0].name)
        Assert.assertEquals(1, list.size)

        list = getListFood("di", true, context)
        Assert.assertEquals(0, list?.size)
    }

    // ----------------------------------------------------------------------------------------------------------
    // ------------------------------------ MEAL CONTROLLER -----------------------------------------------------
    // ----------------------------------------------------------------------------------------------------------

    @Test
    fun testSaveGetDeleteMeal() {

        // Check insertion and query
        val context = InstrumentationRegistry.getContext()
        AppDataBase.clearDatabase()
        val listMealsId = getIds("meal", context) // save meal

        var listMeals = getAllMeals(true, context)
        Assert.assertEquals(4, listMeals?.size)

        // Test get meal with id
        var meal =
            getMealFromDatabase(listMealsId!![0], true, context)
        Assert.assertEquals(4, meal?.listMealItems?.size)

        meal = getMealFromDatabase(listMealsId[1], true, context)
        Assert.assertEquals(4, meal?.listMealItems?.size)

        // Test get list foods from meal
        val listFoods =
            getFoodsFromMeal(listMeals!![0], true, context)
        Assert.assertEquals("Banane", listFoods[0].name)
        Assert.assertEquals(4, listFoods.size)

        // Check deletion 1 meal
        deleteMeal(listMeals[0].id, true, context)
        listMeals = getAllMeals(true, context)
        Assert.assertEquals(3, listMeals?.size)

        // Check deletion all meals
        deleteAllMeals(true, context)
        listMeals = getAllMeals(true, context)
        Assert.assertEquals(0, listMeals?.size)

        val listMealsItems = getAllMealItems(true, context)
        Assert.assertEquals(0, listMealsItems?.size)
    }

    // ----------------------------------------------------------------------------------------------------------
    // ----------------------------------- EVENT CONTROLLER -----------------------------------------------------
    // ----------------------------------------------------------------------------------------------------------

    @Test
    fun testSaveGetDeleteEvent(){

        // Check insertion and query of event
        val context = InstrumentationRegistry.getTargetContext()
        AppDataBase.clearDatabase()
        val listIds = getIds("event", context)

        var listEvents = getAllEvents(true, context)
        Assert.assertEquals(5, listEvents?.size)

        // Check get for one eventType
        listEvents =
            getListEventForOneEventType(listIds!![0], true, context)
        Assert.assertEquals(2, listEvents?.size)

        // Check get events for period of time
        listEvents = getEventsFromDate(
            getDateAsLong(1, 1, 2019, 12, 0),
            true,
            context
        )
        Assert.assertEquals(0, listEvents?.size)

        listEvents = getEventsFromDate(
            getDateAsLong(14, 1, 2019, 12, 0),
            true,
            context
        )
        Assert.assertEquals(2, listEvents?.size)

        // Update event
        val event = listEvents?.get(0)
        event?.dateCode = getDateAsLong(15,1,2019,12,0)
        updateEvent(event!!, true, context)

        listEvents = getEventsFromDate(
            getDateAsLong(14, 1, 2019, 12, 0),
            true,
            context
        )
        Assert.assertEquals(1, listEvents?.size)

        listEvents = getEventsFromDate(
            getDateAsLong(15, 1, 2019, 12, 0),
            true,
            context
        )
        Assert.assertEquals(1, listEvents?.size)

        // Delete 1 event
        deleteEvent(listIds[3], true, context)
        listEvents = getAllEvents(true, context)
        Assert.assertEquals(4, listEvents?.size)

        // Delete all events
        deleteAllEvents(true, context)
        listEvents = getAllEvents(true, context)
        Assert.assertEquals(0, listEvents?.size)
    }

    @Test
    fun testSaveGetDeleteEventTypes(){

        // Check insertion and query of eventTypes
        AppDataBase.clearDatabase()
        val context = InstrumentationRegistry.getTargetContext()
        val listIds = getIds("event", context)

        var listEventTypes = getAllEventTypes(true, context)
        Assert.assertEquals(3, listEventTypes?.size)

        // Check get 1 eventType
        Assert.assertEquals("Reflux", getEventType(
            listIds!![0],
            true,
            context
        )?.name)

        // Update
        val eventType = getEventType(listIds!![0], true, context)
        eventType?.name = "Tout reflux"
        updateEventType(eventType, true, context)
        Assert.assertEquals("Tout reflux", getEventType(
            listIds!![0],
            true,
            context
        )?.name)

        // Delete all events
        deleteAllEvents(true, context)

        // Delete one eventType
        deleteEventType(listIds!![0], true, context)
        listEventTypes = getAllEventTypes(true, context)
        Assert.assertEquals(2, listEventTypes?.size)

        // Delete all eventTypes
        deleteAllEventTypes(true, context)
        listEventTypes = getAllEventTypes(true, context)
        Assert.assertEquals(0, listEventTypes?.size)
    }

    // ----------------------------------------------------------------------------------------------------------
    // ------------------------------------ STAT CONTROLLER -----------------------------------------------------
    // ----------------------------------------------------------------------------------------------------------

    @Test
    fun test_stat() {

        // Initialize database and fill with values
        val context = InstrumentationRegistry.getTargetContext()
        AppDataBase.clearDatabase()
        getIds("meal", context)
        val listIdEvents = getIds("event", context)

        val eventType = getEventType(listIdEvents!![0], true, context)
        val list:List<StatEntry> = getListFoodForEventType(eventType!!, true, context)

        Assert.assertEquals(list[0].food,"Banane")
        Assert.assertEquals(list[1].food,"Poulet")
        Assert.assertEquals(list[2].food,"Salade")
        Assert.assertEquals(list[3].food,"Abricot")
    }

    fun getIds(type:String, context: Context):List<Long?>?{

        when(type){

            "food" -> {

                val idFoodType1 =
                    saveNewFoodType("Fruits", null, true, context)
                val idFoodType2 =
                    saveNewFoodType("Viandes", null, true, context)
                val idFoodType3 =
                    saveNewFoodType("Légumes", null, true, context)
                val idFoodType4 = saveNewFoodType(
                    "Produits laitiers",
                    null,
                    true,
                    context)

                val idFood1 = saveNewFood(
                    "Banane",
                    idFoodType1,
                    null,
                    true,
                    context
                )
                val idFood2 = saveNewFood(
                    "Poulet",
                    idFoodType2,
                    null,
                    true,
                    context
                )
                val idFood3 = saveNewFood(
                    "Salade",
                    idFoodType3,
                    null,
                    true,
                    context
                )
                val idFood4 = saveNewFood(
                    "Yaourt",
                    idFoodType4,
                    null,
                    true,
                    context
                )
                val idFood5 = saveNewFood(
                    "Radis",
                    idFoodType3,
                    null,
                    true,
                    context
                )
                val idFood6 = saveNewFood(
                    "Abricot",
                    idFoodType1,
                    null,
                    true,
                    context
                )

                val keyword1 =
                    saveKeyword("laitue", idFood3, true, context)
                val keyword2 =
                    saveKeyword("salade", idFood3, true, context)
                val keyword3 =
                    saveKeyword("yaourt", idFood4, true, context)
                val keyword4 =
                    saveKeyword("radis", idFood5, true, context)
                val keyword5 =
                    saveKeyword("banane", idFood1, true, context)

                return listOf(idFood1,idFood2,idFood3,idFood4,idFood5,idFood6)
            }

            "foodtype" -> {
                return listOf(
                    saveNewFoodType("Fruits", null, true, context),
                    saveNewFoodType("Viandes", null, true, context),
                    saveNewFoodType("Légumes", null, true, context),
                    saveNewFoodType(
                        "Produits laitiers",
                        null,
                        true,
                        context
                    )
                )
            }

            "meal" -> {

                val idFoodType1 =
                    saveNewFoodType("Fruits", null, true, context)
                val idFoodType2 =
                    saveNewFoodType("Viandes", null, true, context)
                val idFoodType3 =
                    saveNewFoodType("Légumes", null, true, context)
                val idFoodType4 = saveNewFoodType(
                    "Produits laitiers",
                    null,
                    true,
                    context
                )

                val idFood1 = saveNewFood(
                    "Banane",
                    idFoodType1,
                    null,
                    true,
                    context
                )
                val idFood2 = saveNewFood(
                    "Poulet",
                    idFoodType2,
                    null,
                    true,
                    context
                )
                val idFood3 = saveNewFood(
                    "Salade",
                    idFoodType3,
                    null,
                    true,
                    context
                )
                val idFood4 = saveNewFood(
                    "Yaourt",
                    idFoodType4,
                    null,
                    true,
                    context
                )
                val idFood5 = saveNewFood(
                    "Radis",
                    idFoodType3,
                    null,
                    true,
                    context
                )
                val idFood6 = saveNewFood(
                    "Abricot",
                    idFoodType1,
                    null,
                    true,
                    context
                )

                val list1 : MutableList<MealItem> = mutableListOf()
                list1.add(MealItem(null,0,idFood1))
                list1.add(MealItem(null,0,idFood2))
                list1.add(MealItem(null,0,idFood3))

                val list2 : MutableList<MealItem> = mutableListOf()
                list2.add(MealItem(null,0,idFood1))
                list2.add(MealItem(null,0,idFood2))
                list2.add(MealItem(null,0,idFood3))
                list2.add(MealItem(null,0,idFood4))

                val list3 : MutableList<MealItem> = mutableListOf()
                list3.add(MealItem(null,0,idFood5))
                list3.add(MealItem(null,0,idFood6))

                val list4 : MutableList<MealItem> = mutableListOf()
                list4.add(MealItem(null,0,idFood1))
                list4.add(MealItem(null,0,idFood1))
                list4.add(MealItem(null,0,idFood1))
                list4.add(MealItem(null,0,idFood2))
                list4.add(MealItem(null,0,idFood2))
                list1.add(MealItem(null,0,idFood3))
                list4.add(MealItem(null,0,idFood6))

                val idMeal1 = saveNewMeal(
                    list1,
                    getDateAsLong(2, 12, 2018, 12, 0),
                    true,
                    context
                )
                val idMeal2 = saveNewMeal(
                    list2,
                    getDateAsLong(12, 12, 2018, 10, 0),
                    true,
                    context
                )
                val idMeal3 = saveNewMeal(
                    list3,
                    getDateAsLong(14, 1, 2019, 14, 0),
                    true,
                    context
                )
                val idMeal4 = saveNewMeal(
                    list4,
                    getDateAsLong(14, 1, 2019, 10, 0),
                    true,
                    context
                )

                return listOf(idMeal1, idMeal2, idMeal3, idMeal4)
            }

            "event" ->{

                val id1 = saveNewEventType(
                    "Reflux",
                    null,
                    0,
                    3 * 60 * 60 * 1000,
                    true,
                    context
                )
                val id2 = saveNewEventType(
                    "Mal au ventre",
                    null,
                    0,
                    6 * 60 * 60 * 1000,
                    true,
                    context
                )
                val id3 = saveNewEventType(
                    "Mal à la tête",
                    null,
                    0,
                    7 * 60 * 60 * 1000,
                    true,
                    context
                )

                val idEvent1 = saveNewEvent(
                    idEventType = id1,
                    dateCode = getDateAsLong(2, 12, 2018, 12, 0),
                    mode = true,
                    context = context
                )
                val idEvent2 = saveNewEvent(
                    idEventType = id2,
                    dateCode = getDateAsLong(12, 12, 2018, 12, 0),
                    mode = true,
                    context = context
                )
                val idEvent3 = saveNewEvent(
                    idEventType = id3,
                    dateCode = getDateAsLong(14, 1, 2019, 11, 0),
                    mode = true,
                    context = context
                )
                val idEvent4 = saveNewEvent(
                    idEventType = id1,
                    dateCode = getDateAsLong(14, 1, 2019, 12, 0),
                    mode = true,
                    context = context
                )
                val idEvent5 = saveNewEvent(
                    idEventType = id3,
                    dateCode = getDateAsLong(24, 1, 2019, 12, 0),
                    mode = true,
                    context = context
                )

                return listOf(id1, id2, id3, idEvent1, idEvent2, idEvent3, idEvent4, idEvent5)
            }
        }

        return null
    }
}