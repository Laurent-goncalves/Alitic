@file:Suppress("UNCHECKED_CAST")

package com.g.laurent.alitic.Views

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.widget.*
import com.g.laurent.alitic.*
import com.g.laurent.alitic.Controllers.Activities.*
import com.g.laurent.alitic.Controllers.ClassControllers.*
import com.g.laurent.alitic.Models.Event
import com.g.laurent.alitic.Models.EventType
import com.g.laurent.alitic.Models.Food
import com.g.laurent.alitic.Models.TypeDisplay
import kotlinx.android.synthetic.main.time_picker_dialog.*

class SaveDialog : DialogFragment() {

    private lateinit var listFoodtems:List<Food>
    private lateinit var listEventType:List<EventType>
    private lateinit var typeDisplay: TypeDisplay
    private lateinit var contextDialog:Context

    fun newInstance(typeDisplay: TypeDisplay, listItems:List<Any>): SaveDialog {

        val saveDialog = SaveDialog()

        val gson = Gson()
        val jsonList = gson.toJson(listItems)
        val jsonType = gson.toJson(typeDisplay)

        val args = Bundle()
        args.putString(LIST_TO_SAVE, jsonList)
        args.putString(TYPE_DISPLAY, jsonType)
        saveDialog.arguments = args

        return saveDialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val v = inflater.inflate(R.layout.save_pick_layout, container, false)

        // Recover list of items and typeDisplay
        val gson = Gson()
        val jsonType = arguments?.getString(TYPE_DISPLAY, null)
        val propTypeDisplay = object : TypeToken<TypeDisplay>() {}.type
        typeDisplay = gson.fromJson(jsonType, propTypeDisplay)

        val jsonList = arguments?.getString(LIST_TO_SAVE, null)
        val propTypeList = if(typeDisplay.equals(TypeDisplay.MEAL))
            object : TypeToken<List<Food>>() {}.type
        else
            object : TypeToken<List<EventType>>() {}.type

        if(typeDisplay.equals(TypeDisplay.MEAL)){

            listFoodtems = gson.fromJson(jsonList, propTypeList)
            listEventType = listOf() // empty list

            // Configure title and content of dialog
            v.findViewById<TextView>(R.id.save_dialog_title).text = contextDialog.getString(R.string.save_meal_title_dialog)
            v.findViewById<TextView>(R.id.save_dialog_content).text = contextDialog.getString(R.string.save_meal_content_dialog)
        } else {

            listEventType = gson.fromJson(jsonList, propTypeList)
            listFoodtems = listOf() // empty list

            // Configure title and content of dialog
            v.findViewById<TextView>(R.id.save_dialog_title).text = contextDialog.getString(R.string.save_event_title_dialog)
            v.findViewById<TextView>(R.id.save_dialog_content).text = contextDialog.getString(R.string.save_event_content_dialog)
        }

        // Configure buttons YES and NO
        configureButtonsClick(v)

        return v
    }

    private fun configureButtonsClick(view:View){

        /**    BUTTON YES    **/
        view.findViewById<Button>(R.id.button_yes).setOnClickListener {

            // Save the meal or event with today's date
            if(context!=null) {
                if(typeDisplay.equals(TypeDisplay.MEAL)) {

                    // Save new meal in DB
                    saveNewMeal(listFoodtems.map { it.id }, getTodayDate(), context = contextDialog)

                    // Show snackbar "meal successfully saved"
                    val message = contextDialog.resources.getString(R.string.save_meal_success_message_dialog)
                    (activity as PickActivity).showConfirmationMessageInSnackBar(message)

                } else {

                    // Save event(s) in DB
                    if(listEventType.isNotEmpty()){
                        for(eventType in listEventType){
                            val eventToSave = Event(null, eventType.id, getTodayDate())
                            saveNewEvent(eventToSave.idEventType, getTodayDate(), context = contextDialog)
                        }
                    }

                    // Show snackbar "meal successfully saved"
                    val message = contextDialog.resources.getString(R.string.save_event_success_message_dialog)
                    (activity as PickActivity).showConfirmationMessageInSnackBar(message)
                }

            } else
                Toast.makeText(activity, R.string.save_abort_message_dialog, Toast.LENGTH_LONG).show()

            // Dismiss dialog
            dismiss()

            // Go back to Main Page
            (activity as PickActivity).goToBackToMainPage()
        }

        /**    BUTTON NO    **/
        view.findViewById<Button>(R.id.button_no).setOnClickListener {

            // Launch dialog to pick time and date
            val fm = fragmentManager
            val myDialogFragment = DateTimePickerDialog().newInstance(typeDisplay, listFoodtems)
            myDialogFragment.show(fm, "DateTimePickerDialog")

            this.dismiss()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context!=null)
            contextDialog = context
    }
}

class DateTimePickerDialog : DialogFragment() {

    private lateinit var listFoodtems:List<Food>
    private lateinit var listEventType:List<EventType>
    private lateinit var typeDisplay: TypeDisplay
    private var time:Long = 0
    private var date:Long = 0
    private lateinit var contextDialog:Context

    fun newInstance(typeDisplay: TypeDisplay, listItems:List<Any>): DateTimePickerDialog {

        val timeDialog = DateTimePickerDialog()

        val gson = Gson()
        val jsonList = gson.toJson(listItems)
        val jsonType = gson.toJson(typeDisplay)

        val args = Bundle()
        args.putString(LIST_TO_SAVE, jsonList)
        args.putString(TYPE_DISPLAY, jsonType)
        timeDialog.arguments = args

        return timeDialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val v = inflater.inflate(R.layout.time_picker_dialog, container, false)

        // Recover list of items and typeDisplay
        val gson = Gson()
        val jsonType = arguments?.getString(TYPE_DISPLAY, null)
        val propTypeDisplay = object : TypeToken<TypeDisplay>() {}.type
        typeDisplay = gson.fromJson(jsonType, propTypeDisplay)

        val jsonList = arguments?.getString(LIST_TO_SAVE, null)
        val propTypeList = if(typeDisplay.equals(TypeDisplay.MEAL))
            object : TypeToken<List<Food>>() {}.type
        else
            object : TypeToken<List<EventType>>() {}.type

        if(typeDisplay.equals(TypeDisplay.MEAL))
            listFoodtems = gson.fromJson(jsonList, propTypeList)
        else
            listEventType = gson.fromJson(jsonList, propTypeList)

        // Configure button SAVE
        configureButtonSave(v, typeDisplay)

        // Configure date and time pickers
        configureDateAndTimePickers(v)

        return v
    }

    private fun configureButtonSave(view:View, typeDisplay:TypeDisplay){

        /**    BUTTON SAVE    **/
        view.findViewById<Button>(R.id.button_save).setOnClickListener {

            if(date!=0.toLong() && time!=0.toLong()){

                if(typeDisplay.equals(TypeDisplay.MEAL)){
                    // Save new meal in DB
                    saveNewMeal(listFoodtems.map { it.id }, date + time, context = contextDialog)

                    // Show snackbar "meal successfully saved"
                    val message = contextDialog.resources.getString(R.string.save_meal_success_message_dialog)
                    (activity as PickActivity).showConfirmationMessageInSnackBar(message)

                } else {

                    // Save event(s) in DB
                    if(listEventType.isNotEmpty()){
                        for(eventType in listEventType){
                            val eventToSave = Event(null, eventType.id, date + time)
                            saveNewEvent(eventToSave.idEventType, date + time, context = contextDialog)
                        }
                    }

                    // Show snackbar "meal successfully saved"
                    val message = contextDialog.resources.getString(R.string.save_event_success_message_dialog)
                    (activity as PickActivity).showConfirmationMessageInSnackBar(message)
                }

                // Dismiss dialog
                dismiss()

                // Go back to Main Page
                (activity as PickActivity).goToBackToMainPage()

            } else
                Toast.makeText(activity, R.string.save_abort_message_dialog, Toast.LENGTH_LONG).show()
        }
    }

    private fun configureDateAndTimePickers(view:View){
        view.findViewById<TextView>(R.id.date_picker).setOnClickListener {

            if(date==0.toLong()){
                val datePickerDialog = DatePickerDialog(contextDialog, onDateChangeListener,
                    getYear(getTodayDate()),
                    getMonth(getTodayDate()),
                    getDayOfMonth(getTodayDate())
                )
                datePickerDialog.show()
            } else {
                val datePickerDialog = DatePickerDialog(contextDialog, onDateChangeListener,
                    getYear(date),
                    getMonth(date),
                    getDayOfMonth(date)
                )
                datePickerDialog.show()
            }
        }

        view.findViewById<TextView>(R.id.time_picker).setOnClickListener {

            if(time == 0.toLong()) {
                val timePickerDialog = TimePickerDialog(
                    contextDialog, timeSetListener,
                    getHourAsInt(getTodayDate()),
                    getMinutesAsInt(getTodayDate()),
                    true
                )
                timePickerDialog.show()
            } else {
                val timePickerDialog = TimePickerDialog(
                    contextDialog, timeSetListener,
                    getHourAsInt(time),
                    getMinutesAsInt(time),
                    true
                )
                timePickerDialog.show()
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context!=null)
            contextDialog = context
    }

    private val onDateChangeListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        date = getDateAsLong(dayOfMonth, monthOfYear,year,0,0)
        val text = getTextDate(date)
        date_picker.text = text
    }

    private val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
        time = getTimeAsLong(hour, minute)
        val text = getTextTime(hour,minute)
        time_picker.text = text
    }
}

class LegendCalendarDialog : DialogFragment() {

    private lateinit var contextDialog:Context

    fun newInstance(): LegendCalendarDialog {
        return LegendCalendarDialog()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.legend_calendar, container, false)

        // Configure button OK
        view.findViewById<Button>(R.id.button_ok).setOnClickListener {
            this.dismiss()
        }

        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context!=null)
            contextDialog = context
    }
}

class StatInfoDialog : DialogFragment() {

    private lateinit var contextDialog:Context

    fun newInstance(): StatInfoDialog {
        return StatInfoDialog()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.stat_info_layout, container, false)

        view.findViewById<ImageButton>(R.id.close_button).setOnClickListener {
            dismiss()
        }

        val pager = (view as ViewGroup).getChildAt(0) as ViewPager

        val adapter = StatInfoAdapter(childFragmentManager)
        pager.adapter = adapter

        val tabLayout = view.getChildAt(2) as TabLayout
        tabLayout.setupWithViewPager(pager, true)

        return view
    }

    override fun onResume() {
        super.onResume()
        val metrics = resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        dialog?.window?.setLayout((6 * width)/7, (4 * height)/5)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context!=null)
            contextDialog = context
    }
}

const val TAG_SCHEDULE_DIALOG = "schedule_dialog_fragment"
const val LIST_TO_SAVE = "list to save"
const val TYPE_DISPLAY = "typedisplay"
