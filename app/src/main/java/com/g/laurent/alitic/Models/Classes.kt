package com.g.laurent.alitic.Models

import android.arch.persistence.room.*
import android.content.Context

// -------------------------------- MEAL CLASSES ----------------------------------------------

@Entity(tableName = "meal", indices = [Index("id")])
data class Meal(@PrimaryKey(autoGenerate = true) var id: Long?,
                @ColumnInfo(name = "dateCode") var dateCode:Long,
                @Ignore var listMealItems:List<MealItem>?){
    constructor() : this(0, 0, null)
}

@Entity(tableName = "mealItem", indices = [Index("idMeal"), Index("idFood")],
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
                @ColumnInfo(name = "name") var name:String,
                @ColumnInfo(name = "foodType") var foodType:String)

// ------------------------------ EVENTS CLASSES -------------------------------------------

@Entity(tableName = "eventType")
data class EventType(@PrimaryKey(autoGenerate = true) var id: Long?,
                     @ColumnInfo(name = "name") var name:String,
                     @ColumnInfo(name = "minTime") var minTime:Long,
                     @ColumnInfo(name = "maxTime") var maxTime:Long)

@Entity(tableName = "event", indices = [Index("idEventType")],
    foreignKeys = [ForeignKey(entity = EventType::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("idEventType"),
    onDelete = ForeignKey.CASCADE)]
)
data class Event(
    @PrimaryKey(autoGenerate = true) var id: Long?,
    @ColumnInfo(name = "idEventType") var idEventType: Long?,
    @ColumnInfo(name = "dateCode") var dateCode:Long)


// -------------------------------------- DAO -----------------------------------------------

@Dao
interface MealDao {

    @Query("SELECT * from meal")
    fun getAll(): List<Meal>?

    @Query("SELECT * from meal WHERE id =:idMeal")
    fun getMeal(idMeal:Long?): Meal

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(meal: Meal):Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(meal: Meal)

    @Query("DELETE from meal")
    fun deleteAll()
}

@Dao
interface MealItemDao {

    @Query("SELECT * from mealItem WHERE idMeal=:idMeal")
    fun getItemsFromMeal(idMeal:Long?): List<MealItem>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(food: Food):Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(food: Food)

    @Query("DELETE from food")
    fun deleteAll()
}

@Dao
interface EventDao {

    @Query("SELECT * from event")
    fun getAll(): List<Event>?

    @Query("SELECT * from event WHERE dateCode>=:minDate AND dateCode <=:maxDate")
    fun getEventsDate(minDate: Long, maxDate:Long): List<Event>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(event: Event):Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(event: Event)

    @Query("DELETE from Event")
    fun deleteAll()

    @Query("DELETE from Event WHERE id=:idEvent")
    fun deleteEvent(idEvent:Long?)
}

@Dao
interface EventTypeDao {

    @Query("SELECT * from eventType")
    fun getAll(): List<EventType>?

    @Query("SELECT * from eventType WHERE id=:idEvent")
    fun getEventType(idEvent:Long?): EventType?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(eventType: EventType):Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(eventType: EventType)

    @Query("DELETE from eventType WHERE id=:idEventType")
    fun deleteEventType(idEventType:Long?)

    @Query("DELETE from eventType")
    fun deleteAll()
}

// ----------------------------------- DATABASE --------------------------------------------

@Database(entities = [Meal::class, MealItem::class, Food::class, Event::class, EventType::class], version = 2, exportSchema = false)
abstract class AppDataBase : RoomDatabase() {

    abstract fun mealDao(): MealDao
    abstract fun mealItemDao(): MealItemDao
    abstract fun foodDao(): FoodDao
    abstract fun eventDao(): EventDao
    abstract fun eventTypeDao(): EventTypeDao

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
                        INSTANCE = Room.databaseBuilder(context, AppDataBase::class.java, "appDataBase.db")
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