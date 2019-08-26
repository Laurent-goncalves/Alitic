package com.g.laurent.alitic.Controllers.Fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.GridView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.g.laurent.alitic.Controllers.ClassControllers.Chrono
import com.g.laurent.alitic.Controllers.ClassControllers.getChronology
import com.g.laurent.alitic.DAY_ARGS
import com.g.laurent.alitic.MONTH_ARGS
import com.g.laurent.alitic.R
import com.g.laurent.alitic.Views.TimeLineAdapter
import com.g.laurent.alitic.YEAR_ARGS


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

    @SuppressLint("ClickableViewAccessibility")
    private fun displayTimeLine(day:Int, month:Int, year:Int, view:View){

        val list = getChronology(day, month, year, context = contextTimeLine)
        val recyclerView = view.findViewById<RecyclerView>(R.id.timeline_recycler_view)

        layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        timeAdapter = TimeLineAdapter(list.toMutableList(), this, this, context = contextTimeLine)
        recyclerView.adapter = timeAdapter
    }


    class CustomLinearLayoutManager(context: Context):LinearLayoutManager(context, RecyclerView.VERTICAL, false){

        override fun canScrollVertically(): Boolean {

                for(i in 0 .. this.childCount){

                    val gridview = (((this.getChildAt(i) as LinearLayout).getChildAt(1) as FrameLayout).getChildAt(0) as RelativeLayout).getChildAt(2) as GridView

                    if(gridview.isInTouchMode)
                        return false
                }

            return true
        }
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

