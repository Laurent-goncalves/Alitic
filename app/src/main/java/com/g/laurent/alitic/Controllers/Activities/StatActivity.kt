package com.g.laurent.alitic.Controllers.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.g.laurent.alitic.Controllers.ClassControllers.StatList
import com.g.laurent.alitic.Controllers.ClassControllers.getAllEventTypes
import com.g.laurent.alitic.Controllers.ClassControllers.getListFoodForEventType
import com.g.laurent.alitic.Views.StatAdapter
import android.support.v4.view.ViewPager
import com.g.laurent.alitic.Controllers.Fragments.StatFragment
import com.g.laurent.alitic.R


class StatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stat)

        clearDatabase(applicationContext)

        val listEventTypes = getAllEventTypes(context = applicationContext)
        val listStats :MutableList<Long> = mutableListOf()

        if(listEventTypes!=null){
            for(eventType in listEventTypes){

                val list = getListFoodForEventType(eventType, context = applicationContext)

                if(list.isNotEmpty()){
                    val id = eventType.id
                    if(id!=null)
                        listStats.add(id)
                }

            }

            val pager = findViewById<ViewPager>(R.id.stat_viewpager)
            pager.adapter = StatAdapter(supportFragmentManager, listStats)
        }
    }
}


