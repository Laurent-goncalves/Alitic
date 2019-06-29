package com.g.laurent.alitic.Views

import android.content.Context
import android.support.v7.widget.RecyclerView
import com.github.vipulasri.timelineview.TimelineView
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.view.PagerAdapter
import android.support.v4.view.PagerAdapter.POSITION_NONE
import android.support.v4.view.PagerAdapter.POSITION_UNCHANGED
import android.support.v7.app.AlertDialog
import android.view.*
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.*
import com.g.laurent.alitic.*
import com.g.laurent.alitic.Controllers.Activities.*
import com.g.laurent.alitic.Controllers.ClassControllers.*
import com.g.laurent.alitic.Controllers.Fragments.*
import com.g.laurent.alitic.Models.*
import com.roomorama.caldroid.CaldroidGridAdapter



/** FOODTYPE ADAPTER**/
@SuppressLint("RecyclerView")
class FoodTypeAdapter(val list: List<FoodType>, val sWidth:Int, private val OnMenuSelectionListener: OnMenuSelectionListener, val mode:Boolean=false, val context: Context) : RecyclerView.Adapter<FoodTypeViewHolder>() {

    var selection:Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodTypeViewHolder {
        val view = View.inflate(parent.context, R.layout.foodtype_viewholder, null)
        return FoodTypeViewHolder(view, mode, context)
    }

    override fun getItemCount(): Int {
        return list.size + 1
    }

    override fun onBindViewHolder(holder: FoodTypeViewHolder, position: Int) {

        holder.itemView.setOnClickListener {
            if(holder.adapterPosition < list.size) {
                selection = holder.adapterPosition
                OnMenuSelectionListener.onMenuSelected(selection)
                notifyDataSetChanged()
            }
        }

        val vto = holder.itemView.viewTreeObserver
        vto.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                holder.itemView.viewTreeObserver.removeOnPreDrawListener(this)

                if(holder.adapterPosition!=-1 && holder.adapterPosition < list.size - 1)
                    holder.configureFoodTypeViewHolder(selection, sWidth, list[holder.adapterPosition], holder.adapterPosition)
                else if(holder.adapterPosition!=-1 && holder.adapterPosition == list.size) // LAST ITEM OF THE LIST
                    holder.setEmptyViewAsLastItem(sWidth)
                else
                    holder.configureFoodTypeViewHolder(selection, sWidth, null,null)

                return true
            }
        })
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}

/** TIMELINE ADAPTER**/
class TimeLineAdapter(val list: MutableList<Chrono>, val onChronoItemDeleted: OnChronoItemDeleted, val fragment:TimeLineFragment, val mode:Boolean=false, val context: Context) : RecyclerView.Adapter<TimeLineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeLineViewHolder {
        val view = View.inflate(parent.context, R.layout.timeline_viewholder, null)
        return TimeLineViewHolder(view, viewType, mode, context)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: TimeLineViewHolder, position: Int) {
        holder.configureTimeLineViewHolder(list[position])
        configureDeleteAction(list[position], holder)
    }

    override fun getItemViewType(position: Int): Int {
        return TimelineView.getTimeLineViewType(position, itemCount)
    }

    private fun configureDeleteAction(chrono: Chrono, holder: TimeLineViewHolder) {

        val onLongClickListener = View.OnLongClickListener {

            val popupMenu = PopupMenu(fragment.context, it, Gravity.CENTER)
            popupMenu.setOnMenuItemClickListener {

                val context = this.context

                if (it.itemId == R.id.menu_delete && chrono.item.isNotEmpty()) {

                    val builder = AlertDialog.Builder(context)

                    // Display a message on alert dialog
                    if (chrono.typeDisplay.equals(TypeDisplay.EVENT)) {
                        builder.setTitle(context.resources.getString(R.string.event_type_delete_title)) // TITLE
                        builder.setMessage(context.resources.getString(R.string.event_type_delete)) // MESSAGE
                    } else {
                        builder.setTitle(context.resources.getString(R.string.meal_delete_title)) // TITLE
                        builder.setMessage(context.resources.getString(R.string.meal_delete)) // MESSAGE
                    }

                    // Set positive button and its click listener on alert dialog
                    builder.setPositiveButton(context.resources.getString(R.string.yes)) { dialog, _ ->
                        dialog.dismiss()

                        if (chrono.typeDisplay.equals(TypeDisplay.EVENT)) { // Delete event
                            deleteEvent(chrono.id, context = context)
                            Toast.makeText(
                                context,
                                context.resources.getString(R.string.event_type_deleted),
                                Toast.LENGTH_LONG
                            ).show()
                        } else { // Delete meal
                            deleteMeal(chrono.id, context = context)
                            Toast.makeText(
                                context,
                                context.resources.getString(R.string.meal_deleted),
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        // Update TimeLineFragment
                        onChronoItemDeleted.chronoItemDeleted(chrono)
                    }

                    // Display negative button on alert dialog
                    builder.setNegativeButton(context.resources.getString(R.string.no)) { dialog, _ ->
                        dialog.dismiss()
                    }

                    // Finally, make the alert dialog using builder
                    val dialog: AlertDialog = builder.create()

                    // Display the alert dialog on app interface
                    dialog.show()
                }

                true
            }
            popupMenu.inflate(R.menu.menu_chrono)
            popupMenu.show()

            true
        }

        holder.itemView.findViewById<ImageView>(R.id.timeline_viewholder).setOnLongClickListener(onLongClickListener)
    }


}

/** ADAPTER FOR GRIDVIEW TO DISPLAY PICTURE + NAME **/
class GridAdapter(private val listFood: List<*>, var listItemSelected: MutableList<Any>?, private val pickmode:Boolean, private val onItemSelectionListener: OnItemSelectionListener?, val mode:Boolean=false, private val activity:PickActivity?, val context: Context): BaseAdapter() { // any is Event or Meal


    override fun getItem(position: Int): Any {
        return this
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
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

        fun configureItem(view:View){

            val imageView = view.findViewById<ImageView>(R.id.image_meal)
            val textView = view.findViewById<TextView>(R.id.meal_text)
            val frameLayout = view.findViewById<FrameLayout>(R.id.framelayout_viewholder)
            val pickIcon = view.findViewById<ImageView>(R.id.check_item)

            val text = getTextToDisplay()
            val image = getImagePath(listFood[position]!!, mode, context)
            val imageDraw = getImageDrawPath(listFood[position]!!, mode, context)

            // Put the food name in text
            textView.text = text

            // Associate picture if it exists
            getImageFromPath(image, imageDraw, imageView, context)

            // change layout in case of validation for picking
            if(pickmode){
                setPickItem(frameLayout, pickIcon)

                // Set onClickListener
                view.setOnClickListener {

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

                // Configure context menu for deleting food or event type
                view.setOnLongClickListener{

                    when(listFood[position]) {
                        is EventType -> { // if eventType
                            val eventType = listFood[position] as EventType
                            view.tag = eventType.id.toString()
                        }
                        is Food -> {
                            val food = listFood[position] as Food
                            view.tag = food.id.toString()
                        }
                    }

                    val popupMenu = PopupMenu(activity, view)
                    popupMenu.setOnMenuItemClickListener{

                        if(it.itemId == R.id.menu_delete && activity!=null){

                            val builder = AlertDialog.Builder(activity)

                            // Display a message on alert dialog
                            when(listFood[position]) {
                                is EventType -> { // if eventType
                                    builder.setTitle(context.resources.getString(R.string.event_type_delete_title)) // TITLE
                                    builder.setMessage(context.resources.getString(R.string.event_type_delete)) // MESSAGE
                                }
                                is Food -> {
                                    builder.setTitle(context.resources.getString(R.string.food_delete_title)) // TITLE
                                    builder.setMessage(context.resources.getString(R.string.food_delete)) // MESSAGE
                                }
                            }

                            // Set positive button and its click listener on alert dialog
                            builder.setPositiveButton(context.resources.getString(R.string.yes)){ dialog, _ ->
                                dialog.dismiss()

                                val idToDelete = if(listFood[position] is Food){(listFood[position] as Food).id} else {(listFood[position] as EventType).id}
                                val typeDisplay = if(listFood[position] is Food){TypeDisplay.MEAL} else {TypeDisplay.EVENT}

                                /*if(idToDelete!=null)
                                    activity.deleteFromDatabase(idToDelete, typeDisplay)*/
                            }

                            // Display negative button on alert dialog
                            builder.setNegativeButton(context.resources.getString(R.string.no)){ dialog, _ ->
                                dialog.dismiss()
                            }

                            // Finally, make the alert dialog using builder
                            val dialog: AlertDialog = builder.create()

                            // Display the alert dialog on app interface
                            dialog.show()

                        } else if(it.itemId == R.id.menu_modify){

                            when(listFood[position]) {
                                is EventType -> { // if eventType
                                    val eventType = listFood[position] as EventType
                                    //activity?.showDialogAddEventType(eventType)
                                }
                                is Food -> {
                                    val food = listFood[position] as Food
                                    //activity?.showDialogAddFood(food)
                                }
                            }
                        }
                        true
                    }
                    popupMenu.inflate(R.menu.menu_grid)
                    popupMenu.show()
                    true
                }
            } else {
                view.isEnabled = false
                view.isSoundEffectsEnabled = false
            }
        }

        fun configureAddButton(view:View){

            val textView = view.findViewById<TextView>(R.id.add_text)
            textView.text = context.resources.getString(R.string.add)

            // Set onClickListener
            view.setOnClickListener {
                if(activity!=null){
                    if(activity.foodTypeSelected!=null){ // IF MEAL PANEL
                        val food = Food()
                        food.idFoodType = activity.foodTypeSelected?.id
                        food.foodPic = activity.foodTypeSelected?.foodTypePic
                        activity.showDialogAddFood(food)

                    } else { // IF EVENT TYPE PANEL
                        val eventType = EventType()
                        activity.showDialogAddEventType(eventType)
                    }
                }
            }
        }

        // create view
        if (inflater != null) {

            if(position < listFood.size) { // FOOD ITEM
                view = inflater.inflate(R.layout.gridviewholder, parent, false)
                configureItem(view)
            } else { // BUTTON ADD ITEM
                view = inflater.inflate(R.layout.gridviewholder_add, parent, false)
                configureAddButton(view)
            }
        }

        return view
    }

    override fun getCount(): Int {
        return if(pickmode)
            listFood.size + 1
        else
            listFood.size
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

                    // Configure on click listener for displaying timeline
                    view.setOnClickListener {
                        onTimeLineDisplay.displayTimeLineFragment(dateTime.day, dateTime.month, dateTime.year)
                    }

                } else { // if no event this day
                    holder.eventNum?.visibility = View.INVISIBLE
                    holder.fireIcon?.visibility = View.INVISIBLE
                }

            } else {
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
open class StatAdapter(mgr: FragmentManager, val context: Context, var eventType: EventType, private val listEventType: ArrayList<String>): FragmentPagerAdapter(mgr) {


    override fun getItemPosition(`object`: Any): Int {

        //mgr.executePendingTransactions()

        if(`object` is StatDetailFragment)
            return POSITION_NONE
        return POSITION_UNCHANGED
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> StatGlobalFragment().newInstance(null, StatType.GLOBAL_ANALYSIS_NEG)
            1 -> StatGlobalFragment().newInstance(null, StatType.GLOBAL_ANALYSIS_POS)
            else -> StatDetailFragment().newInstance(eventType, StatType.DETAIL_ANALYSIS, listEventType)
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {

        return when (position) {
            0 -> context.resources.getString(StatType.GLOBAL_ANALYSIS_NEG.titleTab)
            1 -> context.resources.getString(StatType.GLOBAL_ANALYSIS_POS.titleTab)
            else -> context.resources.getString(StatType.DETAIL_ANALYSIS.titleTab)
        }
    }
}

/** DETAIL STAT TITLE ADAPTER**/
class DetailStatTitleAdapter(val list:List<String>, val context: Context, private val fragment:StatDetailFragment): PagerAdapter() {

    override fun getCount(): Int {
        return list.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.title_stat_detail, container, false)
        //setTextInEachTitle(layout, position)
        //container.addView(layout)
        return layout
    }

    override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
        container.removeView(view as View)
    }

    override fun isViewFromObject(p0: View, p1: Any): Boolean {
        return p0 == p1
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return list[position]
    }
}

/**  CHRONOLOGY STAT **/
class StatChronoAdapter(val list:List<Long>, val context: Context): RecyclerView.Adapter<StatChronoHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): StatChronoHolder {
        val view = View.inflate(p0.context, R.layout.stat_chrono_viewholder, null)
        return StatChronoHolder(view, list, getMonthItem(list, p1), getYearItem(list, p1))
    }

    override fun getItemCount(): Int {
        return getNumberOfMonths(list)
    }

    override fun onBindViewHolder(p0: StatChronoHolder, p1: Int) {

        val month = getMonthItem(list, p1)
        val year = getYearItem(list, p1)

        // Configure text date
        p0.monthView.text = getMonthText(month, year)

        // Configure grid view
        val list = getListDayGridForGridView(list, month, year)
        p0.gridView.numColumns = getNumberColumnsGridView(getFirstDateGridView(month, year), getLastDateGridView(month, year))
        val gridAdapter = GridChronoAdapter(list, context)
        p0.gridView.adapter = gridAdapter
    }
}

/**  GRIDVIEW CHRONOLOGY STAT **/
class GridChronoAdapter(val list:List<DayGrid>, val context: Context): BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var view: View? = null
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?

        if (inflater != null) {
            view = inflater.inflate(R.layout.grid_stat_chrono, parent, false)
            view.background = ContextCompat.getDrawable(context, list[position].colorId)
        }

        return view
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getItem(position: Int): Any {
        return this
    }

    override fun getCount(): Int {
        return list.size
    }
}

enum class DayGrid(val colorId: Int){
    DONT_EXISTS(android.R.color.white),
    NO_EVENT_DAY(R.color.colorNoEventDay),
    EVENT_LEV1(R.color.colorLevel1),
    EVENT_LEV2(R.color.colorLevel2),
    EVENT_LEV3(R.color.colorLevel3);
}