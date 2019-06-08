package com.g.laurent.alitic.Controllers.Activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Matrix
import android.os.AsyncTask
import android.support.v4.app.FragmentManager
import android.support.v4.view.ViewCompat
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.facebook.stetho.Stetho
import com.g.laurent.alitic.Controllers.ClassControllers.getListFood
import com.g.laurent.alitic.Models.AppDataBase
import com.g.laurent.alitic.Models.EventType
import com.g.laurent.alitic.Models.Food
import com.g.laurent.alitic.Models.insertData
import com.g.laurent.alitic.R
import com.g.laurent.alitic.Views.FoodLayout


const val DURATION_MOVE_CAMERA = 3000.toLong()
const val DURATION_MOVE_PANEL = 2000.toLong()
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

enum class TypeDisplay(val type:String, val idCancel : Int, val idSave : Int, var isNew:Boolean){
    EVENT("EVENT", R.id.button_cancel_event,R.id.button_save_event, true),
    MEAL("MEAL", R.id.button_cancel_meal,R.id.button_save_meal, true);
}

fun moveCamera(imageView: ImageView, fromPosition:Position?, toPosition:Position, matrix: Matrix, activity: MainActivity?){

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

        valueAnimator.addUpdateListener {
            val animProgress:Float = valueAnimator.animatedValue as Float
            if(animProgress > 0.90 && !done) {
                done = true
                activity?.displayMealPicking()
            }
        }

        /*valueAnimator.addListener(object: AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                activity?.displayMealPicking()
            }
        })*/

        AsyncTaskAnimation(valueAnimator).execute()
    }
}

class AsyncTaskAnimation(private val valueAnimator: ValueAnimator) : AsyncTask<String, String, String>() {

    override fun onPreExecute() {
        super.onPreExecute()
        valueAnimator.start()
    }

    override fun doInBackground(vararg p0: String?): String {


        return ""
    }
}

fun openPanel(panel: View){

    val start = (panel.scaleX - Pan.SCALE_PANEL.min)/ (Pan.SCALE_PANEL.max - Pan.SCALE_PANEL.min)

    val valueAnimator = ValueAnimator.ofFloat(start, 1f)
    valueAnimator.interpolator = AccelerateDecelerateInterpolator() // increase the speed first and then decrease
    valueAnimator.duration = DURATION_MOVE_PANEL
    valueAnimator.addUpdateListener { animation ->

        val progress = animation.animatedValue as Float

        val tempX = Loc.SMALL_PANEL_CENTER.position.px + progress * (Loc.BIG_PANEL_CENTER.position.px - Loc.SMALL_PANEL_CENTER.position.px)
        val tempY = Loc.SMALL_PANEL_CENTER.position.py + progress * (Loc.BIG_PANEL_CENTER.position.py - Loc.SMALL_PANEL_CENTER.position.py)

        val scale = Pan.SCALE_PANEL.min + progress * (Pan.SCALE_PANEL.max - Pan.SCALE_PANEL.min)

        panel.translationX = tempX
        panel.translationY = tempY
        panel.scaleX = scale
        panel.scaleY = scale

    }

    valueAnimator.start()
}

fun createFoodListSelected(view: FoodLayout, listSelected:List<Food>){



/*
for(food in listSelected){

    val params = RelativeLayout.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
    )
    view.addView(tv, params)
}
*/

}

fun closePanel(panel: View){

    val start = (Pan.SCALE_PANEL.max - panel.scaleX)/ (Pan.SCALE_PANEL.max - Pan.SCALE_PANEL.min)

    val valueAnimator = ValueAnimator.ofFloat(start, 1f)
    valueAnimator.interpolator = AccelerateDecelerateInterpolator() // increase the speed first and then decrease
    valueAnimator.duration = DURATION_MOVE_PANEL
    valueAnimator.addUpdateListener { animation ->

        val progress = animation.animatedValue as Float

        val tempX = Loc.BIG_PANEL_CENTER.position.px + progress * (Loc.SMALL_PANEL_CENTER.position.px - Loc.BIG_PANEL_CENTER.position.px)
        val tempY = Loc.BIG_PANEL_CENTER.position.py + progress * (Loc.SMALL_PANEL_CENTER.position.py - Loc.BIG_PANEL_CENTER.position.py)

        val scale = Pan.SCALE_PANEL.max - progress * (Pan.SCALE_PANEL.max - Pan.SCALE_PANEL.min)

        panel.translationX = tempX
        panel.translationY = tempY
        panel.scaleX = scale
        panel.scaleY = scale
    }

    valueAnimator.start()
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

        var tempX = 0f
        var tempY = 0f

        val scale = calculWithLimits(scale1 + (delta / Pan.DELTA_Y.max) * (Pan.SCALE_PANEL.max - Pan.SCALE_PANEL.min), Pan.SCALE_PANEL.min, Pan.SCALE_PANEL.max)

        if(delta<0){ // DOWN direction

            val progress = (scale - Pan.SCALE_PANEL.min)/ (Pan.SCALE_PANEL.max - Pan.SCALE_PANEL.min)
            tempX = Loc.SMALL_PANEL_CENTER.position.px + progress * (Loc.BIG_PANEL_CENTER.position.px - Loc.SMALL_PANEL_CENTER.position.px)
            tempY = Loc.SMALL_PANEL_CENTER.position.py + progress * (Loc.BIG_PANEL_CENTER.position.py - Loc.SMALL_PANEL_CENTER.position.py)

        } else if(delta>0){ // UP direction

            val progress = (Pan.SCALE_PANEL.max - scale)/ (Pan.SCALE_PANEL.max - Pan.SCALE_PANEL.min)
            tempX = Loc.BIG_PANEL_CENTER.position.px + progress * (Loc.SMALL_PANEL_CENTER.position.px - Loc.BIG_PANEL_CENTER.position.px)
            tempY = Loc.BIG_PANEL_CENTER.position.py + progress * (Loc.SMALL_PANEL_CENTER.position.py - Loc.BIG_PANEL_CENTER.position.py)

        }

        if(delta!=0f){
            panel.translationX = tempX
            panel.translationY = tempY
            panel.scaleX = scale
            panel.scaleY = scale
        }
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

fun saveData(typeDisplay: TypeDisplay, listSelected:List<Any>){


}

fun configureToolbar(toolbar:Toolbar, typeDisplay: TypeDisplay?, activity:MainActivity, context: Context){

    val settingsIcon = toolbar.menu.findItem(R.id.action_settings)
    val searchIcon = toolbar.menu.findItem(R.id.action_search)

    when {
        typeDisplay==null -> { // MAIN PAGE
            toolbar.title = context.getString(R.string.app_name)

            activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
            searchIcon.isVisible = false
            settingsIcon.isVisible = true
            settingsIcon.setOnMenuItemClickListener {
                // TODO : configure settings
                Toast.makeText(context, "SETTINGS", Toast.LENGTH_LONG).show()
                true
            }
        }
        typeDisplay.equals(TypeDisplay.EVENT) -> { // PICK EVENT
            toolbar.title = context.getString(com.g.laurent.alitic.R.string.title_event_choice)
            settingsIcon.isVisible = false
            searchIcon.isVisible = false

            activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            toolbar.setNavigationOnClickListener {
                activity.goToBackToMainPage(TypeDisplay.EVENT.type)
            }
        }

        typeDisplay.equals(TypeDisplay.MEAL) -> { // PICK MEAL
            toolbar.title = context.getString(R.string.title_meal_choice)
            settingsIcon.isVisible = false
            searchIcon.isVisible = true

            (searchIcon.actionView as SearchView).setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                override fun onQueryTextChange(newText: String): Boolean {

                    val listFoods = getListFood(newText, context = context)
                    if(listFoods!=null)
                        activity.updateListFoods(listFoods)
                    return false
                }

                override fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }
            })

            activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            toolbar.setNavigationOnClickListener {
                activity.goToBackToMainPage(TypeDisplay.MEAL.type)
            }

            (searchIcon.actionView as SearchView).setOnCloseListener {
                activity.resetMealPickingScreenAfterSearch()
                true
            }
        }
    }
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


    insertData(db?.foodTypeDao(), db?.foodDao(), db?.keywordDao(), db?.eventTypeDao(), context)

    Stetho.initializeWithDefaults(context)
}

