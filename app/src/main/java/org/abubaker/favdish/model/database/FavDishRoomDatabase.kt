package org.abubaker.favdish.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.abubaker.favdish.model.entities.FavDish

// Template: https://developer.android.com/codelabs/android-room-with-a-view-kotlin#7

/**
 * This is the backend. The database. This used to be done by the OpenHelper.
 * The fact that this has very few comments emphasizes its coolness.
 */
@Database(entities = [FavDish::class], version = 1)
abstract class FavDishRoomDatabase : RoomDatabase() {

    // Inside the "companion object" we basically need to use the Singleton of our Instance.
    companion object {

        // Singleton: Prevents multiple instances of database opening at the same time.
        // @Volatile: The writes to this field are immediately made visible to other threads.
        @Volatile
        private var INSTANCE: FavDishRoomDatabase? = null

        //
        fun getDatabase(context: Context): FavDishRoomDatabase {

            // If INSTANCE is NULL then create the DATABASE, otherwise return the INSTANCE
            return INSTANCE ?: synchronized(this) {

                //
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FavDishRoomDatabase::class.java,
                    "fav_dish_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                //
                INSTANCE = instance

                // return instance
                instance

            }

        }

    }
}