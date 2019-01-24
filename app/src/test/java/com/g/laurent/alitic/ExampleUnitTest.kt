package com.g.laurent.alitic

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

        println(getDateAsLong(23,1,2019,21,0))
        assertEquals(4, 2 + 2)
    }
}
