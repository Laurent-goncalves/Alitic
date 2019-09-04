package com.g.laurent.alitic.Views

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.*
import com.g.laurent.alitic.*
import com.g.laurent.alitic.Controllers.Activities.StatType
import com.g.laurent.alitic.Controllers.ClassControllers.Chrono
import com.g.laurent.alitic.Controllers.ClassControllers.FoodStatEntry
import com.g.laurent.alitic.Controllers.Fragments.OnDeleteAction
import com.g.laurent.alitic.Models.EventType
import com.g.laurent.alitic.Models.Food
import com.g.laurent.alitic.Models.FoodType
import com.github.vipulasri.timelineview.TimelineView


class TimeLineViewHolder(itemView: View, viewType: Int, val mode:Boolean = false, val context: Context) : RecyclerView.ViewHolder(itemView) {

    private var mTimelineView: TimelineView = itemView.findViewById(R.id.timeline)
    private var hourView : TextView
    private var grid : StaticGridView

    init {
        mTimelineView.initLine(viewType)
        hourView = itemView.findViewById(R.id.hour)
        grid = itemView.findViewById(R.id.grid)
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

class FoodTop10ViewHolder(itemView: View, val context: Context) : RecyclerView.ViewHolder(itemView) {

    private var titleFood: TextView = itemView.findViewById(R.id.title_food)
    private var imageFood : ImageView =  itemView.findViewById(R.id.food_image)

    fun configureFoodTop10ViewHolder(foodStatEntry: FoodStatEntry, view:View, statType:StatType, onDeleteAction: OnDeleteAction) {

        // Set food title
        titleFood.text = foodStatEntry.food.name

        // Set food picture
        setImageResource(foodStatEntry.food.foodPic, imageFood, context)

        println("eee  ${foodStatEntry.food.name}     ${foodStatEntry.food.foodPic} ")

        // Set piechart around found picture
        configureSmallPieChart(foodStatEntry, statType, itemView, context)

        // Configure pop up menu
        imageFood.setOnLongClickListener {
            onDeleteAction.configurePopUpMenuFood(foodStatEntry, itemView, view)
            true
        }
    }
}

class FoodSettingsViewHolder(itemView: View, val context: Context):RecyclerView.ViewHolder(itemView){

    private var nameFood: TextView = itemView.findViewById(R.id.food_name)
    private var checkBox: CheckBox = itemView.findViewById(R.id.checkbox_taken_into_acc)

    fun configureFoodSettingsViewHolder(food: Food){
        nameFood.text = food.name
        checkBox.isChecked = food.forAnalysis
    }
}

class EventTypeSettingsViewHolder(itemView: View, val context: Context):RecyclerView.ViewHolder(itemView){

    private var nameEventType: TextView = itemView.findViewById(R.id.event_name)
    private var periodTakenIntAcc: TextView = itemView.findViewById(R.id.period_taken_into_acc)

    fun configureEventTypeSettingsViewHolder(eventType: EventType){

        fun getTextPeriodTakenIntoAccount():String{

            val forLastMeal = eventType.forLastMeal

            if(forLastMeal!=null && forLastMeal){
                return context.resources.getString(R.string.for_last_meal)
            } else {
                val hourMin = eventType.minTime
                val hourMax = eventType.maxTime

                if(hourMin != null && hourMax != null){
                    return context.resources.getString(R.string.occur_between) + " " + getMinTimeFromEventType(eventType) + " " +
                            context.resources.getString(R.string.and) + " " + getMaxTimeFromEventType(eventType) +
                            context.resources.getString(R.string.hrs_after_meal)
                }

                return context.resources.getString(R.string.undefined)
            }
        }

        nameEventType.text = eventType.name
        periodTakenIntAcc.text = getTextPeriodTakenIntoAccount()
    }
}