package com.g.laurent.alitic.Controllers.Activities

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.graphics.Matrix
import java.lang.Math.round
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.animation.ValueAnimator
import android.graphics.RectF




class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var centerX:Int = 0
    private var centerY:Int = 0
    private lateinit var context: Context
    private var matrix = Matrix()
    private lateinit var imageView:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.g.laurent.alitic.R.layout.activity_main)
        context = applicationContext

        imageView = findViewById(com.g.laurent.alitic.R.id.image_test)
        val View1 = findViewById<View>(com.g.laurent.alitic.R.id.top_left_corner)
        val View2 = findViewById<View>(com.g.laurent.alitic.R.id.top_right_corner)
        val View3 = findViewById<View>(com.g.laurent.alitic.R.id.bottom_left_corner)
        val View4 = findViewById<View>(com.g.laurent.alitic.R.id.bottom_right_corner)

        View1.setOnClickListener(this)
        View2.setOnClickListener(this)
        View3.setOnClickListener(this)
        View4.setOnClickListener(this)

        initCamera()
    }

    fun initCamera(){

        val vto = imageView.viewTreeObserver
        vto.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                // Remove after the first run so it doesn't fire forever
                imageView.viewTreeObserver.removeOnPreDrawListener(this)

                val dWidth = imageView.drawable.intrinsicWidth
                val dHeight = imageView.drawable.intrinsicHeight
                val height = imageView.measuredHeight
                val width = imageView.measuredWidth

                centerX = round((width - dWidth) * 0.5f)
                centerY = round((height - dHeight) * 0.5f)

                moveCamera(null,null)

                return true
            }
        })
    }

    override fun onClick(v: View?) {

        when (v?.id) {
            com.g.laurent.alitic.R.id.top_left_corner -> {
                moveCamera(true, true)
            }

            com.g.laurent.alitic.R.id.top_right_corner -> {
                moveCamera(true, false)
            }

            com.g.laurent.alitic.R.id.bottom_left_corner -> {
                moveCamera(false, true)
            }

            com.g.laurent.alitic.R.id.bottom_right_corner -> {
                moveCamera(false, false)
            }
        }
    }

    fun moveCamera(top:Boolean?, left:Boolean?){

        var dx = 0
        var dy = 0

        if(top==null || left == null){ // CENTER
            matrix.reset()
            matrix.setTranslate(centerX.toFloat(), centerY.toFloat())
            imageView.imageMatrix = matrix

        } else {
            if (top && left) { // ---------- |' |
                dx = - centerX
                dy = - centerY
            } else if (top && !left) { // -- | '|
                dx = centerX
                dy = -centerY
            } else if (!top && left) { // -- |, |
                dx = -centerX
                dy = centerY
            } else { // -------------------- | ,|
                dx = centerX
                dy = centerY
            }

            val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
            valueAnimator.interpolator = AccelerateDecelerateInterpolator() // increase the speed first and then decrease
            valueAnimator.duration = 5000
            valueAnimator.addUpdateListener { animation ->

                val progress = animation.animatedValue as Float

                val tempX = progress * dx.toFloat()
                val tempY = progress * dy.toFloat()

                matrix.reset()
                matrix.setTranslate(centerX + tempX, centerY + tempY)

                imageView.imageMatrix = matrix
            }

            valueAnimator.start()
        }
    }
}
