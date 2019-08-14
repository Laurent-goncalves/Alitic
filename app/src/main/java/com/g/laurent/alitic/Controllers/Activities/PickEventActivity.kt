package com.g.laurent.alitic.Controllers.Activities

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import com.g.laurent.alitic.Controllers.ClassControllers.getAllEventTypes
import com.g.laurent.alitic.Controllers.ClassControllers.getEventType
import com.g.laurent.alitic.Controllers.ClassControllers.updateEventType
import com.g.laurent.alitic.Controllers.DialogFragments.AddEventTypeDialog
import com.g.laurent.alitic.Models.*
import com.g.laurent.alitic.R
import com.g.laurent.alitic.Views.GridAdapter
import com.g.laurent.alitic.Views.SaveDialog
import com.g.laurent.alitic.Views.TAG_SCHEDULE_DIALOG
import kotlinx.android.synthetic.main.pick_event_layout.*


class PickEventActivity : PickActivity(), OnActionPickListener {

    private val onActionPickListener = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // init variables
        typeDisplay = TypeDisplay.EVENT

        // Start moving camera
        movePicture(imageBackground, Loc.CENTER.position, Loc.TOP_RIGHT.position, matrix)
    }

    /** ---------------------------------- CONFIGURATION -----------------------------------------------
    ----------------------------------------------------------------------------------------------------
     */

    override fun configureViews() {
        configureGridView()
        configureButtonsSaveCancel()
        findViewById<FrameLayout>(R.id.layout_event).visibility = View.VISIBLE
    }

    override fun configureGridView() {
        val listEventTypes = getAllEventTypes(context = context)

        if (listEventTypes != null) {
            gridAdapter = GridAdapter(
                listEventTypes,
                listSelected,
                true,
                onActionPickListener,
                context = context
            )
            gridview_event.adapter = gridAdapter
        }
    }

    private fun configureButtonsSaveCancel() {

        // Associate buttons Cancel and save
        val buttonCancel: Button = findViewById(typeDisplay.idCancel)
        val buttonSave: Button = findViewById(typeDisplay.idSave)

        // Set on click listener for each button
        buttonCancel.setOnClickListener {
            getConfirmationToLeavePickActivity(typeDisplay)
        }

        buttonSave.setOnClickListener {
            if (listSelected.isEmpty()) { // IF NO ITEM SELECTED, WARN THE USER
                Toast.makeText(context, context.resources.getString(R.string.error_save_event), Toast.LENGTH_LONG)
                    .show()
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

        searchIcon.isVisible = false
        configureToolbar(
            toolbar,
            title = context.getString(R.string.title_event_choice),
            homeButtonNeeded = true,
            infoIconNeeded = false
        )

        return super.onCreateOptionsMenu(menu)
    }

    /**------------------------------- ACTION UPDATE -------------------------------------------
     * -----------------------------------------------------------------------------------------
     */

    override fun handleDialogClose(typeDisplay: TypeDisplay) {
        configureGridView()
    }

    override fun deleteFromDatabase(any: Any) {

        fun eventTypeTo(
            typeOp: String,
            id: Long,
            listSelected: MutableList<Any>,
            context: Context?
        ): MutableList<Any>? {

            if (typeOp.equals(SELECT) && context != null) {

                val eventType = getEventType(id, context = context)

                if (eventType != null)
                    listSelected.add(eventType)

            } else if (typeOp.equals(UNSELECT)) {

                for (selected in listSelected) {
                    if (selected is EventType) {
                        val idToDelete = selected.id

                        if (idToDelete != null && idToDelete.equals(id)) {
                            listSelected.remove(selected)
                            break
                        }
                    }
                }

            } else if (typeOp.equals(DELETE) && context != null) {

                val eventType = getEventType(id, context = context)
                if (eventType != null) {
                    eventType.takenIntoAcc = false
                    updateEventType(eventType, context = context)
                }

                // Unselect food
                eventTypeTo(UNSELECT, id, listSelected, null)
            }

            return listSelected
        }

        val idToDelete = (any as EventType).id

        if(idToDelete!=null){
            // Delete from Database
            eventTypeTo(DELETE, idToDelete, listSelected, context)
            Toast.makeText(context, context.resources.getString(R.string.event_type_deleted), Toast.LENGTH_LONG).show()

            // Update gridview
            configureGridView()
        }
    }

    override fun onItemSelected(selected: Any) {
        // Update list of selected items
        listSelected = updateListSelected(selected, listSelected)
    }

    /**---------------------------------------- DIALOG FRAG ------------------------------------
     * -----------------------------------------------------------------------------------------
     */

    override fun showAddDialog(){
        val eventType = EventType()

        val fm = supportFragmentManager
        val myDialogFragment = AddEventTypeDialog().newInstance(eventType)
        myDialogFragment.show(fm, null)
    }

    override fun showModifyDialog(any: Any) {
        val fm = supportFragmentManager
        val myDialogFragment = AddEventTypeDialog().newInstance(any as EventType)
        myDialogFragment.show(fm, null)
    }

    private fun showDialogSaveTime() {
        val fm = supportFragmentManager
        val myDialogFragment = SaveDialog().newInstance(typeDisplay, listSelected.toList())
        myDialogFragment.show(fm, TAG_SCHEDULE_DIALOG)
    }

    override fun onMenuItemClick() {
        // NO IMPLEMENTATION
    }
}
