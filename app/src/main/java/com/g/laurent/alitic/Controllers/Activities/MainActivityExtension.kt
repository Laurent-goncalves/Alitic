package com.g.laurent.alitic.Controllers.Activities

import android.content.Context
import android.view.View
import com.g.laurent.alitic.Models.AppDataBase
import com.g.laurent.alitic.Models.EventType
import com.g.laurent.alitic.Models.Food
import com.g.laurent.alitic.Models.insertData
import com.g.laurent.alitic.R

const val DURATION_MOVE_CAMERA = 3000.toLong()
const val DURATION_MOVE_PANEL = 2000.toLong()
const val EVENT = "EVENT"
const val MEAL = "MEAL"
const val DELETE = "DELETE"
const val UNSELECT = "UNSELECT"
const val SELECT = "SELECT"
const val TYPEDISPLAY = "typeDisplay"
const val SHARED_PREF_SWIDTH = "sWidth"
const val SHARED_PREF_SHEIGHT = "sHeight"
const val SHARED_PREF_DWIDTH = "dWidth"
const val SHARED_PREF_DHEIGHT = "dHeight"

interface OnMenuSelectionListener {
    fun onMenuSelected(selection:Int)
}

interface OnItemSelectionListener {
    fun onItemSelected(selected:Any)
}

interface OnFoodToDeleteListener {
    fun onFoodToDelete(nameFood: String)
}

class Position(val px:Float, val py:Float)

enum class Loc(var position : Position) {
    CENTER(Position(0.toFloat(),0.toFloat())),
    TOP_LEFT(Position(0.toFloat(),0.toFloat())),
    TOP_RIGHT(Position(0.toFloat(),0.toFloat())),
    BOTTOM_LEFT(Position(0.toFloat(),0.toFloat())),
    BOTTOM_RIGHT(Position(0.toFloat(),0.toFloat())),
    SMALL_PANEL_CENTER(Position(0.toFloat(),0.toFloat())),
    BIG_PANEL_CENTER(Position(0.toFloat(),0.toFloat()));

    companion object {
        fun setPosition(loc:Loc, newPosition: Position){
            loc.position = newPosition
        }
    }
}

enum class Pan(var min : Float, var max:Float) {

    DIAMETER_PANEL(0.toFloat(),0.toFloat()),
    DELTA_Y(0.toFloat(),0.toFloat()),
    SCALE_PANEL(0.toFloat(),0.toFloat());

    companion object {
        fun setMinMax(pan:Pan, min : Float, max:Float){
            pan.min = min
            pan.max = max
        }
    }
}

enum class TypeDisplay(val type:String, val idCancel : Int, val idSave : Int){
    EVENT("EVENT", R.id.button_cancel_event,R.id.button_save_event),
    MEAL("MEAL", R.id.button_cancel_meal,R.id.button_save_meal);
}

fun calculWithLimits(value:Float, min:Float, max:Float):Float{
    return when {
        value in min..max -> value
        value > max -> max
        else -> min
    }
}

fun movePanel(panel: View, scale1:Float, delta:Float){

    if(panel.scaleX >= Pan.SCALE_PANEL.min && panel.scaleX <= Pan.SCALE_PANEL.max){

        val scale = calculWithLimits(scale1 + (delta / Pan.DELTA_Y.max) * (Pan.SCALE_PANEL.max - Pan.SCALE_PANEL.min), Pan.SCALE_PANEL.min, Pan.SCALE_PANEL.max)

        panel.scaleX = scale
        panel.scaleY = scale
    }
}

fun updateListSelected(any:Any, list:MutableList<Any>?):MutableList<Any>{

    val listUpdated = mutableListOf<Any>()

    if(list!=null)
        listUpdated.addAll(list.toList())

    when(any) {
        is Food -> {
            if (isAlreadySelected(any.id, list) && list!=null) {
                for (i in 0 until list.size) {
                    val foodSelected = list[i] as Food
                    if (any.id == foodSelected.id) {
                        listUpdated.removeAt(i)
                        break
                    }
                }
            } else
                listUpdated.add(any)
        }
        is EventType -> {
            if (isAlreadySelected(any.id, list) && list!=null) {
                for (i in 0 until list.size) {
                    val eventSelected = list[i] as EventType
                    if (any.id == eventSelected.id) {
                        listUpdated.removeAt(i)
                        break
                    }
                }
            } else
                listUpdated.add(any)
        }
    }

    return listUpdated
}

fun isAlreadySelected(idSelected:Long?,  list:MutableList<Any>?):Boolean{

    if(idSelected!=null && list!=null){
        for(any in list){
            when(any){
                is Food -> {
                    if(any.id == idSelected)
                        return true
                }
                is EventType -> {
                    if(any.id == idSelected)
                        return true
                }
            }
        }
    }
    return false
}

fun populateDatabase(context: Context){

    // Clear database
    val db = AppDataBase.getInstance(context)
    db?.mealItemDao()?.deleteAll()
    db?.mealDao()?.deleteAll()
    db?.keywordDao()?.deleteAll()
    db?.foodDao()?.deleteAll()
    db?.foodTypeDao()?.deleteAll()
    db?.eventDao()?.deleteAll()
    db?.eventTypeDao()?.deleteAll()

    // Fill with data
    insertData(db?.foodTypeDao(), db?.foodDao(), db?.keywordDao(), db?.eventTypeDao(), context)
}
