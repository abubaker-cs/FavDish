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
     * API_ENDPOINT = "recipes/random"
     *
     * DOC URL: https://spoonacular.com/food-api/docs#Get-Random-Recipes
     *
     * EXAMPLE: GET https://api.spoonacular.com/recipes/random?number=1&tags=vegetarian,dessert
     */
    @GET(Constants.API_ENDPOINT)
    fun getRandomDish(

        // Query parameter appended to the URL. This is the best practice instead of appending it as we have done in the browser.

        // API Secret Key
        @Query(Constants.API_KEY) apiKey: String,

        // Limit License: Whether the recipes should have an open license that allows display with proper attribution.
        @Query(Constants.LIMIT_LICENSE) limitLicense: Boolean,

        // Tags: The tags (can be diets, meal types, cuisines, or intolerances) that the recipe must have.
        @Query(Constants.TAGS) tags: String,

        // Number: The number of random recipes to be returned (between 1 and 100).
        @Query(Constants.NUMBER) number: Int

    ): Single<RandomDish.Recipes> // The Single class implements the Reactive Pattern for a single value response. Click on the class using the Ctrl + Left Mouse Click to know more.

    // Note: Single class is a RxJava class, which is a Reactive Extension for JVM
    // For more details have a look at http://reactivex.io/documentation/single.html or http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html

}