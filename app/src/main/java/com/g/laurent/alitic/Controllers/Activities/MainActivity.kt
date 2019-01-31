package com.g.laurent.alitic.Controllers.Activities

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.g.laurent.alitic.Controllers.ClassControllers.ChronoItem
import com.g.laurent.alitic.R
import com.g.laurent.alitic.Views.TimeLineAdapter
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var mAdapter: TimeLineAdapter
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = applicationContext
        displayTimeLine()
    }

    fun displayTimeLine(){

        mLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recycler_view.layoutManager = mLayoutManager
        mAdapter = TimeLineAdapter(getList(), context = context)
        recycler_view.adapter = mAdapter
    }




    fun getList():List<ChronoItem>{

/*
        AppDataBase.clearDatabase()

        val idFood1 = saveNewFood("Banane", "Fruit","https://www.auchandirect.fr/backend/media/products_images/0N_57390.jpg", false, context)
        val idFood2 = saveNewFood("Poulet", "Viande","https://boblechef.com/wp-content/uploads/2017/11/boblechef-poulet-roti-citron-romarin.jpg",false, context)
        val idFood3 = saveNewFood("Salade", "Crudités","https://www.auchandirect.fr/backend/media/products_images/0N_57315.jpg",false, context)
        val idFood4 = saveNewFood("Yaourt", "Laitage","https://www.europeconomic.com/wp-content/uploads/2015/07/yaourt.jpg",false, context)
        val idFood5 = saveNewFood("Gateau", "Patisserie","https://cac.img.pmdstatic.net/fit/http.3A.2F.2Fprd2-bone-image.2Es3-website-eu-west-1.2Eamazonaws.2Ecom.2Fcac.2F2018.2F09.2F25.2F78486fcb-8dc2-494a-b86a-53255f9b78ab.2Ejpeg/748x372/quality/80/crop-from/center/biscuits-maison.jpeg",false, context)
        val idFood6 = saveNewFood("Abricot", "Fruit","https://www.aroma-zone.com/cms//sites/default/files/Visuel-plante_Abricot_fotolia.jpg",false, context)

        val time1 = getDateAsLong(2,12,2018,1,0)
        val time2 = getDateAsLong(12,12,2018,11,0)
        val time3 = getDateAsLong(2,12,2018,10,0)
        val time4 = getDateAsLong(14,1,2019,11,30)

        val list1 : MutableList<MealItem> = mutableListOf()
        list1.add(MealItem(null,0,idFood1))
        list1.add(MealItem(null,0,idFood2))
        list1.add(MealItem(null,0,idFood3))

        val list2 : MutableList<MealItem> = mutableListOf()
        list2.add(MealItem(null,0,idFood4))
        list2.add(MealItem(null,0,idFood1))
        list2.add(MealItem(null,0,idFood3))

        val list3 : MutableList<MealItem> = mutableListOf()
        list3.add(MealItem(null,0,idFood5))
        list3.add(MealItem(null,0,idFood3))
        list3.add(MealItem(null,0,idFood6))

        val list4 : MutableList<MealItem> = mutableListOf()
        list4.add(MealItem(null,0,idFood1))
        list4.add(MealItem(null,0,idFood3))
        list4.add(MealItem(null,0,idFood6))
        list4.add(MealItem(null,0,idFood6))

        val idM1 = saveNewMeal(list1,time1, context)
        val idM2 = saveNewMeal(list2,time2, context)
        val idM3 = saveNewMeal(list3,time3, context)
        val idM4 = saveNewMeal(list4,time4, context)

        val id1 = saveNewEventType("Reflux", 0,"https://www.serengo.net/content/uploads/2017/12/reflux-gastriques-cancer-de-la-gorge.jpg",3*60*60*1000, false, context)

        val idE = saveNewEvent(idEventType = id1, dateCode = getDateAsLong(12,1,2019,12,0),
            mode = false, context = context)

        val list:MutableList<ChronoItem> = mutableListOf()
        list.add(ChronoItem("12:00", Event(idE,id1,0)))
        list.add(ChronoItem("13:00", Meal(idM1,0,list1)))
        list.add(ChronoItem("14:00", Meal(idM2,0,list2)))
        list.add(ChronoItem("15:00", Meal(idM3,0,list3)))
        list.add(ChronoItem("16:00", Meal(idM4,0,list4)))
*/
        val list:MutableList<ChronoItem> = mutableListOf()
        return list.toList()
    }
}