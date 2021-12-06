package org.abubaker.favdish.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import org.abubaker.favdish.application.FavDishApplication
import org.abubaker.favdish.databinding.FragmentFavoriteDishesBinding
import org.abubaker.favdish.model.entities.FavDish
import org.abubaker.favdish.view.activities.MainActivity
import org.abubaker.favdish.view.adapters.FavDishAdapter
import org.abubaker.favdish.viewModel.FavDishViewModel
import org.abubaker.favdish.viewModel.FavDishViewModelFactory

class FavoriteDishesFragment : Fragment() {

    private var binding: FragmentFavoriteDishesBinding? = null

    /**
     * We are creating an instance of ViewModel to access the methods that are necessary to populate the UI.
     *
     * To create the ViewModel we used the viewModels delegate, passing in an instance of our FavDishViewModelFactory.
     * This is constructed based on the repository retrieved from the FavDishApplication.
     */
    private val mFavDishViewModel: FavDishViewModel by viewModels {

        // We need to pass the Repository, but to get it we need to first pass our activity's application.
        // (requireActivity().application as FavDishApplication) =
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Initialize the view
        binding = FragmentFavoriteDishesBinding.inflate(inflater, container, false)
        return binding!!.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * Add an observer to get the list of updated favorite dishes.
         *
         * Add an observer on the LiveData returned by getFavoriteDishesList.
         * The onChanged() method fires when the observed data changes and the activity is in the foreground.
         */
        mFavDishViewModel.favoriteDishes.observe(viewLifecycleOwner) { dishes ->
            dishes.let {

                // Set the LayoutManager that this RecyclerView will use.
                // requireActivity() = gets current context
                binding!!.rvFavoriteDishesList.layoutManager =
                    GridLayoutManager(requireActivity(), 2)

                // Adapter class is initialized and list is passed in the param.
                val adapter = FavDishAdapter(this@FavoriteDishesFragment)

                // RecyclerView = Adapter instance is set to the recyclerview to inflate the items.
                binding!!.rvFavoriteDishesList.adapter = adapter

                if (it.isNotEmpty()) {
                    // Print the id and title in the log for now.
                    // for (dish in it) { Log.i("Favorite Dish", "${dish.id} :: ${dish.title}") }

                    // RecyclerView = VISIBLE
                    binding!!.rvFavoriteDishesList.visibility = View.VISIBLE

                    // Fallback Message = INVISIBLE
                    binding!!.tvNoFavoriteDishesAvailable.visibility = View.GONE

                    adapter.dishesList(it)

                } else {
                    Log.i("List of Favorite Dishes", "is empty.")

                    // RecyclerView = INVISIBLE
                    binding!!.rvFavoriteDishesList.visibility = View.GONE

                    // Fallback Message = VISIBLE
                    binding!!.tvNoFavoriteDishesAvailable.visibility = View.VISIBLE

                }
            }
        }

    }

    // Override the onResume function to show the BottomNavigationView when the fragment is completely loaded.
    override fun onResume() {
        super.onResume()

        if (requireActivity() is MainActivity) {
            (activity as MainActivity?)!!.showBottomNavigationView()
        }

    }

    // Override the onDestroy method and make the mBinding null where the method is executed.
    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    // Create a function to navigate to the Dish Details Fragment.
    /**
     * A function to navigate to the Dish Details Fragment.
     *
     * @param favDish
     */
    fun dishDetails(favDish: FavDish) {

        // Hide the BottomNavigationView while navigating to the DetailsFragment.
        if (requireActivity() is MainActivity) {

            // Execute the hideBottomNavigationView() defined in the MainActivity.kt file
            (activity as MainActivity?)!!.hideBottomNavigationView()

        }

        // Navigate to the Details Fragment
        findNavController()
            .navigate(FavoriteDishesFragmentDirections.actionFavoriteDishesToDishDetails(favDish))
    }

}