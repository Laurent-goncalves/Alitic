package com.g.laurent.alitic.Views

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.GridView
import android.widget.ImageButton
import android.widget.TextView
import com.bumptech.glide.Glide.init
import com.g.laurent.alitic.Controllers.ChronoItem
import com.g.laurent.alitic.R
import com.github.vipulasri.timelineview.TimelineView

class TimeLineViewHolder(itemView: View, viewType: Int, val context: Context) : RecyclerView.ViewHolder(itemView) {

    var mTimelineView: TimelineView = itemView.findViewById(R.id.timeline)
    var buttonEdit:ImageButton
    var hourView : TextView
    var grid : GridView

    init {
        mTimelineView.initLine(viewType)
        buttonEdit = itemView.findViewById(R.id.edit_button)
        buttonEdit.setOnClickListener(View.OnClickListener {  }) // TODO button edit to configure
        hourView = itemView.findViewById(R.id.hour)
        grid = itemView.findViewById(R.id.grid)
    }

    fun configureTimeLineViewHolder(chronoItem: ChronoItem) {
        hourView.text = chronoItem.hour
        val adapter = GridAdapter(chronoItem.item, context)
        grid.adapter = adapter
    }
}