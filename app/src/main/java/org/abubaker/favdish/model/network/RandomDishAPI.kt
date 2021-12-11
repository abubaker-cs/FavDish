package org.abubaker.favdish.model.network

import io.reactivex.rxjava3.core.Single
import org.abubaker.favdish.model.entities.RandomDish
import org.abubaker.favdish.utils.Constants
import retrofit2.http.GET
import retrofit2.http.Query

interface RandomDishAPI {

    /**
     * To Make a GET request.
     *
     * Pass the endpoint of the URL that is defined in the Constants.
     *
     *
     */
    @GET(Constants.API_ENDPOINT)
    fun getDishes(

        // Query parameter appended to the URL. This is the best practice instead of appending it as we have done in the browser.

        // API Secret Key
        @Query(Constants.API_KEY) apiKey: String,

        // Limit License?
        @Query(Constants.LIMIT_LICENSE) limitLicense: Boolean,

        // Tags
        @Query(Constants.TAGS) tags: String,

        // Number
        @Query(Constants.NUMBER) number: Int

    ): Single<RandomDish.Recipes> // The Single class implements the Reactive Pattern for a single value response. Click on the class using the Ctrl + Left Mouse Click to know more.

    // For more details have a look at http://reactivex.io/documentation/single.html or http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html

}