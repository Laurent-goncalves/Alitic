@file:Suppress("UNCHECKED_CAST")

package com.g.laurent.alitic.Views

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.g.laurent.alitic.Models.MealItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.support.design.widget.Snackbar
import android.widget.Button
import android.widget.TextView
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import com.g.laurent.alitic.*
import com.g.laurent.alitic.Controllers.Activities.EVENT
import com.g.laurent.alitic.Controllers.Activities.MEAL
import com.g.laurent.alitic.Controllers.Activities.MainActivity
import com.g.laurent.alitic.Controllers.Activities.TypeDisplay
import com.g.laurent.alitic.Controllers.ClassControllers.*
import com.g.laurent.alitic.Models.Event
import kotlinx.android.synthetic.main.time_picker_dialog.*

class SaveDialog : DialogFragment() {

    private lateinit var listItems:List<Any>
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

        val v = inflater.inflate(com.g.laurent.alitic.R.layout.save_pick_layout, container, false)

        // Recover list of items and typeDisplay
        val gson = Gson()
        val jsonList = arguments?.getString(LIST_TO_SAVE, null)
        val propTypeList = object : TypeToken<List<Any>>() {}.type
        listItems = gson.fromJson<List<Any>>(jsonList, propTypeList)

        val jsonType = arguments?.getString(TYPE_DISPLAY, null)
        val propTypeDisplay = object : TypeToken<TypeDisplay>() {}.type
        typeDisplay = gson.fromJson<TypeDisplay>(jsonType, propTypeDisplay)

        // Configure buttons YES and NO
        configureButtonsClick(v)

        return v
    }

    private fun configureButtonsClick(view:View){

        /**    BUTTON YES    **/
        view.findViewById<Button>(R.id.button_yes).setOnClickListener {

            // Save the meal or event with today's date
            if(context!=null)
                saveData(listItems, typeDisplay, view, getTodayDate(), context = contextDialog)
            else
                Snackbar.make(view, R.string.save_abort_message_dialog, Snackbar.LENGTH_LONG).show()

            // Dismiss dialog
            dismiss()

            // Go back to Main Page
            (activity as MainActivity).goToBackToMainPage(typeDisplay.type)
        }

        /**    BUTTON NO    **/
        view.findViewById<Button>(R.id.button_no).setOnClickListener {

            // Launch dialog to pick time and date
            val fm = fragmentManager
            val myDialogFragment = DateTimePickerDialog().newInstance(typeDisplay, listItems)
            myDialogFragment.show(fm, "DateTimePickerDialog")

            // Dismiss dialog
            dismiss()

            // Go back to Main Page
            (activity as MainActivity).goToBackToMainPage(typeDisplay.type)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context!=null)
            contextDialog = context
    }
}

class DateTimePickerDialog : DialogFragment() {

    private lateinit var listItems:List<Any>
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
        val jsonList = arguments?.getString(LIST_TO_SAVE, null)
        val propTypeList = object : TypeToken<List<Any>>() {}.type
        listItems = gson.fromJson<List<Any>>(jsonList, propTypeList)

        val jsonType = arguments?.getString(TYPE_DISPLAY, null)
        val propTypeDisplay = object : TypeToken<TypeDisplay>() {}.type
        typeDisplay = gson.fromJson<TypeDisplay>(jsonType, propTypeDisplay)

        // Configure button SAVE
        configureButtonSave(v)

        // Configure date and time pickers
        configureDateAndTimePickers(v)

        return v
    }

    private fun configureButtonSave(view:View){

        /**    BUTTON SAVE    **/
        view.findViewById<Button>(R.id.button_save).setOnClickListener {

            if(date!=0.toLong() && time!=0.toLong()){

                saveData(listItems, typeDisplay, view, getTodayDate(), context = contextDialog)

                // Dismiss dialog
                dismiss()

                // Go back to Main Page
                (activity as MainActivity).goToBackToMainPage(typeDisplay.type)

            } else
                Snackbar.make(view, R.string.save_abort_message_dialog, Snackbar.LENGTH_LONG).show()
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

fun saveData(list:List<Any>, typeDisplay: TypeDisplay, view:View, dateToSave:Long, context: Context){

    when(typeDisplay.type){

        MEAL-> {

            if(typeDisplay.isNew) // --- Save new meal in database
                saveNewMeal(list as List<MealItem>, dateToSave, context = context)
            else // -------------------- update meal in database
                updateMeal(list as List<MealItem>, dateToSave, context = context)

            // Show snackbar "meal successfully saved"
            val text = context.resources.getString(R.string.save_meal_success_message_dialog)
            Snackbar.make(view, text, Snackbar.LENGTH_LONG).show()
        }
        EVENT -> {

            val listToSave = list as List<Event>
            val eventToSave = listToSave[0]
            eventToSave.dateCode = dateToSave

            if(typeDisplay.isNew) // --- Save new event in database
                saveNewEvent(eventToSave.idEventType, dateToSave, context = context)
            else // -------------------- update event in database
                updateEvent(eventToSave, context = context)

            // Show snackbar "meal successfully saved"
            val text = context.resources.getString(R.string.save_event_success_message_dialog)
            Snackbar.make(view, text, Snackbar.LENGTH_LONG).show()
        }
    }
}

const val TAG_SCHEDULE_DIALOG = "schedule_dialog_fragment"
const val LIST_TO_SAVE = "list to save"
const val TYPE_DISPLAY = "typedisplay"