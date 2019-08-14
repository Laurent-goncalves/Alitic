package com.g.laurent.alitic.Controllers.Activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.Matrix
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import com.g.laurent.alitic.Models.Loc
import com.g.laurent.alitic.Models.Position
import com.g.laurent.alitic.R


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

        // Hide title of App and buttons
        showAppTitleAndButtons(findViewById(R.id.title_and_buttons), false)
    }

    /** -------------------------------- IMAGE BACKGROUND UTILS -------------------------------------------
     *  ---------------------------------------------------------------------------------------------------
     *  ---------------------------------------------------------------------------------------------------
     */

    fun movePicture(imageView: ImageView, fromPosition:Position?, toPosition:Position, matrix: Matrix){

        fun goToCenter(imageView: ImageView, toPosition: Position, matrix: Matrix){
            matrix.reset()
            matrix.setTranslate(toPosition.px, toPosition.py)
            imageView.imageMatrix = matrix
        }

        fun animatePictureOpacity(){

            val valueAnimator = ValueAnimator.ofFloat(OPACITY_LIMIT, 1f) // 1f = transparent ; 0f = opaque
            valueAnimator.interpolator = AccelerateDecelerateInterpolator() // increase the speed first and then decrease
            valueAnimator.duration = DURATION_MOVE_CAMERA
            valueAnimator.addUpdateListener { animation ->

                val progress = animation.animatedValue as Float

                if(toPosition.equals(Loc.CENTER.position)){
                    imageView.alpha = progress // increase value of alpha
                } else {
                    imageView.alpha = 1f + OPACITY_LIMIT - progress // decrease value of alpha
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

    abstract fun doWhenAnimationIsFinished(toPosition: Position)

    private fun showAppTitleAndButtons(titleAndButtons: RelativeLayout, show:Boolean) {

        fun createAndStartAnimationForTitle(titleView: View){
            val valueAnimator = ValueAnimator.ofFloat(0f, 1f) // 1f = opaque ; 0f = transparent
            valueAnimator.interpolator = AccelerateDecelerateInterpolator() // increase the speed first and then decrease
            valueAnimator.duration = DURATION_MOVE_CAMERA
            valueAnimator.addUpdateListener { animation ->

                val progress = animation.animatedValue as Float

                if(show){
                    titleView.alpha = progress // increase value of alpha
                    titleView.visibility = View.VISIBLE
                } else {
                    titleView.alpha = 1f - progress // decrease value of alpha
                }
            }
            valueAnimator.addListener(object: AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    titleView.visibility = if(show) View.VISIBLE else View.GONE
                }
            })

            valueAnimator.start()
        }

        createAndStartAnimationForTitle(titleAndButtons.findViewById(R.id.app_title))
        createAndStartAnimationForTitle(titleAndButtons.findViewById(R.id.top_left_corner))
        createAndStartAnimationForTitle(titleAndButtons.findViewById(R.id.top_right_corner))
        createAndStartAnimationForTitle(titleAndButtons.findViewById(R.id.bottom_left_corner))
        createAndStartAnimationForTitle(titleAndButtons.findViewById(R.id.bottom_right_corner))
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

        // Show title of App and buttons
        showAppTitleAndButtons(findViewById(R.id.title_and_buttons), true)
    }

    override fun onBackPressed() {
        goToBackToMainPage()
    }

    fun finishActivity() {
        finish()
        overridePendingTransition(0, 0)
    }
}

const val OPACITY_LIMIT = 0.25f
const val DURATION_MOVE_CAMERA = 3000.toLong()