package org.abubaker.favdish.model.database

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import org.abubaker.favdish.model.entities.FavDish

// TEMPLATE: https://developer.android.com/codelabs/android-room-with-a-view-kotlin#8

// WHY?
// ===
// Repository will get reference and limited access to the Database through DAO

/**
 * A Repository manages queries and allows you to use multiple backend.
 *
 * The DAO is passed into the repository constructor as opposed to the whole database.
 * This is because it only needs access to the DAO, since the DAO contains all the read/write methods for the database.
 * There's no need to expose the entire database to the repository.
 *
 * @param favDishDao - Pass the FavDishDao as the parameter.
 */
class FavDishRepository(private val favDishDao: FavDishDao) {

    /**
     * By default Room runs suspend queries off the main thread, therefore, we don't need to
     * implement anything else to ensure we're not doing long running database work
     * off the main thread.
     */

    // 1. Suspend function to insert the data and
    // 2. Annotate it with @WorkerThread.
    @WorkerThread
    suspend fun insertFavDishData(favDish: FavDish) {

        // Execute the insertFavDishDetails function from the FavDishDao.kt file
        favDishDao.insertFavDishDetails(favDish)

    }

    /**
     * We are asking to execute the .getAllDishesList() function from our FavDishDao.kt file
     *
     * Create a variable for the dishes list to access it from ViewModel.
     * Room executes all queries on a separate thread.
     * Observed Flow will notify the observer when the data has changed.
     */
    val allDishesList: Flow<List<FavDish>> = favDishDao.getAllDishesList()

    /**
     * This suspend function will be used to update the details that can be called from the ViewModel class.
     */
    @WorkerThread
    suspend fun updateFavDishData(favDish: FavDish) {

        // We are executing the updateFavDishDetails function defined in our DAO (as it has direct access to the DB)
        favDishDao.updateFavDishDetails(favDish)

    }

    /**
     * Get the list of favorite dishes from the DAO and pass it to the ViewModel.
     */
    val favoriteDishes: Flow<List<FavDish>> = favDishDao.getFavoriteDishesList()

    // Create a suspend function using WorkerThread to delete the dish details.
    @WorkerThread
    suspend fun deleteFavDishData(favDish: FavDish) {
        favDishDao.deleteFavDishDetails(favDish)
    }

    // Create a function get the filtered list of Dishes based on the selection.
    /**
     * A function to get the filtered list of Dishes.
     *
     * @param value - dish type selection
     */
    fun filteredListDishes(value: String): Flow<List<FavDish>> =
        favDishDao.getFilteredDishesList(value)
}