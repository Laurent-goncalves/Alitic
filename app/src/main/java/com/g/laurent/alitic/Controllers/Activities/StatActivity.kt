package com.g.laurent.alitic.Controllers.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.g.laurent.alitic.Controllers.ClassControllers.StatList
import com.g.laurent.alitic.Controllers.ClassControllers.getAllEventTypes
import com.g.laurent.alitic.Controllers.ClassControllers.getListFoodForEventType
import com.g.laurent.alitic.R
import com.g.laurent.alitic.Views.StatAdapter
import kotlinx.android.synthetic.main.activity_stat.*


class StatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stat)

        clearDatabase(applicationContext)

        val listEventTypes = getAllEventTypes(context = applicationContext)
        val listStats :MutableList<StatList> = mutableListOf()

        if(listEventTypes!=null){
            for(eventType in listEventTypes){

                val list = getListFoodForEventType(eventType, context = applicationContext)

                if(list.isNotEmpty()){
                    //StatList(eventType.id, list).toString()
                    listStats.add(StatList(eventType.id, list))
                }

            }

            val layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
            stat_recycler_view.layoutManager = layoutManager
            val adapter = StatAdapter(applicationContext, listStats)
            stat_recycler_view.adapter = adapter
        }
    }
}


