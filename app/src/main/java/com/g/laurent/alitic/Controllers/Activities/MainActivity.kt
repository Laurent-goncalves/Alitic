package com.g.laurent.alitic.Controllers.Activities

import android.content.Context
import android.content.Intent
import android.graphics.Matrix
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.*
import java.lang.Math.round
import android.widget.*
import com.g.laurent.alitic.R
import com.g.laurent.alitic.Controllers.DialogFragments.*
import com.g.laurent.alitic.Models.*


class MainActivity : AppCompatActivity(), View.OnClickListener, ResetDatabaseListener {

    private var matrix = Matrix()
    private lateinit var context: Context
    private lateinit var imageBackground: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = applicationContext
        imageBackground = findViewById(R.id.image_background)
        clearDatabase(context) // TODO : to delete after finalization

        // Configuration
        configureToolbar()
        configure4Buttons()

        if(checkScreenDimensions()){
            goToCenter(imageBackground, Loc.CENTER.position, matrix)
        }
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

    /** -------------------- SCREEN INITIALIZATION --------------------------------
     *  ---------------------------------------------------------------------------
     */

    private fun checkScreenDimensions():Boolean{

        val prefs = applicationContext.getSharedPreferences(SHAREDPREF, 0)

        val dWidth = prefs.getInt(SHARED_PREF_DWIDTH,0)
        val dHeight = prefs.getInt(SHARED_PREF_DHEIGHT,0)
        val sHeight = prefs.getInt(SHARED_PREF_SWIDTH,0)
        val sWidth = prefs.getInt(SHARED_PREF_SHEIGHT,0)

        if(dWidth==0 || dHeight==0 || sHeight==0 || sWidth==0){
            getAndSaveScreenDimensions()
            return false
        }

        configureLocEnum(dWidth, dHeight, sWidth, sHeight)
        return true
    }

    private fun getAndSaveScreenDimensions(){

        val imageView = findViewById<ImageView>(R.id.image_background)

        val vto = imageView.viewTreeObserver
        vto.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                // Remove after the first run so it doesn't fire forever
                imageView.viewTreeObserver.removeOnPreDrawListener(this)

                val dWidth = imageView.drawable.intrinsicWidth
                val dHeight = imageView.drawable.intrinsicHeight
                val sHeight = imageView.measuredHeight
                val sWidth = imageView.measuredWidth

                val prefs = applicationContext.getSharedPreferences(SHAREDPREF, 0)
                prefs.edit().putInt(SHARED_PREF_DWIDTH, dWidth).apply()
                prefs.edit().putInt(SHARED_PREF_DHEIGHT, dHeight).apply()
                prefs.edit().putInt(SHARED_PREF_SWIDTH, sWidth).apply()
                prefs.edit().putInt(SHARED_PREF_SHEIGHT, sHeight).apply()

                configureLocEnum(dWidth, dHeight, sWidth, sHeight)

                goToCenter(imageView, Loc.CENTER.position, matrix)

                return true
            }
        })
    }

    fun configureLocEnum(dWidth:Int, dHeight:Int, sWidth:Int, sHeight:Int){

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
    }

    /** ------------------------ TOOLBAR ------------------------------------------
     *  ---------------------------------------------------------------------------
     */

    private fun configureToolbar(){
        val toolbar: Toolbar = findViewById(R.id.activity_main_toolbar)
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_main, menu)
        val toolbar: Toolbar = findViewById(R.id.activity_main_toolbar)
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

const val TYPEDISPLAY = "typeDisplay"
const val SHARED_PREF_SWIDTH = "sWidth"
const val SHARED_PREF_SHEIGHT = "sHeight"
const val SHARED_PREF_DWIDTH = "dWidth"
const val SHARED_PREF_DHEIGHT = "dHeight"