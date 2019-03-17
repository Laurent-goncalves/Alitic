package com.g.laurent.alitic.Controllers.Fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.g.laurent.alitic.*
import com.g.laurent.alitic.Controllers.Activities.StatType
import com.g.laurent.alitic.Controllers.ClassControllers.*
import com.g.laurent.alitic.Models.EventType
import com.mikhaellopez.circularimageview.CircularImageView
import kotlin.math.max


class StatGlobalFragment : Fragment() {

    lateinit var contextFrag: Context
    lateinit var statType: StatType

    fun newInstance(statType: StatType):StatGlobalFragment{

        val frag = StatGlobalFragment()
        val args = Bundle()
        args.putString(STAT_TYPE, statType.name)
        frag.arguments = args

        return frag
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_stat_global, container, false)

        fun initializeVariables(args:Bundle?) {
            if(args != null) {
                val statTypeName = args.getString(STAT_TYPE)

                if (statTypeName != null) {
                    statType = StatType.valueOf(statTypeName)
                }
            }
        }

        initializeVariables(arguments)

        configureTitleFragment(view)
        //configureBigPieChart(view)
        configureEachFood(view)

        return view
    }

    private fun configureTitleFragment(view:View){
        val titleView = view.findViewById<TextView>(R.id.title_statfragment)
        titleView.text = statType.name
    }

    /*
    private fun configureBigPieChart(view:View){
        val listFoodTypes = getListFoodTypesCounts(statType, EventType(), context = contextFrag)
        configureBigPieChart(listFoodTypes, view, contextFrag)
    }*/

    private fun configureEachFood(view:View) {

        val listFood = getFoodStat(statType, null, context = contextFrag)

        println("eee  --------------------- " + statType.name)

        for ((j, i) in getListUsedViewIndex(listFood.size).withIndex()) {

            // --------------------------- Initialize layout
            val foodLayout:LinearLayout = when {
                i<=3 -> view.findViewById(R.id.food_layout_line1)
                i<=7 -> view.findViewById(R.id.food_layout_line2)
                else -> view.findViewById(R.id.food_layout_line3)
            }

            val id = contextFrag.resources.getIdentifier("food$i",  "id", contextFrag.packageName)
            val framelayout = (foodLayout.findViewById<View>(id) as ViewGroup).getChildAt(0) as FrameLayout
            val foodTitle = (foodLayout.findViewById<View>(id) as ViewGroup).getChildAt(1) as TextView

            // -------------------------- Configure layout
            val item = listFood[j]

            // Set food title
            foodTitle.text = item.food.name

            // Set food picture
            val foodPic = framelayout.findViewById<CircularImageView>(R.id.food_image)
            setImageResource(item.food.foodPic, foodPic, contextFrag)

            // Set piechart around found picture
            configureSmallPieChart(item, statType, framelayout, contextFrag)
        }

        for(i in getListUnusedViewIndex(listFood.size)){
            val id = contextFrag.resources.getIdentifier("food$i",  "id", contextFrag.packageName)
            val foodLayout:LinearLayout = when {
                i<=3 -> view.findViewById(R.id.food_layout_line1)
                i<=7 -> view.findViewById(R.id.food_layout_line2)
                else -> view.findViewById(R.id.food_layout_line3)
            }
            foodLayout.findViewById<View>(id).visibility = View.GONE
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context!=null)
            contextFrag = context
    }
}
