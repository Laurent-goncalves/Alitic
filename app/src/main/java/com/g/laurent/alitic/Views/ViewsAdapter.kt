package com.g.laurent.alitic.Views

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.github.vipulasri.timelineview.TimelineView
import android.view.View
import android.widget.BaseAdapter
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import com.g.laurent.alitic.Controllers.ClassControllers.ChronoItem
import com.g.laurent.alitic.Controllers.ClassControllers.getEventType
import com.g.laurent.alitic.Controllers.ClassControllers.getFoodsFromMeal
import com.g.laurent.alitic.Models.Event
import com.g.laurent.alitic.Models.Meal
import android.widget.FrameLayout
import com.g.laurent.alitic.Models.Food
import com.g.laurent.alitic.Models.FoodType
import android.annotation.SuppressLint
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import com.g.laurent.alitic.*


/** FOODTYPE ADAPTER**/
@SuppressLint("RecyclerView")
class FoodTypeAdapter(val list: List<FoodType>,val recyclerView: RecyclerView, val mode:Boolean=false, val context: Context) : RecyclerView.Adapter<FoodTypeViewHolder>() {

    private var selection:Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodTypeViewHolder {
        val view = View.inflate(parent.context, R.layout.foodtype_viewholder, null)
        return FoodTypeViewHolder(view, mode, context)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: FoodTypeViewHolder, position: Int) {

        holder.itemView.setOnClickListener {
            selection = holder.adapterPosition
            notifyDataSetChanged()
        }

        // Get the width of recyclerView and then configure viewholder
        val vto = recyclerView.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                if(holder.adapterPosition!=-1)
                    holder.configureFoodTypeViewHolder(selection, recyclerView.measuredWidth, list[holder.adapterPosition], holder.adapterPosition)
                else
                    holder.configureFoodTypeViewHolder(selection, recyclerView.measuredWidth, null,null)
            }
        })
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}

/** TIMELINE ADAPTER**/
class TimeLineAdapter(val list: List<ChronoItem>, val mode:Boolean=false, val context: Context) : RecyclerView.Adapter<TimeLineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeLineViewHolder {
        val view = View.inflate(parent.context, com.g.laurent.alitic.R.layout.timeline_viewholder, null)
        return TimeLineViewHolder(view, viewType, mode, context)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: TimeLineViewHolder, position: Int) {
        holder.configureTimeLineViewHolder(list[position])
    }

    override fun getItemViewType(position: Int): Int {
        return TimelineView.getTimeLineViewType(position, itemCount)
    }
}

/** ADAPTER FOR GRIDVIEW TO DISPLAY PICTURE + NAME **/
class GridAdapter(val any: Any, val pickmode:Boolean, val mode:Boolean=false, val context: Context): BaseAdapter() { // any is Event or Meal

    override fun getItem(position: Int): Any {
        return this
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

        var view: View? = null
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?

        fun getTextToDisplay(any: Any):String?{

            when (any) {
                is Event -> { // if event
                    val eventType =
                        getEventType(any.idEventType, mode, context)
                    return eventType?.name
                }
                is Meal -> { // if meal
                    val foods = getFoodsFromMeal(any, mode, context)
                    return foods[position].name
                }
                is List<*> -> {
                    val food = any[position] as Food
                    return food.name
                }
                else ->
                    return null
            }
        }

        fun setPickItem(any:Any, frame:FrameLayout, pickIcon:ImageView, pickmode:Boolean){

            fun setPicked(chosen:Boolean){
                if(chosen){
                    frame.setBackgroundResource(com.g.laurent.alitic.R.drawable.border_validation)
                    pickIcon.visibility = View.VISIBLE
                }
            }

            if(pickmode){
                when (any) {
                    is Event -> { setPicked(any.chosen)}    // if event

                    is Meal -> {  // if meal
                        val foods = getFoodsFromMeal(any, mode, context)
                        setPicked(foods[position].chosen)
                    }
                    is List<*> -> {
                        val food = any[position] as Food
                        setPicked(food.chosen)
                    }
                }
            }
        }

        if (inflater != null) {
            view = inflater.inflate(com.g.laurent.alitic.R.layout.gridviewholder, parent, false)

            val imageView = view.findViewById<ImageView>(com.g.laurent.alitic.R.id.image_meal)
            val textView = view.findViewById<TextView>(com.g.laurent.alitic.R.id.meal_text)
            val frameLayout = view.findViewById<FrameLayout>(com.g.laurent.alitic.R.id.framelayout_viewholder)
            val pickIcon = view.findViewById<ImageView>(com.g.laurent.alitic.R.id.check_item)

            val text = getTextToDisplay(any)
            val image = getImagePath(any, position, mode, context)
            val imageDraw = getImageDrawPath(any, position, mode, context)

            // Put the food name in text
            textView.text = text

            // Associate picture if it exists
            getImageFromPath(image, imageDraw, imageView, context)

            // change layout in case of validation for picking
            setPickItem(any, frameLayout, pickIcon, pickmode)
        }

        return view
    }

    override fun getCount(): Int {
        return when (any) {
            is Event -> 1
            is Meal -> {
                val listMealItems = any.listMealItems
                listMealItems?.size ?: 0
            }
            is List<*> -> { // list foods
                any.size
            }
            else -> 0
        }
    }
}