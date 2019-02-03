package com.g.laurent.alitic

import android.content.Context
import android.webkit.URLUtil
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.g.laurent.alitic.Controllers.ClassControllers.getEventType
import com.g.laurent.alitic.Controllers.ClassControllers.getFoodType
import com.g.laurent.alitic.Controllers.ClassControllers.getFoodsFromMeal
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

fun getImageFromPath(path:String?, pathDraw:String?, imageView: ImageView, context: Context){

    if(path!=null){
        if(URLUtil.isValidUrl(path)){ // image path is URL
            Glide.with(context)
                .load(path)
                .apply(RequestOptions().placeholder(getResourceId(pathDraw, context)))
                .into(imageView)
        } else {  // -------------------- image path is drawable type
            if(!setImageResource(path, imageView, context)){
                if(pathDraw!=null)
                    setImageResource(pathDraw, imageView, context) // placeholder of the category of food type
            }
        }
    }
}

fun getImagePath(any: Any):String?{

    return when (any) {
        is Food -> {
            any.foodPic
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

fun getImageDrawPath(any: Any, mode:Boolean, context: Context):String?{

    return when (any) {
        is EventType -> { // if eventType
            any.eventPic
        }
        is Food -> { // if food
            getFoodType(any.idFoodType, mode, context)?.foodTypePic
        }
        else ->
            null
    }
}