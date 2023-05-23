package com.example.easyfood.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.easyfood.pojo.Meal

// update version whenever change is made in database
// exportSchema false doesn't allow database to be exported as JSON file. False by default
@Database(entities = [Meal::class], version = 2, exportSchema = false)
@TypeConverters(MealTypeConvertor::class)  // To convert non-primitive data to primitive type
abstract class MealDatabase : RoomDatabase() {

    // Initialising DAO
    abstract fun mealDao(): MealDao

    companion object{
        @Volatile  // -> Any change is visible to any other thread
        var INSTANCE: MealDatabase? = null

        @Synchronized  // -> ensures that only 1 database instance is created
        fun getInstance(context: Context): MealDatabase{
            if(INSTANCE == null){
                INSTANCE = Room.databaseBuilder(
                    context,
                    MealDatabase::class.java,
                    "meal.db"
                ).fallbackToDestructiveMigration()  // -> In case database version is changed new database is created with same data
                    .build()
            }
            return INSTANCE as MealDatabase
        }
    }
}