package org.abubaker.favdish.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.abubaker.favdish.databinding.ItemDishLayoutBinding
import org.abubaker.favdish.model.entities.FavDish
import org.abubaker.favdish.view.fragments.AllDishesFragment

class FavDishAdapter(private val fragment: Fragment) :
    RecyclerView.Adapter<FavDishAdapter.ViewHolder>() {

    // It will be a list of favorite dishes
    private var dishes: List<FavDish> = listOf()

    // ViewHolder will require 3 Members
    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class ViewHolder(view: ItemDishLayoutBinding) : RecyclerView.ViewHolder(view.root) {
        // Holds the TextView that will add each item to
        val ivDishImage = view.ivDishImage
        val tvTitle = view.tvDishTitle
    }

    // Member 1/3 - Inflate the XML file: item_dish_layout.xml
    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding: ItemDishLayoutBinding =
            ItemDishLayoutBinding.inflate(
                LayoutInflater.from(fragment.context),
                parent,
                false
            )

        return ViewHolder(binding)
    }

    // Member 2/3 - How each item will look like?
    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        // Dish we are currently looking at
        val dish = dishes[position]

        // Load the dish image in the ImageView (Thumbnail)
        Glide.with(fragment)
            .load(dish.image)
            .into(holder.ivDishImage)

        // Update the Title: tv_dish_title in item_dish_layout.xml file
        holder.tvTitle.text = dish.title

        // onClick() Event for the Grid Items:
        // Assign the click event to the itemview and perform the required action.
        holder.itemView.setOnClickListener {

            // Verify that the request is coming from correct source:
            if (fragment is AllDishesFragment) {

                // Ask the AllDishesFragment.kt file to execute the dishDetails() function,
                // so the user can be navigated to the DishDetailsFragment
                // dish: will be passed as a required parm value
                fragment.dishDetails(dish)

            }

        }

    }

    // Member 3/3
    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return dishes.size
    }

    // Create a function that will have the updated list of dishes that we will bind it to the adapter class.
    fun dishesList(list: List<FavDish>) {

        // List of our dishes
        dishes = list

        // It will notify any registered observer that the data has been changed
        notifyDataSetChanged()
    }

}