package com.g.laurent.alitic

import com.g.laurent.alitic.Controllers.getListInDescendingOrder
import com.g.laurent.alitic.Models.Food
import org.junit.Assert
import org.junit.Test

import org.junit.Assert.*
import java.sql.Date

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun time_today() {

        println(getDateAsLong(2,12,2018,1,0))
        println(getDateAsLong(2,12,2018,10,0))
        println(getDateAsLong(12,12,2018,11,0))
        println(getDateAsLong(14,1,2019,8,0))


        assertEquals(4, 2 + 2)
    }

    @Test
    fun test_descending_order() {

        var list:HashMap<Food, Int> = hashMapOf()
        list[Food(0, "Banane", "Fruit")] = 2
        list[Food(0, "Abricot", "Fruit")] = 1
        list[Food(0, "Pomme", "Fruit")] = 4
        list[Food(0, "Avocat", "Fruit")] = 0
        list[Food(0, "Cerise", "Fruit")] = 0
        list[Food(0, "Poire", "Fruit")] = 1

        val result = getListInDescendingOrder(list)

        Assert.assertEquals("Pomme", result.get(0).food)
        Assert.assertEquals("Banane", result.get(1).food)
        Assert.assertEquals("Poire", result.get(2).food)
        Assert.assertEquals("Abricot", result.get(3).food)
        Assert.assertEquals("Avocat", result.get(4).food)
        Assert.assertEquals("Cerise", result.get(5).food)
    }
}
