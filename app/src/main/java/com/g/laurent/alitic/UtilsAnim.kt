package com.g.laurent.alitic

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Handler
import android.view.View
import android.widget.FrameLayout
import com.g.laurent.alitic.Views.FoodTypeViewHolder
import com.ianpinto.androidrangeseekbar.rangeseekbar.RangeSeekBar

fun enlargeThumbnail(holder:FoodTypeViewHolder, duration:Long, color: Int, ratio:Float){

    val scaleDownX = ObjectAnimator.ofFloat(holder.itemView, "scaleX", 1/ratio)
    val scaleDownY = ObjectAnimator.ofFloat(holder.itemView, "scaleY", 1/ratio)
    val moveRightX = ObjectAnimator.ofFloat(holder.itemView, "x", holder.itemView.width.toFloat() * (1-ratio)/(2*ratio))

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        holder.itemView.elevation = 3f
    }

    // Move viewholder
    move(scaleDownX, scaleDownY, moveRightX, duration)

    // Change color viewholder
    holder.itemView.setBackgroundColor(color)

    // Make a click effect on menu selected
    Handler().postDelayed({
        ObjectAnimator.ofFloat(holder.itemView, "alpha", 1f, 0.1f, 1f).setDuration(duration).start()
    }, 50)
}

fun reduceThumbnail(holder:FoodTypeViewHolder, duration:Long, color: Int){

    val scaleDownX = ObjectAnimator.ofFloat(holder.itemView, "scaleX", 1f)
    val scaleDownY = ObjectAnimator.ofFloat(holder.itemView, "scaleY", 1f)
    val moveRightX = ObjectAnimator.ofFloat(holder.itemView, "x", 0f)

    // Move viewholder
    move(scaleDownX, scaleDownY, moveRightX, duration)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        holder.itemView.elevation = 0f
    }

    // Change color viewholder
    holder.itemView.setBackgroundColor(color)
}

fun move(scaleDownX:ObjectAnimator, scaleDownY:ObjectAnimator, moveRightX:ObjectAnimator, duration:Long){
    scaleDownX.duration = duration
    scaleDownY.duration = duration
    moveRightX.duration = duration

    val scaleDown = AnimatorSet()
    val moveRight = AnimatorSet()

    scaleDown.play(scaleDownX).with(scaleDownY)
    moveRight.play(moveRightX)

    scaleDown.start()
    moveRight.start()
}

