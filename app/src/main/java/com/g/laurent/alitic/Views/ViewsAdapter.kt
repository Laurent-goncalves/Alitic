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
import com.bumptech.glide.Glide
import com.g.laurent.alitic.Controllers.ChronoItem
import com.g.laurent.alitic.Controllers.getEventType
import com.g.laurent.alitic.Controllers.getFoodsFromMeal
import com.g.laurent.alitic.Models.Event
import com.g.laurent.alitic.Models.Meal
import android.webkit.URLUtil
import com.bumptech.glide.request.RequestOptions
import com.g.laurent.alitic.Controllers.getFoodType


/** TIMELINE ADAPTER**/
class TimeLineAdapter(list: List<ChronoItem>, val mode:Boolean=false, val context: Context) : RecyclerView.Adapter<TimeLineViewHolder>() {

    val listChrono = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeLineViewHolder {
        val view = View.inflate(parent.context, com.g.laurent.alitic.R.layout.timeline_viewholder, null)
        return TimeLineViewHolder(view, viewType, mode, context)
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
class GridAdapter(val any: Any, val mode:Boolean=false, val context: Context): BaseAdapter() { // any is Event or Meal

    override fun getItem(position: Int): Any {
        return this
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

        var view: View? = null
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?

        fun getResourceId(path:String?):Int{
            val resources = context.resources
            return resources.getIdentifier(path, "drawable", context.packageName)
        }

        fun setImage(path:String?, imageView:ImageView):Boolean{
            val resourceId = getResourceId(path)
            if(resourceId!=0) { // if resource found
                imageView.setImageResource(resourceId)
                return true
            }
            return false
        }

        if (inflater != null) {
            view = inflater.inflate(com.g.laurent.alitic.R.layout.gridviewholder, parent, false)

            val imageView = view.findViewById<ImageView>(com.g.laurent.alitic.R.id.image_meal)
            val textView = view.findViewById<TextView>(com.g.laurent.alitic.R.id.meal_text)

            var text:String? = null
            var image:String? = null
            var imageDraw:String? = null

            if(any is Event){ // if event
                val eventType = getEventType(any.idEventType, false, context)
                text = eventType?.name
                image = eventType?.eventPic
            } else if(any is Meal){ // if meal
                val foods = getFoodsFromMeal(any, mode, context)
                text = foods[position].name
                image = foods[position].foodPic
                imageDraw = getFoodType(getFoodsFromMeal(any, mode, context)[position].idFoodType, context = context)?.foodTypePic
            }

            // Put the food name in text
            textView.text = text

            // Associate picture if it exists
            if(image!=null){

                if(URLUtil.isValidUrl(image)){ // image path is URL
                    Glide.with(context)
                        .load(image)
                        .apply(RequestOptions().placeholder(getResourceId(imageDraw)))
                        .into(imageView)
                } else {  // -------------------- image path is drawable type
                    if(!setImage(image, imageView)){
                        if(any is Meal){
                            if(imageDraw!=null)
                                setImage(imageDraw, imageView) // placeholder of the category of food type
                        }
                    }
                }
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