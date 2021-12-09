package org.abubaker.favdish.view.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import org.abubaker.favdish.R
import org.abubaker.favdish.application.FavDishApplication
import org.abubaker.favdish.databinding.DialogCustomListBinding
import org.abubaker.favdish.databinding.FragmentAllDishesBinding
import org.abubaker.favdish.model.entities.FavDish
import org.abubaker.favdish.utils.Constants
import org.abubaker.favdish.view.activities.AddUpdateDishActivity
import org.abubaker.favdish.view.activities.MainActivity
import org.abubaker.favdish.view.adapters.CustomListItemAdapter
import org.abubaker.favdish.view.adapters.FavDishAdapter
import org.abubaker.favdish.viewModel.FavDishViewModel
import org.abubaker.favdish.viewModel.FavDishViewModelFactory

class AllDishesFragment : Fragment() {

    // Create a global variable for the ViewBinding.
    private lateinit var mBinding: FragmentAllDishesBinding

    /**
     *
     * What to do?
     * ===========
     * Create a ViewModel instance (mFavDishViewModel) to access the methods, which we will use to
     * populate data from the database into our fragment.
     *
     * Theory:
     * =======
     * To create the ViewModel we used the viewModels delegate, passing in an instance of our FavDishViewModelFactory.
     * This is constructed based on the repository retrieved from the FavDishApplication.
     *
     * FavDishViewModelFactory = Defined in our AllDishesViewModel.kt file
     * requireActivity = Returns FragmentActivity
     *
     */
    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }

    // Make the global variable for FavDishAdapter class as below instead of local.
    // A global variable for FavDishAdapter Class
    private lateinit var mFavDishAdapter: FavDishAdapter

    // Make the CustomItemsListDialog as global instead of local as below.
    private lateinit var mCustomListDialog: Dialog

    /**
     * onCreate()
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable menu in the ActionBar
        setHasOptionsMenu(true)
    }


    /**
     * onCreateView() - Inflate Layout all_dishes_fragment.xml
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Initialize the mBinding variable.
        mBinding = FragmentAllDishesBinding.inflate(inflater, container, false)
        return mBinding.root

    }

    // Override the onViewCreated method and get the dishes list and print the title in Log for now.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        // #1 Define Layout | GridLayoutManager = 2 Columns
        //
        // Initialize the RecyclerView and bind the adapter class
        // Set the LayoutManager that this RecyclerView will use.
        mBinding.rvDishesList.layoutManager = GridLayoutManager(requireActivity(), 2)

        // #2 - Set Adapter | Adapter class is initialized and list is passed in the param.
        mFavDishAdapter = FavDishAdapter(this@AllDishesFragment)

        // #3 - Bind to the RecyclerView | adapter instance is set to the recyclerview to inflate the items.
        mBinding.rvDishesList.adapter = mFavDishAdapter

        /**
         * What we are doing?
         * ==================
         * We are using our mFavDishViewModel to call allDishesList to observe using viewLifecycleOwner for any changes.
         *
         * Add an observer on the LiveData returned by getAllDishesList.
         * The onChanged() method fires when the observed data changes and the activity is in the foreground.
         */
        mFavDishViewModel.allDishesList.observe(viewLifecycleOwner) { dishes ->
            dishes.let {

                // Pass the dishes list to the adapter class.
                if (it.isNotEmpty()) {

                    // Show Dish List (RecyclerView)
                    mBinding.rvDishesList.visibility = View.VISIBLE

                    // Hide Fallback Message
                    mBinding.tvNoDishesAddedYet.visibility = View.GONE

                    // Populate the List | it = List of the favorite dishes that we get from
                    // our observer, which basically gets the data from the database.
                    mFavDishAdapter.dishesList(it)

                } else {

                    // Hide Dish List (RecyclerView)
                    mBinding.rvDishesList.visibility = View.GONE

                    // Show Fallback Message
                    mBinding.tvNoDishesAddedYet.visibility = View.VISIBLE

                }

            }
        }

    }

    // Override the onResume method and call the function to show the BottomNavigationView when user is on the AllDishesFragment.
    override fun onResume() {
        super.onResume()

        // Show - Bottom Navigation on the MainActivity
        if (requireActivity() is MainActivity) {
            (activity as MainActivity?)!!.showBottomNavigationView()
        }
    }

    /**
     * A function to navigate to the Dish Details Fragment.
     * Note: We will execute it from the FavDishAdapter.kt file (Adapter)
     * We assigned a required param so data can be passed to the targeted Fragment.
     */
    fun dishDetails(favDish: FavDish) {

        // Step 1. Hide the Bottom Navigation
        // Call the hideBottomNavigationView function when user wants to navigate to the DishDetailsFragment.
        if (requireActivity() is MainActivity) {
            (activity as MainActivity?)!!.hideBottomNavigationView()
        }

        // Step 2. Navigate to the DishDetailsFragment
        // Set the target where we want to navigate to, i.e. DishDetailsFragment
        // We are adding the favDish as the required argument.
        findNavController()
            .navigate(AllDishesFragmentDirections.actionAllDishesToDishDetails(favDish))

    }

    /**
     * onCreateOptionsMenu()
     *
     * Inflate XML: menu_all_dishes
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        // Inflate: menu_all_dishes.xml
        inflater.inflate(R.menu.menu_all_dishes, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * onOptionsItemSelected()
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            // Filter List
            R.id.action_filter_dishes -> {

                // Executing the function to launch the custom dialog for "Filter List"
                filterDishesListDialog()

                // Return true
                return true

            }

            // Add Dish
            R.id.action_add_dish -> {
                // Note: Inside the Fragment we cannot say "this", because the Fragment
                // does not have a "context" by itself. So, we need to ge the "context" of the
                // Activity in which it exists by using requireActivity() + Where we want to goto
                startActivity(Intent(requireActivity(), AddUpdateDishActivity::class.java))
                return true
            }

        }

        return super.onOptionsItemSelected(item)
    }

    // Create a function to show an AlertDialog while delete the dish details.
    /**
     * Method is used to show the Alert Dialog while deleting the dish details.
     *
     * @param dish - Dish details that we want to delete.
     */
    fun deleteDish(dish: FavDish) {

        val builder = AlertDialog.Builder(requireActivity())

        // set title for alert dialog
        builder.setTitle(resources.getString(R.string.title_delete_dish))

        // set message for alert dialog
        builder.setMessage(resources.getString(R.string.msg_delete_dish_dialog, dish.title))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        // Button: YES / Confirmed
        builder.setPositiveButton(resources.getString(R.string.lbl_yes)) { dialogInterface, _ ->

            //  Delete the DATA from the Database.
            mFavDishViewModel.delete(dish)

            // Dismiss the Dialog.
            dialogInterface.dismiss()

        }

        // Button: No
        builder.setNegativeButton(resources.getString(R.string.lbl_no)) { dialogInterface, _ ->

            // Dismiss the Dialog.
            dialogInterface.dismiss()

        }

        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()

        // Set other dialog properties

        // Optional: Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.setCancelable(false)

        // show the dialog to UI
        alertDialog.show()
    }

    /**
     * A function to launch the custom dialog for "Filter List"
     */
    private fun filterDishesListDialog() {

        // We are defining our custom dialog
        mCustomListDialog = Dialog(requireActivity())

        // Binding the XML Layout: dialog_custom_list.xml and setting the ContentView at top-level
        val binding: DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)
        mCustomListDialog.setContentView(binding.root)

        // Dialog Title
        binding.tvTitle.text = resources.getString(R.string.title_select_item_to_filter)

        // Getting the List of "Dish Types" defined in Constants.kt file
        val dishTypes = Constants.dishTypes()

        // We are adding a new dishType: "All Items" at the 0-Index Position in the recently fetched List
        dishTypes.add(0, Constants.ALL_ITEMS)

        // Set the LayoutManager that this RecyclerView will use.
        binding.rvList.layoutManager = LinearLayoutManager(requireActivity())

        // Defining parameters for the "adapter"
        // Adapter class is initialized and list is passed in the param.
        val adapter = CustomListItemAdapter(

            // Active Context
            requireActivity(),

            //
            this@AllDishesFragment,

            // List of Dish Types defined in Constants.kt
            dishTypes,

            // CLICKED ITEM which the user will like to SEARCH/FILTER
            Constants.FILTER_SELECTION

        )

        // Setting up the RecyclerView based on the recently configured adapter's parameters.
        // adapter instance is set to the recyclerview to inflate the items.
        binding.rvList.adapter = adapter

        // Initialize the dialog and display it on screen.
        mCustomListDialog.show()
    }

    // Create a function to get the filter item selection and get the list from database accordingly.
    /**
     * A function to get the filter item selection and get the list from database accordingly.
     *
     * @param filterItemSelection
     */
    fun filterSelection(filterItemSelection: String) {

        mCustomListDialog.dismiss()

        Log.i("Filter Selection", filterItemSelection)

        if (filterItemSelection == Constants.ALL_ITEMS) {

            //
            mFavDishViewModel.allDishesList.observe(viewLifecycleOwner) { dishes ->

                dishes.let {

                    //
                    if (it.isNotEmpty()) {

                        mBinding.rvDishesList.visibility = View.VISIBLE
                        mBinding.tvNoDishesAddedYet.visibility = View.GONE

                        mFavDishAdapter.dishesList(it)

                    } else {

                        mBinding.rvDishesList.visibility = View.GONE
                        mBinding.tvNoDishesAddedYet.visibility = View.VISIBLE
                    }

                }
            }
        } else {

            //
            Log.i("Filter List", "Get Filter List")

        }
    }

}