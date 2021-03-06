package com.g.laurent.alitic.Views

import android.content.Context
import android.support.v7.widget.RecyclerView
import com.github.vipulasri.timelineview.TimelineView
import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.*
import android.widget.*
import com.g.laurent.alitic.*
import com.g.laurent.alitic.Controllers.Activities.*
import com.g.laurent.alitic.Controllers.ClassControllers.*
import com.g.laurent.alitic.Controllers.DialogFragments.OnChangeStateForAnalysis
import com.g.laurent.alitic.Controllers.Fragments.*
import com.g.laurent.alitic.Models.*
import com.roomorama.caldroid.CaldroidGridAdapter
import kotlinx.android.synthetic.main.timeline_viewholder.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.ceil


/** FOODTYPE ADAPTER**/
@SuppressLint("RecyclerView")
class FoodTypeAdapter(val list: List<FoodType>, private val OnMenuSelectionListener: OnMenuSelectionListener, val mode:Boolean=false, val context: Context) : RecyclerView.Adapter<FoodTypeViewHolder>() {

    var selection:Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodTypeViewHolder {
        val view = View.inflate(parent.context, R.layout.foodtype_viewholder, null)
        return FoodTypeViewHolder(view, mode, context)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: FoodTypeViewHolder, position: Int) {

        holder.configureFoodTypeViewHolder(
            holder.adapterPosition, selection, list[holder.adapterPosition]
        )

        holder.itemView.setOnClickListener {
            if(holder.adapterPosition < list.size) {
                selection = holder.adapterPosition
                OnMenuSelectionListener.onMenuSelected(selection)
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}

/** TIMELINE ADAPTER**/
class TimeLineAdapter(val list: MutableList<Chrono>, private val onChronoItemDeleted: OnChronoItemDeleted, private val fragment:TimeLineFragment, val mode:Boolean=false, val context: Context) : RecyclerView.Adapter<TimeLineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeLineViewHolder {
        val view = View.inflate(parent.context, R.layout.timeline_viewholder, null)
        return TimeLineViewHolder(view, viewType, mode, context)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: TimeLineViewHolder, position: Int) {
        holder.configureTimeLineViewHolder(list[position])
        configureGridHeight(position, holder)
        configureDeleteAction(list[position], holder)
    }

    override fun getItemViewType(position: Int): Int {
        return TimelineView.getTimeLineViewType(position, itemCount)
    }

    private fun configureDeleteAction(chrono: Chrono, holder: TimeLineViewHolder) {

        val onLongClickListener = View.OnLongClickListener {

            val popupMenu = PopupMenu(fragment.context, it, Gravity.CENTER)
            popupMenu.setOnMenuItemClickListener { item ->

                val context = this.context

                if (item.itemId == R.id.menu_delete && chrono.item.isNotEmpty()) {

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

    private fun configureGridHeight(position:Int, holder: TimeLineViewHolder){

        if(list[position].item.size > 4){

            val prefs = context.getSharedPreferences(SHAREDPREF, 0)

            val heightGrid = prefs.getInt(SHARED_PREF_GRID_DHEIGHT,0)

            if(heightGrid<=0) {
                val vto = holder.itemView.grid.viewTreeObserver
                vto.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        // Remove after the first run so it doesn't fire forever
                        holder.itemView.grid.viewTreeObserver.removeOnPreDrawListener(this)
                        prefs.edit().putInt(SHARED_PREF_GRID_DHEIGHT, holder.itemView.grid.layoutParams.height).apply()
                        notifyDataSetChanged()
                        return true
                    }
                })
            } else {
                val factor = ceil(list[position].item.size.toDouble() / 4.toDouble()).toInt()
                holder.itemView.framelayout_timeline.layoutParams.height = factor * heightGrid + (0.6666 * heightGrid).toInt()
            }
        }
    }
}

/** ADAPTER FOR GRIDVIEW TO DISPLAY PICTURE + NAME **/
class GridAdapter(private val listItems: List<*>, var listItemSelected: MutableList<Any>?, private val pickmode:Boolean, private val onActionPickListener: OnActionPickListener?, val mode:Boolean=false, val context: Context): BaseAdapter() { // any is Event or Meal

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

            return when (listItems[position]) {
                is EventType -> { // if eventType
                    val eventType = listItems[position] as EventType
                    eventType.name
                }
                is Event -> { // if event
                    val event = listItems[position] as Event
                    getEventType(event.idEventType, mode, context)?.name
                }
                is Food -> {
                    val food = listItems[position] as Food
                    food.name
                }
                else ->
                    null
            }
        }

        fun setPickItem(frame:FrameLayout, pickIcon:ImageView){

            fun isAlreadySelected(idSelected:Long?,  list:MutableList<Any>?):Boolean{

                if(idSelected!=null && list!=null){
                    for(item in list){
                        when(item){
                            is Food -> {
                                if(item.id == idSelected)
                                    return true
                            }
                            is EventType -> {
                                if(item.id == idSelected)
                                    return true
                            }
                        }
                    }
                }
                return false
            }

            fun selectItem(select:Boolean){
                if(select){
                    frame.setBackgroundResource(R.drawable.border_validation)
                    pickIcon.visibility = View.VISIBLE
                } else {
                    frame.setBackgroundResource(0)
                    pickIcon.visibility = View.GONE
                }
            }

            if(onActionPickListener!=null){
                when (listItems[position]) {
                    is EventType -> { // if eventType
                        val eventType = listItems[position] as EventType
                        selectItem(isAlreadySelected(eventType.id, listItemSelected))
                    }
                    is Food -> {
                        val food = listItems[position] as Food
                        selectItem(isAlreadySelected(food.id, listItemSelected))
                    }
                }
            }
        }

        fun configureItem(view:View){

            val imageView = view.findViewById<ImageView>(R.id.image_meal)
            val textView = view.findViewById<TextView>(R.id.meal_text)
            val frameLayout = view.findViewById<FrameLayout>(R.id.framelayout_viewholder)
            val pickIcon = view.findViewById<ImageView>(R.id.check_item)

            // Put the food name in text
            val text = getTextToDisplay()
            textView.text = text

            // Associate picture if it exists
            setImageInImageView(listItems[position]!!, imageView, mode, context)

            // change layout in case of validation for picking
            if(pickmode){
                setPickItem(frameLayout, pickIcon)

                // Set onClickListener
                view.setOnClickListener {

                    // Remove or add item in listSelected from Activity & from this adapter
                    if(listItems[position]!=null) {

                        // from Activity
                        onActionPickListener?.onItemSelected(listItems[position]!!)

                        // from this adapter
                        listItemSelected = onActionPickListener?.updateListSelected(listItems[position]!!, listItemSelected)
                    }

                    // Update adapter
                    notifyDataSetChanged()
                }

                // Configure context menu for deleting food or event type
                view.setOnLongClickListener{

                    when(listItems[position]) {
                        is EventType -> { // if eventType
                            val eventType = listItems[position] as EventType
                            view.tag = eventType.id.toString()
                        }
                        is Food -> {
                            val food = listItems[position] as Food
                            view.tag = food.id.toString()
                        }
                    }

                    // Show pop up menu
                    val any = listItems[position]
                    if(any!=null)
                        onActionPickListener?.showPopUpMenu(view, any)

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
                onActionPickListener?.showAddDialog()
            }
        }

        // create view
        if (inflater != null) {

            when {
                position < listItems.size -> { // FOOD ITEM
                    view = inflater.inflate(R.layout.gridviewholder, parent, false)
                    configureItem(view)
                }
                position == listItems.size -> { // BUTTON ADD ITEM
                    view = inflater.inflate(R.layout.gridviewholder_add, parent, false)
                    configureAddButton(view)
                }
                else -> {
                    view = inflater.inflate(R.layout.empty_gridviewholder, parent, false)
                }
            }
        }

        return view
    }

    override fun getCount(): Int {
        return if(pickmode)
            listItems.size + 4
        else
            listItems.size
    }
}

/** CALENDAR ADAPTER**/
class CalendarAdapter(private val frag:ChronoFragment, context: Context, private val onTimeLineDisplay: OnTimeLineDisplay, month: Int, year: Int, val mode:Boolean = false, caldroidData: Map<String, Any>, extraData: Map<String, Any>) : CaldroidGridAdapter(context, month, year, caldroidData, extraData) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?

        if(inflater!=null){

            val view = inflater.inflate(R.layout.calendar_item, parent, false)
            val holder = ChronoViewHolder(view)
            val dateTime = this.datetimeList[position]

            // Change content of textview and imageview
            if (dateTime.month == month) {

                val hasEvent = frag.chronoEvents.contains(getDateTimeFromLong(dateTime.getMilliseconds(TimeZone.getDefault())))
                val hasMeal = frag.chronoMeals.contains(getDateTimeFromLong(dateTime.getMilliseconds(TimeZone.getDefault())))

                // Set day number
                holder.dayNum?.text = dateTime.day.toString()

                // Set image background
                if (hasMeal && hasEvent) {
                    //frag.setBackgroundDrawableForDate(eventAndMeal, date)
                    holder.imageBackground?.setImageResource(R.drawable.background_event_and_meal)
                } else if (hasMeal) {
                    //frag.setBackgroundDrawableForDate(mealOnly, date)
                    holder.imageBackground?.setImageResource(R.drawable.background_meal_only)
                } else if (hasEvent){
                    //frag.setBackgroundDrawableForDate(eventOnly, date)
                    holder.imageBackground?.setImageResource(R.drawable.background_event_only)
                }

                // Configure on click listener for displaying timeline
                if (hasEvent || hasMeal) {
                    view.setOnClickListener {
                        onTimeLineDisplay.displayTimeLineFragment(dateTime.day, dateTime.month, dateTime.year)
                    }
                }
            }
            return view
        }
        return null
    }
}

/** STAT ADAPTER**/
open class StatAdapter(mgr: FragmentManager, val context: Context, var position: Int, private val listEventType: List<EventType>): FragmentPagerAdapter(mgr) {

    private fun getTitlesFromListEventTypes(list:List<EventType>):ArrayList<String>{

        val result = arrayListOf<String>()

        if(list.isNotEmpty()){
            for(e in list){
                val title = e.name
                if(title!=null){
                    result.add(title)
                }
            }
        }
        return result
    }

    override fun getItemPosition(`object`: Any): Int {
        if(`object` is StatDetailFragment)
            return POSITION_NONE
        return POSITION_UNCHANGED
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> StatGlobalFragment().newInstance(StatType.GLOBAL_ANALYSIS_NEG)
            1 -> StatGlobalFragment().newInstance(StatType.GLOBAL_ANALYSIS_POS)
            else -> StatDetailFragment().newInstance(getTitlesFromListEventTypes(listEventType))
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

/** STAT INFO ADAPTER**/
class StatInfoAdapter (mgr: FragmentManager): FragmentPagerAdapter(mgr){

    override fun getItem(page: Int): Fragment {
        return StatInfoFragment().newInstance(page)
    }

    override fun getCount(): Int {
        return 5
    }

    class StatInfoFragment : Fragment() {

        fun newInstance(page:Int):StatInfoFragment{

            val frag = StatInfoFragment()
            val args = Bundle()
            args.putInt(PAGE, page)
            frag.arguments = args

            return frag
        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {

            val args = arguments

            return if(args!=null){

                when(args.getInt(PAGE, 0)){
                    0-> inflater.inflate(R.layout.stat_info_panel1, container, false)
                    1-> inflater.inflate(R.layout.stat_info_panel2, container, false)
                    2-> inflater.inflate(R.layout.stat_info_panel3, container, false)
                    3-> inflater.inflate(R.layout.stat_info_panel4, container, false)
                    else -> inflater.inflate(R.layout.stat_info_panel5, container, false)
                }
            } else
                inflater.inflate(R.layout.stat_info_panel1, container, false)
        }
    }
}

/**  CHRONOLOGY STAT **/
class StatChronoAdapter(val list:List<Long>, val context: Context): RecyclerView.Adapter<StatChronoHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): StatChronoHolder {
        val view = View.inflate(p0.context, R.layout.stat_chrono_viewholder, null)
        return StatChronoHolder(view)
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

/** FOOD ITEMS MEAL PICK ADAPTER**/
@SuppressLint("RecyclerView")
class MealPickAdapter(private val listSelected: MutableList<Food>, private val onFoodToDeleteListener:OnFoodToDeleteListener, val context: Context) : RecyclerView.Adapter<MealPickViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealPickViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.food_item_pick_meal_layout, parent, false)
        //val view = View.inflate(parent.context, R.layout.food_item_pick_meal_layout, null)
        return MealPickViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if(listSelected.isEmpty())
            1
        else
            listSelected.size
    }

    override fun onBindViewHolder(holder: MealPickViewHolder, position: Int) {

        if(listSelected.isEmpty()){

            holder.foodName.text = context.resources.getString(R.string.message_no_food)
            holder.deleteButton.visibility = View.GONE

        } else {

            val foodName = listSelected[holder.adapterPosition].name

            holder.foodName.text = foodName

            holder.deleteButton.setOnClickListener {
                if(foodName!=null) {
                    onFoodToDeleteListener.onFoodToDelete(foodName)
                    if(holder.adapterPosition >= 0 && holder.adapterPosition < listSelected.size)
                        listSelected.removeAt(holder.adapterPosition)
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}

/**  TOP 10 FOOD STAT **/
class FoodTop10Adapter(val list:List<FoodStatEntry>, val view:View, private val statType: StatType, private val onDeleteAction:OnDeleteAction, val context: Context): RecyclerView.Adapter<FoodTop10ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): FoodTop10ViewHolder {
        val view = View.inflate(p0.context, R.layout.food_stat_layout, null)
        return FoodTop10ViewHolder(view, context)
    }

    override fun getItemCount(): Int {
        return if(list.size > 10)
            10
        else
            list.size
    }

    override fun onBindViewHolder(p0: FoodTop10ViewHolder, p1: Int) {
        p0.configureFoodTop10ViewHolder(list[p1], view, statType, onDeleteAction)
    }
}

/**  FOOD SETTINGS **/
class FoodSettingsAdapter(val list:List<Food>, private val onChangeStateForAnalysis: OnChangeStateForAnalysis, val context: Context): RecyclerView.Adapter<FoodSettingsViewHolder>() {

    private val listFood = list.toMutableList()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): FoodSettingsViewHolder {
        val view = View.inflate(p0.context, R.layout.food_setting_layout, null)
        return FoodSettingsViewHolder(view, context)
    }

    override fun getItemCount(): Int {
        return listFood.size
    }

    override fun onBindViewHolder(holder: FoodSettingsViewHolder, p1: Int) {
        holder.configureFoodSettingsViewHolder(listFood[holder.adapterPosition])

        holder.itemView.findViewById<CheckBox>(R.id.checkbox_taken_into_acc).setOnCheckedChangeListener { _, isChecked ->

            if(holder.adapterPosition >= 0 && holder.adapterPosition < list.size){
                val food = listFood[holder.adapterPosition]
                food.forAnalysis = isChecked
                listFood[holder.adapterPosition] = food
                onChangeStateForAnalysis.changeStateForAnalysis(food)
            }
        }
    }
}

/**  EVENT SETTINGS **/
class EventTypeSettingsAdapter(val list:List<EventType>, val context: Context): RecyclerView.Adapter<EventTypeSettingsViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): EventTypeSettingsViewHolder {
        val view = View.inflate(p0.context, R.layout.event_setting_layout, null)
        return EventTypeSettingsViewHolder(view, context)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: EventTypeSettingsViewHolder, p1: Int) {
        holder.configureEventTypeSettingsViewHolder(list[holder.adapterPosition])
    }
}

enum class DayGrid(val colorId: Int){
    DONT_EXISTS(android.R.color.transparent),
    NO_EVENT_DAY(R.color.colorNoEventDay),
    EVENT_LEV1(R.color.colorLevel1),
    EVENT_LEV2(R.color.colorLevel2),
    EVENT_LEV3(R.color.colorLevel3);
}

enum class RowDay(val dayOfWeek: Int, val row: Int){
    MONDAY(Calendar.MONDAY, 0),
    TUESDAY(Calendar.TUESDAY, 1),
    WEDNESDAY(Calendar.WEDNESDAY, 2),
    THURSDAY(Calendar.THURSDAY, 3),
    FRIDAY(Calendar.FRIDAY, 4),
    SATURDAY(Calendar.SATURDAY, 5),
    SUNDAY(Calendar.SUNDAY, 6);
}