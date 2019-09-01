package com.g.laurent.alitic.Controllers.Activities

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.FrameLayout
import android.widget.PopupMenu
import com.g.laurent.alitic.Controllers.DialogFragments.*
import com.g.laurent.alitic.Models.*
import com.g.laurent.alitic.R
import com.g.laurent.alitic.Views.GridAdapter


/**
 * Parent activity for picking food or eventTypes and save them
 */
abstract class PickActivity : BaseActivity(), DialogCloseListener {

    protected lateinit var gridAdapter: GridAdapter
    protected lateinit var typeDisplay: TypeDisplay
    protected var listSelected: MutableList<Any> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_pick)
        super.onCreate(savedInstanceState)
    }

    override fun doWhenAnimationIsFinished(toPosition: Position) {
        if (toPosition.equals(Loc.CENTER.position)) { // if picture move to center
            finishActivity()
        } else { // if picture move to left or right top corner
            configureViews()
        }
    }

    /** ---------------------------------- CONFIGURATION -----------------------------------------------
    ----------------------------------------------------------------------------------------------------
     */

    abstract fun configureViews()

    abstract fun configureGridView()

    /** ------------------------------------- TOOLBAR  -------------------------------------------------
    ----------------------------------------------------------------------------------------------------
     */

    override fun onClickBackButtonToolbar() {
        goToBackToMainPage()
    }

    /**---------------------------------------- DIALOG FRAG ------------------------------------
     * -----------------------------------------------------------------------------------------
     */

    protected fun getConfirmationToLeavePickActivity(typeDisplay: TypeDisplay) {
        val builder = AlertDialog.Builder(this@PickActivity)

        // Display a message on alert dialog
        if (typeDisplay.equals(TypeDisplay.MEAL)) {
            builder.setTitle(context.resources.getString(R.string.error_cancel_meal_title)) // TITLE
            builder.setMessage(context.resources.getString(R.string.error_cancel_meal)) // MESSAGE
        } else {
            builder.setTitle(context.resources.getString(R.string.error_cancel_event_title)) // TITLE
            builder.setMessage(context.resources.getString(R.string.error_cancel_event)) // MESSAGE
        }

        // Set positive button and its click listener on alert dialog
        builder.setPositiveButton(context.resources.getString(R.string.yes)) { dialog, _ ->
            dialog.dismiss()
            goToBackToMainPage()
        }

        // Display negative button on alert dialog
        builder.setNegativeButton(context.resources.getString(R.string.no)) { dialog, _ ->
            dialog.dismiss()
        }

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()
    }

    fun showConfirmationMessageInSnackBar(message: String) {
        Snackbar.make(this.findViewById(R.id.pickactivity_layout), message, Snackbar.LENGTH_SHORT).show()
    }

    /**---------------------------------------- BACK ACTION ------------------------------------
     * -----------------------------------------------------------------------------------------
     */

    public override fun goToBackToMainPage() {

        super.goToBackToMainPage()

        if (typeDisplay.equals(TypeDisplay.EVENT)) {
            // Hide layout for event picking
            findViewById<FrameLayout>(R.id.layout_event).visibility = View.GONE
            // Move camera to the center of image in background
            movePicture(imageBackground, Loc.TOP_RIGHT.position, Loc.CENTER.position, matrix)
        } else {
            // Hide layout for meal picking
            findViewById<FrameLayout>(R.id.layout_meal).visibility = View.GONE
            // Move camera to the center of image in background
            movePicture(imageBackground, Loc.TOP_LEFT.position, Loc.CENTER.position, matrix)
        }
    }

    override fun onBackPressed() {
        getConfirmationToLeavePickActivity(typeDisplay)
    }

    /**---------------------------------------- POP UP MENU ------------------------------------
     * -----------------------------------------------------------------------------------------
     */

    abstract fun deleteFromDatabase(any:Any)

    abstract fun showModifyDialog(any:Any)

    fun showPopUpMenu(view:View, any : Any){
        val popupMenu = PopupMenu(this, view)
        popupMenu.setOnMenuItemClickListener{

            if(it.itemId == R.id.menu_delete){

                val builder = AlertDialog.Builder(this)

                // Display a message on alert dialog
                when(any) {
                    is EventType -> { // if eventType
                        builder.setTitle(context.resources.getString(R.string.event_type_delete_title)) // TITLE
                        builder.setMessage(context.resources.getString(R.string.event_type_delete)) // MESSAGE
                    }
                    is Food -> {
                        builder.setTitle(context.resources.getString(R.string.food_delete_title)) // TITLE
                        builder.setMessage(context.resources.getString(R.string.food_delete)) // MESSAGE
                    }
                }

                // Set positive button and its click listener on alert dialog
                builder.setPositiveButton(context.resources.getString(R.string.yes)){ dialog, _ ->
                    dialog.dismiss()
                    deleteFromDatabase(any)
                }

                // Display negative button on alert dialog
                builder.setNegativeButton(context.resources.getString(R.string.no)){ dialog, _ ->
                    dialog.dismiss()
                }

                // Finally, make the alert dialog using builder
                val dialog: AlertDialog = builder.create()

                // Display the alert dialog on app interface
                dialog.show()

            } else if(it.itemId == R.id.menu_modify){
                showModifyDialog(any)
            }
            true
        }
        popupMenu.inflate(R.menu.menu_grid)
        popupMenu.show()
    }

    /**------------------------ ACTION UPDATE LIST SELECTED ------------------------------------
     * -----------------------------------------------------------------------------------------
     */

    fun updateListSelected(any:Any, list:MutableList<Any>?):MutableList<Any>{

        fun isAlreadySelected(idSelected:Long?,  list:MutableList<Any>?):Boolean{

            if(idSelected!=null && list!=null){
                for(item in list){
                    when(item){
                        is Food -> {
                            if(item.id == idSelected)
                                return true
                        }
                        is EventType -> {
                            if(item.id == idSelected)
                                return true
                        }
                    }
                }
            }
            return false
        }

        val listUpdated = mutableListOf<Any>()

        if(list!=null)
            listUpdated.addAll(list.toList())

        when(any) {
            is Food -> {
                if (isAlreadySelected(any.id, list) && list!=null) {
                    for (i in 0 until list.size) {
                        val foodSelected = list[i] as Food
                        if (any.id == foodSelected.id) {
                            listUpdated.removeAt(i)
                            break
                        }
                    }
                } else
                    listUpdated.add(any)
            }
            is EventType -> {
                if (isAlreadySelected(any.id, list) && list!=null) {
                    for (i in 0 until list.size) {
                        val eventSelected = list[i] as EventType
                        if (any.id == eventSelected.id) {
                            listUpdated.removeAt(i)
                            break
                        }
                    }
                } else
                    listUpdated.add(any)
            }
        }

        return listUpdated
    }
}

interface OnMenuSelectionListener {
    fun onMenuSelected(selection: Int)
}

interface OnActionPickListener {
    fun onItemSelected(selected: Any)

    fun showAddDialog()

    fun showPopUpMenu(view:View, any : Any)

    fun updateListSelected(any:Any, list:MutableList<Any>?):MutableList<Any>
}

interface OnFoodToDeleteListener {
    fun onFoodToDelete(nameFood: String)
}

interface OnPickMealSaveListener {
    fun saveMeal()
}
