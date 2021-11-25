package org.abubaker.favdish.model.database

import androidx.room.Dao
import androidx.room.Insert
import org.abubaker.favdish.model.entities.FavDish

// TEMPLATE: https://developer.android.com/codelabs/android-room-with-a-view-kotlin#5

// WHY?
// ===
// DAO will avoid exposing the entire database, and rather an limited access to the DB will be provided
// in the Repository through our DAO

/**
 * Defined an interface named as FavDishDao
 * that we will use to specify SQL queries and associate them with method calls.
 */
@Dao
interface FavDishDao {

    /**
     * Important Note:
     * ===============
     * All queries must be executed on a separate thread.
     * They cannot be executed from Main Thread or it will cause an crash.
     *
     * ---------------
     *
     * suspend:
     * Room has Kotlin coroutines support. This allows your queries to be annotated with the suspend
     * modifier and then called from a coroutine or from another suspension function.
     */

    /**
     * A function to insert favorite dish details to the local database using Room.
     *
     * @param favDish - Here we will pass the entity class that we have created.
     */
    @Insert
    suspend fun insertFavDishDetails(favDish: FavDish)
}