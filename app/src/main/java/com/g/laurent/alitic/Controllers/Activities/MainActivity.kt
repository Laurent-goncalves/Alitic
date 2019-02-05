package com.g.laurent.alitic.Controllers.Activities

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.g.laurent.alitic.Controllers.ClassControllers.ChronoItem
import com.g.laurent.alitic.R
import com.g.laurent.alitic.Views.TimeLineAdapter
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var mAdapter: TimeLineAdapter
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = applicationContext
        displayTimeLine()
    }

    fun displayTimeLine(){

        mLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recycler_view.layoutManager = mLayoutManager
       // mAdapter = TimeLineAdapter(null, context = context)
        recycler_view.adapter = mAdapter
    }




}