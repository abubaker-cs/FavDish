package org.abubaker.favdish.model.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.abubaker.favdish.model.entities.FavDish

// TEMPLATE: https://developer.android.com/codelabs/android-room-with-a-view-kotlin#5

// WHY?
// ===
// DAO will avoid exposing the entire database, and rather an limited access to the DB will be provided
// in the Repository through our DAO

/**
 * Defined an interface named as FavDishDao.
 * that we will use to specify SQL queries and associate them with method calls.
 */
@Dao
interface FavDishDao {

    /**
     * A function to insert favorite dish details to the local database using Room.
     *
     * @param favDish - Here we will pass the entity class that we have created.
     *
     * * Important Note:
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
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavDishDetails(favDish: FavDish)

    // Create a function to get the list of dishes from database using @Query.
    /**
     * What we need to do to detect changes inside the database?
     * =========================================================
     * When data changes, you usually want to take some action, such as displaying the updated data in the UI.
     * This means you have to observe the data so when it changes, you can react.
     *
     * Flow Observer (Coroutines)
     * ==========================
     * To observe data changes we will use Flow from kotlinx-coroutines.
     * Use a return value of type Flow in your method description,
     * and Room generates all necessary code to update the Flow when the database is updated.
     *
     * A Flow is an async sequence of values
     * =====================================
     * Flow produces values one at a time (instead of all at once) that can generate values from async operations
     * like network requests, database calls, or other async code.
     * It supports coroutines throughout its API, so you can transform a flow using coroutines as well!
     */
    @Query("SELECT * FROM FAV_DISHES_TABLE ORDER BY ID")
    fun getAllDishesList(): Flow<List<FavDish>>

    /**
     * A function to update favorite dish details to the local database using Room.
     *
     * @param favDish - Here we will pass the entity class that we have created with updated details along with "id".
     */
    @Update
    suspend fun updateFavDishDetails(favDish: FavDish)


    /**
     * Get Favorite List - It will get the list of favorite dishes from the database.
     *
     * Important Note:
     * ===============
     * SQLite does not have a boolean data type.
     * Room maps it to an INTEGER column, mapping true to 1 and false to 0.
     */
    @Query("SELECT * FROM FAV_DISHES_TABLE WHERE favorite_dish = 1")
    fun getFavoriteDishesList(): Flow<List<FavDish>>

    // Create a suspend function to delete the dish item from database.
    /**
     * A function to delete favorite dish details from the local database using Room.
     *
     * @param favDish - Here we will pass the entity class with details that we want to delete.
     */
    @Delete
    suspend fun deleteFavDishDetails(favDish: FavDish)

    /**
     * A function to get the list of dishes based on the dish type from the database.
     *
     * @param filterType - DishType
     */
    @Query("SELECT * FROM FAV_DISHES_TABLE WHERE type = :filterType")
    fun getFilteredDishesList(filterType: String): Flow<List<FavDish>>

}