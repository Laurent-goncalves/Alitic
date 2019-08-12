package com.g.laurent.alitic.Views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.g.laurent.alitic.Models.Food
import android.support.constraint.ConstraintSet
import android.support.constraint.Group
import android.support.v7.widget.AppCompatTextView
import android.view.MotionEvent
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.RelativeLayout
import com.g.laurent.alitic.Controllers.Activities.*
import com.g.laurent.alitic.Models.Pan
import com.g.laurent.alitic.R
import kotlinx.android.synthetic.main.counter_layout.*
import kotlinx.android.synthetic.main.counter_layout.view.*
import kotlinx.android.synthetic.main.panel_meal_content.view.*
import kotlinx.android.synthetic.main.pick_meal_layout.*
import kotlinx.android.synthetic.main.pick_meal_layout.view.*


class FoodLayout : ConstraintLayout {

    lateinit var onFoodToDeleteListener:OnFoodToDeleteListener
    private val groupVisible = Group(context)

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private fun removeFood(view: View) {
        onFoodToDeleteListener.onFoodToDelete(view.findViewById<TextView>(R.id.food_name).text.toString())
    }

    fun addFood(food: Food): FoodViewId? {

        val view = getFoodView(food)
        if (view != null) {
            this.addView(view,0)
            return FoodViewId(view.id, food.name!!)
        }

        return null
    }

    private fun getFoodView(food:Food):View?{

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        if (inflater != null){

            val view: View = inflater.inflate(R.layout.food_selected_layout, this, false)
            view.id = View.generateViewId()
            view.findViewById<TextView>(R.id.food_name).text = food.name
            view.findViewById<ImageView>(R.id.button_delete)
                .setColorFilter(ContextCompat.getColor(context, android.R.color.holo_red_dark),  PorterDuff.Mode.MULTIPLY)
            view.setOnClickListener { removeFood(view) }
            return view
        }
        return null
    }

    fun organizeViews(listFoodViewIds:List<FoodViewId>){

        val list = listFoodViewIds.toMutableList()
        var topId = ConstraintSet.PARENT_ID
        val set = ConstraintSet()
        set.clone(this)

        while(list.isNotEmpty()){

            // init new line
            var sumChar = 0
            val listIds = mutableListOf<FoodViewId>()

            // Connect the first item from the new line
            set.connect(list[0].idView, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)

            if(topId == ConstraintSet.PARENT_ID)
                set.connect(list[0].idView, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            else
                set.connect(list[0].idView, ConstraintSet.TOP, topId, ConstraintSet.BOTTOM)

            var startId = list[0].idView
            list.removeAt(0)

                // organize views in the new line
                if(list.size >= 1){
                for(i in 0 until list.size){
                    if(sumChar + list[i].nameFood.length + 5 <= 25){

                        set.connect(list[i].idView, ConstraintSet.START, startId , ConstraintSet.END)

                        if(topId == ConstraintSet.PARENT_ID)
                            set.connect(list[i].idView, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
                        else
                            set.connect(list[i].idView, ConstraintSet.TOP, topId, ConstraintSet.BOTTOM)

                        startId = list[i].idView
                        listIds.add(list[i])
                        sumChar += list[i].nameFood.length + 5

                    } else {
                        topId = list[0].idView
                        break
                    }
                }

                // Remove ids from list
                for(i in 0 until listIds.size){
                    list.remove(listIds[i])
                }
            }
        }


        val result = mutableListOf<Int>()
        if(this.childCount > 0){
            for(i in 0 until this.childCount)
                result.add(this.getChildAt(i).id)
        }

        groupVisible.referencedIds = result.toIntArray()
        set.applyTo(this)
    }
}

class FoodViewId(val idView:Int, val nameFood:String)

class MealTextView : AppCompatTextView {

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle){
        applyStyle(context)
    }

    private fun applyStyle(context: Context){
        val customFont : String = resources.getString(R.string.Satisfy_Regular)
        val tf : Typeface = Typeface.createFromAsset(context.assets, "fonts/$customFont.ttf")
        typeface = tf
    }
}

class AppTitleTextView : AppCompatTextView {

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle){
        applyStyle(context)
    }

    private fun applyStyle(context: Context){
        val customFont : String = resources.getString(R.string.Knewave_Regular)
        val tf : Typeface = Typeface.createFromAsset(context.assets, "fonts/$customFont.ttf")
        typeface = tf
    }
}

class PanelLayout : RelativeLayout {

    private var scale1:Float = 0f
    private var posY1:Float = 0f

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    fun configureMealPanel(listSelected:List<Any>, emptyPanel:View, counterLayout:View, sWidth:Int, sHeight:Int, pickActivity:PickActivity){

        configureFoodLayout(listSelected, pickActivity)

        configureCounter(listSelected, counterLayout.findViewById(R.id.counter_meal))

        // Set panel touch listener to control movement of the panel
        val panelTouchListener = OnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    posY1 = event.rawY
                    scale1 = emptyPanel.scaleX
                }
                MotionEvent.ACTION_MOVE -> {
                    movePanel(emptyPanel, scale1, posY1 - event.rawY)
                }
                MotionEvent.ACTION_UP -> {
                    if (event.rawY > posY1 && emptyPanel.scaleX >= Pan.SCALE_PANEL.min) { // UP direction
                        closePanel(emptyPanel)
                        this.visibility = View.GONE
                        counterLayout.visibility = View.VISIBLE
                        configureCounter(pickActivity.getListSelected(), counterLayout.findViewById(R.id.counter_meal))

                    } else if (event.rawY < posY1 && emptyPanel.scaleX >= Pan.SCALE_PANEL.min) { // DOWN direction
                        openPanel(emptyPanel)
                        counterLayout.visibility = View.GONE
                        configureFoodLayout(pickActivity.getListSelected(), pickActivity)
                        this.visibility = View.VISIBLE
                    }
                }
            }

            emptyPanel.invalidate()

            v?.onTouchEvent(event) ?: true
        }
        emptyPanel.setOnTouchListener(panelTouchListener)
        linear_layout_panel.setOnTouchListener(panelTouchListener)

        // Configure size of panel emptyPanel
        val vto = emptyPanel.viewTreeObserver
        vto.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {

                emptyPanel.viewTreeObserver.removeOnPreDrawListener(this)

                Pan.setMinMax(Pan.DIAMETER_PANEL, (sWidth / 3f), (7f/4f)*sHeight ) //(diameter small panel, diameter big panel)
                Pan.setMinMax(Pan.SCALE_PANEL,Pan.DIAMETER_PANEL.min/emptyPanel.width,
                    Pan.DIAMETER_PANEL.max/emptyPanel.height) // (scale small panel, scale big panel)

                Pan.setMinMax(Pan.DELTA_Y, 0.toFloat(), Pan.DIAMETER_PANEL.max / (2f))

                // Position panel on the bottom start with the small size
                emptyPanel.scaleX = Pan.SCALE_PANEL.min
                emptyPanel.scaleY = Pan.SCALE_PANEL.min

                emptyPanel.translationX = -(1f/2f)*emptyPanel.width //Loc.SMALL_PANEL_CENTER.position.px
                emptyPanel.translationY = (1f/2f)*emptyPanel.height //Loc.SMALL_PANEL_CENTER.position.py

                return true
            }
        })
    }

    private fun configureFoodLayout(listSelected:List<Any>, pickActivity:PickActivity){

        if(listSelected.isNotEmpty()){

            no_food_in_meal.visibility = View.GONE

            layout_all_foods.visibility = View.VISIBLE
            layout_all_foods.onFoodToDeleteListener = pickActivity
            layout_all_foods.removeAllViews()

            val listIds = mutableListOf<FoodViewId>()

            for(i in 0 until listSelected.size) {
                val id = layout_all_foods.addFood(listSelected[i] as Food)
                if(id!=null)
                    listIds.add(id)
            }

            layout_all_foods.organizeViews(listIds)

        } else {
            no_food_in_meal.visibility = View.VISIBLE
            layout_all_foods.visibility = View.GONE
        }
    }

    fun configureCounter(listSelected:List<Any>, counterTextView:TextView){
        if(listSelected.isEmpty())
            counterTextView.text = 0.toString()
        else
            counterTextView.text = listSelected.size.toString()
    }

    fun deleteFood(listSelected:List<Any>){

        // If no more food selected, show message "no food selected"
        if(listSelected.isEmpty()){
            no_food_in_meal.visibility = View.VISIBLE
            title_meal.visibility = View.VISIBLE
            layout_all_foods.visibility = View.GONE
        } else {
            // organize views from constraint layout
            layout_all_foods.removeAllViews()
            val listIds = mutableListOf<FoodViewId>()
            for(i in 0 until listSelected.size) {
                val id = layout_all_foods.addFood(listSelected[i] as Food)
                if(id!=null)
                    listIds.add(id)
            }
            layout_all_foods.organizeViews(listIds)
        }
    }

    private fun openPanel(panel: View){

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

    private fun closePanel(panel: View){

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

    private fun movePanel(panel: View, scale1:Float, delta:Float){

        if(panel.scaleX >= Pan.SCALE_PANEL.min && panel.scaleX <= Pan.SCALE_PANEL.max){

            val scale = calculWithLimits(scale1 + (delta / Pan.DELTA_Y.max) * (Pan.SCALE_PANEL.max - Pan.SCALE_PANEL.min), Pan.SCALE_PANEL.min, Pan.SCALE_PANEL.max)

            panel.scaleX = scale
            panel.scaleY = scale
        }
    }

    private fun calculWithLimits(value:Float, min:Float, max:Float):Float{
        return when {
            value in min..max -> value
            value > max -> max
            else -> min
        }
    }
}

const val DURATION_MOVE_PANEL = 2000.toLong()