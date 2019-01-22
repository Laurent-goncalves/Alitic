package com.g.laurent.alitic

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.content.Context


// -------------------------------- MEAL CLASSES ----------------------------------------------

@Entity(tableName = "meal")
data class Meal(@PrimaryKey(autoGenerate = true) var id: Long?,
                @ColumnInfo(name = "dateCode") var dateCode:Long)

@Entity(tableName = "mealItem",
    foreignKeys = [ForeignKey(entity = Meal::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("idMeal"),
    onDelete = ForeignKey.CASCADE), ForeignKey(entity = Food::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("idFood"),
    onDelete = ForeignKey.CASCADE)])
data class MealItem(
                @PrimaryKey(autoGenerate = true) var id: Long?,
                @ColumnInfo(name = "idMeal") var idMeal: Long?,
                @ColumnInfo(name = "idFood") var idFood:Long?)

@Entity(tableName = "food")
data class Food(@PrimaryKey(autoGenerate = true) var id: Long?,
           @ColumnInfo(name = "name") var name:String)

// ------------------------------ EVENTS CLASSES -------------------------------------------

@Entity(tableName = "eventName")
data class EventName(@PrimaryKey(autoGenerate = true) var id: Long?,
              @ColumnInfo(name = "name") var name:String)

@Entity(tableName = "event", foreignKeys = arrayOf(
    ForeignKey(entity = Event::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("idEventName"),
        onDelete = ForeignKey.CASCADE)))
data class Event(
            @PrimaryKey(autoGenerate = true) var id: Long?,
            @ColumnInfo(name = "idEventName") var idEventName: Long?,
            @ColumnInfo(name = "dateCode") var dateCode:Long)

// -------------------------------------- DAO -----------------------------------------------

@Dao
interface MealDao {

    @Query("SELECT * from meal")
    fun getAll(): List<Meal>?

    @Insert(onConflict = REPLACE)
    fun insert(meal: Meal)

    @Update(onConflict = REPLACE)
    fun update(meal: Meal)

    @Query("DELETE from meal")
    fun deleteAll()
}

@Dao
interface MealItemDao {

    @Query("SELECT * from mealItem WHERE idMeal=:idMeal")
    fun getItemsFromMeal(idMeal:Long?): List<MealItem>?

    @Insert(onConflict = REPLACE)
    fun insert(mealItem: MealItem)

    @Query("DELETE from mealItem WHERE idMeal=:idMeal")
    fun deleteItemsFromMeal(idMeal:Long?)
}

@Dao
interface FoodDao {

    @Query("SELECT * from food")
    fun getAll(): List<Food>?

    @Query("SELECT * from food WHERE id=:idFood")
    fun getFood(idFood: Long?): Food?

    @Insert(onConflict = REPLACE)
    fun insert(food: Food)

    @Update(onConflict = REPLACE)
    fun update(food: Food)

    @Query("DELETE from food")
    fun deleteAll()
}

@Dao
interface EventDao {

    @Query("SELECT * from event")
    fun getAll(): List<Event>?

    @Query("SELECT * from event WHERE dateCode>:minDate AND dateCode <:maxDate")
    fun getEventsDate(minDate: Long, maxDate:Long): List<Event>?

    @Insert(onConflict = REPLACE)
    fun insert(event: Event)

    @Update(onConflict = REPLACE)
    fun update(event: Event)

    @Query("DELETE from Event")
    fun deleteAll()

    @Query("DELETE from Event WHERE id=:idEvent")
    fun deleteEvent(idEvent:Long?)
}

@Dao
interface EventNameDao {

    @Query("SELECT * from eventName")
    fun getAll(): List<EventName>?

    @Query("SELECT * from eventName WHERE id=:idEvent")
    fun getEventName(idEvent:Long?): EventName?

    @Insert(onConflict = REPLACE)
    fun insert(eventName: EventName)

    @Update(onConflict = REPLACE)
    fun update(eventName: EventName)

    @Query("DELETE from eventName WHERE id=:idEventName")
    fun deleteEventName(idEventName:Long?)
}

// ----------------------------------- DATABASE --------------------------------------------

@Database(entities = arrayOf(Meal::class, MealItem::class, Food::class, Event::class, EventName::class), version = 1)
abstract class AppDataBase : RoomDatabase() {

    abstract fun mealDao(): MealDao
    abstract fun mealItemDao(): MealItemDao
    abstract fun foodDao(): FoodDao
    abstract fun eventDao(): EventDao
    abstract fun eventNameDao(): EventNameDao

    companion object {

        private var INSTANCE: AppDataBase? = null

        var TEST_MODE = false

        fun getInstance(context: Context): AppDataBase? {
            if (INSTANCE == null) {
                if (TEST_MODE) {
                    synchronized(AppDataBase::class) {
                        INSTANCE = Room.inMemoryDatabaseBuilder(context, AppDataBase::class.java)
                            .allowMainThreadQueries()
                            .build()
                    }
                } else {
                    synchronized(AppDataBase::class) {
                        INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDataBase::class.java, "appDataBase.db")
                            .build()
                    }
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}