package org.abubaker.favdish.viewModel

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.abubaker.favdish.model.database.FavDishRepository
import org.abubaker.favdish.model.entities.FavDish

/**
 * Repository <<<< --- >>> ViewModel <<< --- >>> UI (Activity/Fragment)
 *
 * The ViewModel's role is to provide data to the UI and survive configuration changes.
 * A ViewModel acts as a communication center between the Repository and the UI.
 * You can also use a ViewModel to share data between fragments.
 * The ViewModel is part of the lifecycle library.
 *
 * @param repository - The repository class is
 */
class FavDishViewModel(private val repository: FavDishRepository) : ViewModel() {

    /**
     * Coroutine - Launching a new coroutine to insert the data in a non-blocking way.
     */
    fun insert(dish: FavDish) = viewModelScope.launch {

        // Call the repository function and pass the details.
        // ---
        // Our ViewModel (FavDishViewModel.kt) will execute the function insertFavDishData()
        // inside the repository (FavDishRepository.kt)
        repository.insertFavDishData(dish)

    }

    //  Get all the dishes list from the database in the ViewModel to pass it to the UI.
    /** Using LiveData and caching what allDishes returns has several benefits:
     * We can put an observer on the data (instead of polling for changes) and only
     * update the UI when the data actually changes.
     * Repository is completely separated from the UI through the ViewModel.
     */
    val allDishesList: LiveData<List<FavDish>> = repository.allDishesList.asLiveData()
}

/**
 * To create the ViewModel we implement a ViewModelProvider.Factory that gets as a parameter the dependencies
 * needed to create FavDishViewModel: the FavDishRepository.
 *
 * By using viewModels and ViewModelProvider.Factory then the framework will take care of the lifecycle of the ViewModel.
 *
 * It will survive configuration changes and even if the Activity is recreated,
 * you'll always get the right instance of the FavDishViewModel class.
 */
class FavDishViewModelFactory(private val repository: FavDishRepository) :
    ViewModelProvider.Factory {

    // Required Member: create()
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        //
        if (modelClass.isAssignableFrom(FavDishViewModel::class.java)) {

            // T = ViewModel
            // @Suppress("UNCHECKED_CAST") = we are asking to receive warnings
            @Suppress("UNCHECKED_CAST")
            return FavDishViewModel(repository) as T

        }

        // Catch and Throw Exception Error
        throw IllegalArgumentException("Unknown ViewModel class")

    }

}