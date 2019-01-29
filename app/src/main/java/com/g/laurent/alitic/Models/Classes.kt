package com.g.laurent.alitic.Models

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.*
import android.content.Context
import android.arch.persistence.room.OnConflictStrategy
import java.util.concurrent.Executors


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
        onDelete = ForeignKey.NO_ACTION)])
data class MealItem(
    @PrimaryKey(autoGenerate = true) var id: Long?,
    @ColumnInfo(name = "idMeal") var idMeal: Long?,
    @ColumnInfo(name = "idFood") var idFood:Long?)



@Entity(tableName = "food", indices = [Index("idFoodType")], foreignKeys = [ForeignKey(entity = FoodType::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("idFoodType"),
    onDelete = ForeignKey.NO_ACTION)])
data class Food(@PrimaryKey(autoGenerate = true) var id: Long?,
                @ColumnInfo(name = "name") var name:String,
                @ColumnInfo(name = "idFoodType") var idFoodType:Long?,
                @ColumnInfo(name = "counter") var counter:Int,
                @ColumnInfo(name = "foodPic") var foodPic: String?)



@Entity(tableName = "foodtype")
data class FoodType(@PrimaryKey(autoGenerate = true) var id: Long?,
                @ColumnInfo(name = "name") var name:String,
                @ColumnInfo(name = "foodTypePic") var foodTypePic: String?)


@Entity(tableName = "keyword", indices = [Index("idfood")], foreignKeys = [ForeignKey(entity = Food::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("idfood"),
    onDelete = ForeignKey.NO_ACTION)])
data class Keyword(@PrimaryKey(autoGenerate = true) var id: Long?,
                @ColumnInfo(name = "name") var name:String,
                @ColumnInfo(name = "idfood") var idFood:Long?)

// ------------------------------ EVENTS CLASSES -------------------------------------------

@Entity(tableName = "eventType")
data class EventType(@PrimaryKey(autoGenerate = true) var id: Long?,
                     @ColumnInfo(name = "name") var name:String,
                     @ColumnInfo(name = "eventPic") var eventPic:String?,
                     @ColumnInfo(name = "minTime") var minTime:Long,
                     @ColumnInfo(name = "maxTime") var maxTime:Long)


@Entity(tableName = "event", indices = [Index("idEventType")],
    foreignKeys = [ForeignKey(entity = EventType::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("idEventType"),
    onDelete = ForeignKey.NO_ACTION)])
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

    @Query("DELETE from meal WHERE id=:idMeal")
    fun deleteMeal(idMeal:Long?)
}

@Dao
interface MealItemDao {

    @Query("SELECT * from mealItem")
    fun getAll(): List<MealItem>?

    @Query("SELECT * from mealItem WHERE idMeal=:idMeal")
    fun getItemsFromMeal(idMeal:Long?): List<MealItem>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(mealItem: MealItem)

    @Query("DELETE from mealItem WHERE idMeal=:idMeal")
    fun deleteItemsFromMeal(idMeal:Long?)

    @Query("DELETE from mealItem")
    fun deleteAll()
}

@Dao
interface FoodDao {

    @Query("SELECT * from food")
    fun getAll(): List<Food>?

    @Query("SELECT * from food WHERE id=:idFood")
    fun getFood(idFood: Long?): Food?

    @Query("SELECT * from food WHERE LOWER(name) LIKE :search")
    fun getListFoodSearch(search: String?): List<Food>

    @Query("SELECT * from food WHERE idFoodType=:idFoodtype")
    fun getFoodByType(idFoodtype: Long?): List<Food>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(food: Food):Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(food: Food)

    @Query("DELETE from food WHERE id=:idFood")
    fun deleteFood(idFood:Long?)

    @Query("DELETE from food")
    fun deleteAll()
}

@Dao
interface FoodTypeDao {

    @Query("SELECT * from foodtype")
    fun getAll(): List<FoodType>?

    @Query("SELECT * from foodtype WHERE id=:idFoodType")
    fun getFoodType(idFoodType: Long?): FoodType?

    @Query("SELECT * from foodtype WHERE name=:nameFoodType")
    fun getFoodType(nameFoodType: String?): FoodType?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(foodtype: FoodType):Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(foodtype: FoodType)

    @Query("DELETE from foodtype WHERE id=:idFoodType")
    fun deleteFoodType(idFoodType:Long?)

    @Query("DELETE from foodtype")
    fun deleteAll()
}

@Dao
interface KeywordDao {

    @Query("SELECT * from keyword")
    fun getAll(): List<Keyword>?

    @Query("SELECT * from keyword WHERE LOWER(name) LIKE :search")
    fun getKeywords(search: String): List<Keyword>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(key: Keyword):Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<Keyword>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(key: Keyword)

    @Query("DELETE from keyword WHERE id=:idKey")
    fun deleteKeyword(idKey:Long?)

    @Query("DELETE from keyword")
    fun deleteAll()
}

@Dao
interface EventDao {

    @Query("SELECT * from event")
    fun getAll(): List<Event>?

    @Query("SELECT * from event WHERE dateCode>=:minDate AND dateCode <=:maxDate")
    fun getEventsDate(minDate: Long, maxDate:Long): List<Event>?

    @Query("SELECT * from event WHERE idEventType =:idType")
    fun getEventsByType(idType: Long?): List<Event>?

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

@Database(entities = [Meal::class, MealItem::class, Food::class, FoodType::class, Keyword::class, Event::class, EventType::class], version = 6, exportSchema = false)
abstract class AppDataBase : RoomDatabase() {

    abstract fun mealDao(): MealDao
    abstract fun mealItemDao(): MealItemDao
    abstract fun foodDao(): FoodDao
    abstract fun foodTypeDao(): FoodTypeDao
    abstract fun keywordDao(): KeywordDao
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
                            .fallbackToDestructiveMigration()
                            .build()
                    }
                } else {
                    synchronized(AppDataBase::class) {
                        INSTANCE = Room.databaseBuilder(context, AppDataBase::class.java, "appDataBase.db")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .addCallback(object : Callback() {
                                override fun onCreate(db: SupportSQLiteDatabase) {
                                    super.onCreate(db)
                                    // insert the data on the IO Thread
                                    ioThread {
                                        insertData(INSTANCE?.foodTypeDao(), INSTANCE?.foodDao(), INSTANCE?.keywordDao())
                                    }
                                }
                            })
                            .build()
                    }
                }
            }
            return INSTANCE
        }

        fun clearDatabase() {
            INSTANCE?.clearAllTables()
        }

        fun destroyInstance() {
            INSTANCE = null
        }

        private val IO_EXECUTOR = Executors.newSingleThreadExecutor()

        fun ioThread(f : () -> Unit) {
            IO_EXECUTOR.execute(f)
        }
    }
}