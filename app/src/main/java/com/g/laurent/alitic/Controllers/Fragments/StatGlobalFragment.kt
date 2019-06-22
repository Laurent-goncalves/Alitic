package com.g.laurent.alitic.Controllers.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.g.laurent.alitic.*
import com.g.laurent.alitic.Controllers.Activities.StatType
import com.g.laurent.alitic.Controllers.ClassControllers.*
import com.mikhaellopez.circularimageview.CircularImageView


class StatGlobalFragment : StatFragment() {

    fun newInstance(idEventType:Long?, statType: StatType):StatGlobalFragment{

        val frag = StatGlobalFragment()
        val args = Bundle()
        if(idEventType!=null)
            args.putLong(ID_EVENTTYPE, idEventType)
        args.putString(STAT_TYPE, statType.name)
        frag.arguments = args

        return frag
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_stat_global, container, false)

        // Initialize variables and configure StatDetailFragment
        val args = arguments
        if(args != null) {
            initializeVariables(args)
            configureStatGlobalFragment(view)
        }

        return view
    }

    private fun configureStatGlobalFragment(view:View) {
        // Configure title of fragment
        configureTitlesFragment(view)

        // Configure the food labels
        configureEachFood(view)

        // Configure PieChart for foodTypes
        configureBigPieChart(view)
    }

    /** TITLES FRAGMENT **/
    private fun configureTitlesFragment(view:View){

        val titleFoodView = view.findViewById<TextView>(R.id.foods_list_title)
        titleFoodView.text = contextFrag.resources.getString(statType.titleFood!!)

        val titlePieChart = view.findViewById<TextView>(R.id.piechart_title)
        titlePieChart.text = contextFrag.resources.getString(statType.titlePieChart!!)
    }

    /** FOOD LABELS **/
    private fun configureEachFood(view:View) {

        val listFood = getFoodStat(statType, null, context = contextFrag)

        if(listFood.isNotEmpty()){
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
    }

    /** PIE CHART FOODTYPES **/
    private fun configureBigPieChart(view:View){
        val listFoodTypes = getListFoodTypesStats(statType, null, context = contextFrag)
        configureBigPieChart(listFoodTypes, view, contextFrag)
    }
}
