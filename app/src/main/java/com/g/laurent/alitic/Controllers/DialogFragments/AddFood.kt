package com.g.laurent.alitic.Controllers.DialogFragments

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.DialogFragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.g.laurent.alitic.Controllers.Activities.TypeDisplay
import com.g.laurent.alitic.Controllers.ClassControllers.getAllFood
import com.g.laurent.alitic.Controllers.ClassControllers.getAllFoodTypes
import com.g.laurent.alitic.Controllers.ClassControllers.saveNewFood
import com.g.laurent.alitic.Controllers.ClassControllers.updateFood
import com.g.laurent.alitic.Models.Food
import com.g.laurent.alitic.Models.FoodType
import com.g.laurent.alitic.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AddFoodDialog : DialogFragment(){

    private var listFoodTypes : List<FoodType> = listOf()
    private var foodToSave:Food = Food()
    private var foodInit:Food = Food()
    private lateinit var contextDialog:Context

    fun newInstance(food: Food): AddFoodDialog {

        val addFoodDialog = AddFoodDialog()

        val gson = Gson()
        val json = gson.toJson(food)
        val args = Bundle()
        args.putString(FOOD_PARAMS, json)
        addFoodDialog.arguments = args

        return addFoodDialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.add_food, container)

        val arg = arguments
        if(arg!=null){
            val gson = Gson()
            val json = arg.getString(FOOD_PARAMS, null)
            val foodTransf = object : TypeToken<Food>() {}.type
            foodToSave = gson.fromJson<Food>(json, foodTransf)
            foodInit.foodPic = foodToSave.foodPic
            foodInit.idFoodType = foodToSave.idFoodType
        }

        // init list foodTypes
        val list = getAllFoodTypes(context = contextDialog)
        if(list!=null){
            listFoodTypes = list
        }

        // Configure views
        configureViews(view)

        return view
    }

    private fun configureViews(view: View) {
        configureChoiceFoodType(view)
        configureCheckBox(view)
        configureFieldName(view)
        configureButtonSave(view)
    }

    private fun configureFieldName(view: View){

        val nameFoodView = view.findViewById<EditText>(R.id.field_name_food)

        // init
        nameFoodView.setText(foodToSave.name)

        // Listener change
        nameFoodView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                foodToSave.name = s.toString()
            }
        })
    }

    private fun configureChoiceFoodType(view: View) {

        val foodTypeFoodView = view.findViewById<Spinner>(R.id.field_foodtype)

        // Spinner click listener
        foodTypeFoodView.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                foodToSave.idFoodType = null
                foodToSave.foodPic = null
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(listFoodTypes.isNotEmpty()) {
                    foodToSave.idFoodType = listFoodTypes[position].id
                    foodToSave.foodPic = listFoodTypes[position].foodTypePic
                }
            }
        }

        if(listFoodTypes.isNotEmpty()){

            // Create list of foodtypes
            val list = arrayListOf<String>()
            for(foodType in listFoodTypes){
                list.add(foodType.name)
            }

            // Creating adapter for spinner
            val dataAdapter = ArrayAdapter(contextDialog, android.R.layout.simple_spinner_item, list)

            // Drop down layout style - list view with radio button
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)

            // attaching data adapter to spinner
            foodTypeFoodView.adapter = dataAdapter

            // Select foodtype in the list
            if(foodToSave.idFoodType!=null)
                foodTypeFoodView.setSelection(listFoodTypes.indexOfFirst { it.id == foodToSave.idFoodType })
        }
    }

    private fun configureCheckBox(view: View){
        val checkBox = view.findViewById<CheckBox>(R.id.count_for_analysis)
        // Init
        checkBox.isChecked = foodToSave.forAnalysis

        // Listener change
        checkBox.setOnClickListener {
            foodToSave.forAnalysis = checkBox.isChecked
        }
    }

    private fun allInformationOK():Boolean{

        // Check if name is null or empty
        if(foodToSave.name == null || foodToSave.name.toString().replace("\\s+","").equals("")){
            Toast.makeText(contextDialog, contextDialog.getText(R.string.error_food_without_name), Toast.LENGTH_LONG).show()
            return false
        }

        // Check if name is not too long
        if(foodToSave.name.toString().length >= 20){
            Toast.makeText(contextDialog, contextDialog.getText(R.string.error_food_name_too_long), Toast.LENGTH_LONG).show()
            return false
        }

        // Check if name is not already in the list of foods in DB
        val listFoods = getAllFood(context = contextDialog)
        if(listFoods!=null && foodToSave.id == null){
            val found = listFoods.any{ foodToSave.name.equals(it.name)}
            if(found){
                Toast.makeText(contextDialog, contextDialog.getText(R.string.error_food_already_in_db), Toast.LENGTH_LONG).show()
                return false
            }
        }

        if(foodInit.foodPic!=null && foodToSave.idFoodType == foodInit.idFoodType) // if foodTypePic had an initial value (could be changed to initial item selection)
            foodToSave.foodPic = foodInit.foodPic

        // Check if foodType is null
        if(foodToSave.idFoodType==null){
            Toast.makeText(contextDialog, contextDialog.getText(R.string.error_food_without_foodtype), Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }

    private fun configureButtonSave(view: View) {

        val buttonSave = view.findViewById<Button>(R.id.button_save_food)
        buttonSave.setOnClickListener {
            if(allInformationOK()){
                if(foodToSave.id == null){
                    saveNewFood(foodToSave.name, foodToSave.idFoodType, foodToSave.foodPic, foodToSave.forAnalysis, context = contextDialog)
                    Toast.makeText(contextDialog, contextDialog.resources.getString(R.string.save_food_new), Toast.LENGTH_LONG).show()
                } else {
                    updateFood(foodToSave, context = contextDialog)
                    Toast.makeText(contextDialog, contextDialog.resources.getString(R.string.save_food_update), Toast.LENGTH_LONG).show()
                }

                this.dismiss()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        val activity = activity
        if(activity is DialogCloseListener){
            activity.handleDialogClose(TypeDisplay.MEAL)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context!=null)
            contextDialog = context
    }
}

const val FOOD_PARAMS = "food_params"

interface DialogCloseListener {
    fun handleDialogClose(typeDisplay: TypeDisplay)
}