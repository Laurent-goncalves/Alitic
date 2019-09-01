package com.g.laurent.alitic.Controllers.Fragments

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.g.laurent.alitic.*
import com.g.laurent.alitic.Controllers.Activities.StatType
import com.g.laurent.alitic.Controllers.ClassControllers.*
import com.g.laurent.alitic.Models.EventType
import com.g.laurent.alitic.Views.FoodTop10Adapter
import com.github.mikephil.charting.charts.PieChart


class StatGlobalFragment : StatFragment(), OnDeleteAction {

    private val onDeleteAction = this

    fun newInstance(statType: StatType):StatGlobalFragment{

        val frag = StatGlobalFragment()
        val args = Bundle()
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

        val listFood = getFoodStat(statType, EventType(), context = contextFrag)

        fun showGraph(show:Boolean){
            view.findViewById<TextView>(R.id.not_enough_data_small_piecharts).visibility = if(show) View.GONE else View.VISIBLE
            view.findViewById<RecyclerView>(R.id.food_recycler_view).visibility = if(show) View.VISIBLE else View.GONE
        }

        showGraph(listFood.isNotEmpty())

        if(listFood.isNotEmpty()){

            val foodRecyclerView = view.findViewById<RecyclerView>(R.id.food_recycler_view)
            val mLayoutManager = LinearLayoutManager(contextFrag, RecyclerView.HORIZONTAL, false)
            foodRecyclerView.layoutManager = mLayoutManager
            val adapter = FoodTop10Adapter(listFood, view, statType, onDeleteAction, context = contextFrag)
            foodRecyclerView.adapter = adapter
        }
    }

    override fun configurePopUpMenuFood(statEntry: FoodStatEntry, foodImage:View, view:View){
        val popupMenu = PopupMenu(activity, foodImage)
        popupMenu.setOnMenuItemClickListener{

            if(it.itemId == R.id.menu_not_for_analysis){

                val builder = AlertDialog.Builder(contextFrag)

                // Display a message on alert dialog
                builder.setTitle(contextFrag.resources.getString(R.string.menu_not_for_analysis_title)) // TITLE

                val contentMessage = contextFrag.resources.getString(R.string.menu_not_for_analysis_content1) +
                        statEntry.food.name + contextFrag.resources.getString(R.string.menu_not_for_analysis_content2)
                builder.setMessage(contentMessage) // MESSAGE

                // Set positive button and its click listener on alert dialog
                builder.setPositiveButton(contextFrag.resources.getString(R.string.yes)){ dialog, _ ->
                    dialog.dismiss()

                    // Ignore food
                    ignoreFood(statEntry.food.id, context = contextFrag)

                    // Update layout analysis
                    configureEachFood(view)
                    configureBigPieChart(view)

                    // Show message to user
                    val messageToUser = contextFrag.resources.getString(R.string.menu_not_for_analysis_confirm)
                    Toast.makeText(contextFrag, messageToUser, Toast.LENGTH_LONG).show()
                }

                // Display negative button on alert dialog
                builder.setNegativeButton(contextFrag.resources.getString(R.string.no)){ dialog, _ ->
                    dialog.dismiss()
                }

                // Finally, make the alert dialog using builder
                val dialog: AlertDialog = builder.create()

                // Display the alert dialog on app interface
                dialog.show()
            }

            true
        }
        popupMenu.inflate(R.menu.menu_stats)
        popupMenu.show()
    }

    /** PIE CHART FOODTYPES **/
    private fun configureBigPieChart(view:View){
        val listFoodTypes = getListFoodTypesStats(statType, EventType(), context = contextFrag)

        if(listFoodTypes.isNotEmpty()){
            configureBigPieChart(listFoodTypes, view, contextFrag)

        } else {
            view.findViewById<TextView>(R.id.not_enough_data_big_piecharts).visibility = View.VISIBLE
            view.findViewById<PieChart>(R.id.global_pie_chart).visibility = View.GONE
        }
    }
}

interface OnDeleteAction {
    fun configurePopUpMenuFood(statEntry: FoodStatEntry, foodImage:View, view:View)
}