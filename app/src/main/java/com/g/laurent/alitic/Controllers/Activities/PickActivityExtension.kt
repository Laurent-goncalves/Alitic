package com.g.laurent.alitic.Controllers.Activities

import android.animation.ValueAnimator
import android.content.Context
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.g.laurent.alitic.Controllers.ClassControllers.*
import com.g.laurent.alitic.Models.EventType
import com.g.laurent.alitic.Models.Food

fun openPanel(panel: View){

    val start = (panel.scaleX - Pan.SCALE_PANEL.min)/ (Pan.SCALE_PANEL.max - Pan.SCALE_PANEL.min)

    val valueAnimator = ValueAnimator.ofFloat(start, 1f)
    valueAnimator.interpolator = AccelerateDecelerateInterpolator() // increase the speed first and then decrease
    valueAnimator.duration = DURATION_MOVE_PANEL

    valueAnimator.addUpdateListener { animation ->
        val progress = animation.animatedValue as Float
        val scale = calculWithLimits(Pan.SCALE_PANEL.min + progress * (Pan.SCALE_PANEL.max - Pan.SCALE_PANEL.min), Pan.SCALE_PANEL.min, Pan.SCALE_PANEL.max)
        panel.scaleX = scale
        panel.scaleY = scale
    }

    valueAnimator.start()
}

fun closePanel(panel: View){

    val start = (Pan.SCALE_PANEL.max - panel.scaleX)/ (Pan.SCALE_PANEL.max - Pan.SCALE_PANEL.min)

    val valueAnimator = ValueAnimator.ofFloat(start, 1f)
    valueAnimator.interpolator = AccelerateDecelerateInterpolator() // increase the speed first and then decrease
    valueAnimator.duration = DURATION_MOVE_PANEL
    valueAnimator.addUpdateListener { animation ->

        val progress = animation.animatedValue as Float
        val scale = calculWithLimits(Pan.SCALE_PANEL.max - progress * (Pan.SCALE_PANEL.max - Pan.SCALE_PANEL.min), Pan.SCALE_PANEL.min, Pan.SCALE_PANEL.max)

        panel.scaleX = scale
        panel.scaleY = scale
    }

    valueAnimator.start()
}

fun foodTo(typeOp:String, id:Long, listSelected:MutableList<Any>, context: Context?):MutableList<Any>?{

    if(typeOp.equals(SELECT) && context!=null){

        val food = getFood(id, context = context)

        if(food!=null)
            listSelected.add(food)

    } else if(typeOp.equals(UNSELECT)){

        for(any in listSelected){
            if(any is Food){
                val idToDelete = any.id

                if(idToDelete!=null && idToDelete.equals(id)) {
                    listSelected.remove(any)
                    break
                }
            }
        }
    } else if(typeOp.equals(DELETE) && context!=null){

        val food = getFood(id, context = context)
        if(food!=null){
            food.takenIntoAcc = false
            updateFood(food, context = context)
        }

        // Unselect food
        foodTo(UNSELECT, id, listSelected, null)
    }

    return listSelected
}

fun eventTypeTo(typeOp:String, id:Long, listSelected:MutableList<Any>, context: Context?):MutableList<Any>?{

    if(typeOp.equals(SELECT) && context!=null){

        val eventType = getEventType(id, context = context)

        if(eventType!=null)
            listSelected.add(eventType)

    } else if(typeOp.equals(UNSELECT)){

        for(any in listSelected){
            if(any is EventType){
                val idToDelete = any.id

                if(idToDelete!=null && idToDelete.equals(id)) {
                    listSelected.remove(any)
                    break
                }
            }
        }

    } else if(typeOp.equals(DELETE) && context!=null){

        val eventType = getEventType(id, context = context)
        if(eventType!=null){
            eventType.takenIntoAcc = false
            updateEventType(eventType, context = context)
        }

        // Unselect food
        eventTypeTo(UNSELECT, id, listSelected, null)
    }

    return listSelected
}
