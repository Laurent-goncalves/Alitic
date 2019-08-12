package com.g.laurent.alitic.Controllers.DialogFragments

import android.animation.ObjectAnimator
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.*
import com.g.laurent.alitic.Controllers.ClassControllers.*
import com.g.laurent.alitic.Models.EventType
import com.g.laurent.alitic.Models.TypeDisplay
import com.g.laurent.alitic.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ianpinto.androidrangeseekbar.rangeseekbar.RangeSeekBar


class AddEventTypeDialog : DialogFragment(), RangeSeekBar.OnRangeSeekBarChangeListener<Int> {

    private var eventTypeToSave = EventType()
    private var heightRangeSeekBar = 0
    private lateinit var contextDialog:Context

    fun newInstance(eventType: EventType): AddEventTypeDialog {

        val addEventTypeDialog = AddEventTypeDialog()

        val gson = Gson()
        val json = gson.toJson(eventType)
        val args = Bundle()
        args.putString(EVENT_TYPE_PARAMS, json)
        addEventTypeDialog.arguments = args

        return addEventTypeDialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.add_event_type, container)

        val arg = arguments
        if(arg!=null){
            val gson = Gson()
            val json = arg.getString(EVENT_TYPE_PARAMS, null)
            val eventTypeTransf = object : TypeToken<EventType>() {}.type
            eventTypeToSave = gson.fromJson(json, eventTypeTransf)
        }

        configureViews(view)

        return view
    }

    private fun configureViews(view: View) {
        configureFieldName(view)
        configureRange(view)
        configureButtonSave(view)
    }

    private fun configureFieldName(view:View){

        // init
        val nameEventTypeView = view.findViewById<EditText>(R.id.field_name_eventtype)
        nameEventTypeView.setText(eventTypeToSave.name)

        // listener
        nameEventTypeView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                eventTypeToSave.name = s.toString()
            }
        })
    }

    private fun configureRadioButtons(view:View){

        val radioGroup  = view.findViewById<RadioGroup>(R.id.group_radio_buttons)
        val radioButtonTake  = view.findViewById<RadioButton>(R.id.take_last_meal)
        val radioButtonSel  = view.findViewById<RadioButton>(R.id.select_period)

        // init
        val forLastMeal = eventTypeToSave.forLastMeal
        if(forLastMeal!=null && forLastMeal || forLastMeal==null){
            radioButtonTake.isChecked = true
            radioButtonSel.isChecked = false
            val text = contextDialog.getString(R.string.select_period_add_eventtype)
            radioButtonSel.text = text

        } else if(!forLastMeal){
            radioButtonTake.isChecked = false
            radioButtonSel.isChecked = true
            val text = getTextTimeSelection()

            radioButtonSel.text = text

            val rangeSeekBarFrameLayout  = view.findViewById<FrameLayout>(R.id.layout_range_seek_bar)
            rangeSeekBarFrameLayout.visibility = View.VISIBLE

            val rangeSeekBar  = view.findViewById<RangeSeekBar<Int>>(R.id.range_seek_bar)
            rangeSeekBar.selectedMinValue = getMinTimeFromEventType(eventTypeToSave)
            rangeSeekBar.selectedMaxValue = getMaxTimeFromEventType(eventTypeToSave)
        }

        // listener
        radioGroup.setOnCheckedChangeListener { _, checkedId ->

            when (checkedId) {
                R.id.take_last_meal ->
                    if (radioButtonTake.isChecked) {
                        eventTypeToSave.forLastMeal = true
                        // Hide range seekbar
                        hideOrRevealRangeSeekBar(view.findViewById(R.id.layout_range_seek_bar), heightRangeSeekBar, true)

                        // Adapt the text of radio button
                        val text = contextDialog.getString(R.string.select_period_add_eventtype)
                        radioButtonSel.text = text
                    }
                R.id.select_period ->
                    if (radioButtonSel.isChecked) {
                        eventTypeToSave.forLastMeal = false

                        // Show range seekbar
                        hideOrRevealRangeSeekBar(view.findViewById(R.id.layout_range_seek_bar), heightRangeSeekBar, false)

                        // Set min and max values of rangeSeekBar
                        val rangeSeekBar  = view.findViewById<RangeSeekBar<Int>>(R.id.range_seek_bar)
                        eventTypeToSave.minTime = rangeSeekBar.selectedMinValue.toLong() * (60 * 60 * 1000)
                        eventTypeToSave.maxTime = rangeSeekBar.selectedMaxValue.toLong() * (60 * 60 * 1000)

                        // Adapt the text of radio button
                        val text = getTextTimeSelection()
                        radioButtonSel.text = text
                    }
            }
        }
    }

    private fun configureRange(view:View){
        val rangeSeekBar  = view.findViewById<RangeSeekBar<Int>>(R.id.range_seek_bar)
        rangeSeekBar.setOnRangeSeekBarChangeListener(this)

        val vto = rangeSeekBar.viewTreeObserver
        vto.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                // Remove after the first run so it doesn't fire forever
                rangeSeekBar.viewTreeObserver.removeOnPreDrawListener(this)
                heightRangeSeekBar = rangeSeekBar.measuredHeight
                configureRadioButtons(view)
                return true
            }
        })
    }

    override fun onRangeSeekBarValuesChanged(bar: RangeSeekBar<*>?, minValue: Int?, maxValue: Int?) {
        if(minValue!=null){
            eventTypeToSave.minTime = minValue.toLong() * (60 * 60 * 1000)
        }
        if(maxValue!=null){
            eventTypeToSave.maxTime = maxValue.toLong() * (60 * 60 * 1000)
        }

        val view = this.view

        if(view !=null && context!=null) {
            val radioButton = view.findViewById<RadioButton>(R.id.select_period)
            radioButton.text = getTextTimeSelection()
        }
    }

    private fun allInformationOK():Boolean{

        // Check if name is null or empty
        val name = eventTypeToSave.name
        if(name == null || name.toString().replace("\\s+","").equals("") ){
            Toast.makeText(contextDialog, contextDialog.getText(R.string.error_event_without_name),Toast.LENGTH_LONG).show()
            return false
        }

        // Check if name is not too long
        if(eventTypeToSave.name.toString().length >= 20){
            Toast.makeText(contextDialog, contextDialog.getText(R.string.error_event_type_name_too_long), Toast.LENGTH_LONG).show()
            return false
        }

        // Check if the selected range of time is not 0 - 0
        val maxTime = eventTypeToSave.maxTime
        val minTime = eventTypeToSave.minTime

        if(minTime != null && maxTime != null){
            if(minTime.toInt()==0 && maxTime.toInt()==0){
                Toast.makeText(contextDialog, contextDialog.getText(R.string.error_event_with_wrong_range), Toast.LENGTH_LONG).show()
                return false
            }
        }

        // Check if name is not already in the list of foods in DB
        val listFoods = getAllFood(context = contextDialog)
        if(listFoods!=null){
            val found = listFoods.any{ name.equals(it.name)} || name.equals(contextDialog.getText(R.string.all_event_types))
            if(found){
                Toast.makeText(contextDialog, contextDialog.getText(R.string.error_event_already_in_db),Toast.LENGTH_LONG).show()
                return false
            }
        }

        return true
    }

    private fun configureButtonSave(view:View) {

        val buttonSave = view.findViewById<Button>(R.id.button_save_eventtype)
        buttonSave.setOnClickListener {
            if(allInformationOK()){

                if(eventTypeToSave.id == null){
                    saveNewEventType(eventTypeToSave.name, eventTypeToSave.eventPic,
                        eventTypeToSave.minTime, eventTypeToSave.maxTime, eventTypeToSave.forLastMeal, context = contextDialog)
                    Toast.makeText(contextDialog, contextDialog.resources.getString(R.string.save_add_eventtype), Toast.LENGTH_LONG).show()
                } else {
                    updateEventType(eventTypeToSave, context = contextDialog)
                    Toast.makeText(contextDialog, contextDialog.resources.getString(R.string.save_update_eventtype), Toast.LENGTH_LONG).show()
                }

                this.dismiss()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        val activity = activity
        if(activity is DialogCloseListener){
            activity.handleDialogClose(TypeDisplay.EVENT)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context!=null)
            contextDialog = context
    }

    private fun hideOrRevealRangeSeekBar(rangeSeekBar: FrameLayout, height:Int, hide:Boolean){

        val factor = if(hide) 0 else -1

        ObjectAnimator.ofFloat(rangeSeekBar, "translationY", factor * height.toFloat()).apply {
            duration = 1000
            start()
        }

        if(hide)
            rangeSeekBar.visibility = View.GONE
        else
            rangeSeekBar.visibility = View.VISIBLE
    }

    private fun getTextTimeSelection():String{
        return contextDialog.getString(R.string.select_period_add_eventtype1) + " " + getMinTimeFromEventType(eventTypeToSave) +
                contextDialog.getString(R.string.select_period_add_eventtype2) + " " + getMaxTimeFromEventType(eventTypeToSave) +
                contextDialog.getString(R.string.select_period_add_eventtype3)
    }

    private fun getMinTimeFromEventType(eventType:EventType):Int?{

        val minTime:Long? = eventType.minTime

        return if(minTime!=null){
            (minTime / (60 * 60 * 1000)).toInt()
        } else
            null
    }

    private fun getMaxTimeFromEventType(eventType:EventType):Int?{

        val maxTime:Long? = eventType.maxTime

        return if(maxTime!=null){
            (maxTime / (60 * 60 * 1000)).toInt()
        } else
            null
    }
}

const val EVENT_TYPE_PARAMS = "event_type_params"
