package com.g.laurent.alitic.Controllers.Activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Matrix
import android.os.AsyncTask
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.ProgressBar
import com.g.laurent.alitic.Models.EventType
import com.g.laurent.alitic.R
import java.lang.ref.WeakReference

fun getTitlesFromListEventTypes(list:List<EventType>):ArrayList<String>{

    val result = arrayListOf<String>()

    if(list.isNotEmpty()){
        for(e in list){
            val title = e.name
            if(title!=null){
                result.add(title)
            }
        }
    }
    return result
}

fun configureToolbar(toolbar: Toolbar, activity:StatActivity, context: Context){

    val infoIcon = toolbar.menu.findItem(R.id.action_info)

    // Show info when clicking on info icon
    infoIcon.setOnMenuItemClickListener {
        activity.displayInformations()
            true
    }

    // Set return icon
    activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    toolbar.setNavigationOnClickListener {
        activity.goToBackToMainPage()
    }

    // Set title of toolbar
    toolbar.title = context.getString(R.string.title_toolbar_stat)
}

fun movePicture(imageView: ImageView, fromPosition:Position?, toPosition:Position, matrix: Matrix, activity: StatActivity?){

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
                    activity?.configureStatActivity()
                }
            }
        })
        valueAnimator.start()
    }
}