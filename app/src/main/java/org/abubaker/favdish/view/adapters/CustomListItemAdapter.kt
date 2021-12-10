package org.abubaker.favdish.view.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import org.abubaker.favdish.databinding.ItemCustomListLayoutBinding
import org.abubaker.favdish.view.activities.AddUpdateDishActivity
import org.abubaker.favdish.view.fragments.AllDishesFragment

/**
 * Create a custom list adapter to use it while showing the list item in the RecyclerView.
 */
class CustomListItemAdapter(
    private val activity: Activity,
    private val fragment: Fragment?,
    private val listItems: List<String>,
    private val selection: String
) :
    RecyclerView.Adapter<CustomListItemAdapter.ViewHolder>() {

    // ViewHolder will require 3 Members
    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class ViewHolder(view: ItemCustomListLayoutBinding) : RecyclerView.ViewHolder(view.root) {

        // Holds the TextView that will add each item to tvText in item_custom_list_layout.xml
        val tvText = view.tvText

    }

    // Member 1/3 - "Select Dish Type" | XML file with the list will be inflated
    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        // Select file to bind: item_custom_list_layout.xml
        val binding: ItemCustomListLayoutBinding =
            ItemCustomListLayoutBinding.inflate(
                LayoutInflater.from(activity),
                parent,
                false
            )

        // Bind the selected file to the ViewHolder
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

        // We are selecting the CURRENT item in the LIST total records
        val item = listItems[position]

        // Title
        holder.tvText.text = item

        // Very IMPORTANT: Define the ItemView click event
        holder.itemView.setOnClickListener {

            // Are we currently in the AddUpdateDishActivity ?
            if (activity is AddUpdateDishActivity) {

                //  Send the result to the base class.
                activity.selectedListItem(item, selection)

            }

            // Call the function and pass the required details.
            if (fragment is AllDishesFragment) {

                // We want to select the user's requested item
                fragment.filterSelection(item)

            }

        }

    }

    // Member 3/3
    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return listItems.size
    }

}