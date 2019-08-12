package com.g.laurent.alitic.Controllers.DialogFragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.g.laurent.alitic.Controllers.Activities.OnFoodToDeleteListener
import com.g.laurent.alitic.Controllers.Activities.OnPickMealSaveListener
import com.g.laurent.alitic.Models.Food
import com.g.laurent.alitic.R
import com.g.laurent.alitic.Views.LIST_TO_SAVE
import com.g.laurent.alitic.Views.MealPickAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.pick_meal_dialog.*
import kotlinx.android.synthetic.main.pick_meal_dialog.button_cancel_meal
import kotlinx.android.synthetic.main.pick_meal_dialog.button_save_meal


class MealPickDialog : DialogFragment() {

    private lateinit var contextDialog: Context
    private lateinit var listSelected:List<Food>
    private lateinit var onFoodToDeleteListener:OnFoodToDeleteListener
    private lateinit var onPickMealSaveListener: OnPickMealSaveListener

    fun newInstance(listSelected: List<Food>): MealPickDialog {

        val mealPickDialog = MealPickDialog()

        val gson = Gson()
        val jsonList = gson.toJson(listSelected)
        val args = Bundle()
        args.putString(LIST_TO_SAVE, jsonList)

        mealPickDialog.arguments = args

        return mealPickDialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.pick_meal_dialog, container)

        val arg = arguments

        if(arg!=null){
            val gson = Gson()
            val jsonList = arguments?.getString(LIST_TO_SAVE, null)
            val propTypeList = object : TypeToken<List<Food>>() {}.type
            listSelected = gson.fromJson(jsonList, propTypeList)
        }

        if(activity is OnFoodToDeleteListener)
            onFoodToDeleteListener = activity as OnFoodToDeleteListener
        if(activity is OnPickMealSaveListener)
            onPickMealSaveListener = activity as OnPickMealSaveListener

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configure views
        configureViews()
    }

    private fun configureViews() {
        configureFoodList()
        configureButtonSaveAndCancel()
    }

    fun foodDeleted(foodName:String){

        // Remove food from listSelected
        val filteredList:MutableList<Food> = mutableListOf()
        filteredList.addAll(listSelected)

        for(food in listSelected){
            if(food.name.equals(foodName)){
                filteredList.remove(food)
                break
            }
        }

        this.listSelected = filteredList.toList()

        // Update buttons
        configureButtonSaveAndCancel()
    }

    private fun configureFoodList(){

        val mLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        layout_all_foods.layoutManager = mLayoutManager

        val mealPickAdapter = MealPickAdapter(listSelected.toMutableList(), onFoodToDeleteListener, contextDialog)
        layout_all_foods.adapter = mealPickAdapter
    }

    private fun configureButtonSaveAndCancel() {

        if(listSelected.isEmpty()){
            button_save_meal.visibility = View.GONE
        } else {
            button_save_meal.visibility = View.VISIBLE
            button_save_meal.findViewById<Button>(R.id.button_save_meal).setOnClickListener {

                if(listSelected.isEmpty()){ // IF NO ITEM SELECTED, WARN THE USER
                    Toast.makeText(contextDialog, contextDialog.resources.getString(R.string.error_save_meal), Toast.LENGTH_LONG).show()
                } else { // IF AT LEAST ONE ITEM SELECTED, DATA CAN BE SAVED
                    /**    Show dialog fragment to confirm date for saving  **/
                    onPickMealSaveListener.saveMeal()
                }

                this.dismiss()
            }
        }

        button_cancel_meal.setOnClickListener {
            this.dismiss()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context!=null)
            contextDialog = context
    }
}