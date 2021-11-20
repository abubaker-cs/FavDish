package org.abubaker.favdish.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Create a new package name as entities in the model package. After creating the entities package
 * create data class with the entities (Dish Items) that we want to insert in the database.
 */

// Define the Table name
@Entity(tableName = "fav_dishes_table")
data class FavDish(
    @ColumnInfo val image: String,
    @ColumnInfo val imageSource: String, // Local or Online
    @ColumnInfo val title: String,
    @ColumnInfo val type: String,
    @ColumnInfo val category: String,
    @ColumnInfo val ingredients: String,

    // Specifies the name of the column in the table if you want it to be different from the name of the member variable.
    @ColumnInfo(name = "cookingTime") val cooking_time: String,
    @ColumnInfo(name = "instructions") val direction_to_cook: String,
    @ColumnInfo(name = "favoriteDish") var favorite_dish: Boolean = false,

    @PrimaryKey(autoGenerate = true) val id: Int = 0
)