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
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.support.v4.content.ContextCompat
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import com.g.laurent.alitic.*
import com.g.laurent.alitic.Controllers.Activities.*
import com.g.laurent.alitic.Controllers.ClassControllers.*
import com.g.laurent.alitic.Models.*
import com.github.mikephil.charting.data.BarData
import com.roomorama.caldroid.CaldroidGridAdapter
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet


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
class TimeLineAdapter(val list: List<Chrono>, val mode:Boolean=false, val context: Context) : RecyclerView.Adapter<TimeLineViewHolder>() {

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
                is EventType -> { // if eventType
                    val eventType = listFood[position] as EventType
                    eventType.name
                }
                is Event -> { // if event
                    val event = listFood[position] as Event
                    getEventType(event.idEventType, mode, context)?.name
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
                is EventType -> { // if eventType
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
            val image = getImagePath(listFood[position]!!, mode, context)
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

/** CALENDAR ADAPTER**/
class CalendarAdapter(context: Context, private val onTimeLineDisplay: OnTimeLineDisplay, month: Int, year: Int, val mode:Boolean = false, caldroidData: Map<String, Any>, extraData: Map<String, Any>) : CaldroidGridAdapter(context, month, year, caldroidData, extraData) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?

        if(inflater!=null){

            val view = inflater.inflate(R.layout.calendar_item, parent, false)
            val holder = ChronoViewHolder(view)

            val chronoEvents = getChronoEvents(month, year, mode, context)
            val dateTime = this.datetimeList[position]

            // Change content of textview and imageview
            if (dateTime.month == month) {

                holder.dayNum?.text = dateTime.day.toString()

                    if (chronoEvents[dateTime] != 0){ // if there is an event this day
                    val text = "(" + chronoEvents[dateTime] + ")"
                    holder.eventNum?.text = text
                    holder.fireIcon?.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(context, android.R.color.holo_red_dark), PorterDuff.Mode.MULTIPLY)
                } else { // if no event this day
                    holder.eventNum?.visibility = View.INVISIBLE
                    holder.fireIcon?.visibility = View.INVISIBLE
                }

                // Configure on click listener for displaying timeline
                view.setOnClickListener {
                    onTimeLineDisplay.displayTimeLineFragment(dateTime.day, dateTime.month, dateTime.year)
                }

            } else {
                holder.eventNum?.setBackgroundResource(android.R.color.darker_gray)
                holder.fireIcon?.setBackgroundResource(android.R.color.darker_gray)
                holder.dayNum?.setBackgroundResource(android.R.color.darker_gray)
                holder.eventNum?.visibility = View.INVISIBLE
                holder.fireIcon?.visibility = View.INVISIBLE
                holder.dayNum?.visibility = View.INVISIBLE
            }


            return view
        }

        return null
    }
}

/** STAT ADAPTER**/
class StatAdapter(val context: Context, val listStats:List<StatList>): RecyclerView.Adapter<StatViewHolder>(){

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): StatViewHolder {
        val inflatedView = LayoutInflater.from(p0.context).inflate(R.layout.barchart_layout, p0, false)
        return StatViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return listStats.size
    }

    override fun onBindViewHolder(p0: StatViewHolder, position: Int) {

        val valueSet = arrayListOf<BarEntry>()
        val listStatEntries = listStats[p0.adapterPosition].listEntries
        val listFood = mutableListOf<String>()
        val eventType = getEventType(listStats[p0.adapterPosition].idEventType, context = context)?.name

        // Set title of the chart
        //p0.titleChart.text = eventType

        // Create bar entries
        if(listStatEntries.isNotEmpty()){
            for(i in 0 until listStatEntries.size){
                valueSet.add(BarEntry(i.toFloat(), listStatEntries[i].countNOK.toFloat()))
                listFood.add(listStatEntries[i].food!!)
            }
        }



        val barDataSet = BarDataSet(valueSet, eventType)
        val dataSets = ArrayList<IBarDataSet>()
        dataSets.add(barDataSet)

        // hide Y-axis and gridlines
        val left = p0.barChart.axisLeft
        left.setDrawLabels(false)
        left.setDrawGridLines(false)

        val right = p0.barChart.axisRight
        right.setDrawLabels(false)
        right.setDrawGridLines(false)

        // custom X-axis labels
        val xAxis = p0.barChart.xAxis
        xAxis.textSize = 18f
        xAxis.granularity = 1f // restrict the minimum interval of your axis to "1"
        xAxis.valueFormatter = MyXAxisValueFormatter(listFood.toTypedArray())

        // Finalize bar chart
        val barData = BarData(barDataSet)
        barData.barWidth = 0.3f
        p0.barChart.data = barData
        p0.barChart.legend.isEnabled = false
        p0.barChart.invalidate()
    }

}