package org.abubaker.favdish.utils

/**
 * This is used to define the constant values that we can use throughout the application.
 */
object Constants {

    const val DISH_TYPE: String = "DishType"
    const val DISH_CATEGORY: String = "DishCategory"
    const val DISH_COOKING_TIME: String = "DishCookingTime"

    // We will use following for the Image Source while inserting the Dish details.
    const val DISH_IMAGE_SOURCE_LOCAL: String = "Local"
    const val DISH_IMAGE_SOURCE_ONLINE: String = "Online"

    /**
     * Dish Type
     */
    fun dishTypes(): ArrayList<String> {

        // Define List
        val list = ArrayList<String>()

        // Add Items
        list.add("breakfast")
        list.add("lunch")
        list.add("snacks")
        list.add("dinner")
        list.add("salad")
        list.add("side dish")
        list.add("dessert")
        list.add("other")

        // Return List
        return list

    }

    /**
     *  Dish Category
     */
    fun dishCategories(): ArrayList<String> {

        // Define List
        val list = ArrayList<String>()

        // Add Items
        list.add("Pizza")
        list.add("BBQ")
        list.add("Bakery")
        list.add("Burger")
        list.add("Cafe")
        list.add("Chicken")
        list.add("Dessert")
        list.add("Drinks")
        list.add("Hot Dogs")
        list.add("Juices")
        list.add("Sandwich")
        list.add("Tea & Coffee")
        list.add("Wraps")
        list.add("Other")

        // Return List
        return list

    }


    /**
     *  Dish Cooking Time
     */
    fun dishCookTime(): ArrayList<String> {

        // Define the List
        val list = ArrayList<String>()

        // Add items
        list.add("10")
        list.add("15")
        list.add("20")
        list.add("30")
        list.add("45")
        list.add("50")
        list.add("60")
        list.add("90")
        list.add("120")
        list.add("150")
        list.add("180")

        // Return List
        return list
    }

}