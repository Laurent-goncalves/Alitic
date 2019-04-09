package com.g.laurent.alitic.Controllers.Activities

import android.content.Context
import android.content.Intent
import android.graphics.Matrix
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import com.g.laurent.alitic.Controllers.ClassControllers.getAllEventTypes
import com.g.laurent.alitic.Controllers.ClassControllers.getAllFoodTypes
import com.g.laurent.alitic.Controllers.ClassControllers.getListFoodByType
import kotlinx.android.synthetic.main.pick_event_layout.*
import kotlinx.android.synthetic.main.pick_meal_layout.*
import java.lang.Math.round
import android.widget.*
import com.g.laurent.alitic.Controllers.DialogFragments.NewEventTypeDialogFragment
import com.g.laurent.alitic.Controllers.DialogFragments.NewFoodDialogFragment
import com.g.laurent.alitic.Models.Food
import com.g.laurent.alitic.R
import com.g.laurent.alitic.Views.*


class MainActivity : AppCompatActivity(), View.OnClickListener, OnMenuSelectionListener, OnItemSelectionListener, OnFoodToDeleteListener {


    private var matrix = Matrix()
    private lateinit var context: Context
    private lateinit var imageView: ImageView
    private lateinit var menuAdapter: FoodTypeAdapter
    private lateinit var gridAdapter: GridAdapter
    private val onMenuSelectionListener = this
    private val onItemSelectionListener = this
    private var listSelected:MutableList<Any> = mutableListOf()
    private var scale1:Float = 0f
    private var posY1:Float = 0f
    private var sWidth = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = applicationContext
        clearDatabase(context)

        // Configure Toolbar
        configureToolbar(findViewById(R.id.activity_main_toolbar), supportFragmentManager, null, this, applicationContext)

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
                sWidth = imageView.measuredWidth

                Loc.setPosition(Loc.CENTER,
                    Position(round((dWidth - sWidth) * -0.5f).toFloat(), round((dHeight - sHeight) * -0.5f).toFloat()))
                Loc.setPosition(Loc.TOP_RIGHT,
                    Position(-(dWidth - sWidth).toFloat(), 0f))
                Loc.setPosition(Loc.BOTTOM_LEFT,
                    Position(0f, -(dHeight - sHeight).toFloat()))
                Loc.setPosition(Loc.BOTTOM_RIGHT,
                    Position(-(dWidth - sWidth).toFloat(), -(dHeight - sHeight).toFloat()))
                Loc.setPosition(Loc.SMALL_PANEL_CENTER,
                    Position((0.7071 * 7 * sWidth / 12).toFloat(), (-0.7071 * 7 * sWidth / 12).toFloat())) // 0.7071 = cos(45°) = sin(45°)
                Loc.setPosition(Loc.BIG_PANEL_CENTER,
                    Position((-sWidth/9).toFloat(), ((sHeight-sWidth)/3).toFloat()))

                moveCamera(imageView, null, Loc.CENTER.position, matrix)

                return true
            }
        })
    }

    private fun initMealPanel(){

        val panel = findViewById<View>(R.id.panel)

        val vto = panel.viewTreeObserver
        vto.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {

                panel.viewTreeObserver.removeOnPreDrawListener(this)

                Pan.setMinMax(Pan.DIAMETER_PANEL, (2*sWidth/3).toFloat(), (4*sWidth/3).toFloat()) //(diameter small panel, diameter big panel)
                Pan.setMinMax(Pan.SCALE_PANEL,Pan.DIAMETER_PANEL.min/panel.width,
                    Pan.DIAMETER_PANEL.max/panel.height) // (scale small panel, scale big panel)

                Pan.setMinMax(Pan.DELTA_Y, 0.toFloat(), Loc.SMALL_PANEL_CENTER.position.py - Loc.BIG_PANEL_CENTER.position.py)

                // Position panel on the top right with the small size
                panel.scaleX = Pan.SCALE_PANEL.min
                panel.scaleY =Pan.SCALE_PANEL.min
                panel.translationX =Loc.SMALL_PANEL_CENTER.position.px
                panel.translationY =Loc.SMALL_PANEL_CENTER.position.py

                return true
            }
        })

        // Create onTouchEventListener for panel moving and scalling
        panel.setOnTouchListener(object : View.OnTouchListener {

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        posY1 = event.rawY
                        scale1 = panel.scaleX
                    }
                    MotionEvent.ACTION_MOVE -> {
                        movePanel(panel, scale1, posY1 - event.rawY)
                    }

                    MotionEvent.ACTION_UP -> {

                        if( event.rawY < posY1 && panel.scaleX >= Pan.SCALE_PANEL.min) { // UP direction
                            closePanel(panel)
                        } else if( event.rawY > posY1 && panel.scaleX >= Pan.SCALE_PANEL.min){ // DOWN direction
                            openPanel(panel)
                            Handler().postDelayed({
                                showMeal()
                            }, 1000)
                        }
                    }
                }

                panel.invalidate()

                return v?.onTouchEvent(event) ?: true
            }
        })
    }

    override fun onFoodToDelete(nameFood: String) {

        for(any in listSelected){
            if(any is Food){
                if(any.name.equals(nameFood)) {
                    listSelected.remove(any)
                    gridAdapter.listItemSelected = listSelected
                    gridAdapter.notifyDataSetChanged()
                    break
                }
            }
        }
    }

    override fun onClick(v: View?) {

        val fm = supportFragmentManager
        val toolbar: Toolbar = findViewById(R.id.activity_main_toolbar)
        setSupportActionBar(toolbar)

        when (v?.id) {
            R.id.top_left_corner -> { //       |' |
                moveCamera(imageView, Loc.CENTER.position, Loc.TOP_LEFT.position,matrix)
                displayMealPicking()
                configureButtons(TypeDisplay.MEAL)
                configureToolbar(toolbar, fm, TypeDisplay.MEAL, this, applicationContext)
                /*val fm = supportFragmentManager
                val frag = NewFoodDialogFragment().newInstance()
                frag.show(fm, null)*/
            }

            R.id.top_right_corner -> {//       | '|
                moveCamera(imageView,Loc.CENTER.position, Loc.TOP_RIGHT.position,matrix)
                displayEventPicking()
                configureButtons(TypeDisplay.EVENT)
                configureToolbar(toolbar, fm, TypeDisplay.EVENT, this,applicationContext)
            }

            R.id.bottom_left_corner -> {//     |, |
                moveCamera(imageView,Loc.CENTER.position, Loc.BOTTOM_LEFT.position,matrix)
                val intent = Intent(this, ChronoActivity::class.java)
                startActivity(intent)
            }

            R.id.bottom_right_corner -> {//    | ,|
                moveCamera(imageView,Loc.CENTER.position, Loc.BOTTOM_RIGHT.position,matrix)
                val intent = Intent(this, StatActivity::class.java)
                startActivity(intent)
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

        val fm = supportFragmentManager
        val toolbar: Toolbar = findViewById(R.id.activity_main_toolbar)
        setSupportActionBar(toolbar)

        when(typeDisplay){
            EVENT -> {
                // Hide layout for meal picking
                findViewById<FrameLayout>(R.id.layout_event).visibility = View.GONE

                // Move camera to the center of image in background
                Handler().postDelayed({
                    moveCamera(imageView, Loc.TOP_RIGHT.position,Loc.CENTER.position, matrix)
                },DELAY_HIDE)

                // Configure Toolbar
                configureToolbar(toolbar, fm, null, this, applicationContext)
            }
            MEAL -> {
                // Hide layout for meal picking
                findViewById<FrameLayout>(com.g.laurent.alitic.R.id.layout_meal).visibility = View.GONE

                // Move camera to the center of image in background
                Handler().postDelayed({
                    moveCamera(imageView, Loc.TOP_LEFT.position,Loc.CENTER.position, matrix)
                },DELAY_HIDE)

                // Configure Toolbar
                configureToolbar(toolbar, fm, null, this, applicationContext)
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

            findViewById<FrameLayout>(com.g.laurent.alitic.R.id.layout_meal).visibility = View.VISIBLE

            initMealPanel()

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

    fun showMeal(){

        val panel = findViewById<LinearLayout>(com.g.laurent.alitic.R.id.panel_content)
        panel.visibility = View.VISIBLE

        val panelLayout = panel.findViewById<FoodLayout>(com.g.laurent.alitic.R.id.layout_all_foods)
        panelLayout.onFoodToDeleteListener = this

        // Show relevant views of panel
        panel.findViewById<TextView>(com.g.laurent.alitic.R.id.title_meal).visibility = View.VISIBLE
        panelLayout.visibility = View.VISIBLE

        if(listSelected.size > 0){
            panelLayout.visibility = View.VISIBLE

            val listIds = mutableListOf<FoodViewId>()

            for(i in 0 until listSelected.size) {
                val id = panelLayout.addFood(listSelected[i] as Food)
                if(id!=null)
                    listIds.add(id)
            }

            panelLayout.organizeViews(listIds)

        } else {
            panel.findViewById<TextView>(com.g.laurent.alitic.R.id.no_food_in_meal).visibility = View.VISIBLE
        }
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

        findViewById<FrameLayout>(com.g.laurent.alitic.R.id.layout_event).visibility = View.VISIBLE
        val listEventTypes = getAllEventTypes(context = context)!!
        gridAdapter = GridAdapter(listEventTypes, listSelected, true, onItemSelectionListener, context = context)


        Handler().postDelayed({

            gridview_event.adapter = gridAdapter

        },2000)

    }
}