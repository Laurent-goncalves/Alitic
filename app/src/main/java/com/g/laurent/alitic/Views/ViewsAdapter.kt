package com.g.laurent.alitic.Views

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.github.vipulasri.timelineview.TimelineView
import android.view.View
import android.widget.BaseAdapter
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.g.laurent.alitic.Controllers.ChronoItem
import com.g.laurent.alitic.Controllers.getEventType
import com.g.laurent.alitic.Controllers.getFoodsFromMeal
import com.g.laurent.alitic.Models.Event
import com.g.laurent.alitic.Models.Meal
import com.g.laurent.alitic.R

/** TIMELINE ADAPTER**/
class TimeLineAdapter(list: List<ChronoItem>, val context: Context) : RecyclerView.Adapter<TimeLineViewHolder>() {

    val listChrono = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeLineViewHolder {
        val view = View.inflate(parent.context, com.g.laurent.alitic.R.layout.timeline_viewholder, null)
        return TimeLineViewHolder(view, viewType, context)
    }

    override fun getItemCount(): Int {
        return listChrono.size
    }

    override fun onBindViewHolder(holder: TimeLineViewHolder, position: Int) {
        holder.configureTimeLineViewHolder(listChrono[position])
    }

    override fun getItemViewType(position: Int): Int {
        return TimelineView.getTimeLineViewType(position, getItemCount());
    }
}

/** ADAPTER FOR GRIDVIEW TO DISPLAY PICTURE + NAME **/
class GridAdapter(val any: Any, val context: Context): BaseAdapter() { // any is Event or Meal

    override fun getItem(position: Int): Any {
        return this
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

        var view: View? = null
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?

        if (inflater != null) {
            view = inflater.inflate(com.g.laurent.alitic.R.layout.gridviewholder, parent, false)

            val imageView = view.findViewById<ImageView>(R.id.image_meal)
            val textView = view.findViewById<TextView>(R.id.meal_text)

            var text:String? = null
            var imageUri:String? = null

            if(any is Event){ // if event
                val eventType = getEventType(any.idEventType, false, context)
                text = eventType?.name
                imageUri = eventType?.eventPic

            } else if(any is Meal){ // if meal
                val foods = getFoodsFromMeal(any, context)
                text = foods[position].name
                imageUri = foods[position].foodPic
            }

            // Put the food name in text
            textView.text = text

            // Associate picture if it exists

            println(imageUri)

            if(imageUri!=null){
                Glide.with(context)
                    .load(imageUri)
                    //TODO .apply(RequestOptions().placeholder(R.drawable.icon_friend))
                    .into(imageView)
            }
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
            else -> 0
        }
    }
}