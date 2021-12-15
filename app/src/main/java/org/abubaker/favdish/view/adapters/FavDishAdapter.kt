package org.abubaker.favdish.view.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.abubaker.favdish.R
import org.abubaker.favdish.databinding.ItemDishLayoutBinding
import org.abubaker.favdish.model.entities.FavDish
import org.abubaker.favdish.utils.Constants
import org.abubaker.favdish.view.activities.AddUpdateDishActivity
import org.abubaker.favdish.view.fragments.AllDishesFragment
import org.abubaker.favdish.view.fragments.FavoriteDishesFragment

class FavDishAdapter(private val fragment: Fragment) :
    RecyclerView.Adapter<FavDishAdapter.ViewHolder>() {

    // It will be a list of favorite dishes
    private var dishes: List<FavDish> = listOf()

    // ViewHolder will require 3 Members
    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class ViewHolder(view: ItemDishLayoutBinding) : RecyclerView.ViewHolder(view.root) {

        // Thumbnail
        val ivDishImage = view.ivDishImage

        // Title
        val tvTitle = view.tvDishTitle

        // Popup (Dropdown) menu icon
        val ibMore = view.ibMore

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
        // Assign the click event to the itemView and perform the required action.
        holder.itemView.setOnClickListener {

            // Verify that the request is coming from correct source:
            if (fragment is AllDishesFragment) {

                // Ask the AllDishesFragment.kt file to execute the dishDetails() function,
                // so the user can be navigated to the DishDetailsFragment
                // dish: will be passed as a required parameter value
                fragment.dishDetails(dish)

            } else if (fragment is FavoriteDishesFragment) {

                // FavoriteDishesFragment
                fragment.dishDetails(dish)

            }

        }

        /**
         * Important: Display popup menu only for the "All Dishes" fragment
         * ==========
         * We want the menu icon should be visible only in the AllDishesFragment
         * not in the FavoriteDishesFragment so add the below to achieve it.
         */
        if (fragment is AllDishesFragment) {

            // Display in the All Dishes fragment
            holder.ibMore.visibility = View.VISIBLE

        } else if (fragment is FavoriteDishesFragment) {

            // Hide in the Favorite Dishes fragment
            holder.ibMore.visibility = View.GONE

        }

        /**
         * Click event for the ib_more (Dropdown) icon and Popup the menu items.
         */
        holder.ibMore.setOnClickListener {

            // Popup menu Object - create a new popup menu with an anchor view.
            val popup = PopupMenu(fragment.context, holder.ibMore)

            // Inflating the Popup using xml file
            popup.menuInflater.inflate(R.menu.menu_adapter, popup.menu)

            // Assign the click event to the menu items as below and print the Log or You can display the Toast message for now.
            popup.setOnMenuItemClickListener {

                // Edit Menu item?
                if (it.itemId == R.id.action_edit_dish) {

                    // Edit Button was clicked
                    Log.i("You have clicked on", "Edit Option of ${dish.title}")

                    // Based on the "active context", we will use the following code to pass
                    // the dish details to AddUpdateDishActivity.
                    val intent =
                        Intent(fragment.requireActivity(), AddUpdateDishActivity::class.java)

                    // This constant "EXTRA_DISH_DETAILS" will pass the "DishDetails" as a string
                    intent.putExtra(Constants.EXTRA_DISH_DETAILS, dish)

                    // Initialize the intent
                    fragment.requireActivity().startActivity(intent)

                } else if (it.itemId == R.id.action_delete_dish) {

                    // Delete Button was clicked
                    Log.i("You have clicked on", "Delete Option of ${dish.title}")

                    // Remove the log and call the function that we have created to delete.
                    if (fragment is AllDishesFragment) {

                        fragment.deleteDish(dish)

                    }

                }

                // It is important to return true
                true
            }

            // Initialize the popup menu
            popup.show()
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
    @SuppressLint("NotifyDataSetChanged")
    fun dishesList(list: List<FavDish>) {

        // List of our dishes
        dishes = list

        // It will notify any registered observer that the data has been changed
        notifyDataSetChanged()
    }

}