package com.g.laurent.alitic.Views

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.g.laurent.alitic.Controllers.ClassControllers.MEAL_SAVE
import com.g.laurent.alitic.Controllers.ClassControllers.saveNewMeal
import com.g.laurent.alitic.Models.MealItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.support.design.widget.Snackbar
import android.widget.Button
import android.widget.TextView
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import com.g.laurent.alitic.*
import kotlinx.android.synthetic.main.time_picker_dialog.*

class SaveDialog : DialogFragment() {

    private lateinit var listMealItems:List<MealItem>

    fun newInstance(listMealItems:List<MealItem>): SaveDialog {

        val saveDialog = SaveDialog()

        val gson = Gson()
        val json = gson.toJson(listMealItems)
        val args = Bundle()
        args.putString(MEAL_SAVE, json)
        saveDialog.arguments = args

        return saveDialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val v = inflater.inflate(com.g.laurent.alitic.R.layout.save_pick_layout, container, false)

        // Recover list of meal items
        val gson = Gson()
        val json = arguments?.getString(MEAL_SAVE, null)
        val propType = object : TypeToken<List<MealItem>>() {}.type
        listMealItems = gson.fromJson<List<MealItem>>(json, propType)

        // Configure buttons YES and NO
        configureButtonsClick(v)

        return v
    }

    fun configureButtonsClick(view:View){

        view.findViewById<Button>(com.g.laurent.alitic.R.id.button_yes).setOnClickListener {

            // Save the meal with today's date
            val context : Context? = activity?.application?.applicationContext

            if(context!=null) {
                // Save meal in database
                saveNewMeal(listMealItems, getTodayDate(), context = context)

                // Show snackbar "meal successfully saved"
                val text = context.resources.getString(com.g.laurent.alitic.R.string.save_meal_success_message_dialog)
                Snackbar.make(view, text, Snackbar.LENGTH_LONG).show()
            } else {
                Snackbar.make(view, com.g.laurent.alitic.R.string.save_meal_abort_message_dialog, Snackbar.LENGTH_LONG).show()
            }

            // Dismiss dialog
            dismiss()
        }

        view.findViewById<Button>(com.g.laurent.alitic.R.id.button_no).setOnClickListener {

            // Launch dialog to pick time and date
            val fm = fragmentManager
            val myDialogFragment = DateTimePickerDialog().newInstance(listMealItems)
            myDialogFragment.show(fm, "DateTimePickerDialog")

            dismiss()
        }
    }
}

class DateTimePickerDialog : DialogFragment() {

    private lateinit var listMealItems:List<MealItem>
    private var time:Long = 0
    private var date:Long = 0
    private lateinit var contextDialog:Context

    fun newInstance(listMealItems:List<MealItem>): DateTimePickerDialog {

        val timeDialog = DateTimePickerDialog()

        val gson = Gson()
        val json = gson.toJson(listMealItems)
        val args = Bundle()
        args.putString(MEAL_SAVE, json)
        timeDialog.arguments = args

        return timeDialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val v = inflater.inflate(R.layout.time_picker_dialog, container, false)

        // Recover list of meal items
        val gson = Gson()
        val json = arguments?.getString(MEAL_SAVE, null)
        val propType = object : TypeToken<List<MealItem>>() {}.type
        listMealItems = gson.fromJson<List<MealItem>>(json, propType)

        // Configure button SAVE
        configureButtonSave(v)

        // Configure date and time pickers
        configureDateAndTimePickers(v)

        return v
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

    fun configureButtonSave(view:View){

        view.findViewById<Button>(R.id.button_save).setOnClickListener {

            if(date!=0.toLong() && time!=0.toLong()){

                // Save the meal
                saveNewMeal(listMealItems, date + time, context = contextDialog)

                // Show snackbar "meal successfully saved"
                if(activity!=null){
                    val text = contextDialog.resources.getString(R.string.save_meal_success_message_dialog)
                    Snackbar.make(activity!!.findViewById(R.id.mealactivity), text, Snackbar.LENGTH_LONG).show()
                }

                // Dismiss dialog
                dismiss()
            } else
                Toast.makeText(contextDialog, R.string.save_meal_abort_message_dialog, Toast.LENGTH_LONG).show()
        }
    }

    fun configureDateAndTimePickers(view:View){
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
}

const val TAG_SCHEDULE_DIALOG = "schedule_dialog_fragment"