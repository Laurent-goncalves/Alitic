package com.g.laurent.alitic.Controllers.Activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.Matrix
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import com.g.laurent.alitic.Controllers.DialogFragments.SHAREDPREF
import com.g.laurent.alitic.R
import kotlin.math.roundToInt

abstract class BaseActivity : AppCompatActivity() {

    protected var matrix = Matrix()
    protected lateinit var context: Context
    protected lateinit var imageBackground: ImageView
    protected lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = applicationContext
        imageBackground = findViewById(R.id.image_background)

        // Configure Toolbar
        toolbar = findViewById(R.id.activity_toolbar)
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)

        if(checkScreenDimensions()){
            goToCenter(imageBackground, Loc.CENTER.position, matrix)
        }
    }

    /** -------------------------------- IMAGE BACKGROUND UTILS -------------------------------------------
     *  ---------------------------------------------------------------------------------------------------
     *  ---------------------------------------------------------------------------------------------------
     */

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

    fun movePicture(imageView: ImageView, fromPosition:Position?, toPosition:Position, matrix: Matrix){

        fun animatePictureOpacity(){

            val valueAnimator = ValueAnimator.ofFloat(0.5f, 1f) // 1f = transparent ; 0.5f = semi-transparent
            valueAnimator.interpolator = AccelerateDecelerateInterpolator() // increase the speed first and then decrease
            valueAnimator.duration = DURATION_MOVE_CAMERA
            valueAnimator.addUpdateListener { animation ->

                val progress = animation.animatedValue as Float

                if(toPosition.equals(Loc.CENTER.position)){
                    imageView.alpha = progress // increase value of alpha
                } else {
                    imageView.alpha = 1.5f - progress // decrease value of alpha
                }
            }

            valueAnimator.start()
        }

        fun animatePictureMovement(){

            val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
            valueAnimator.interpolator = AccelerateDecelerateInterpolator() // increase the speed first and then decrease
            valueAnimator.duration = DURATION_MOVE_CAMERA
            valueAnimator.addUpdateListener { animation ->

                val progress = animation.animatedValue as Float

                val tempX = fromPosition!!.px + progress * (toPosition.px - fromPosition.px)
                val tempY = fromPosition.py + progress * (toPosition.py - fromPosition.py)

                matrix.reset()
                matrix.setTranslate(tempX, tempY)

                imageView.imageMatrix = matrix
            }

            valueAnimator.addListener(object: AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    doWhenAnimationIsFinished(toPosition)
                }
            })
            valueAnimator.start()
        }

        if(fromPosition==null){
            goToCenter(imageView, toPosition, matrix)
        } else {
            animatePictureOpacity()
            animatePictureMovement()
        }
    }

    abstract fun doWhenAnimationIsFinished(toPosition:Position)

    fun goToCenter(imageView: ImageView, toPosition:Position, matrix: Matrix){
        matrix.reset()
        matrix.setTranslate(toPosition.px, toPosition.py)
        imageView.imageMatrix = matrix
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

    /** -------------------------------- TOOLBAR CONFIGURATION --------------------------------------------
     *  ---------------------------------------------------------------------------------------------------
     *  ---------------------------------------------------------------------------------------------------
     */

    abstract fun onClickBackButtonToolbar()

    abstract fun onMenuItemClick()

    fun configureToolbar(toolbar: Toolbar, title:String, homeButtonNeeded:Boolean, infoIconNeeded:Boolean){

        // Set title of toolbar
        toolbar.title = title

        // Show info when clicking on info icon
        if(infoIconNeeded){
            val infoIcon = toolbar.menu.findItem(R.id.action_info)
            infoIcon.setOnMenuItemClickListener {
                onMenuItemClick()
                true
            }
        } else {
            toolbar.menu.findItem(R.id.action_info)?.isVisible = false
        }

        // Set home button icon
        supportActionBar?.setDisplayHomeAsUpEnabled(homeButtonNeeded)
        if(homeButtonNeeded){
            toolbar.setNavigationOnClickListener {
                onClickBackButtonToolbar()
            }
        }
    }

    /** -------------------------------------- NAVIGATION -------------------------------------------------
     *  ---------------------------------------------------------------------------------------------------
     *  ---------------------------------------------------------------------------------------------------
     */

    protected open fun goToBackToMainPage(){
        toolbar.title = context.getString(R.string.app_name)
        this.actionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onBackPressed() {
        goToBackToMainPage()
    }

    fun finishActivity() {
        finish()
        overridePendingTransition(0, 0)
    }
}
