package com.g.laurent.alitic.Controllers.Fragments

import android.content.Context
import android.support.v4.app.Fragment
import android.os.Bundle
import com.g.laurent.alitic.Controllers.Activities.StatType
import com.g.laurent.alitic.STAT_TYPE


open class StatFragment : Fragment() {

    protected lateinit var contextFrag: Context
    protected lateinit var statType:StatType

    open fun initializeVariables(args:Bundle) {
        val statTypeName = args.getString(STAT_TYPE)
        if(statTypeName!=null) {
            statType = StatType.valueOf(statTypeName)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context!=null)
            contextFrag = context
    }
}