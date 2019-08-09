package com.g.laurent.alitic.Views

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.Typeface
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
import com.g.laurent.alitic.Controllers.Activities.OnFoodToDeleteListener
import com.g.laurent.alitic.R


class FoodLayout : ConstraintLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    lateinit var onFoodToDeleteListener:OnFoodToDeleteListener
    private val groupVisible = Group(context)

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