package com.g.laurent.alitic.Controllers.Activities

import android.graphics.Matrix
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.g.laurent.alitic.Controllers.ClassControllers.saveNewEvent
import com.g.laurent.alitic.Controllers.ClassControllers.saveNewEventType
import com.g.laurent.alitic.Controllers.Fragments.*
import com.g.laurent.alitic.R
import com.g.laurent.alitic.getDateAsLong
import kotlinx.android.synthetic.main.activity_stat.*


class ChronoActivity : AppCompatActivity(), OnTimeLineDisplay {

    private var matrix = Matrix()
    private val chronoFragment = ChronoFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chrono)

        val imageView = this.findViewById<ImageView>(R.id.image_background)

        /*matrix.reset()
        matrix.setTranslate(Loc.BOTTOM_LEFT.position.px, Loc.BOTTOM_LEFT.position.py)
        imageView.imageMatrix = matrix*/
        moveCamera(imageView,Loc.CENTER.position, Loc.BOTTOM_LEFT.position,matrix, this, "eee")

        //clearDatabase(applicationContext)




    }

    fun showChronoFragment(){
        val fragmentManager = supportFragmentManager.beginTransaction()
        fragmentManager.replace(R.id.calendar_fragment, chronoFragment)
        fragmentManager.commit()
    }

    override fun displayTimeLineFragment(day: Int, month: Int, year: Int) {

        val timeLineFragment = TimeLineFragment()

        val bundle=Bundle()
        bundle.putInt(DAY_ARGS, day)
        bundle.putInt(MONTH_ARGS, month)
        bundle.putInt(YEAR_ARGS, year)
        timeLineFragment.arguments = bundle

        val fragmentManager = supportFragmentManager.beginTransaction()
        fragmentManager.replace(R.id.calendar_fragment, timeLineFragment)
        fragmentManager.commit()
    }

}

interface OnTimeLineDisplay {
    fun displayTimeLineFragment(day:Int, month:Int, year:Int)
}