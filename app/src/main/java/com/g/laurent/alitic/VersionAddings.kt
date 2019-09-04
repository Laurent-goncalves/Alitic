package com.g.laurent.alitic

import android.content.Context
import android.content.pm.ApplicationInfo
import com.g.laurent.alitic.Controllers.ClassControllers.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


fun getLegalNotice(context: Context):String{

    val appName = context.resources.getString(R.string.app_name)
    val legalNotice = context.resources.getString(R.string.legal_notice)
    val versionName = BuildConfig.VERSION_NAME
    val versionNum = BuildConfig.VERSION_CODE

    // Get the date of creation of APK
    val appInfo : ApplicationInfo = context.packageManager.getApplicationInfo(context.packageName, 0)
    val appFile : String = appInfo.sourceDir
    val time : Long = File(appFile).lastModified()
    val dateFormat= SimpleDateFormat("EEE d MMM yyyy", Locale.FRENCH)

    val dateApk = dateFormat.format(time)

    return "$appName $versionName (v$versionNum)\n$dateApk\n\n$legalNotice"
}

fun performActionsForVersion(version:Int, context: Context){

    when(version){

        1 -> {
            // NO ADDING - initial version
        }
        2 -> {
            // add "pates" in SQLite
            val foodType = getFoodType("Céréales", context = context)
            val listFood = getAllFood(context = context)

            if(foodType!=null && listFood!=null){

                if(listFood.any { it.name.equals("pâtes")}){
                    val pates = listFood.find {it.name.equals("pâtes")}

                    if(pates!=null){
                        pates.foodPic = "pates"
                        updateFood(pates, context = context)
                    }

                } else {
                    saveNewFood("pâtes",foodType.id, "pates", true, context = context)
                }
            }

            // update food pic of "autres cereales" and "autres sauces"
            if(listFood!=null){

                val autresCereales = listFood.find { it.name.equals("Autres céréales") }

                if(autresCereales!=null){
                    autresCereales.foodPic = "cereale"
                    updateFood(autresCereales, context = context)
                }

                val autresSauces = listFood.find { it.name.equals("Autres sauces") }
                if(autresSauces!=null){
                    autresSauces.foodPic = "sauce"
                    updateFood(autresSauces, context = context)
                }
            }

            // Update colors id's
            updateIndexColorsFoodTypes(context)
        }
    }
}

private fun updateIndexColorsFoodTypes(context: Context){

    val listFoodTypes = getAllFoodTypes(context = context)

    if(listFoodTypes!=null){

        for(foodtype in listFoodTypes){
            when(foodtype.name){

                "Boissons" -> {
                    foodtype.foodTypeColor = R.color.boissons_color
                    updateFoodType(foodtype, context = context)
                }
                "Viandes" -> {
                    foodtype.foodTypeColor = R.color.viandes_color
                    updateFoodType(foodtype, context = context)
                }
                "Poissons" -> {
                    foodtype.foodTypeColor = R.color.poissons_color
                    updateFoodType(foodtype, context = context)
                }
                "Légumes" -> {
                    foodtype.foodTypeColor = R.color.legumes_color
                    updateFoodType(foodtype, context = context)
                }
                "Laitages" -> {
                    foodtype.foodTypeColor = R.color.prodlait_color
                    updateFoodType(foodtype, context = context)
                }
                "Fruits" -> {
                    foodtype.foodTypeColor = R.color.fruits_color
                    updateFoodType(foodtype, context = context)
                }
                "Sucreries" -> {
                    foodtype.foodTypeColor = R.color.sucrerie_color
                    updateFoodType(foodtype, context = context)
                }
                "Céréales" -> {
                    foodtype.foodTypeColor = R.color.cereale_color
                    updateFoodType(foodtype, context = context)
                }
                "Sauces" -> {
                    foodtype.foodTypeColor = R.color.sauces_color
                    updateFoodType(foodtype, context = context)
                }
                "Autres" -> {
                    foodtype.foodTypeColor = R.color.autres_color
                    updateFoodType(foodtype, context = context)
                }
            }
        }
    }
}