package com.g.laurent.alitic

import android.content.Context
import android.webkit.URLUtil
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.g.laurent.alitic.Controllers.ClassControllers.getEventType
import com.g.laurent.alitic.Controllers.ClassControllers.getFoodType
import com.g.laurent.alitic.Models.*

fun getResourceId(path:String?, context: Context):Int{
    val resources = context.resources
    return resources.getIdentifier(path, "drawable", context.packageName)
}

fun setImageResource(path:String?, imageView: ImageView, context: Context):Boolean{
    val resourceId = getResourceId(path, context)
    if(resourceId!=0) { // if resource found
        imageView.setImageResource(resourceId)
        return true
    }
    return false
}

fun isDrawable(path:String?, context: Context):Boolean {

    if(path==null){
        return false
    } else {
        if(getResourceId(path, context)!=0)
            return true
    }
    return false
}

fun isUrl(path:String?):Boolean {

    if(path==null){
        return false
    } else {
        if(URLUtil.isValidUrl(path))
            return true
    }
    return false
}

fun setImageInImageView(any: Any , imageView: ImageView, mode:Boolean = false, context: Context){

    val path = getImagePath(any, mode, context)

    if (isUrl(path))
        Glide.with(context)
            .load(path)
            .into(imageView)
    else if (isDrawable(path, context))
        imageView.setImageResource(getResourceId(path, context))
}

fun getImagePath(any: Any, mode:Boolean = false, context: Context):String?{

    return when (any) {
        is Food -> {
            if(isUrl(any.foodPic) || isDrawable(any.foodPic, context)){
                any.foodPic
            } else
                getFoodType(any.idFoodType, mode, context)?.foodTypePic
        }
        is Event -> {
            getEventType(any.idEventType, mode, context)?.eventPic
        }
        is EventType -> {
            any.eventPic
        }
        is FoodType -> {
            any.foodTypePic
        }
        else ->
            null
    }
}






