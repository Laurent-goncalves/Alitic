package com.g.laurent.alitic.Controllers.Activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Matrix
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.*
import kotlinx.android.synthetic.main.pick_event_layout.*
import kotlinx.android.synthetic.main.pick_meal_layout.*
import java.lang.Math.round
import android.widget.*
import com.g.laurent.alitic.Views.*
import android.view.inputmethod.InputMethodManager
import com.g.laurent.alitic.R
import android.view.MotionEvent
import android.view.View.OnTouchListener
import com.g.laurent.alitic.Controllers.ClassControllers.*
import com.g.laurent.alitic.Controllers.DialogFragments.*
import com.g.laurent.alitic.Models.*


class MainActivity : AppCompatActivity(), View.OnClickListener, OnMenuSelectionListener, OnItemSelectionListener, OnFoodToDeleteListener,
    DialogCloseListener, ResetDatabaseListener {



    private var matrix = Matrix()
    private lateinit var context: Context
    private lateinit var imageView: ImageView
    private lateinit var menuAdapter: FoodTypeAdapter
    private lateinit var gridAdapter: GridAdapter
    private lateinit var listFoodType: List<FoodType>
    private val onMenuSelectionListener = this
    private val onItemSelectionListener = this
    private var listSelected:MutableList<Any> = mutableListOf()
    private var scale1:Float = 0f
    private var posY1:Float = 0f
    private var sWidth = 0
    private var sHeight = 0
    private var typeDisplay:TypeDisplay? = null
    var foodTypeSelected: FoodType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = applicationContext
        clearDatabase(context)

        val toolbar: Toolbar = findViewById(R.id.activity_main_toolbar)
        setSupportActionBar(toolbar)

        listFoodType = getAllFoodTypes(context = context)!!

        imageView = findViewById(R.id.image_background)
        findViewById<View>(R.id.top_left_corner).setOnClickListener(this)
        findViewById<View>(R.id.top_right_corner).setOnClickListener(this)
        findViewById<View>(R.id.bottom_left_corner).setOnClickListener(this)
        findViewById<View>(R.id.bottom_right_corner).setOnClickListener(this)

        initCamera()
    }

    fun showDialogAddFood(food:Food){
        val fm = supportFragmentManager
        val myDialogFragment = AddFoodDialog().newInstance(food)
        myDialogFragment.show(fm, null)
    }

    fun showDialogAddEventType(eventType: EventType){
        val fm = supportFragmentManager
        val myDialogFragment = AddEventTypeDialog().newInstance(eventType)
        myDialogFragment.show(fm, null)
    }

    override fun handleDialogClose(typeDisplay: TypeDisplay) {
        configureGridView(typeDisplay)
    }

    fun showSettingsDialog(){
        val fm = supportFragmentManager
        val myDialogFragment = SettingsDialog().newInstance()
        myDialogFragment.show(fm, null)
    }

    override fun emptyDatabase() {

        val builder = AlertDialog.Builder(this@MainActivity)

        // Display a message on alert dialog

        builder.setTitle(context.resources.getString(R.string.reset_data)) // TITLE
        builder.setMessage(context.resources.getString(R.string.confirmation_data_reset)) // MESSAGE


        // Set positive button and its click listener on alert dialog
        builder.setPositiveButton(context.resources.getString(R.string.yes)){ dialog, _ ->
            dialog.dismiss()

            val db = AppDataBase.getInstance(context)
            db?.mealItemDao()?.deleteAll()
            db?.mealDao()?.deleteAll()
            db?.eventDao()?.deleteAll()

            Toast.makeText(context, context.resources.getString(R.string.data_reset),Toast.LENGTH_LONG).show()
        }

        // Display negative button on alert dialog
        builder.setNegativeButton(context.resources.getString(R.string.no)){ dialog, _ ->
            dialog.dismiss()
        }

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()
    }

    private fun initCamera(){

        val vto = imageView.viewTreeObserver
        vto.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                // Remove after the first run so it doesn't fire forever
                imageView.viewTreeObserver.removeOnPreDrawListener(this)

                val dWidth = imageView.drawable.intrinsicWidth
                val dHeight = imageView.drawable.intrinsicHeight
                sHeight = imageView.measuredHeight
                sWidth = imageView.measuredWidth

                Loc.setPosition(Loc.CENTER,
                    Position(round((dWidth - sWidth) * -0.5f).toFloat(), round((dHeight - sHeight) * -0.5f).toFloat()))
                Loc.setPosition(Loc.TOP_RIGHT,
                    Position(-(dWidth - sWidth).toFloat(), 0f))
                Loc.setPosition(Loc.BOTTOM_LEFT,
                    Position(0f, -(dHeight - sHeight).toFloat()))
                Loc.setPosition(Loc.BOTTOM_RIGHT,
                    Position(-(dWidth - sWidth).toFloat(), -(dHeight - sHeight).toFloat()))

                // Center from small and big meal panel :
                //       x -> in the middle of the width of foodtype recyclerView
                //       y -> screen height - (radius of small panel + margin (=a quarter of the radius of the small panel))

                val xCenter = (1f/8f)*sWidth.toFloat()
                val yCenter = (sHeight - (5f/4f)*(1f/6f)*sWidth)

                Loc.setPosition(Loc.SMALL_PANEL_CENTER, Position(xCenter, yCenter))
                Loc.setPosition(Loc.BIG_PANEL_CENTER, Position(xCenter, yCenter))

                moveCamera(imageView, null, Loc.CENTER.position, matrix, null)

                val mLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                food_recycler_view.layoutManager = mLayoutManager
                foodTypeSelected = listFoodType[0]
                menuAdapter = FoodTypeAdapter(listFoodType, sWidth / 4, onMenuSelectionListener, context = context)
                food_recycler_view.adapter = menuAdapter

                return true
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_main, menu)
        val toolbar: Toolbar = findViewById(R.id.activity_main_toolbar)
        configureToolbar(toolbar, typeDisplay, this, context)
        return super.onCreateOptionsMenu(menu)
    }

    fun deleteFromDatabase(id:Long, typeDisplay: TypeDisplay){

        // Delete from Database
        if(typeDisplay.equals(TypeDisplay.MEAL)) {
            foodTo(DELETE, id, listSelected, context)
            Toast.makeText(context, context.resources.getString(R.string.food_deleted), Toast.LENGTH_LONG).show()
        } else {
            eventTypeTo(DELETE, id, listSelected, context)
            Toast.makeText(context, context.resources.getString(R.string.event_type_deleted), Toast.LENGTH_LONG).show()
        }
        // Update gridview
        configureGridView(typeDisplay)
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

        // organize views from constraint layout
        val panelLayout = findViewById<LinearLayout>(R.id.panel_content)
                            .findViewById<FoodLayout>(R.id.layout_all_foods)
        panelLayout.removeAllViews()

        val listIds = mutableListOf<FoodViewId>()

        for(i in 0 until listSelected.size) {
            val id = panelLayout.addFood(listSelected[i] as Food)
            if(id!=null)
                listIds.add(id)
        }

        panelLayout.organizeViews(listIds)
    }

    private fun configureGridView(typeDisplay: TypeDisplay?){
        if(typeDisplay!=null){
            if(typeDisplay.equals(TypeDisplay.MEAL)) {
                val listFoods = if(foodTypeSelected!=null){
                    getListFoodByType(foodTypeSelected?.id,context = context)
                } else {
                    getListFoodByType(listFoodType[0].id,context = context)
                }

                gridAdapter = GridAdapter(listFoods!!, listSelected, true, onItemSelectionListener, activity = this, context = context)
                gridview_food.adapter = gridAdapter

            } else if(typeDisplay.equals(TypeDisplay.EVENT)) {

                val listEventTypes = getAllEventTypes(context = context)

                if(listEventTypes!=null){
                    gridAdapter = GridAdapter(listEventTypes, listSelected, true, onItemSelectionListener, activity = this, context = context)
                    gridview_event.adapter = gridAdapter
                }
            }
        }
    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.top_left_corner -> { //       |' |
                typeDisplay = TypeDisplay.MEAL

                displayMealPicking()
                moveCamera(imageView, Loc.CENTER.position, Loc.TOP_LEFT.position, matrix, this)


                configureButtons(TypeDisplay.MEAL)
                invalidateOptionsMenu()
            }

            R.id.top_right_corner -> {//       | '|
                typeDisplay = TypeDisplay.EVENT
                //moveCamera(imageView,Loc.CENTER.position, Loc.TOP_RIGHT.position,matrix, this)
                displayEventPicking()
                configureButtons(TypeDisplay.EVENT)
                invalidateOptionsMenu()
            }

            R.id.bottom_left_corner -> {//     |, |
                //moveCamera(imageView,Loc.CENTER.position, Loc.BOTTOM_LEFT.position,matrix, this)
                showChronoActivity()
            }

            R.id.bottom_right_corner -> {//    | ,|
                //moveCamera(imageView,Loc.CENTER.position, Loc.BOTTOM_RIGHT.position,matrix, this)
                val intent = Intent(this, StatActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
        }
    }

    fun showChronoActivity(){
        val intent = Intent(this, ChronoActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    private fun configureButtons(typeDisplay:TypeDisplay){

        // Associate buttons Cancel and save
        val buttonCancel: Button = findViewById(typeDisplay.idCancel)
        val buttonSave:Button = findViewById(typeDisplay.idSave)

        // Set on click listener for each button
        buttonCancel.setOnClickListener {

            val builder = AlertDialog.Builder(this@MainActivity)

            // Display a message on alert dialog
            if(typeDisplay.equals(TypeDisplay.MEAL)){
                builder.setTitle(context.resources.getString(R.string.error_cancel_meal_title)) // TITLE
                builder.setMessage(context.resources.getString(R.string.error_cancel_meal)) // MESSAGE
            } else{
                builder.setTitle(context.resources.getString(R.string.error_cancel_event_title)) // TITLE
                builder.setMessage(context.resources.getString(R.string.error_cancel_event)) // MESSAGE
            }

            // Set positive button and its click listener on alert dialog
            builder.setPositiveButton(context.resources.getString(R.string.yes)){ dialog, _ ->
                dialog.dismiss()
                goToBackToMainPage(typeDisplay.type)
            }

            // Display negative button on alert dialog
            builder.setNegativeButton(context.resources.getString(R.string.no)){ dialog, _ ->
                dialog.dismiss()
            }

            // Finally, make the alert dialog using builder
            val dialog: AlertDialog = builder.create()

            // Display the alert dialog on app interface
            dialog.show()
        }

        buttonSave.setOnClickListener {

            if(listSelected.size == 0){ // IF NO ITEM SELECTED, WARN THE USER

                if(typeDisplay.equals(TypeDisplay.MEAL))
                    Toast.makeText(context, context.resources.getString(R.string.error_save_meal), Toast.LENGTH_LONG).show()
                else
                    Toast.makeText(context, context.resources.getString(R.string.error_save_event), Toast.LENGTH_LONG).show()

            } else { // IF AT LEAST ONE ITEM SELECTED, DATA CAN BE SAVED
                /**    Show dialog fragment to confirm date for saving  **/
                val fm = supportFragmentManager
                val myDialogFragment = SaveDialog().newInstance(typeDisplay, listSelected.toList())
                myDialogFragment.show(fm, TAG_SCHEDULE_DIALOG)
            }
        }
    }

    fun goToBackToMainPage(typeDisplay:String){

        this.typeDisplay = null
        this.foodTypeSelected = null

        when(typeDisplay){
            EVENT -> {
                // Hide layout for meal picking
                findViewById<FrameLayout>(R.id.layout_event).visibility = View.GONE

                // Move camera to the center of image in background
                Handler().postDelayed({
                    moveCamera(imageView, Loc.TOP_RIGHT.position,Loc.CENTER.position, matrix, null)
                },DELAY_HIDE)

                // Configure Toolbar
                invalidateOptionsMenu()
            }
            MEAL -> {
                // Hide layout for meal picking
                findViewById<FrameLayout>(R.id.layout_meal).visibility = View.GONE

                // Move camera to the center of image in background
                Handler().postDelayed({
                    moveCamera(imageView, Loc.TOP_LEFT.position,Loc.CENTER.position, matrix, null)
                },DELAY_HIDE)

                // Configure Toolbar
                invalidateOptionsMenu()
            }
        }

        // Re-initialize listSelected
        listSelected = mutableListOf()
    }

    /**
    -------------------------- MEAL PICKING --------------------------------------------------------------------------
     */

    fun displayMealPicking(){
        configureGridView(TypeDisplay.MEAL)
        findViewById<FrameLayout>(R.id.layout_meal).visibility = View.VISIBLE
        initMealPanel()
    }

    fun updateListFoods(listFoods:List<Food>){
        gridAdapter = GridAdapter(listFoods, listSelected, true, onItemSelectionListener,false, this, context = context)
        gridview_food.adapter = gridAdapter
    }

    override fun onMenuSelected(selection: Int) {

        val listFoodTypes = getAllFoodTypes(context = context)!!
        foodTypeSelected = listFoodTypes[selection]
        resetMealPickingScreenAfterSearch()
    }

    fun resetMealPickingScreenAfterSearch(){

        // Reset list foodtype by re-selecting foodTypeSelected
        val listFoodTypes = getAllFoodTypes(context = context)!!
        menuAdapter.selection = listFoodTypes.indexOf(foodTypeSelected)
        menuAdapter.notifyDataSetChanged()

        // Reset list of foods related to foodTypeSelected
        configureGridView(typeDisplay)

        // Reset toolbar and close android keyboard
        invalidateOptionsMenu()

        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onItemSelected(selected: Any) {
        listSelected = updateListSelected(selected, listSelected)

        // Update counter
        counter_meal.text = listSelected.size.toString()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initMealPanel(){

        val panel = findViewById<View>(R.id.panel)
        val panelContent = findViewById<LinearLayout>(R.id.panel_content)

        // init constraint layout with all foods (remove all views)
        panelContent.findViewById<FoodLayout>(R.id.layout_all_foods).removeAllViews()
        panelContent.findViewById<FoodLayout>(R.id.layout_all_foods).visibility = View.GONE
        counter_meal.text = 0.toString()

        // Set panel touch listener to control movement of the panel
        val panelTouchListener = OnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    posY1 = event.rawY
                    scale1 = panel.scaleX
                }
                MotionEvent.ACTION_MOVE -> {
                    movePanel(panel, scale1, posY1 - event.rawY)
                }

                MotionEvent.ACTION_UP -> {
                    if( event.rawY > posY1 && panel.scaleX >= Pan.SCALE_PANEL.min) { // UP direction
                        closePanel(panel)
                        panel_content.visibility = View.GONE
                        counter_meal.visibility = View.VISIBLE
                        counter_meal.text = listSelected.size.toString()

                    } else if( event.rawY < posY1 && panel.scaleX >= Pan.SCALE_PANEL.min){ // DOWN direction
                        openPanel(panel)
                        counter_meal.visibility = View.GONE
                        Handler().postDelayed({
                            showMeal()
                        }, 1000)
                    }
                }
            }

            panel.invalidate()

            v?.onTouchEvent(event) ?: true
        }

        val vto = panel.viewTreeObserver
        vto.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {

                panel.viewTreeObserver.removeOnPreDrawListener(this)

                Pan.setMinMax(Pan.DIAMETER_PANEL, (sWidth / 3f), (7f/4f)*sHeight ) //(diameter small panel, diameter big panel)
                Pan.setMinMax(Pan.SCALE_PANEL,Pan.DIAMETER_PANEL.min/panel.width,
                    Pan.DIAMETER_PANEL.max/panel.height) // (scale small panel, scale big panel)

                Pan.setMinMax(Pan.DELTA_Y, 0.toFloat(), Pan.DIAMETER_PANEL.max / (2f))

                // Position panel on the bottom start with the small size
                panel.scaleX = Pan.SCALE_PANEL.min
                panel.scaleY = Pan.SCALE_PANEL.min

                panel.translationX = -(1f/2f)*panel.width //Loc.SMALL_PANEL_CENTER.position.px
                panel.translationY = (1f/2f)*panel.height //Loc.SMALL_PANEL_CENTER.position.py

                return true
            }
        })

        // Create onTouchEventListener for panel moving and scalling
        panel.setOnTouchListener(panelTouchListener)
        panelContent.setOnTouchListener(panelTouchListener)
    }

    private fun showMeal(){

        val panel = findViewById<LinearLayout>(R.id.panel_content)
        panel.visibility = View.VISIBLE

        val panelLayout = panel.findViewById<FoodLayout>(R.id.layout_all_foods)
        panelLayout.onFoodToDeleteListener = this

        // Show relevant views of panel
        panel.findViewById<TextView>(R.id.title_meal).visibility = View.VISIBLE
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
            panel.findViewById<TextView>(R.id.no_food_in_meal).visibility = View.VISIBLE
        }
    }

    /**
    -------------------------- EVENT PICKING ----------------------------------------------------------------------------
     */

    private fun displayEventPicking(){
        findViewById<FrameLayout>(R.id.layout_event).visibility = View.VISIBLE
        configureGridView(TypeDisplay.EVENT)
    }

    fun getActivity():MainActivity{
        return this
    }
}