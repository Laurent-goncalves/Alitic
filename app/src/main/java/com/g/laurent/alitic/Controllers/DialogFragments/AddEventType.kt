package com.g.laurent.alitic.Controllers.DialogFragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.*
import com.g.laurent.alitic.Controllers.ClassControllers.getAllFood
import com.g.laurent.alitic.Controllers.ClassControllers.saveNewEventType
import com.g.laurent.alitic.R
import com.g.laurent.alitic.hideOrRevealRangeSeekBar
import com.ianpinto.androidrangeseekbar.rangeseekbar.RangeSeekBar

class NewEventTypeDialogFragment : DialogFragment(), RangeSeekBar.OnRangeSeekBarChangeListener<Int> {


    private var newEventType: NewEventTypeDialogFragment.NewData = NewEventTypeDialogFragment.NewData(null, null, null, null)
    private var heightRangeSeekBar = 0

    fun newInstance(): NewEventTypeDialogFragment {
        return NewEventTypeDialogFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.add_event_type, container)

        val context = activity?.applicationContext

        if(context != null){
            configureViews(view, context)
        }

        return view
    }

    private fun configureViews(view: View, context: Context) {
        configureFieldName(view)
        configureRange(view)
        configureRadioButtons(view, context)
        configureButtonSave(view, context)
    }

    private fun configureRadioButtons(view:View, context: Context){

        val radioGroup  = view.findViewById<RadioGroup>(R.id.group_radio_buttons)
        val radioButtonTake  = view.findViewById<RadioButton>(R.id.take_last_meal)
        val radioButtonSel  = view.findViewById<RadioButton>(R.id.select_period)
        radioButtonSel.isChecked = true

        val text = context.getString(R.string.select_period_add_eventtype)
        radioButtonSel.text = text

        radioGroup.setOnCheckedChangeListener { _, checkedId ->

            when (checkedId) {
                R.id.take_last_meal ->
                    if (radioButtonTake.isChecked) {
                        newEventType.forLastMeal = true
                        // Hide range seekbar
                        hideOrRevealRangeSeekBar(view.findViewById(R.id.layout_range_seek_bar), heightRangeSeekBar, false)
                    }
                R.id.select_period ->
                    if (radioButtonSel.isChecked) {
                        newEventType.forLastMeal = false
                        // Show range seekbar
                        hideOrRevealRangeSeekBar(view.findViewById(R.id.layout_range_seek_bar), heightRangeSeekBar, true)
                    }
            }
        }
    }

    private fun configureFieldName(view:View){

        val edit = view.findViewById<EditText>(com.g.laurent.alitic.R.id.field_name_eventtype)
        edit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                newEventType.name = s.toString()
            }
        })
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
                return true
            }
        })
    }

    override fun onRangeSeekBarValuesChanged(bar: RangeSeekBar<*>?, minValue: Int?, maxValue: Int?) {
        if(minValue!=null){
            newEventType.minTime = minValue.toLong()
        }
        if(maxValue!=null){
            newEventType.maxTime = maxValue.toLong()
        }

        val view = this.view
        val context = context
        if(view !=null && context!=null) {
            val radioButton = view.findViewById<RadioButton>(R.id.select_period)

            val text = context.getString(R.string.select_period_add_eventtype1) + " " + newEventType.minTime +
                    context.getString(R.string.select_period_add_eventtype2) + " " + newEventType.maxTime +
                    context.getString(R.string.select_period_add_eventtype3)
            radioButton.text = text
        }
    }

    private fun configureButtonSave(view:View, context: Context) {

        val buttonSave = view.findViewById<Button>(R.id.button_save_eventtype)
        buttonSave.setOnClickListener {
            if(allInformationOK(context)){
                saveNewEventType(newEventType.name, context.getString(R.string.eventtype_default_pic),
                    newEventType.minTime, newEventType.maxTime, newEventType.forLastMeal, context = context)
                this.dismiss()
            }
        }
    }

    private fun allInformationOK(context: Context):Boolean{

        // Check if name is null or empty
        val name = newEventType.name
        if(name == null || name.toString().replace("\\s+","").equals("") ){
            Toast.makeText(context, context.getText(R.string.error_event_without_name),Toast.LENGTH_LONG).show()
            return false
        }

        // Check if name is not already in the list of foods in DB
        val listFoods = getAllFood(context = context)
        if(listFoods!=null){
            val found = listFoods.any{ name.equals(it.name)} || name.equals(context.getText(R.string.all_event_types))
            if(found){
                Toast.makeText(context, context.getText(R.string.error_event_already_in_db),Toast.LENGTH_LONG).show()
                return false
            }
        }

        return true
    }

    class NewData(var name:String?, var minTime:Long?, var maxTime:Long?, var forLastMeal:Boolean?)
}

