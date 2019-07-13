package com.g.laurent.alitic.Controllers.Fragments

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.g.laurent.alitic.*
import com.g.laurent.alitic.Controllers.Activities.StatType
import com.g.laurent.alitic.Controllers.Activities.TypeDisplay
import com.g.laurent.alitic.Controllers.ClassControllers.*
import com.g.laurent.alitic.Models.EventType
import com.g.laurent.alitic.Models.Food
import com.github.mikephil.charting.charts.PieChart
import com.mikhaellopez.circularimageview.CircularImageView
import kotlinx.android.synthetic.*


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

                // Configure pop up menu
                configurePopUpMenuFood(item, framelayout, view)
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

    private fun configurePopUpMenuFood(statEntry: FoodStatEntry, frameLayout:View, view:View){
        frameLayout.findViewById<CircularImageView>(R.id.food_image).setOnLongClickListener{
            val popupMenu = PopupMenu(activity, frameLayout)
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
            true
        }
    }

    /** PIE CHART FOODTYPES **/
    private fun configureBigPieChart(view:View){
        val listFoodTypes = getListFoodTypesStats(statType, null, context = contextFrag)
        configureBigPieChart(listFoodTypes, view, contextFrag)
    }
}
