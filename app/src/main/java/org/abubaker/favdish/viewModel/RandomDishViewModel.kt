package org.abubaker.favdish.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import org.abubaker.favdish.model.entities.RandomDish
import org.abubaker.favdish.model.network.RandomDishApiService

/**
 * This ViewModel will allow us to run the RandomDishAPI, observe it and use the details once we will receive them.
 */
class RandomDishViewModel : ViewModel() {

    /**
     *
     * Summary: Why we are using this approach?
     * =======================================
     * Basically we can ue "observable" which TRIGGER EVENTS/SENTIMENTS and observables are then
     * observed by:
     * 1. Observer or
     * 2. Subscriber
     *
     * In our case, the RandomDishViewModel is going to be the subscriber/observer of that observable.
     *
     * Controlling returned type using Singles (RxJava) :
     * ================================================
     * We are also using Singles, in order to make sure that we jet get "one kind" of response. So,
     * it is either going to be:
     * 1. Result - the "result" that we want to have (i.e. random recipe/dish)
     * 2. Error - it is going to be an ERROR
     *
     */

    // 01
    // An instance for RandomDishApiService class.
    private val randomRecipeApiService = RandomDishApiService()

    /**
     * 02 - Prevention against Memory Leak:
     * https://subscription.packtpub.com/book/application_development/9781787289901/2/ch02lvl1sec10/disposables
     *
     * A disposable container that can hold onto multiple other Disposables and
     * offers time complexity for following operations:
     *
     * 1. add(Disposable)
     * 2. remove(Disposable)
     * 3. Delete(Disposable)
     *
     */
    private val compositeDisposable = CompositeDisposable()

    /**
     * Creating three instances of API response observer.
     * Creates a MutableLiveData (changeable) with no value assigned to it.
     */
    val loadRandomDish = MutableLiveData<Boolean>()
    val randomDishResponse = MutableLiveData<RandomDish.Recipes>()
    val randomDishLoadingError = MutableLiveData<Boolean>()

    // This function will initialize all the instance and set the observers to it.
    fun getRandomDishFromAPI() {

        // Define the value of the load random dish.
        loadRandomDish.value = true

        /**
         * We are making sure that the request will not run forever, instead it will be disposed
         * when the task will be completed. So, we will add a Disposable to this container or disposes
         * it if the container has been disposed.
         */
        compositeDisposable.add(

            // Call the RandomDish method of RandomDishApiService class.
            randomRecipeApiService.getRandomDish()

                // Asynchronously subscribes SingleObserver to this Single on the specified Scheduler.
                /**
                 * Static factory methods for returning standard Scheduler instances.
                 *
                 * The initial and runtime values of the various scheduler types can be overridden via the
                 * {RxJavaPlugins.setInit(scheduler name)SchedulerHandler()} and
                 * {RxJavaPlugins.set(scheduler name)SchedulerHandler()} respectively.
                 */
                .subscribeOn(Schedulers.newThread())

                /**
                 * Signals the success item or the terminal signals of the current Single on the specified Scheduler,
                 * asynchronously.
                 *
                 * A Scheduler which executes actions on the Android main thread.
                 */
                .observeOn(AndroidSchedulers.mainThread())

                /**
                 * Subscribes a given SingleObserver (subclass) to this Single and returns the given
                 * SingleObserver as is.
                 */
                .subscribeWith(object : DisposableSingleObserver<RandomDish.Recipes>() {

                    // onSuccess()
                    override fun onSuccess(value: RandomDish.Recipes?) {
                        // Update the values with response in the success method.
                        loadRandomDish.value = false
                        randomDishResponse.value = value!!
                        randomDishLoadingError.value = false
                    }

                    // onError()
                    override fun onError(e: Throwable?) {
                        // Update the values in the response in the error method
                        loadRandomDish.value = false
                        randomDishLoadingError.value = true
                        e!!.printStackTrace()
                    }

                })
        )
    }

}