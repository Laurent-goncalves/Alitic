package com.g.laurent.alitic.Views

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.support.v7.widget.AppCompatTextView
import com.g.laurent.alitic.R


class MealTextView : AppCompatTextView {

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle){
        applyStyle(context)
    }

    private fun applyStyle(context: Context){
        val customFont : String = resources.getString(R.string.Satisfy_Regular)
        val tf : Typeface = Typeface.createFromAsset(context.assets, "fonts/$customFont.ttf")
        typeface = tf
    }
}

class AppTitleTextView : AppCompatTextView {

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle){
        applyStyle(context)
    }

    private fun applyStyle(context: Context){
        val customFont : String = resources.getString(R.string.Knewave_Regular)
        val tf : Typeface = Typeface.createFromAsset(context.assets, "fonts/$customFont.ttf")
        typeface = tf
    }
}