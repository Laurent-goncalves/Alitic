package com.g.laurent.alitic.Controllers.Activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.Matrix
import android.support.v7.widget.Toolbar
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import com.g.laurent.alitic.R


fun configureToolbarWhenChronoFragment(toolbar: Toolbar, title:String, activity:ChronoActivity){

    // Show icon info
    val infoIcon = toolbar.menu.findItem(R.id.action_info)
    infoIcon.setOnMenuItemClickListener {
        activity.showInfo()
        true
    }

    // Set return icon
    activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    toolbar.setNavigationOnClickListener {
        activity.goToBackToMainPage()
    }

    // Set title of toolbar
    toolbar.title = title
}

fun configureToolbarWhenTimeLineFragment(toolbar: Toolbar, day:String, activity:ChronoActivity){

    // Hide icon info
    toolbar.menu.findItem(R.id.action_info).isVisible = false

    // Set return icon
    activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    toolbar.setNavigationOnClickListener {
        activity.showChronoFragment()
        toolbar.title = activity.resources.getString(R.string.chronology)
    }

    // Set title of toolbar (day of the time line)
    toolbar.title = day
}

fun movePicture(imageView: ImageView, fromPosition:Position?, toPosition:Position, matrix: Matrix, activity: ChronoActivity?){

    if(fromPosition==null){
        matrix.reset()
        matrix.setTranslate(toPosition.px, toPosition.py)
        imageView.imageMatrix = matrix

    } else {

        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.interpolator = AccelerateDecelerateInterpolator() // increase the speed first and then decrease
        valueAnimator.duration = DURATION_MOVE_CAMERA
        valueAnimator.addUpdateListener { animation ->

            val progress = animation.animatedValue as Float

            val tempX = fromPosition.px + progress * (toPosition.px - fromPosition.px)
            val tempY = fromPosition.py + progress * (toPosition.py - fromPosition.py)

            matrix.reset()
            matrix.setTranslate(tempX, tempY)

            imageView.imageMatrix = matrix
        }

        valueAnimator.addListener(object: AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                if(toPosition.equals(Loc.CENTER.position)){ // if picture move to center
                    activity?.finishActivity()
                } else { // if picture move to bottom left corner
                    activity?.configureChronoActivity()
                }
            }
        })
        valueAnimator.start()
    }
}