package com.g.laurent.alitic.Controllers.Activities

import android.content.Context
import android.support.v7.widget.Toolbar
import com.g.laurent.alitic.R

fun configureToolbar(toolbar: Toolbar, activity:StatActivity, context: Context){

    val infoIcon = toolbar.menu.findItem(R.id.action_info)

    // Show info when clicking on info icon
    infoIcon.setOnMenuItemClickListener {
        activity.displayInformations()
            true
    }

    // Set return icon
    activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    toolbar.setNavigationOnClickListener {
        activity.finish()
    }

    // Set title of toolbar
    toolbar.title = context.getString(R.string.title_toolbar_stat)
}
