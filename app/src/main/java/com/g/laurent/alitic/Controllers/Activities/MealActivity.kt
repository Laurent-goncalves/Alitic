package com.g.laurent.alitic.Controllers.Activities

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.facebook.stetho.Stetho
import com.g.laurent.alitic.Controllers.ClassControllers.getAllFoodTypes
import com.g.laurent.alitic.Controllers.ClassControllers.getListFoodByType
import com.g.laurent.alitic.Controllers.ClassControllers.getMealItemsFromListFoods
import com.g.laurent.alitic.Models.*
import com.g.laurent.alitic.R
import com.g.laurent.alitic.Views.FoodTypeAdapter
import com.g.laurent.alitic.Views.GridAdapter
import com.g.laurent.alitic.Views.SaveDialog
import com.g.laurent.alitic.Views.TAG_SCHEDULE_DIALOG
import kotlinx.android.synthetic.main.pick_meal_layout.*


class MealActivity : AppCompatActivity(), OnMenuSelectionListener, OnItemSelectionListener {

    private lateinit var menuAdapter: FoodTypeAdapter
    private lateinit var gridAdapter: GridAdapter
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var context: Context
    private lateinit var listFoodTypes:List<FoodType>
    private val onMenuSelectionListener = this
    private val onItemSelectionListener = this
    private var listSelected:MutableList<Any> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal)
        context = applicationContext

        clearDatabase(context)

        Stetho.initializeWithDefaults(this)

        displayFoodTypesMenu()
        configureButtons()
    }

    private fun configureButtons(){
        button_cancel.setOnClickListener {
            // TODO configure cancel
        }

        button_save.setOnClickListener {
            // create list of Meal items
            val listMealItems = getMealItemsFromListFoods(listSelected.toList())

            // Show dialog fragment to confirm date for saving
            val fm = supportFragmentManager
            val myDialogFragment = SaveDialog().newInstance(listMealItems)
            myDialogFragment.show(fm, TAG_SCHEDULE_DIALOG)
        }
    }

    override fun onMenuSelected(selection: Int) {
        val listFoods = getListFoodByType(listFoodTypes[selection].id, false, context)
        gridAdapter = GridAdapter(listFoods!!, listSelected,true, onItemSelectionListener, false, context)
        gridview_food.adapter = gridAdapter
    }

    override fun onItemSelected(selected: Any) {
        listSelected = updateListSelected(selected, listSelected)
    }

    private fun displayFoodTypesMenu(){

        listFoodTypes = getAllFoodTypes(context = context)!!

        mLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        food_recycler_view.layoutManager = mLayoutManager
        menuAdapter = FoodTypeAdapter(listFoodTypes, food_recycler_view, onMenuSelectionListener,false, this)
        food_recycler_view.adapter = menuAdapter

        val listFoods = getListFoodByType(listFoodTypes[0].id,false,context)
        gridAdapter = GridAdapter(listFoods!!, listSelected, true, onItemSelectionListener,false, context)
        gridview_food.adapter = gridAdapter
    }
}

interface OnMenuSelectionListener {
    fun onMenuSelected(selection:Int)
}

interface OnItemSelectionListener {
    fun onItemSelected(selected:Any)
}

fun updateListSelected(any:Any, list:MutableList<Any>?):MutableList<Any>{

    val listUpdated = mutableListOf<Any>()

    if(list!=null)
        listUpdated.addAll(list.toList())

    when(any) {
        is Food -> {
            if (isAlreadySelected(any.id, list) && list!=null) {
                for (i in 0 until list.size) {
                    val foodSelected = list[i] as Food
                    if (any.id == foodSelected.id) {
                        listUpdated.removeAt(i)
                        break
                    }
                }
            } else
                listUpdated.add(any)
        }
        is EventType -> {
            if (isAlreadySelected(any.id, list) && list!=null) {
                for (i in 0 until list.size) {
                    val eventSelected = list[i] as EventType
                    if (any.id == eventSelected.id) {
                        list.removeAt(i)
                        break
                    }
                }
            } else
                listUpdated.add(any)
        }
    }

    return listUpdated
}

fun isAlreadySelected(idSelected:Long?,  list:MutableList<Any>?):Boolean{

    if(idSelected!=null && list!=null){
        for(any in list){
            when(any){
                is Food -> {
                    if(any.id == idSelected)
                        return true
                }
                is EventType -> {
                    if(any.id == idSelected)
                        return true
                }
            }
        }
    }
    return false
}

fun clearDatabase(context: Context){

    val db = AppDataBase.getInstance(context)
    db?.mealItemDao()?.deleteAll()
    db?.mealDao()?.deleteAll()
    db?.keywordDao()?.deleteAll()
    db?.foodDao()?.deleteAll()
    db?.foodTypeDao()?.deleteAll()
    db?.eventDao()?.deleteAll()
    db?.eventTypeDao()?.deleteAll()


    insertData(db?.foodTypeDao(), db?.foodDao(), db?.keywordDao())
}