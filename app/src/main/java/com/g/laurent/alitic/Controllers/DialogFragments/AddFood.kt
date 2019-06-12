package com.g.laurent.alitic.Controllers.DialogFragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.g.laurent.alitic.Controllers.ClassControllers.getAllFood
import com.g.laurent.alitic.Controllers.ClassControllers.getAllFoodTypes
import com.g.laurent.alitic.Controllers.ClassControllers.saveNewFood
import com.g.laurent.alitic.Models.FoodType
import com.g.laurent.alitic.R

class NewFoodDialogFragment : DialogFragment(), AdapterView.OnItemSelectedListener{

    private var listFoodTypes : List<FoodType> = listOf()
    private var newFood:NewData = NewData(null, null, false)

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if(listFoodTypes.isNotEmpty())
            newFood.foodType = listFoodTypes[position]
    }

    fun newInstance(): NewFoodDialogFragment {
        return NewFoodDialogFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.add_food, container)

        val context = activity?.applicationContext

        if(context != null){
            // init list foodTypes
            val list = getAllFoodTypes(context = context)
            if(list!=null){
                listFoodTypes = list

                // Configure views
                configureViews(view, context)
            }
        }

        return view
    }

    private fun configureViews(view: View, context: Context) {
        configureChoiceFoodType(view)
        configureCheckBox(view)
        configureFieldName(view)
        configureButtonSave(view, context)
    }

    private fun configureChoiceFoodType(view: View) {

        val choice = view.findViewById<Spinner>(R.id.field_foodtype)

        val context = activity?.applicationContext

        if(context != null){

            // Spinner click listener
            choice.onItemSelectedListener = this

            if(listFoodTypes.isNotEmpty()){

                // Create list of foodtypes
                val list = arrayListOf<String>()
                for(foodType in listFoodTypes){
                    list.add(foodType.name)
                }

                // Creating adapter for spinner
                val dataAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, list)

                // Drop down layout style - list view with radio button
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)

                // attaching data adapter to spinner
                choice.adapter = dataAdapter
            }
        }
    }

    private fun configureButtonSave(view: View, context: Context) {

        val buttonSave = view.findViewById<Button>(R.id.button_save_food)
        buttonSave.setOnClickListener {
            if(allInformationOK(context)){
                saveNewFood(newFood.name, newFood.foodType?.id, newFood.foodType?.foodTypePic, newFood.forAnalysis, context = context)
                this.dismiss()
            }
        }
    }

    private fun configureCheckBox(view: View){
        val checkBox = view.findViewById<CheckBox>(R.id.count_for_analysis)
        checkBox.setOnClickListener {
            newFood.forAnalysis = checkBox.isChecked
        }
    }

    private fun configureFieldName(view: View){

        val edit = view.findViewById<EditText>(R.id.field_name_food)
        edit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                newFood.name = s.toString()
            }
        })
    }

    private fun allInformationOK(context: Context):Boolean{

        // Check if name is null or empty
        if(newFood.name == null || newFood.name.toString().replace("\\s+","").equals("")){
            Toast.makeText(context, context.getText(R.string.error_food_without_name), Toast.LENGTH_LONG).show()
            return false
        }

        // Check if name is not already in the list of foods in DB
        val listFoods = getAllFood(context = context)
        if(listFoods!=null){
            val found = listFoods.any{ newFood.name.equals(it.name)}
            if(found){
                Toast.makeText(context, context.getText(R.string.error_food_already_in_db), Toast.LENGTH_LONG).show()
                return false
            }
        }

        // Check if foodType is null
        if(newFood.foodType==null){
            Toast.makeText(context, context.getText(R.string.error_food_without_foodtype), Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }

    class NewData(var name:String?, var foodType: FoodType?, var forAnalysis:Boolean)
}