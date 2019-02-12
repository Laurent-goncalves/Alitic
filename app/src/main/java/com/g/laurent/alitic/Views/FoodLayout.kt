package com.g.laurent.alitic.Views

import android.content.Context
import android.graphics.PorterDuff
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.g.laurent.alitic.Models.Food
import android.support.constraint.ConstraintSet


class FoodLayout: ConstraintLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    fun removeFood(view: View) {
        this.removeView(view)
    }

    fun addFood(food: Food): FoodViewId? {

        val view = getFoodView(food)
        if (view != null) {
            view.id = View.generateViewId()
            this.addView(view,0)
            return FoodViewId(view.id, food.name!!)
        }

        return null
    }

    fun getFoodView(food:Food):View?{

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        if (inflater != null){

            val view: View = inflater.inflate(com.g.laurent.alitic.R.layout.food_selected_layout, this, false)
            view.findViewById<TextView>(com.g.laurent.alitic.R.id.food_name).text = food.name
            view.findViewById<ImageView>(com.g.laurent.alitic.R.id.button_delete)
                .setColorFilter(ContextCompat.getColor(context, android.R.color.holo_red_dark),  PorterDuff.Mode.MULTIPLY)
            view.findViewById<ImageView>(com.g.laurent.alitic.R.id.button_delete).setOnClickListener { removeFood(this)
            }

            return view
        }
        return null
    }

    fun organize(listFoodViewIds:List<FoodViewId>){

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
                        topId = listIds[0].idView
                        break
                    }
                }

                // Remove ids from list
                for(i in 0 until listIds.size){
                    list.remove(listIds[i])
                }
            }
        }

        set.applyTo(this)
    }
}

class FoodViewId(val idView:Int, val nameFood:String)