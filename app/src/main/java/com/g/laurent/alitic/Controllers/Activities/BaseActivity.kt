package com.g.laurent.alitic.Controllers.Activities

import android.content.Context
import android.graphics.Color
import android.graphics.Matrix
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.ViewTreeObserver
import android.widget.ImageView
import com.g.laurent.alitic.Controllers.DialogFragments.SHAREDPREF
import com.g.laurent.alitic.R

open class BaseActivity : AppCompatActivity() {

    protected var matrix = Matrix()
    protected lateinit var context: Context
    protected lateinit var imageBackground: ImageView
    protected lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = applicationContext
        imageBackground = findViewById(R.id.image_background)

        configureToolbar()

        if(checkScreenDimensions()){
            goToCenter(imageBackground, Loc.CENTER.position, matrix)
        }
    }

    private fun checkScreenDimensions():Boolean{

        val prefs = applicationContext.getSharedPreferences(SHAREDPREF, 0)

        val dWidth = prefs.getInt(SHARED_PREF_DWIDTH,0)
        val dHeight = prefs.getInt(SHARED_PREF_DHEIGHT,0)
        val sHeight = prefs.getInt(SHARED_PREF_SHEIGHT,0)
        val sWidth = prefs.getInt(SHARED_PREF_SWIDTH,0)

        if(dWidth==0 || dHeight==0 || sHeight==0 || sWidth==0){
            getAndSaveScreenDimensions()
            return false
        }

        configureLocEnum(dWidth, dHeight, sWidth, sHeight)
        return true
    }

    private fun getAndSaveScreenDimensions(){

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

    fun goToCenter(imageView: ImageView, toPosition:Position, matrix: Matrix){
        matrix.reset()
        matrix.setTranslate(toPosition.px, toPosition.py)
        imageView.imageMatrix = matrix
    }

    private fun configureLocEnum(dWidth:Int, dHeight:Int, sWidth:Int, sHeight:Int){

        Loc.setPosition(Loc.CENTER,
            Position(Math.round((dWidth - sWidth) * -0.5f).toFloat(), Math.round((dHeight - sHeight) * -0.5f).toFloat()))
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

    private fun configureToolbar(){
        toolbar = findViewById(R.id.activity_toolbar)
        toolbar.setTitleTextColor(Color.WHITE)

        setSupportActionBar(toolbar)
    }

    protected open fun goToBackToMainPage(){
        toolbar.title = context.getString(R.string.app_name)
        this.actionBar?.setDisplayHomeAsUpEnabled(false)
    }

    fun finishActivity() {
        finish()
        overridePendingTransition(0, 0)
    }
}
