package com.g.laurent.alitic

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.g.laurent.alitic.Controllers.ClassControllers.*
import com.g.laurent.alitic.Models.AppDataBase
import com.g.laurent.alitic.Models.MealItem
import org.junit.After
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.text.DecimalFormat


@RunWith(AndroidJUnit4::class)
class StatTest {

    @After
    fun tearDown() {
        AppDataBase.clearDatabase()
    }


    @Test
    fun test_stat_foodtype(){

        val context = InstrumentationRegistry.getTargetContext()
        val listIdMeals = getIds("meal", context)
        val listIdEvents = getIds("event", context)
        val df = DecimalFormat("#.###")

        var idEventType = 0L

        if(listIdEvents!=null){
            if(listIdEvents[0]!=null){
                idEventType = listIdEvents[0]!!
                val eventtype = getEventType(idEventType,true, context)
                val result = getListFoodTypesCounts(eventtype!!, true, context)

                Assert.assertEquals(3, result.size)

                for(value in result){
                    when(value.foodType.name){
                        "Légumes" -> {
                            Assert.assertEquals("0.5", df.format(value.percent.toFloat()))
                        }
                        "Viandes" -> {
                            Assert.assertEquals("0.167", df.format(value.percent.toFloat()))
                        }
                        "Fruits" -> {
                            Assert.assertEquals("0.333", df.format(value.percent.toFloat()))
                        }
                    }
                }
            } else
                Assert.assertEquals(0, 1)
        } else
            Assert.assertEquals(0, 1)
    }

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

    @Test
    fun test_evolution(){

        val context = InstrumentationRegistry.getTargetContext()
        val listIdMeals = getIds("meal", context)
        val listIdEvents = getListEvents(context)

        if(listIdEvents!=null){
            if(listIdEvents[0]!=null){
                val listDates = getDatesEvents(listIdEvents[0],true,context)
                val evolution = getEvolution(listDates, true, context)

                assertEquals(5, listDates.size)
                assertEquals(Evolution.NEGATIVE, evolution)

            } else
                assertTrue(false)
        } else
            assertTrue(false)
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
            true,
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
            idEventType = id1,
            dateCode = getDateAsLong(14, 1, 2019, 15, 30),
            mode = true,
            context = context
        )

        return listOf(id1)

    }
}