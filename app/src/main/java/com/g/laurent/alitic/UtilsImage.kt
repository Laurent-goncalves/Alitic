package com.g.laurent.alitic

import android.content.Context
import android.webkit.URLUtil
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.g.laurent.alitic.Controllers.ClassControllers.getEventType
import com.g.laurent.alitic.Controllers.ClassControllers.getFoodType
import com.g.laurent.alitic.Controllers.ClassControllers.getFoodsFromMeal
import com.g.laurent.alitic.Models.Event
import com.g.laurent.alitic.Models.Food
import com.g.laurent.alitic.Models.FoodType
import com.g.laurent.alitic.Models.Meal

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

fun getImagePath(any: Any, position:Int, mode:Boolean, context: Context):String?{
    when (any) {
        is Event -> { // if event
            val eventType =
                getEventType(any.idEventType, mode, context)
            return eventType?.eventPic
        }
        is Meal -> { // if meal
            val foods = getFoodsFromMeal(any, mode, context)
            return foods[position].foodPic
        }
        is List<*> -> {
            val food = any[position] as Food
            return food.foodPic
        }
        is FoodType -> {
            return any.foodTypePic
        }
        else ->
            return null
    }
}

fun getImageDrawPath(any: Any, position:Int, mode:Boolean, context: Context):String?{
    when (any) {
        is Event -> { // if event
            val eventType =
                getEventType(any.idEventType, mode, context)
            return eventType?.eventPic
        }
        is Meal -> { // if meal
            return getFoodType(
                getFoodsFromMeal(any, mode, context)[position].idFoodType, context = context)?.foodTypePic
        }
        is List<*> -> {
            val food = any[position] as Food
            return getFoodType(food.idFoodType, mode, context)?.foodTypePic
        }
        is FoodType -> {
            return any.foodTypePic
        }
        else ->
            return null
    }
}