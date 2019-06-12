package com.g.laurent.alitic.Views

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.*
import com.g.laurent.alitic.*
import com.g.laurent.alitic.Controllers.ClassControllers.Chrono
import com.g.laurent.alitic.Models.FoodType
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.vipulasri.timelineview.TimelineView

class TimeLineViewHolder(itemView: View, viewType: Int, val mode:Boolean = false, val context: Context) : RecyclerView.ViewHolder(itemView) {

    private var mTimelineView: TimelineView = itemView.findViewById(R.id.timeline)
    private var buttonEdit:ImageButton
    private var hourView : TextView
    private var grid : GridView

    init {
        mTimelineView.initLine(viewType)
        buttonEdit = itemView.findViewById(R.id.edit_button)
        buttonEdit.setOnClickListener(View.OnClickListener {  }) // TODO button edit to configure
        hourView = itemView.findViewById(R.id.hour)
        grid = itemView.findViewById(R.id.grid)
    }

    fun configureTimeLineViewHolder(chrono: Chrono) {
        hourView.text = chrono.hour
        // TODO à adapter : val adapter = GridAdapter(chrono.item, null, false,null, mode, context)
        //grid.adapter = adapter
    }
}

class FoodTypeViewHolder(itemView: View, private val mode:Boolean = false, val context: Context) : RecyclerView.ViewHolder(itemView) {

    private var imageFood:ImageView = itemView.findViewById(R.id.image_foodtype)
    private var textFood:TextView = itemView.findViewById(R.id.text_foodtype)
    private val ratio = 0.8f
    private var index:Int = 0
    private var oldSelect:Int = -1
    private val duration = context.resources.getInteger(R.integer.duration_anim).toLong()
    private val colorNotSelected = ContextCompat.getColor(context, R.color.colorFoodTypeNOTselected)
    private val colorSelected = ContextCompat.getColor(context, R.color.colorFoodTypeSelected)

    fun configureFoodTypeViewHolder(newSelect:Int, width:Int, foodType: FoodType?, ind:Int?) {

        fun setNotSelected(){
            // WIDTH
            /*itemView.layoutParams.width = (width * ratio).toInt()
            imageFood.layoutParams.width = (width * ratio).toInt()
            textFood.layoutParams.width = (width * ratio).toInt()*/
            reduceThumbnail(this, 0, colorNotSelected)

            // COLOR
            itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorFoodTypeNOTselected))
        }

        // Initialization
        if(foodType!=null && ind!=null) {
            index = ind

            // Edit text to display
            textFood.text = foodType.name

            // Edit image to display
            val image = getImagePath(foodType, mode, context)
            val imageDraw = getImageDrawPath(foodType, mode, context)
            getImageFromPath(image, imageDraw, imageFood, context)

        } else { // empty foodtype
            setNotSelected()
        }

        // Adapt the layout of the viewholder (if selected or not)
        if(oldSelect!=-1 && newSelect!=-1){
            if(oldSelect!=newSelect && oldSelect==index)
                reduceThumbnail(this, duration, colorNotSelected)
            else if(index==newSelect)
                enlargeThumbnail(this, duration, colorSelected, ratio)
        } else if(oldSelect==-1 && newSelect==index)
                enlargeThumbnail(this, duration, colorSelected, ratio)
            else
                setNotSelected()

        oldSelect = newSelect
    }

    fun setEmptyViewAsLastItem(width:Int) {
        // WIDTH
        itemView.layoutParams.width = (width * ratio).toInt()
        imageFood.layoutParams.width = (width * ratio).toInt()
        textFood.layoutParams.width = (width * ratio).toInt()

        // COLOR TRANSPARENT
        itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
    }
}

class ChronoViewHolder(itemView: View?){
    var dayNum: TextView? = itemView?.findViewById(R.id.day_number)
    var fireIcon: ImageView? = itemView?.findViewById(R.id.fire_icon)
    var eventNum: TextView? = itemView?.findViewById(R.id.number_events)
}

class StatChronoHolder(itemView: View, list:List<Long>, month:Int, year:Int): RecyclerView.ViewHolder(itemView){
    var monthView: TextView = itemView.findViewById(R.id.month_text)
    var gridView: GridView = itemView.findViewById(R.id.grid_month)

    fun configureGridView(month:Int, year:Int){



    }
}
