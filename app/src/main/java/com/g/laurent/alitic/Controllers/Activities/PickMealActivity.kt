package com.g.laurent.alitic.Controllers.Activities

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import com.g.laurent.alitic.*
import com.g.laurent.alitic.Controllers.ClassControllers.*
import com.g.laurent.alitic.Controllers.DialogFragments.AddFoodDialog
import com.g.laurent.alitic.Controllers.DialogFragments.MealPickDialog
import com.g.laurent.alitic.Models.*
import com.g.laurent.alitic.Views.FoodTypeAdapter
import com.g.laurent.alitic.Views.GridAdapter
import com.g.laurent.alitic.Views.SaveDialog
import kotlinx.android.synthetic.main.counter_layout.*
import kotlinx.android.synthetic.main.pick_meal_layout.*

class PickMealActivity : PickActivity(), OnFoodToDeleteListener, OnMenuSelectionListener, OnActionPickListener,
    OnPickMealSaveListener {

    private val onMenuSelectionListener = this
    private val onActionPickListener = this
    private var sWidth: Int = 0
    private var foodTypeSelected: FoodType? = null
    private lateinit var mealPickDialog: MealPickDialog
    private lateinit var menuAdapter: FoodTypeAdapter
    private lateinit var listFoodType: List<FoodType>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // init variables
        typeDisplay = TypeDisplay.MEAL
        listFoodType = getAllFoodTypes(context = applicationContext)!!

        // Get dimensions of screen in SharedPreferences
        val prefs = applicationContext.getSharedPreferences(SHAREDPREF, 0)
        sWidth = prefs.getInt(SHARED_PREF_SWIDTH, 0)

        // Start moving camera
        movePicture(imageBackground, Loc.CENTER.position, Loc.TOP_LEFT.position, matrix)
    }

    /** ---------------------------------- CONFIGURATION -----------------------------------------------
    ----------------------------------------------------------------------------------------------------
     */

    override fun configureViews() {
        configureFoodTypes()
        configureGridView()
        configureCounter()
        findViewById<FrameLayout>(R.id.layout_meal).visibility = View.VISIBLE
    }

    private fun configureFoodTypes() {
        val mLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        food_recycler_view.layoutManager = mLayoutManager
        foodTypeSelected = listFoodType[0]
        menuAdapter = FoodTypeAdapter(listFoodType, onMenuSelectionListener, context = context)
        food_recycler_view.adapter = menuAdapter
    }

    private fun configureCounter() {

        counter_meal.text = listSelected.size.toString()

        counter_layout.setOnClickListener {
            showDialogMealPick()
        }
    }

    override fun configureGridView() {

        val listFoods = if (foodTypeSelected != null) {
            getListFoodByType(foodTypeSelected?.id, context = context)
        } else {
            getListFoodByType(listFoodType[0].id, context = context)
        }

        gridAdapter =
            GridAdapter(listFoods!!, listSelected, true, onActionPickListener, context = context)
        gridview_food.adapter = gridAdapter
    }

    /** ------------------------------------- TOOLBAR  -------------------------------------------------
    ----------------------------------------------------------------------------------------------------
     */

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_pick, menu)

        val searchIcon = toolbar.menu.findItem(R.id.action_search)

        configureToolbar(
            toolbar,
            title = context.getString(R.string.title_meal_choice),
            homeButtonNeeded = true,
            infoIconNeeded = false
        )

        searchIcon.isVisible = true

        val searchView = (searchIcon.actionView as SearchView)

        fun hideKeyboard() {
            val imm : InputMethodManager= context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchView.windowToken, 0)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {

                val listFoods = getListFood(newText, context = context)
                if (listFoods != null)
                    updateListFoodsAfterQueryChange(listFoods)
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
        })

        searchView.setOnCloseListener {
            resetMealPickingScreenAfterSearch()
            searchView.clearFocus()
            true
        }

        searchView.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if(!hasFocus){
                hideKeyboard()
            }
        }

        return super.onCreateOptionsMenu(menu)
    }

    fun updateListFoodsAfterQueryChange(listFoods: List<Food>) {
        gridAdapter =
            GridAdapter(listFoods, listSelected, true, onActionPickListener, false, context = context)
        gridview_food.adapter = gridAdapter
    }

    private fun resetMealPickingScreenAfterSearch() {

        // Reset list foodtype by re-selecting foodTypeSelected
        val listFoodTypes = getAllFoodTypes(context = context)!!
        menuAdapter.selection = listFoodTypes.indexOf(foodTypeSelected)
        menuAdapter.notifyDataSetChanged()

        // Reset list of foods related to foodTypeSelected
        configureGridView()

        // Reset toolbar and close android keyboard
        invalidateOptionsMenu()

        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    /**------------------------------- ACTION UPDATE -------------------------------------------
     * -----------------------------------------------------------------------------------------
     */

    override fun onItemSelected(selected: Any) {

        // Update list of selected items
        listSelected = updateListSelected(selected, listSelected)

        // Update counter
        counter_meal.text = listSelected.size.toString()
    }

    override fun onMenuSelected(selection: Int) {
        val listFoodTypes = getAllFoodTypes(context = context)!!
        foodTypeSelected = listFoodTypes[selection]
        resetMealPickingScreenAfterSearch()
    }

    override fun saveMeal() {
        if (listSelected.isEmpty()) { // IF NO ITEM SELECTED, WARN THE USER
            Toast.makeText(context, context.resources.getString(R.string.error_save_meal), Toast.LENGTH_LONG).show()
        } else { // IF AT LEAST ONE ITEM SELECTED, DATA CAN BE SAVED
            showDialogSaveTime()
        }
    }

    override fun onFoodToDelete(nameFood: String) {

        for (any in listSelected) {
            if (any is Food) {
                if (any.name.equals(nameFood)) {
                    listSelected.remove(any)
                    gridAdapter.listItemSelected = listSelected
                    gridAdapter.notifyDataSetChanged()
                    break
                }
            }
        }

        if (mealPickDialog.isVisible) {
            mealPickDialog.foodDeleted(nameFood)
        }

        configureCounter()
    }

    override fun deleteFromDatabase(any: Any) {

        fun foodTo(typeOp: String, id: Long, listSelected: MutableList<Any>, context: Context?): MutableList<Any>? {

            if (typeOp.equals(SELECT) && context != null) {

                val food = getFood(id, context = context)

                if (food != null)
                    listSelected.add(food)

            } else if (typeOp.equals(UNSELECT)) {

                for (selected in listSelected) {
                    if (selected is Food) {
                        val idToDelete = selected.id

                        if (idToDelete != null && idToDelete.equals(id)) {
                            listSelected.remove(selected)
                            break
                        }
                    }
                }
            } else if (typeOp.equals(DELETE) && context != null) {

                val food = getFood(id, context = context)
                if (food != null) {
                    food.takenIntoAcc = false
                    updateFood(food, context = context)
                }

                // Unselect food
                foodTo(UNSELECT, id, listSelected, null)
            }

            return listSelected
        }

        val idToDelete = (any as Food).id

        if(idToDelete!=null){
            // Delete from Database
            foodTo(DELETE, idToDelete, listSelected, context)
            Toast.makeText(context, context.resources.getString(R.string.food_deleted), Toast.LENGTH_LONG).show()

            // Update gridview
            configureGridView()
        }
    }

    override fun handleDialogClose(typeDisplay: TypeDisplay) {
        configureGridView()
    }

    /**---------------------------------------- DIALOG FRAG ------------------------------------
     * -----------------------------------------------------------------------------------------
     */

    private fun showDialogSaveTime() {
        val fm = supportFragmentManager
        val myDialogFragment = SaveDialog().newInstance(typeDisplay, listSelected.toList())
        myDialogFragment.show(fm, TAG_SCHEDULE_DIALOG)
    }

    private fun showDialogMealPick() {
        val fm = supportFragmentManager
        val list = mutableListOf<Food>()
        for (any in listSelected) {
            list.add(any as Food)
        }
        mealPickDialog = MealPickDialog().newInstance(list.toList())
        mealPickDialog.show(fm, null)
    }

    override fun showAddDialog(){
        val food = Food()
        food.idFoodType = foodTypeSelected?.id
        food.foodPic = foodTypeSelected?.foodTypePic

        val fm = supportFragmentManager
        val myDialogFragment = AddFoodDialog().newInstance(food)
        myDialogFragment.show(fm, null)
    }

    override fun showModifyDialog(any: Any) {
        val fm = supportFragmentManager
        val myDialogFragment = AddFoodDialog().newInstance(any as Food)
        myDialogFragment.show(fm, null)
    }

    override fun onMenuItemClick() {
        // NO IMPLEMENTATION
    }
}
