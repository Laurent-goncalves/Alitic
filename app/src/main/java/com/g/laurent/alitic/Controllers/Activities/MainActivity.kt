package com.g.laurent.alitic.Controllers.Activities

import android.content.Intent
import android.graphics.Color
import android.graphics.Matrix
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.*
import com.g.laurent.alitic.*
import com.g.laurent.alitic.Controllers.DialogFragments.*
import com.g.laurent.alitic.Models.*
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity(), View.OnClickListener, ResetDatabaseListener, OnFoodEventSettingsClick {

    private lateinit var toolbar: Toolbar
    private var matrix = Matrix()

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)

        // Configure Toolbar
        toolbar = findViewById(R.id.activity_toolbar)
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)

        if(checkScreenDimensions(findViewById(R.id.image_background))){
            goToCenter(findViewById(R.id.image_background), Loc.CENTER.position, matrix)
        }

        // Configure MainActivity
        configureMainActivity()
    }

    private fun checkScreenDimensions(imageBackground: ImageView):Boolean{

        val prefs = applicationContext.getSharedPreferences(SHAREDPREF, 0)

        val dWidth = prefs.getInt(SHARED_PREF_DWIDTH,0)
        val dHeight = prefs.getInt(SHARED_PREF_DHEIGHT,0)
        val sHeight = prefs.getInt(SHARED_PREF_SHEIGHT,0)
        val sWidth = prefs.getInt(SHARED_PREF_SWIDTH,0)

        if(dWidth==0 || dHeight==0 || sHeight==0 || sWidth==0){
            getAndSaveScreenDimensions(imageBackground)
            return false
        }

        configureLocEnum(dWidth, dHeight, sWidth, sHeight)
        return true
    }

    private fun getAndSaveScreenDimensions(imageBackground: ImageView){

        val vto = imageBackground.viewTreeObserver
        vto.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                // Remove after the first run so it doesn't fire forever
                imageBackground.viewTreeObserver.removeOnPreDrawListener(this)

                val dWidth = imageBackground.drawable.intrinsicWidth
                val dHeight = imageBackground.drawable.intrinsicHeight
                val sHeight = imageBackground.measuredHeight
                val sWidth = imageBackground.measuredWidth

                val prefs = applicationContext.getSharedPreferences(SHAREDPREF, 0)
                prefs.edit().putInt(SHARED_PREF_DWIDTH, dWidth).apply()
                prefs.edit().putInt(SHARED_PREF_DHEIGHT, dHeight).apply()
                prefs.edit().putInt(SHARED_PREF_SWIDTH, sWidth).apply()
                prefs.edit().putInt(SHARED_PREF_SHEIGHT, sHeight).apply()

                configureLocEnum(dWidth, dHeight, sWidth, sHeight)

                goToCenter(imageBackground, Loc.CENTER.position, matrix)

                return true
            }
        })
    }

    private fun configureLocEnum(dWidth:Int, dHeight:Int, sWidth:Int, sHeight:Int){

        Loc.setPosition(Loc.CENTER,
            Position(((dWidth - sWidth) * -0.5f).roundToInt().toFloat(), ((dHeight - sHeight) * -0.5f).roundToInt().toFloat()))
        Loc.setPosition(Loc.TOP_RIGHT,
            Position(-(dWidth - sWidth).toFloat(), 0f))
        Loc.setPosition(Loc.TOP_LEFT,
            Position(0f, 0f))
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

    private fun goToCenter(imageView: ImageView, toPosition: Position, matrix: Matrix){
        matrix.reset()
        matrix.setTranslate(toPosition.px, toPosition.py)
        imageView.imageMatrix = matrix
    }

    private fun configureMainActivity(){

        // Check if database has already been populated
        val prefs = applicationContext.getSharedPreferences(SHAREDPREF, 0)
        //prefs.edit().putBoolean(POPULATE_DATABASE, false).apply()
        val isDatabasePopulated = prefs.getBoolean(POPULATE_DATABASE, false)

        if(!isDatabasePopulated){
            populateDatabase(applicationContext)
            prefs.edit().putBoolean(POPULATE_DATABASE, true).apply()
        }

        // Perform actions if new version
        val version = BuildConfig.VERSION_CODE
        for(i in 1 .. version){

            val sharedPrefVers = prefs.getBoolean(SHARED_PREF_VERSION + i, false)

            if(!sharedPrefVers){
                performActionsForVersion(i, applicationContext)
                prefs.edit().putBoolean(SHARED_PREF_VERSION + i, true).apply()
            }
        }

        // Configure the 5 areas to click
        configure4Buttons()
    }

    /** ------------------------- BUTTONS ACTIONS ---------------------------------
     *  ---------------------------------------------------------------------------
     */

    private fun configure4Buttons(){
        findViewById<Button>(R.id.top_left_corner).setOnClickListener(this)
        findViewById<Button>(R.id.top_right_corner).setOnClickListener(this)
        findViewById<Button>(R.id.bottom_left_corner).setOnClickListener(this)
        findViewById<Button>(R.id.bottom_right_corner).setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.top_left_corner -> { /** MEAL PICKING */
                showPickActivity(TypeDisplay.MEAL)
                invalidateOptionsMenu()
            }

            R.id.top_right_corner -> {/** EVENT PICKING */
                showPickActivity(TypeDisplay.EVENT)
                invalidateOptionsMenu()
            }

            R.id.bottom_left_corner -> {/** CHRONOLOGY */
                showChronoActivity()
            }

            R.id.bottom_right_corner -> {/** STATISTICS */
                showStatActivity()
            }
        }
    }

    /** ------------------------ TOOLBAR ------------------------------------------
     *  ---------------------------------------------------------------------------
     */

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_main, menu)

        fun configureToolbar(toolbar: Toolbar, title:String){

            // Set title of toolbar
            toolbar.title = title

            // Hide info icon
            toolbar.menu.findItem(R.id.action_info)?.isVisible = false

            // Disable home button icon
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }

        configureToolbar(toolbar, getTitleToolbarMainActivity())

        // Settings icon click listener
        val settingsIcon = toolbar.menu.findItem(R.id.action_settings)
        settingsIcon.setOnMenuItemClickListener {
            showSettingsDialog()
            true
        }

        return super.onCreateOptionsMenu(menu)
    }

    private fun getTitleToolbarMainActivity():String{
        val prefs = applicationContext.getSharedPreferences(SHAREDPREF, 0)
        val nameUser = prefs.getString(NAME_USER, null)

        return if(nameUser == null || nameUser.isEmpty()){
            applicationContext.getString(R.string.app_name)
        } else
            "${applicationContext.getString(R.string.hello)} $nameUser!"
    }

    private fun showSettingsDialog(){
        val fm = supportFragmentManager
        val myDialogFragment = SettingsDialog().newInstance()
        myDialogFragment.show(fm, null)
    }

    fun updateToolbarTitle() {
        toolbar.title = getTitleToolbarMainActivity()
    }

    override fun emptyDatabase() {

        val builder = AlertDialog.Builder(this@MainActivity)

        // Display a message on alert dialog
        builder.setTitle(applicationContext.resources.getString(R.string.reset_data)) // TITLE
        builder.setMessage(applicationContext.resources.getString(R.string.confirmation_data_reset)) // MESSAGE

        // Set positive button and its click listener on alert dialog
        builder.setPositiveButton(applicationContext.resources.getString(R.string.yes)){ dialog, _ ->
            dialog.dismiss()
            populateDatabase(applicationContext)
            Toast.makeText(applicationContext, applicationContext.resources.getString(R.string.data_reset),Toast.LENGTH_LONG).show()
        }

        // Display negative button on alert dialog
        builder.setNegativeButton(applicationContext.resources.getString(R.string.no)){ dialog, _ ->
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
        val intent = if(typeDisplay.equals(TypeDisplay.MEAL))
            Intent(this, PickMealActivity::class.java)
        else
            Intent(this, PickEventActivity::class.java)

        intent.putExtra(TYPEDISPLAY, typeDisplay.type)
        intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    /** ---------------------------- DIALOG ---------------------------------------
     *  ---------------------------------------------------------------------------
     */

    override fun showFoodSettingsDialog() {
        val fm = supportFragmentManager
        val myDialogFragment = FoodSettingsDialog().newInstance()
        myDialogFragment.show(fm, TAG_FOOD_SETTINGS_DIALOG)
    }

    override fun showEventSettingsDialog() {
        val fm = supportFragmentManager
        val myDialogFragment = EventSettingsDialog().newInstance()
        myDialogFragment.show(fm, TAG_EVENT_SETTINGS_DIALOG)
    }

    override fun showSnackBarSettingsSaved(message:String) {
        Snackbar.make(this.findViewById(R.id.layout_main_activity), message, Snackbar.LENGTH_SHORT).show()
    }
}



