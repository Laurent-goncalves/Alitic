package com.g.laurent.alitic

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.g.laurent.alitic.Controllers.Activities.StatType
import com.g.laurent.alitic.Controllers.ClassControllers.*
import com.g.laurent.alitic.Models.AppDataBase
import com.g.laurent.alitic.Models.MealItem
import org.junit.After
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.text.DecimalFormat

/**   OK    */
@RunWith(AndroidJUnit4::class)
class StatTest {

    @After
    fun tearDown() {
        AppDataBase.clearDatabase()
    }

    /**   OK    */
    @Test
    fun test_food_stat() {

        // Initialize database and fill with values
        val context = InstrumentationRegistry.getTargetContext()
        AppDataBase.clearDatabase()
        getIds("meal", context)
        val listIds = getListEvents(context)
        var list = getFoodStat(StatType.GLOBAL_ANALYSIS_NEG, null, true, context)

        Assert.assertTrue(list.size==6)

        for(value in list){
            when(value.food.name){
                "Salade" -> { Assert.assertTrue(value.counter.countOK == 1 && value.counter.countNOK==3)}
                "Abricot" -> { Assert.assertTrue(value.counter.countOK == 1 && value.counter.countNOK==2)}
                "Banane" -> { Assert.assertTrue(value.counter.countOK == 0 && value.counter.countNOK==3)}
                "Radis" -> { Assert.assertTrue(value.counter.countOK == 2 && value.counter.countNOK==1)}
                "Poulet" -> { Assert.assertTrue(value.counter.countOK == 1 && value.counter.countNOK==3)}
                "Yaourt" -> { Assert.assertTrue(value.counter.countOK == 2 && value.counter.countNOK==1)}
            }
        }

        list = getFoodStat(StatType.GLOBAL_ANALYSIS_POS, null, true, context)

        for(value in list){
            when(value.food.name){
                "Salade" -> { Assert.assertTrue(value.counter.countOK == 1 && value.counter.countNOK==3)}
                "Abricot" -> { Assert.assertTrue(value.counter.countOK == 1 && value.counter.countNOK==2)}
                "Banane" -> { Assert.assertTrue(value.counter.countOK == 0 && value.counter.countNOK==3)}
                "Radis" -> { Assert.assertTrue(value.counter.countOK == 2 && value.counter.countNOK==1)}
                "Poulet" -> { Assert.assertTrue(value.counter.countOK == 1 && value.counter.countNOK==3)}
                "Yaourt" -> { Assert.assertTrue(value.counter.countOK == 2 && value.counter.countNOK==1)}
            }
        }

        var eventType = getEventType(listIds!![0], true, context)
        list = getFoodStat(StatType.DETAIL_ANALYSIS, eventType, true, context)

        for(value in list){
            when(value.food.name){
                "Salade" -> { Assert.assertTrue(value.counter.countOK == 1 && value.counter.countNOK==3)}
                "Abricot" -> { Assert.assertTrue(value.counter.countOK == 1 && value.counter.countNOK==2)}
                "Banane" -> { Assert.assertTrue(value.counter.countOK == 0 && value.counter.countNOK==3)}
                "Radis" -> { Assert.assertTrue(value.counter.countOK == 2 && value.counter.countNOK==1)}
                "Poulet" -> { Assert.assertTrue(value.counter.countOK == 1 && value.counter.countNOK==3)}
                "Yaourt" -> { Assert.assertTrue(value.counter.countOK == 2 && value.counter.countNOK==1)}
            }
        }

        eventType = getEventType(listIds[1], true, context)
        list = getFoodStat(StatType.DETAIL_ANALYSIS, eventType, true, context)

        for(value in list){
            when(value.food.name){
                "Salade" -> { Assert.assertTrue(value.counter.countOK == 3 && value.counter.countNOK==1)}
                "Abricot" -> { Assert.assertTrue(value.counter.countOK == 2 && value.counter.countNOK==1)}
                "Banane" -> { Assert.assertTrue(value.counter.countOK == 2 && value.counter.countNOK==1)}
                "Radis" -> { Assert.assertTrue(value.counter.countOK == 2 && value.counter.countNOK==1)}
                "Poulet" -> { Assert.assertTrue(value.counter.countOK == 3 && value.counter.countNOK==1)}
                "Yaourt" -> { Assert.assertTrue(value.counter.countOK == 2 && value.counter.countNOK==1)}
            }
        }
    }

    /**   OK    */
    @Test
    fun test_foodtype_stat(){

        val context = InstrumentationRegistry.getTargetContext()
        getIds("meal", context)
        val listIdEvents = getListEvents(context)
        val df = DecimalFormat("#.###")

        // Get food stat for all eventTypes for negative food
        var result = getListFoodTypesStats(StatType.GLOBAL_ANALYSIS_NEG, null, true, context)

        Assert.assertEquals(4, result.size)

        for(value in result){
            when(value.foodType.name){
                "Légumes" -> {
                    Assert.assertEquals("0.308", df.format(value.ratio))
                }
                "Viandes" -> {
                    Assert.assertEquals("0.231", df.format(value.ratio))
                }
                "Fruits" -> {
                    Assert.assertEquals("0.385", df.format(value.ratio))
                }
                "Produits laitiers" -> {
                    Assert.assertEquals("0.077", df.format(value.ratio))
                }
            }
        }

        // Get food stat for all eventTypes for positive food
        result = getListFoodTypesStats(StatType.GLOBAL_ANALYSIS_POS, null, true, context)

        Assert.assertEquals(4, result.size)

        for(value in result){
            when(value.foodType.name){
                "Légumes" -> {
                    Assert.assertEquals("0.429", df.format(value.ratio))
                }
                "Viandes" -> {
                    Assert.assertEquals("0.143", df.format(value.ratio))
                }
                "Fruits" -> {
                    Assert.assertEquals("0.143", df.format(value.ratio))
                }
                "Produits laitiers" -> {
                    Assert.assertEquals("0.286", df.format(value.ratio))
                }
            }
        }

        // Get food stat for one eventType
        val idEventType = listIdEvents!![1]
        val eventtype = getEventType(idEventType,true, context)
        result = getListFoodTypesStats(StatType.DETAIL_ANALYSIS, eventtype!!, true, context)

        for(value in result){
            when(value.foodType.name){
                "Légumes" -> {
                    Assert.assertEquals("0.333", df.format(value.ratio))
                }
                "Viandes" -> {
                    Assert.assertEquals("0.167", df.format(value.ratio))
                }
                "Fruits" -> {
                    Assert.assertEquals("0.333", df.format(value.ratio))
                }
                "Produits laitiers" -> {
                    Assert.assertEquals("0.167", df.format(value.ratio))
                }
            }
        }
    }

    /**   OK    */
    @Test
    fun test_min_max_event_meal(){

        val context = InstrumentationRegistry.getTargetContext()
        val listIdMeals = getIds("meal", context)
        val listIdEvents = getIds("event", context)

        val dateEmin = getDateAsLong(2, 12, 2018, 12, 30)
        val dateEmax = getDateAsLong(14, 1, 2019, 15, 30)
        val dateMmin = getDateAsLong(2, 12, 2018, 12, 0)
        val dateMmax = getDateAsLong(14, 1, 2019, 14, 0)

        val test1 = getOldestEvent(true,context)
        val test2 = getLatestEvent(true,context)
        val test3= getOldestMeal(true,context)
        val test4 = getLatestMeal(true,context)

        if(test1!=null && test2!=null && test3!=null && test4!=null){

            assertTrue(test1 in dateEmin-1000L .. dateEmin+1000L)
            assertTrue(test2 in dateEmax-1000L .. dateEmax+1000L)
            assertTrue(test3 in dateMmin-1000L .. dateMmin+1000L)
            assertTrue(test4 in dateMmax-1000L .. dateMmax+1000L)
        } else
            assertTrue(false)

        assertTrue(getMinTime(test1, test3) == test3)
        assertTrue(getMaxTime(test2, test4) == test2)
        assertTrue(getMinTime(null, test3) == test3)
        assertTrue(getMaxTime(null, test4) == test4)
        assertTrue(getMinTime(null, null) == null)
        assertTrue(getMaxTime(null, null) == null)
        assertTrue(getMinTime(test1, null) == test1)
        assertTrue(getMaxTime(test2, null) == test2)
    }

    /**   OK    */
    @Test
    fun test_evolution(){

        val context = InstrumentationRegistry.getTargetContext()
        getIds("meal", context)
        val listIdEvents = getListEvents(context)

        // Get evolution for one eventType
        var eventType = getEventType(listIdEvents!![0],true,context)
        var listDates = getChronologyEvents(eventType!!,true,context)
        var evolution = getEvolution(listDates, true, context)

        assertEquals(4, listDates.size)
        assertEquals(Evolution.NEGATIVE, evolution)

        eventType = getEventType(listIdEvents!![1],true,context)
        listDates = getChronologyEvents(eventType!!,true,context)
        evolution = getEvolution(listDates, true, context)

        assertEquals(2, listDates.size)
        assertEquals(Evolution.NEGATIVE, evolution)

        // Get evolution for all eventTypes
        listDates = getChronologyEvents(null,true,context)
        evolution = getEvolution(listDates, true, context)

        assertEquals(6, listDates.size)
        assertEquals(Evolution.NEGATIVE, evolution)
    }

    fun getIds(type:String, context: Context):List<Long?>?{

        when(type){

            "food" -> {

                val idFoodType1 =
                    saveNewFoodType("Fruits", null, R.color.colorFoodPicked,true, context)
                val idFoodType2 =
                    saveNewFoodType("Viandes", null, R.color.colorFoodPicked,true, context)
                val idFoodType3 =
                    saveNewFoodType("Légumes", null, R.color.colorFoodPicked,true, context)
                val idFoodType4 = saveNewFoodType(
                    "Produits laitiers",
                    null,R.color.colorFoodPicked,
                    true,
                    context)

                val idFood1 = saveNewFood(
                    "Banane",
                    idFoodType1,
                    null,
                    true,true,
                    context
                )
                val idFood2 = saveNewFood(
                    "Poulet",
                    idFoodType2,
                    null,
                    true,true,
                    context
                )
                val idFood3 = saveNewFood(
                    "Salade",
                    idFoodType3,
                    null,
                    true,true,
                    context
                )
                val idFood4 = saveNewFood(
                    "Yaourt",
                    idFoodType4,
                    null,
                    true,true,
                    context
                )
                val idFood5 = saveNewFood(
                    "Radis",
                    idFoodType3,
                    null,
                    true,true,
                    context
                )
                val idFood6 = saveNewFood(
                    "Abricot",
                    idFoodType1,
                    null,
                    true,true,
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
                    saveNewFoodType("Fruits", null, R.color.colorFoodPicked,true, context),
                    saveNewFoodType("Viandes", null, R.color.colorFoodPicked,true, context),
                    saveNewFoodType("Légumes", null, R.color.colorFoodPicked,true, context),
                    saveNewFoodType(
                        "Produits laitiers",
                        null,R.color.colorFoodPicked,
                        true,
                        context
                    )
                )
            }

            "meal" -> {

                val idFoodType1 =
                    saveNewFoodType("Fruits", null, R.color.colorFoodPicked,true, context)
                val idFoodType2 =
                    saveNewFoodType("Viandes", null, R.color.colorFoodPicked,true, context)
                val idFoodType3 =
                    saveNewFoodType("Légumes", null, R.color.colorFoodPicked,true, context)
                val idFoodType4 =
                    saveNewFoodType("Produits laitiers",null,R.color.colorFoodPicked,true,context)

                val idFood1 = saveNewFood(
                    "Banane",
                    idFoodType1,
                    null,
                    true,true,
                    context
                )
                val idFood2 = saveNewFood(
                    "Poulet",
                    idFoodType2,
                    null,
                    true,true,
                    context
                )
                val idFood3 = saveNewFood(
                    "Salade",
                    idFoodType3,
                    null,
                    true,true,
                    context
                )
                val idFood4 = saveNewFood(
                    "Yaourt",
                    idFoodType4,
                    null,
                    true,true,
                    context
                )
                val idFood5 = saveNewFood(
                    "Radis",
                    idFoodType3,
                    null,
                    true,true,
                    context
                )
                val idFood6 = saveNewFood(
                    "Abricot",
                    idFoodType1,
                    null,
                    true,true,
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
                list4.add(MealItem(null,0,idFood2))
                list4.add(MealItem(null,0,idFood3))
                list4.add(MealItem(null,0,idFood6))

                val list5 : MutableList<MealItem> = mutableListOf()
                list5.add(MealItem(null,0,idFood3))
                list5.add(MealItem(null,0,idFood4))
                list5.add(MealItem(null,0,idFood5))
                list5.add(MealItem(null,0,idFood6))

                val list6 : MutableList<MealItem> = mutableListOf()
                list6.add(MealItem(null,0,idFood2))
                list6.add(MealItem(null,0,idFood4))
                list6.add(MealItem(null,0,idFood5))


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

                val idMeal5 = saveNewMeal(
                    list5,
                    getDateAsLong(1, 1, 2019, 10, 0),
                    true,
                    context
                )

                val idMeal6 = saveNewMeal(
                    list6,
                    getDateAsLong(5, 1, 2019, 10, 0),
                    true,
                    context
                )

                return listOf(idMeal1, idMeal2, idMeal3, idMeal4, idMeal5, idMeal6)
            }

            "event" ->{

                val id1 = saveNewEventType(
                    "Reflux",
                    null,
                    0,
                    3 * 60 * 60 * 1000,
                    true, false,
                    context
                )
                val id2 = saveNewEventType(
                    "Mal au ventre",
                    null,
                    0,
                    6 * 60 * 60 * 1000,
                    true, true,
                    context
                )
                val id3 = saveNewEventType(
                    "Mal à la tête",
                    null,
                    0,
                    7 * 60 * 60 * 1000,
                    true, false,
                    context
                )

                val idEvent1 = saveNewEvent(
                    idEventType = id1,
                    dateCode = getDateAsLong(2, 12, 2018, 12, 30),
                    mode = true,
                    context = context
                )
                val idEvent2 = saveNewEvent(
                    idEventType = id2,
                    dateCode = getDateAsLong(12, 12, 2018, 11, 0),
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
                    dateCode = getDateAsLong(14, 1, 2019, 15, 0),
                    mode = true,
                    context = context
                )
                val idEvent5 = saveNewEvent(
                    idEventType = id3,
                    dateCode = getDateAsLong(14, 1, 2019, 15, 30),
                    mode = true,
                    context = context
                )

                return listOf(id1, id2, id3, idEvent1, idEvent2, idEvent3, idEvent4, idEvent5)
            }
        }

        return null
    }

    fun getListEvents(context: Context):List<Long?>?{

        val id1 = saveNewEventType(
            "Reflux",
            null,
            0,
            3 * 60 * 60 * 1000,
            false, true,
            context
        )

        val id2 = saveNewEventType(
            "Ballonnement",
            null,
            0,
            1 * 60 * 60 * 1000,
            true, true,
            context
        )

        val idEvent1 = saveNewEvent(
            idEventType = id1,
            dateCode = getDateAsLong(2, 12, 2018, 12, 30),
            mode = true,
            context = context
        )
        val idEvent2 = saveNewEvent(
            idEventType = id1,
            dateCode = getDateAsLong(12, 12, 2018, 11, 0),
            mode = true,
            context = context
        )
        val idEvent3 = saveNewEvent(
            idEventType = id1,
            dateCode = getDateAsLong(14, 1, 2019, 11, 0),
            mode = true,
            context = context
        )
        val idEvent4 = saveNewEvent(
            idEventType = id1,
            dateCode = getDateAsLong(14, 1, 2019, 15, 0),
            mode = true,
            context = context
        )
        val idEvent5 = saveNewEvent(
            idEventType = id2,
            dateCode = getDateAsLong(14, 1, 2019, 15, 30),
            mode = true,
            context = context
        )

        val idEvent6 = saveNewEvent(
            idEventType = id2,
            dateCode = getDateAsLong(12, 12, 2018, 19, 30),
            mode = true,
            context = context
        )

        return listOf(id1, id2)

    }
}