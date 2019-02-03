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
import android.widget.FrameLayout
import android.annotation.SuppressLint
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import com.g.laurent.alitic.*
import com.g.laurent.alitic.Controllers.Activities.OnItemSelectionListener
import com.g.laurent.alitic.Controllers.Activities.OnMenuSelectionListener
import com.g.laurent.alitic.Controllers.Activities.isAlreadySelected
import com.g.laurent.alitic.Controllers.Activities.updateListSelected
import com.g.laurent.alitic.Controllers.ClassControllers.*
import com.g.laurent.alitic.Models.*


/** FOODTYPE ADAPTER**/
@SuppressLint("RecyclerView")
class FoodTypeAdapter(val list: List<FoodType>, val recyclerView: RecyclerView, val OnMenuSelectionListener: OnMenuSelectionListener, val mode:Boolean=false, val context: Context) : RecyclerView.Adapter<FoodTypeViewHolder>() {

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
            OnMenuSelectionListener.onMenuSelected(selection)
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
class GridAdapter(val listFood: List<*>, var listItemSelected: MutableList<Any>?, val pickmode:Boolean, val onItemSelectionListener: OnItemSelectionListener?, val mode:Boolean=false, val context: Context): BaseAdapter() { // any is Event or Meal

    override fun getItem(position: Int): Any {
        return this
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

        var view: View? = null
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?

        fun getTextToDisplay():String?{

            return when (listFood[position]) {
                is EventType -> { // if event
                    val eventType = listFood[position] as EventType
                    eventType.name
                }
                is Food -> {
                    val food = listFood[position] as Food
                    food.name
                }
                else ->
                    null
            }
        }

        fun setPickItem(frame:FrameLayout, pickIcon:ImageView){

            fun selectItem(select:Boolean){
                if(select){
                    frame.setBackgroundResource(R.drawable.border_validation)
                    pickIcon.visibility = View.VISIBLE
                } else {
                    frame.setBackgroundResource(0)
                    pickIcon.visibility = View.GONE
                }
            }

            when (listFood[position]) {
                is EventType -> { // if event
                    val eventType = listFood[position] as EventType
                    selectItem(isAlreadySelected(eventType.id, listItemSelected))
                }
                is Food -> {
                    val food = listFood[position] as Food
                    selectItem(isAlreadySelected(food.id, listItemSelected))
                }
            }
        }

        // create view
        if (inflater != null && listFood[position]!=null) {
            view = inflater.inflate(com.g.laurent.alitic.R.layout.gridviewholder, parent, false)

            val imageView = view.findViewById<ImageView>(com.g.laurent.alitic.R.id.image_meal)
            val textView = view.findViewById<TextView>(com.g.laurent.alitic.R.id.meal_text)
            val frameLayout = view.findViewById<FrameLayout>(com.g.laurent.alitic.R.id.framelayout_viewholder)
            val pickIcon = view.findViewById<ImageView>(com.g.laurent.alitic.R.id.check_item)

            val text = getTextToDisplay()
            val image = getImagePath(listFood[position]!!)
            val imageDraw = getImageDrawPath(listFood[position]!!, mode, context)

            // Put the food name in text
            textView.text = text

            // Associate picture if it exists
            getImageFromPath(image, imageDraw, imageView, context)

            // change layout in case of validation for picking
            if(pickmode)
                setPickItem(frameLayout, pickIcon)
        }

        // Set onClickListener
        view?.setOnClickListener {

            // Remove or add item in listSelected from Activity & from this adapter
            if(listFood[position]!=null) {

                // from Activity
                onItemSelectionListener?.onItemSelected(listFood[position]!!)

                // from this adapter
                listItemSelected = updateListSelected(listFood[position]!!, listItemSelected)
            }

            // Update adapter
            notifyDataSetChanged()
        }

        return view
    }

    override fun getCount(): Int {
        return listFood.size
    }
}