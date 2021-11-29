package org.abubaker.favdish.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import org.abubaker.favdish.R
import org.abubaker.favdish.application.FavDishApplication
import org.abubaker.favdish.databinding.FragmentAllDishesBinding
import org.abubaker.favdish.view.activities.AddUpdateDishActivity
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
     * FavDishViewModelFactory = Defined in our FavDishViewModel.kt file
     * requireActivity = Returns FragmentActivity
     *
     */
    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }

    /**
     * onCreate()
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable menu in the ActionBar
        setHasOptionsMenu(true)
    }


    /**
     * onCreateView()
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

        // Initialize the RecyclerView and bind the adapter class
        // Set the LayoutManager that this RecyclerView will use.
        mBinding.rvDishesList.layoutManager = GridLayoutManager(requireActivity(), 2)

        // Adapter class is initialized and list is passed in the param.
        val favDishAdapter = FavDishAdapter(this@AllDishesFragment)

        // adapter instance is set to the recyclerview to inflate the items.
        mBinding.rvDishesList.adapter = favDishAdapter

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

                    mBinding.rvDishesList.visibility = View.VISIBLE
                    mBinding.tvNoDishesAddedYet.visibility = View.GONE

                    favDishAdapter.dishesList(it)
                } else {

                    mBinding.rvDishesList.visibility = View.GONE
                    mBinding.tvNoDishesAddedYet.visibility = View.VISIBLE
                }

            }
        }

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

}