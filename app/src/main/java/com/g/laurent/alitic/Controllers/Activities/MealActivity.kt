package com.g.laurent.alitic.Controllers.Activities

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.facebook.stetho.Stetho
import com.g.laurent.alitic.Controllers.ClassControllers.deleteFoodType
import com.g.laurent.alitic.Controllers.ClassControllers.getAllFoodTypes
import com.g.laurent.alitic.Controllers.ClassControllers.saveNewFoodType
import com.g.laurent.alitic.Models.AppDataBase
import com.g.laurent.alitic.Models.insertData
import com.g.laurent.alitic.Views.FoodTypeAdapter
import kotlinx.android.synthetic.main.pick_meal_layout.*


class MealActivity : AppCompatActivity() {

    private lateinit var mAdapter: FoodTypeAdapter
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.g.laurent.alitic.R.layout.activity_meal)
        context = applicationContext
        /*val db = AppDataBase.getInstance(context)
        insertData(db?.foodTypeDao(), db?.foodDao(), db?.keywordDao())*/
        Stetho.initializeWithDefaults(this)
        displayFoodTypesMenu()
    }

    fun displayFoodTypesMenu(){

        val listFoodTypes = getAllFoodTypes(context = context)

        if(listFoodTypes!=null){
            mLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            food_recycler_view.layoutManager = mLayoutManager
            mAdapter = FoodTypeAdapter(listFoodTypes, food_recycler_view, false, this)
            food_recycler_view.adapter = mAdapter
        }
    }

}
