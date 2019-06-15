package com.g.laurent.alitic

import android.content.Context
import android.widget.Toast
import com.g.laurent.alitic.Controllers.ClassControllers.*
import jxl.Workbook
import jxl.write.Label
import jxl.write.WritableWorkbook
import jxl.write.WriteException
import java.io.File
import java.io.IOException

fun writeToExcelFile(filePath:File, context: Context) {

    lateinit var myFirstWbook:WritableWorkbook

    try{

        myFirstWbook = Workbook.createWorkbook(filePath)

        val excelSheet = myFirstWbook.createSheet("Sheet 1", 0)
        // add something into the Excel sheet

        /** CREATE COLUMNS **/
        excelSheet.addCell(Label(0, 0, context.resources.getString(R.string.col_date_meal)))
        excelSheet.addCell(Label(1, 0, context.resources.getString(R.string.col_date_meal_long)))
        excelSheet.addCell(Label(2, 0, context.resources.getString(R.string.col_foodtype)))
        excelSheet.addCell(Label(3, 0, context.resources.getString(R.string.col_foods)))
        excelSheet.addCell(Label(4, 0, context.resources.getString(R.string.col_food_taken_into_acc)))

        excelSheet.addCell(Label(6, 0, context.resources.getString(R.string.col_date_event)))
        excelSheet.addCell(Label(7, 0, context.resources.getString(R.string.col_date_event_long)))
        excelSheet.addCell(Label(8, 0, context.resources.getString(R.string.col_event_type_name)))

        /**  TABLE MEALS  **/
        val listMealData = getAllMealData(context = context)

        if(listMealData!=null){
            var counter = 1

            for(mealData in listMealData){

                excelSheet.addCell(Label(0, counter, getTextDate(mealData.dateLong, context)))
                excelSheet.addCell(Label(1, counter, mealData.dateLong.toString()))
                excelSheet.addCell(Label(2, counter, mealData.foodTypeName))
                excelSheet.addCell(Label(3, counter, mealData.foodName))

                if(mealData.foodForAnalysis){
                    excelSheet.addCell(Label(4, counter, context.resources.getString(R.string.yes)))
                } else {
                    excelSheet.addCell(Label(4, counter, context.resources.getString(R.string.no)))
                }

                counter++
            }
        }

        /**  TABLE EVENTS  **/
        val listEventData = getAllEventData(context = context)

        if(listEventData!=null){
            var counter = 1

            for(eventData in listEventData){

                excelSheet.addCell(Label(6, counter, getTextDate(eventData.dateLong, context)))
                excelSheet.addCell(Label(7, counter, eventData.dateLong.toString()))
                excelSheet.addCell(Label(8, counter, eventData.eventTypeName))

                counter++
            }
        }

        myFirstWbook.write()
        myFirstWbook.close()

        Toast.makeText(context, context.resources.getString(R.string.file_excel_created), Toast.LENGTH_LONG).show()

    } catch (e: IOException) {
        Toast.makeText(context, context.resources.getString(R.string.file_excel_not_created) + "\n" + e.message, Toast.LENGTH_LONG).show()
    } catch (e: WriteException) {
        Toast.makeText(context, context.resources.getString(R.string.file_excel_not_created) + "\n" + e.message, Toast.LENGTH_LONG).show()
    }
}

class MealData(val dateLong:Long?, val foodTypeName:String, val foodName:String, val foodForAnalysis:Boolean)
class EventData(val dateLong:Long?, val eventTypeName:String)

/*fun getAllMealDatas(context: Context):List<MealData>{

    val result:MutableList<MealData> = mutableListOf()
    val listMeals = getAllMeals(context = context)

    if(listMeals!=null){

        for(meal in listMeals){

            val listMealItems = meal.listMealItems

            if(listMealItems!=null){
                for(mealItem in listMealItems){

                    val idFood = mealItem.idFood

                    if(idFood!=null){

                        val food = getFood(idFood, context = context)

                        if(food!=null){
                            val foodType = getFoodType(food.idFoodType, context = context)




                        }
                    }

                    //getTextDate()

                }
            }
        }
    }


    return result.toList()
}

fun getAllEventDatas(context: Context):List<EventData>{

    val result:MutableList<EventData> = mutableListOf()
    val listEvents = getAllEvents(context = context)



    return result.toList()
}*/