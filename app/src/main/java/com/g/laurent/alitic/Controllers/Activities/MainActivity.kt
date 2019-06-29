package com.g.laurent.alitic.Controllers.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.*
import android.widget.*
import com.g.laurent.alitic.R
import com.g.laurent.alitic.Controllers.DialogFragments.*
import com.g.laurent.alitic.Models.*


class MainActivity : BaseActivity(), View.OnClickListener, ResetDatabaseListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)

        configureMainActivity()
    }

    private fun configureMainActivity(){
        clearDatabase(context) // TODO : to delete after finalization
        configure4Buttons()
    }

    /** ------------------------- BUTTONS ACTIONS ---------------------------------
     *  ---------------------------------------------------------------------------
     */

    private fun configure4Buttons(){
        findViewById<View>(R.id.top_left_corner).setOnClickListener(this)
        findViewById<View>(R.id.top_right_corner).setOnClickListener(this)
        findViewById<View>(R.id.bottom_left_corner).setOnClickListener(this)
        findViewById<View>(R.id.bottom_right_corner).setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.top_left_corner -> { //       |' |
                showPickActivity(TypeDisplay.MEAL)
                invalidateOptionsMenu()
            }

            R.id.top_right_corner -> {//       | '|
                showPickActivity(TypeDisplay.EVENT)
                invalidateOptionsMenu()
            }

            R.id.bottom_left_corner -> {//     |, |
                showChronoActivity()
            }

            R.id.bottom_right_corner -> {//    | ,|
                showStatActivity()
            }
        }
    }

    /** ------------------------ TOOLBAR ------------------------------------------
     *  ---------------------------------------------------------------------------
     */

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_main, menu)
        configureToolbar(toolbar, this, context)
        return super.onCreateOptionsMenu(menu)
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

    /** ----------------------- LAUNCH ACTIVITIES ---------------------------------
     *  ---------------------------------------------------------------------------
     */

    private fun showChronoActivity(){
        val intent = Intent(this, ChronoActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    private fun showStatActivity(){
        val intent = Intent(this, StatActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    private fun showPickActivity(typeDisplay:TypeDisplay){
        val intent = Intent(this, PickActivity::class.java)
        intent.putExtra(TYPEDISPLAY, typeDisplay.type)
        intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        overridePendingTransition(0, 0)
    }
}