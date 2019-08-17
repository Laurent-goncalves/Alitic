package com.g.laurent.alitic.Views

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import android.view.View
import android.widget.*
import com.g.laurent.alitic.*
import com.g.laurent.alitic.Controllers.ClassControllers.Chrono
import com.g.laurent.alitic.Models.FoodType
import com.github.vipulasri.timelineview.TimelineView


class TimeLineViewHolder(itemView: View, viewType: Int, val mode:Boolean = false, val context: Context) : RecyclerView.ViewHolder(itemView) {

    private var mTimelineView: TimelineView = itemView.findViewById(R.id.timeline)
    private var hourView : TextView
    private var grid : GridView

    init {
        mTimelineView.initLine(viewType)
        hourView = itemView.findViewById(R.id.hour)
        grid = itemView.findViewById(R.id.grid)

        grid.setOnTouchListener { v, e ->
            (v.parent.parent.parent.parent as TimeLineRecyclerView).layoutManager?.canScrollVertically()
            true
        }


        //val scrollview = itemView.findViewById<ScrollView>(R.id.scrollview)
/*
        grid.setOnTouchListener { v, _ ->
            v.parent.requestDisallowInterceptTouchEvent(false)
            v.parent.parent.requestDisallowInterceptTouchEvent(false)
            v.parent.parent.parent.requestDisallowInterceptTouchEvent(false)
            v.parent.parent.parent.parent.requestDisallowInterceptTouchEvent(false)
            true
        }*/
    }

    fun configureTimeLineViewHolder(chrono: Chrono) {

        // Configure GridAdapter
        hourView.text = chrono.hour
        val adapter = GridAdapter(chrono.item, null, false,null, mode, context)
        grid.adapter = adapter
    }
}

class FoodTypeViewHolder(itemView: View, private val mode:Boolean = false, val context: Context) : RecyclerView.ViewHolder(itemView) {

    private var image:ImageView = itemView.findViewById(R.id.image_foodtype)
    private var name:TextView = itemView.findViewById(R.id.name_foodtype)
    private var oldSelectIndex = 0

    fun configureFoodTypeViewHolder(position:Int, selectIndex:Int, foodType: FoodType) {

        // Edit text to display
        name.text = foodType.name

        // set image to display
        setImageInImageView(foodType, image, mode, context)

        // Adapt the layout of the viewholder (if selected or not)
        if(selectIndex==-1){ // INITIALIZATION -> select the first item
            select(position==0)
        } else { // CHANGE OF ITEM SELECTED
            select(position==selectIndex)
            oldSelectIndex = selectIndex
        }
    }

    private fun select(select:Boolean){
        if(select){
            itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorFoodTypeSelected))
        } else {
            itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
        }
    }
}

class ChronoViewHolder(itemView: View?){
    var dayNum: TextView? = itemView?.findViewById(R.id.day_number)
    var imageBackground: ImageView? = itemView?.findViewById(R.id.image_background)
}

class MealPickViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    var foodName: MealTextView = itemView.findViewById(R.id.food_name)
    var deleteButton: ImageView = itemView.findViewById(R.id.delete_button)
}

class StatChronoHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    var monthView: TextView = itemView.findViewById(R.id.month_text)
    var gridView: GridView = itemView.findViewById(R.id.grid_month)
}
