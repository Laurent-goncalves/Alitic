package com.g.laurent.alitic.Controllers.Activities

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.g.laurent.alitic.Controllers.ClassControllers.getAllEventTypes
import com.g.laurent.alitic.Controllers.ClassControllers.getAllFoodTypes
import com.g.laurent.alitic.Controllers.ClassControllers.getListFood
import com.g.laurent.alitic.Controllers.ClassControllers.getListFoodByType
import com.g.laurent.alitic.Controllers.DialogFragments.AddEventTypeDialog
import com.g.laurent.alitic.Controllers.DialogFragments.AddFoodDialog
import com.g.laurent.alitic.Controllers.DialogFragments.DialogCloseListener
import com.g.laurent.alitic.Controllers.DialogFragments.SHAREDPREF
import com.g.laurent.alitic.Models.EventType
import com.g.laurent.alitic.Models.Food
import com.g.laurent.alitic.Models.FoodType
import com.g.laurent.alitic.R
import com.g.laurent.alitic.Views.*
import kotlinx.android.synthetic.main.pick_event_layout.*
import kotlinx.android.synthetic.main.pick_meal_layout.*

/**
 * Activity for picking food or eventTypes and save them
 */
class PickActivity : BaseActivity(), OnMenuSelectionListener, OnFoodToDeleteListener, OnItemSelectionListener, DialogCloseListener {

    private lateinit var menuAdapter: FoodTypeAdapter
    private lateinit var gridAdapter: GridAdapter
    private lateinit var listFoodType: List<FoodType>
    private lateinit var typeDisplay:TypeDisplay
    private val onMenuSelectionListener = this
    private val onItemSelectionListener = this
    private var listSelected:MutableList<Any> = mutableListOf()
    private var scale1:Float = 0f
    private var posY1:Float = 0f
    private var sWidth:Int = 0
    private var sHeight:Int = 0
    var foodTypeSelected: FoodType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_pick)
        super.onCreate(savedInstanceState)

        // init variables
        listFoodType = getAllFoodTypes(context = applicationContext)!!

        // Recover TypeDisplay
        val bundle = intent.extras
        typeDisplay = if(bundle!=null){
            val nameTypeDisplay = bundle.getString(TYPEDISPLAY)
            if(nameTypeDisplay!=null)
                TypeDisplay.valueOf(nameTypeDisplay)
            else
                TypeDisplay.MEAL
        } else {
            TypeDisplay.MEAL
        }

        // Get dimensions of screen in SharedPreferences
        val prefs = applicationContext.getSharedPreferences(SHAREDPREF, 0)
        sWidth = prefs.getInt(SHARED_PREF_SWIDTH, 0)
        sHeight = prefs.getInt(SHARED_PREF_SHEIGHT, 0)

        // Start moving camera
        if(typeDisplay.equals(TypeDisplay.MEAL))
            movePicture(imageBackground,Loc.CENTER.position, Loc.TOP_LEFT.position,matrix)
        else
            movePicture(imageBackground,Loc.CENTER.position, Loc.TOP_RIGHT.position,matrix)
    }

    private fun configurePickActivity() {

        if(typeDisplay.equals(TypeDisplay.MEAL)){
            findViewById<FrameLayout>(R.id.layout_meal).visibility = View.VISIBLE
            configureFoodTypes()
            configureGridView(TypeDisplay.MEAL)
            configureMealPanel()
            configureButtonsSaveCancel(TypeDisplay.MEAL)
        } else {
            findViewById<FrameLayout>(R.id.layout_event).visibility = View.VISIBLE
            configureGridView(TypeDisplay.EVENT)
            configureButtonsSaveCancel(TypeDisplay.EVENT)
        }
    }

    private fun configureFoodTypes(){
        val mLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        food_recycler_view.layoutManager = mLayoutManager
        foodTypeSelected = listFoodType[0]
        menuAdapter = FoodTypeAdapter(listFoodType, sWidth / 4, onMenuSelectionListener, context = context)
        food_recycler_view.adapter = menuAdapter
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

    private fun getConfirmationToLeavePickActivity(typeDisplay:TypeDisplay){
        val builder = AlertDialog.Builder(this@PickActivity)

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
            goToBackToMainPage()
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

    private fun configureButtonsSaveCancel(typeDisplay:TypeDisplay){

        // Associate buttons Cancel and save
        val buttonCancel: Button = findViewById(typeDisplay.idCancel)
        val buttonSave: Button = findViewById(typeDisplay.idSave)

        // Set on click listener for each button
        buttonCancel.setOnClickListener {
            getConfirmationToLeavePickActivity(typeDisplay)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_pick, menu)

        val searchIcon = toolbar.menu.findItem(R.id.action_search)

        when {
            typeDisplay.equals(TypeDisplay.EVENT) -> { // PICK EVENT
                searchIcon.isVisible = false
                configureToolbar(toolbar,
                    title = context.getString(R.string.title_event_choice),
                    homeButtonNeeded = true,
                    infoIconNeeded = false
                )
            }

            typeDisplay.equals(TypeDisplay.MEAL) -> { // PICK MEAL
                configureToolbar(toolbar,
                    title = context.getString(R.string.title_meal_choice),
                    homeButtonNeeded = true,
                    infoIconNeeded = false
                )

                searchIcon.isVisible = true

                (searchIcon.actionView as SearchView).setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                    override fun onQueryTextChange(newText: String): Boolean {

                        val listFoods = getListFood(newText, context = context)
                        if(listFoods!=null)
                            updateListFoodsAfterQueryChange(listFoods)
                        return false
                    }

                    override fun onQueryTextSubmit(query: String): Boolean {
                        return false
                    }
                })

                (searchIcon.actionView as SearchView).setOnCloseListener {
                    resetMealPickingScreenAfterSearch()
                    true
                }

            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onClickBackButtonToolbar() {
        goToBackToMainPage()
    }

    override fun onMenuItemClick() {}

    override fun onMenuSelected(selection: Int) {
        val listFoodTypes = getAllFoodTypes(context = context)!!
        foodTypeSelected = listFoodTypes[selection]
        resetMealPickingScreenAfterSearch()
    }

    override fun onItemSelected(selected: Any) {

        // Update list of selected items
        listSelected = updateListSelected(selected, listSelected)

        // Update counter
        counter_meal.text = listSelected.size.toString()
    }

    private fun configureMealPanel(){

        fun showMeal(){

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

        val panel = findViewById<View>(R.id.panel)
        val panelContent = findViewById<LinearLayout>(R.id.panel_content)

        // init constraint layout with all foods (remove all views)
        panelContent.findViewById<FoodLayout>(R.id.layout_all_foods).removeAllViews()
        panelContent.findViewById<FoodLayout>(R.id.layout_all_foods).visibility = View.GONE
        counter_meal.text = 0.toString()

        // Set panel touch listener to control movement of the panel
        val panelTouchListener = View.OnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    posY1 = event.rawY
                    scale1 = panel.scaleX
                }
                MotionEvent.ACTION_MOVE -> {
                    movePanel(panel, scale1, posY1 - event.rawY)
                }
                MotionEvent.ACTION_UP -> {
                    if (event.rawY > posY1 && panel.scaleX >= Pan.SCALE_PANEL.min) { // UP direction
                        closePanel(panel)
                        panel_content.visibility = View.GONE
                        counter_meal.visibility = View.VISIBLE
                        counter_meal.text = listSelected.size.toString()

                    } else if (event.rawY < posY1 && panel.scaleX >= Pan.SCALE_PANEL.min) { // DOWN direction
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

    fun updateListFoodsAfterQueryChange(listFoods:List<Food>){
        gridAdapter = GridAdapter(listFoods, listSelected, true, onItemSelectionListener,false, this, context = context)
        gridview_food.adapter = gridAdapter
    }

    private fun resetMealPickingScreenAfterSearch(){

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

        val panelLayout = findViewById<LinearLayout>(R.id.panel_content)
            .findViewById<FoodLayout>(R.id.layout_all_foods)

        // If no more food selected, show message "no food selected"
        if(listSelected.isEmpty()){
            findViewById<LinearLayout>(R.id.panel_content)
                .findViewById<MealTextView>(R.id.no_food_in_meal).visibility = View.VISIBLE
        }

        // organize views from constraint layout
        panelLayout.removeAllViews()
        val listIds = mutableListOf<FoodViewId>()
        for(i in 0 until listSelected.size) {
            val id = panelLayout.addFood(listSelected[i] as Food)
            if(id!=null)
                listIds.add(id)
        }
        panelLayout.organizeViews(listIds)
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

    override fun doWhenAnimationIsFinished(toPosition: Position) {
        if(toPosition.equals(Loc.CENTER.position)){ // if picture move to center
            finishActivity()
        } else { // if picture move to left or right top corner
            configurePickActivity()
        }
    }

    public override fun goToBackToMainPage(){

        super.goToBackToMainPage()

        if(typeDisplay.equals(TypeDisplay.EVENT)){
            // Hide layout for event picking
            findViewById<FrameLayout>(R.id.layout_event).visibility = View.GONE
            // Move camera to the center of image in background
            movePicture(imageBackground, Loc.TOP_RIGHT.position,Loc.CENTER.position, matrix)
        } else {
            // Hide layout for meal picking
            findViewById<FrameLayout>(R.id.layout_meal).visibility = View.GONE
            // Move camera to the center of image in background
            movePicture(imageBackground, Loc.TOP_LEFT.position,Loc.CENTER.position, matrix)
        }
    }

    /**---------------------------------------- DIALOG ADD -------------------------------------
     * -----------------------------------------------------------------------------------------
     */

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

    fun showConfirmationMessageInSnackBar(message:String){
        Snackbar.make(this.findViewById(R.id.pickactivity_layout), message, Snackbar.LENGTH_SHORT).show()
    }

    override fun handleDialogClose(typeDisplay: TypeDisplay) {
        configureGridView(typeDisplay)
    }

    override fun onBackPressed() {
        getConfirmationToLeavePickActivity(typeDisplay)
    }
}
