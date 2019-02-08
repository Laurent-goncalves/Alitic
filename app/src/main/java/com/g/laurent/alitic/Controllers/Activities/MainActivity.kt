package com.g.laurent.alitic.Controllers.Activities

import android.content.Context
import android.content.Intent
import android.graphics.Matrix
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import com.g.laurent.alitic.Controllers.ClassControllers.getAllEventTypes
import com.g.laurent.alitic.Controllers.ClassControllers.getAllFoodTypes
import com.g.laurent.alitic.Controllers.ClassControllers.getListFoodByType
import com.g.laurent.alitic.R
import com.g.laurent.alitic.Views.FoodTypeAdapter
import com.g.laurent.alitic.Views.GridAdapter
import com.g.laurent.alitic.Views.SaveDialog
import com.g.laurent.alitic.Views.TAG_SCHEDULE_DIALOG
import kotlinx.android.synthetic.main.pick_event_layout.*
import kotlinx.android.synthetic.main.pick_meal_layout.*
import java.lang.Math.round


class MainActivity : AppCompatActivity(), View.OnClickListener, OnMenuSelectionListener, OnItemSelectionListener {

    private var matrix = Matrix()
    private lateinit var context: Context
    private lateinit var imageView: ImageView
    private lateinit var menuAdapter: FoodTypeAdapter
    private lateinit var gridAdapter: GridAdapter
    private val onMenuSelectionListener = this
    private val onItemSelectionListener = this
    private var listSelected:MutableList<Any> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.g.laurent.alitic.R.layout.activity_main)
        context = applicationContext
        clearDatabase(context)

        imageView = findViewById(R.id.image_background)
        findViewById<View>(R.id.top_left_corner).setOnClickListener(this)
        findViewById<View>(R.id.top_right_corner).setOnClickListener(this)
        findViewById<View>(R.id.bottom_left_corner).setOnClickListener(this)
        findViewById<View>(R.id.bottom_right_corner).setOnClickListener(this)

        initCamera()
    }

    private fun initCamera(){

        val vto = imageView.viewTreeObserver
        vto.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                // Remove after the first run so it doesn't fire forever
                imageView.viewTreeObserver.removeOnPreDrawListener(this)

                val dWidth = imageView.drawable.intrinsicWidth
                val dHeight = imageView.drawable.intrinsicHeight
                val sHeight = imageView.measuredHeight
                val sWidth = imageView.measuredWidth

                Loc.setPosition(Loc.CENTER,
                    Position(round((dWidth - sWidth) * -0.5f).toFloat(), round((dHeight - sHeight) * -0.5f).toFloat()))
                Loc.setPosition(Loc.TOP_RIGHT,
                    Position(-(dWidth - sWidth).toFloat(), 0f))
                Loc.setPosition(Loc.BOTTOM_LEFT,
                    Position(0f, -(dHeight - sHeight).toFloat()))
                Loc.setPosition(Loc.BOTTOM_RIGHT,
                    Position(-(dWidth - sWidth).toFloat(), -(dHeight - sHeight).toFloat()))

                moveCamera(imageView, null, Loc.CENTER.position, matrix)

                return true
            }
        })
    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.top_left_corner -> { //       |' |
                moveCamera(imageView, Loc.CENTER.position, Loc.TOP_LEFT.position,matrix)
                displayMealPicking()
                configureButtons(TypeDisplay.MEAL)
            }

            R.id.top_right_corner -> {//       | '|
                moveCamera(imageView,Loc.CENTER.position, Loc.TOP_RIGHT.position,matrix)
                displayEventPicking()
                configureButtons(TypeDisplay.EVENT)
            }

            R.id.bottom_left_corner -> {//     |, |
                moveCamera(imageView,Loc.CENTER.position, Loc.BOTTOM_LEFT.position,matrix)
                val intent = Intent(this, ChronoActivity::class.java)
                startActivity(intent)
            }

            R.id.bottom_right_corner -> {//    | ,|
                moveCamera(imageView,Loc.CENTER.position, Loc.BOTTOM_RIGHT.position,matrix)
            }
        }
    }

    private fun configureButtons(typeDisplay:TypeDisplay){

        // Associate buttons Cancel and save
        val buttonCancel: Button = findViewById(typeDisplay.idCancel)
        val buttonSave:Button = findViewById(typeDisplay.idSave)

        // Set on click listener for each button
        buttonCancel.setOnClickListener { goToBackToMainPage(typeDisplay.type) }

        buttonSave.setOnClickListener {
            /**    Show dialog fragment to confirm date for saving  **/
            val fm = supportFragmentManager
            val myDialogFragment = SaveDialog().newInstance(typeDisplay, listSelected.toList())
            myDialogFragment.show(fm, TAG_SCHEDULE_DIALOG)
        }
    }

    fun goToBackToMainPage(typeDisplay:String){

        when(typeDisplay){
            EVENT -> {
                // Hide layout for meal picking
                findViewById<FrameLayout>(R.id.layout_event).visibility = View.GONE

                // Move camera to the center of image in background
                Handler().postDelayed({
                    moveCamera(imageView, Loc.TOP_RIGHT.position,Loc.CENTER.position, matrix)
                },DELAY_HIDE)

            }
            MEAL -> {
                // Hide layout for meal picking
                findViewById<FrameLayout>(R.id.layout_meal).visibility = View.GONE

                // Move camera to the center of image in background
                Handler().postDelayed({
                    moveCamera(imageView, Loc.TOP_LEFT.position,Loc.CENTER.position, matrix)
                },DELAY_HIDE)
            }
        }

        // Re-initialize listSelected
        listSelected = mutableListOf()
    }

    /**
    -------------------------- MEAL PICKING --------------------------------------------------------------------------
     */

    private fun displayMealPicking(){

        Handler().postDelayed({

            findViewById<FrameLayout>(R.id.layout_meal).visibility = View.VISIBLE

            val listFoodTypes = getAllFoodTypes(context = context)!!

            val mLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            food_recycler_view.layoutManager = mLayoutManager
            menuAdapter = FoodTypeAdapter(listFoodTypes, food_recycler_view, onMenuSelectionListener, context = this)
            food_recycler_view.adapter = menuAdapter

            val listFoods = getListFoodByType(listFoodTypes[0].id,context = context)
            gridAdapter = GridAdapter(listFoods!!, listSelected, true, onItemSelectionListener,context = context)
            gridview_food.adapter = gridAdapter

        }, DELAY_SHOW)
    }

    override fun onMenuSelected(selection: Int) {

        val listFoodTypes = getAllFoodTypes(context = context)!!

        val listFoods = getListFoodByType(listFoodTypes[selection].id, false, context)
        gridAdapter = GridAdapter(listFoods!!, listSelected,true, onItemSelectionListener, false, context)
        gridview_food.adapter = gridAdapter
    }

    override fun onItemSelected(selected: Any) {
        listSelected = updateListSelected(selected, listSelected)
    }

    /**
    -------------------------- EVENT PICKING ----------------------------------------------------------------------------
     */

    private fun displayEventPicking(){

        Handler().postDelayed({

            findViewById<FrameLayout>(R.id.layout_event).visibility = View.VISIBLE

            val listEventTypes = getAllEventTypes(context = context)!!

            gridAdapter = GridAdapter(listEventTypes, listSelected, true, onItemSelectionListener, context = context)
            gridview_event.adapter = gridAdapter

        }, DELAY_SHOW)
    }
}
