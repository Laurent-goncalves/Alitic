package com.g.laurent.alitic.Controllers.Activities

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.FrameLayout
import android.support.v7.widget.SearchView
import android.widget.Toast
import com.g.laurent.alitic.Controllers.ClassControllers.*
import com.g.laurent.alitic.Controllers.DialogFragments.*
import com.g.laurent.alitic.Models.*
import com.g.laurent.alitic.R
import com.g.laurent.alitic.Views.FoodTypeAdapter
import com.g.laurent.alitic.Views.GridAdapter
import com.g.laurent.alitic.Views.SaveDialog
import com.g.laurent.alitic.Views.TAG_SCHEDULE_DIALOG
import kotlinx.android.synthetic.main.counter_layout.*
import kotlinx.android.synthetic.main.pick_event_layout.*
import kotlinx.android.synthetic.main.pick_meal_layout.*

/**
 * Activity for picking food or eventTypes and save them
 */
class PickActivity : BaseActivity(), OnMenuSelectionListener, OnFoodToDeleteListener, OnItemSelectionListener, DialogCloseListener, OnPickMealSaveListener {

    private lateinit var menuAdapter: FoodTypeAdapter
    private lateinit var gridAdapter: GridAdapter
    private lateinit var listFoodType: List<FoodType>
    private lateinit var typeDisplay: TypeDisplay
    private val onMenuSelectionListener = this
    private val onItemSelectionListener = this
    private var listSelected:MutableList<Any> = mutableListOf()
    private var sWidth:Int = 0
    private var sHeight:Int = 0
    var foodTypeSelected: FoodType? = null
    private lateinit var mealPickDialog : MealPickDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_pick)
        super.onCreate(savedInstanceState)

        // init variables
        listFoodType = getAllFoodTypes(context = applicationContext)!!

        val listFoodTotal = getAllFood(context = applicationContext)

        if(listFoodTotal!=null){
            for(i in 0 .. 25){
                listSelected.add(listFoodTotal[i])
            }
        }

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
            movePicture(imageBackground, Loc.CENTER.position, Loc.TOP_LEFT.position,matrix)
        else
            movePicture(imageBackground,Loc.CENTER.position, Loc.TOP_RIGHT.position,matrix)
    }

    override fun doWhenAnimationIsFinished(toPosition: Position) {
        if(toPosition.equals(Loc.CENTER.position)){ // if picture move to center
            finishActivity()
        } else { // if picture move to left or right top corner
            configurePickActivity()
        }
    }

    /** ---------------------------------- CONFIGURATION -----------------------------------------------
    ----------------------------------------------------------------------------------------------------
     */

    private fun configurePickActivity() {

        if(typeDisplay.equals(TypeDisplay.MEAL)){
            findViewById<FrameLayout>(R.id.layout_meal).visibility = View.VISIBLE
            configureFoodTypes()
        } else {
            findViewById<FrameLayout>(R.id.layout_event).visibility = View.VISIBLE
        }

        configureGridView(typeDisplay)
        configureButtonsSaveCancel(typeDisplay)
        configureCounter()
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

    private fun configureCounter(){

        counter_meal.text = listSelected.size.toString()

        counter_layout.setOnClickListener{
            showDialogMealPick()
        }
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
            if(listSelected.isEmpty()){ // IF NO ITEM SELECTED, WARN THE USER

                if(typeDisplay.equals(TypeDisplay.MEAL))
                    Toast.makeText(context, context.resources.getString(R.string.error_save_meal), Toast.LENGTH_LONG).show()
                else
                    Toast.makeText(context, context.resources.getString(R.string.error_save_event), Toast.LENGTH_LONG).show()

            } else { // IF AT LEAST ONE ITEM SELECTED, DATA CAN BE SAVED
                showDialogSaveTime()
            }
        }
    }

    /** ------------------------------------- TOOLBAR  -------------------------------------------------
    ----------------------------------------------------------------------------------------------------
     */

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

        if(mealPickDialog.isVisible){
            mealPickDialog.onFoodToDelete(nameFood)
        }

        configureCounter()
    }

    override fun saveMeal() {
        if(listSelected.isEmpty()){ // IF NO ITEM SELECTED, WARN THE USER
            Toast.makeText(context, context.resources.getString(R.string.error_save_meal), Toast.LENGTH_LONG).show()
        } else { // IF AT LEAST ONE ITEM SELECTED, DATA CAN BE SAVED
            showDialogSaveTime()
        }
    }

    fun deleteFromDatabase(id:Long, typeDisplay: TypeDisplay){

        fun foodTo(typeOp:String, id:Long, listSelected:MutableList<Any>, context: Context?):MutableList<Any>?{

            if(typeOp.equals(SELECT) && context!=null){

                val food = getFood(id, context = context)

                if(food!=null)
                    listSelected.add(food)

            } else if(typeOp.equals(UNSELECT)){

                for(any in listSelected){
                    if(any is Food){
                        val idToDelete = any.id

                        if(idToDelete!=null && idToDelete.equals(id)) {
                            listSelected.remove(any)
                            break
                        }
                    }
                }
            } else if(typeOp.equals(DELETE) && context!=null){

                val food = getFood(id, context = context)
                if(food!=null){
                    food.takenIntoAcc = false
                    updateFood(food, context = context)
                }

                // Unselect food
                foodTo(UNSELECT, id, listSelected, null)
            }

            return listSelected
        }

        fun eventTypeTo(typeOp:String, id:Long, listSelected:MutableList<Any>, context: Context?):MutableList<Any>?{

            if(typeOp.equals(SELECT) && context!=null){

                val eventType = getEventType(id, context = context)

                if(eventType!=null)
                    listSelected.add(eventType)

            } else if(typeOp.equals(UNSELECT)){

                for(any in listSelected){
                    if(any is EventType){
                        val idToDelete = any.id

                        if(idToDelete!=null && idToDelete.equals(id)) {
                            listSelected.remove(any)
                            break
                        }
                    }
                }

            } else if(typeOp.equals(DELETE) && context!=null){

                val eventType = getEventType(id, context = context)
                if(eventType!=null){
                    eventType.takenIntoAcc = false
                    updateEventType(eventType, context = context)
                }

                // Unselect food
                eventTypeTo(UNSELECT, id, listSelected, null)
            }

            return listSelected
        }

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

    /**---------------------------------------- DIALOG FRAG ------------------------------------
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

    private fun showDialogMealPick(){
        val fm = supportFragmentManager
        val list = mutableListOf<Food>()
        for(any in listSelected){
            list.add(any as Food)
        }
        mealPickDialog = MealPickDialog().newInstance(list.toList())
        mealPickDialog.show(fm, null)
    }

    private fun showDialogSaveTime(){
        val fm = supportFragmentManager
        val myDialogFragment = SaveDialog().newInstance(TypeDisplay.MEAL, listSelected.toList())
        myDialogFragment.show(fm, TAG_SCHEDULE_DIALOG)
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

    fun showConfirmationMessageInSnackBar(message:String){
        Snackbar.make(this.findViewById(R.id.pickactivity_layout), message, Snackbar.LENGTH_SHORT).show()
    }

    override fun handleDialogClose(typeDisplay: TypeDisplay) {
        configureGridView(typeDisplay)
    }

    /**---------------------------------------- BACK ACTION ------------------------------------
     * -----------------------------------------------------------------------------------------
     */

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

    override fun onBackPressed() {
        getConfirmationToLeavePickActivity(typeDisplay)
    }
}

interface OnMenuSelectionListener {
    fun onMenuSelected(selection:Int)
}

interface OnItemSelectionListener {
    fun onItemSelected(selected:Any)
}

interface OnFoodToDeleteListener {
    fun onFoodToDelete(nameFood: String)
}

interface OnPickMealSaveListener {
    fun saveMeal()
}
