package com.g.laurent.alitic.Controllers.Activities

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Matrix
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import com.facebook.stetho.Stetho
import com.g.laurent.alitic.Models.AppDataBase
import com.g.laurent.alitic.Models.EventType
import com.g.laurent.alitic.Models.Food
import com.g.laurent.alitic.Models.insertData
import com.g.laurent.alitic.R


const val DURATION_MOVE_CAMERA = 3000.toLong()
const val EVENT = "EVENT"
const val MEAL = "MEAL"
const val DELAY_SHOW = 2000.toLong()
const val DELAY_HIDE = 100.toLong()

interface OnMenuSelectionListener {
    fun onMenuSelected(selection:Int)
}

interface OnItemSelectionListener {
    fun onItemSelected(selected:Any)
}

class Position(val px:Float, val py:Float)

enum class Loc(var position : Position) {
    CENTER(Position(0.toFloat(),0.toFloat())),
    TOP_LEFT(Position(0.toFloat(),0.toFloat())),
    TOP_RIGHT(Position(0.toFloat(),0.toFloat())),
    BOTTOM_LEFT(Position(0.toFloat(),0.toFloat())),
    BOTTOM_RIGHT(Position(0.toFloat(),0.toFloat()));

    companion object {
        fun setPosition(loc:Loc, newPosition: Position){
            loc.position = newPosition
        }
    }
}

enum class TypeDisplay(val type:String, val idCancel : Int, val idSave : Int, var isNew:Boolean){
    EVENT("EVENT", R.id.button_cancel_event,R.id.button_save_event, true),
    MEAL("MEAL",R.id.button_cancel_meal,R.id.button_save_meal, true);
}

fun moveCamera(imageView: ImageView, fromPosition:Position?, toPosition:Position, matrix: Matrix){

    if(fromPosition==null){
        matrix.reset()
        matrix.setTranslate(toPosition.px, toPosition.py)
        imageView.imageMatrix = matrix

    } else {

        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.interpolator = AccelerateDecelerateInterpolator() // increase the speed first and then decrease
        valueAnimator.duration = DURATION_MOVE_CAMERA
        valueAnimator.addUpdateListener { animation ->

            val progress = animation.animatedValue as Float

            val tempX = fromPosition.px + progress * (toPosition.px - fromPosition.px)
            val tempY = fromPosition.py + progress * (toPosition.py - fromPosition.py)

            matrix.reset()
            matrix.setTranslate(tempX, tempY)

            imageView.imageMatrix = matrix
        }

        valueAnimator.start()
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
                        list.removeAt(i)
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

fun saveData(typeDisplay: TypeDisplay, listSelected:List<Any>){


}

fun clearDatabase(context: Context){

    val db = AppDataBase.getInstance(context)
    db?.mealItemDao()?.deleteAll()
    db?.mealDao()?.deleteAll()
    db?.keywordDao()?.deleteAll()
    db?.foodDao()?.deleteAll()
    db?.foodTypeDao()?.deleteAll()
    db?.eventDao()?.deleteAll()
    db?.eventTypeDao()?.deleteAll()


    insertData(db?.foodTypeDao(), db?.foodDao(), db?.keywordDao(), db?.eventTypeDao())

    Stetho.initializeWithDefaults(context)
}

