package com.g.laurent.alitic.Controllers.Activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Matrix
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import com.g.laurent.alitic.Controllers.ClassControllers.*
import com.g.laurent.alitic.Models.EventType
import com.g.laurent.alitic.Models.Food
import com.g.laurent.alitic.R

fun configureToolbar(toolbar: Toolbar, typeDisplay: TypeDisplay, activity:PickActivity, context: Context){

    val searchIcon = toolbar.menu.findItem(R.id.action_search)

    when {
        typeDisplay.equals(TypeDisplay.EVENT) -> { // PICK EVENT
            toolbar.title = context.getString(R.string.title_event_choice)
            searchIcon.isVisible = false

            activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            toolbar.setNavigationOnClickListener {
                activity.goToBackToMainPage()
            }
        }

        typeDisplay.equals(TypeDisplay.MEAL) -> { // PICK MEAL
            toolbar.title = context.getString(R.string.title_meal_choice)
            searchIcon.isVisible = true

            (searchIcon.actionView as SearchView).setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                override fun onQueryTextChange(newText: String): Boolean {

                    val listFoods = getListFood(newText, context = context)
                    if(listFoods!=null)
                        activity.updateListFoodsAfterQueryChange(listFoods)
                    return false
                }

                override fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }
            })

            activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            toolbar.setNavigationOnClickListener {
                activity.goToBackToMainPage()
            }

            (searchIcon.actionView as SearchView).setOnCloseListener {
                activity.resetMealPickingScreenAfterSearch()
                true
            }
        }
    }
}

fun unConfigureToolbar(toolbar: Toolbar, activity:PickActivity, context: Context){
    val searchIcon = toolbar.menu.findItem(R.id.action_search)
    searchIcon.isVisible = false
    toolbar.title = context.getString(R.string.app_name)
    activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
}

fun movePicture(imageView: ImageView, fromPosition:Position?, toPosition:Position, matrix: Matrix, activity: PickActivity?){

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

        var done = false

        /*valueAnimator.addUpdateListener {
            val animProgress:Float = valueAnimator.animatedValue as Float
            if(animProgress > 0.90 && !done) {
                done = true
                activity?.displayMealPicking()
            }
        }*/

        valueAnimator.addListener(object: AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                if(toPosition.equals(Loc.CENTER.position)){ // if picture move to center
                    activity?.finishPickActivity()
                } else { // if picture move to left or right top corner
                    activity?.configurePickActivity()
                }
            }
        })
        valueAnimator.start()
    }
}

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
