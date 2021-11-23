package org.abubaker.favdish.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Create a new package name as entities in the model package. After creating the entities package
 * create data class with the entities (Dish Items) that we want to insert in the database.
 */

// Define the Table name using @Entity() and set the class to be a "data class"
@Entity(tableName = "fav_dishes_table")
data class FavDish(

    // Image
    @ColumnInfo val image: String,

    // Image Source: Local or Online
    @ColumnInfo val imageSource: String,

    // Title
    @ColumnInfo val title: String,

    // Type
    @ColumnInfo val type: String,

    // Category
    @ColumnInfo val category: String,

    // Ingredients
    @ColumnInfo val ingredients: String,

    /**
     * Specifies the name of the column in the table
     * if you want it to be different from the name of the member variable.
     */

    // Cooking Time
    @ColumnInfo(name = "cookingTime") val cooking_time: String,

    // Instructions
    @ColumnInfo(name = "instructions") val direction_to_cook: String,

    // Favorite Dish
    @ColumnInfo(name = "favoriteDish") var favorite_dish: Boolean = false,

    // PrimaryKey
    @PrimaryKey(autoGenerate = true) val id: Int = 0

)