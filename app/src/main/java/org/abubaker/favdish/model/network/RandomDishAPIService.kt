package org.abubaker.favdish.model.network

import io.reactivex.rxjava3.core.Single
import org.abubaker.favdish.model.entities.RandomDish
import org.abubaker.favdish.utils.Constants
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RandomDishApiService {

    /**
     * Retrofit adapts a Java interface to HTTP calls by using annotations on the declared methods to
     * define how requests are made. Create instances using {@linkplain Builder the builder} and pass
     * your interface to {create} to generate an implementation.
     */
    private val api = Retrofit.Builder()

        // Set the API base URL.
        .baseUrl(Constants.BASE_URL)

        /**
         * 01 Converter Factory - To convert JSON
         * ********************
         * A Converter.Factory converter which uses Gson for JSON.
         * We are adding a converter factory for serialization and deserialization of objects.
         *
         * GSON supports multiple data types:
         * Because Gson is so flexible in the types it supports, this converter assumes that it can handle
         * all types.
         */
        .addConverterFactory(GsonConverterFactory.create())

        /**
         * 02 Adapter Factory - For Observables
         * ******************
         * A CallAdapter.Factory call adapter which uses RxJava 3 for creating observables.
         *
         * Adding this class to Retrofit allows you to return an Observable, Flowable, Single, Completable
         * or Maybe from service methods.
         */
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())

        // Create the Retrofit instance using the configured values.
        .build()

        /**
         *  Create an implementation of the API endpoints defined by the service interface,
         *  in our case it is in RandomDishAPI.kt
         */
        .create(RandomDishAPI::class.java)

    // This function will initial the API call and returns the API response.
    fun getRandomDish(): Single<RandomDish.Recipes> {

        // Pass the values to the method as required params
        return api.getRandomDish(
            Constants.API_KEY_VALUE,
            Constants.LIMIT_LICENSE_VALUE,
            Constants.TAGS_VALUE,
            Constants.NUMBER_VALUE
        )

    }

}