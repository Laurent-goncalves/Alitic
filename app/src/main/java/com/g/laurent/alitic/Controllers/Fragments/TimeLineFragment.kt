package com.g.laurent.alitic.Controllers.Fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.g.laurent.alitic.Controllers.ClassControllers.Chrono
import com.g.laurent.alitic.Controllers.ClassControllers.getChronology
import com.g.laurent.alitic.R
import com.g.laurent.alitic.Views.TimeLineAdapter


class TimeLineFragment : Fragment(), OnChronoItemDeleted {

    private lateinit var timeAdapter: TimeLineAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var contextTimeLine: Context
    private var day:Int = 1
    private var month:Int = 1
    private var year:Int = 2019

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_time_line, container, false)

        val args = arguments

        if(args!=null){
            day = args.getInt(DAY_ARGS)
            month = args.getInt(MONTH_ARGS)
            year=args.getInt(YEAR_ARGS)

            displayTimeLine(day, month, year, view)
        }
        return view
    }

    private fun displayTimeLine(day:Int, month:Int, year:Int, view:View){

        val list = getChronology(day, month, year, context = contextTimeLine)
        val recyclerView = view.findViewById<RecyclerView>(R.id.timeline_recycler_view)

        layoutManager = LinearLayoutManager(contextTimeLine, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        timeAdapter = TimeLineAdapter(list.toMutableList(), this, this, context = contextTimeLine)
        recyclerView.adapter = timeAdapter
    }

    override fun chronoItemDeleted(chrono:Chrono) {
        timeAdapter.list.remove(chrono)
        timeAdapter.notifyDataSetChanged()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context!=null)
            contextTimeLine = context
    }
}

interface OnChronoItemDeleted {
    fun chronoItemDeleted(chrono:Chrono)
}

const val DAY_ARGS = "day_args"
const val MONTH_ARGS = "month_args"
const val YEAR_ARGS = "year_args"