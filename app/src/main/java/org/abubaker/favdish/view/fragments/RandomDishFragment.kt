package org.abubaker.favdish.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import org.abubaker.favdish.databinding.FragmentRandomDishBinding
import org.abubaker.favdish.viewModel.RandomDishViewModel

class RandomDishFragment : Fragment() {

    private var binding: FragmentRandomDishBinding? = null

    // An instance of the ViewModel Class
    private lateinit var mRandomDishViewModel: RandomDishViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Initialize the ViewBinding.
        binding = FragmentRandomDishBinding.inflate(inflater, container, false)
        return binding!!.root

    }

    // TODO Step 2: Override the onViewCreated method and Initialize the ViewModel variable.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the ViewModel variable.
        mRandomDishViewModel =
            ViewModelProvider(this).get(RandomDishViewModel::class.java)

        // Call the function to get the response from API.
        mRandomDishViewModel.getRandomDishFromAPI()

        // Call the observer function.
        randomDishViewModelObserver()
    }

    // TODO Step 4: Create a function to get the data in the observer after the API is triggered.
    // START
    /**
     * A function to get the data in the observer after the API is triggered.
     */
    private fun randomDishViewModelObserver() {

        mRandomDishViewModel.randomDishResponse.observe(
            viewLifecycleOwner,
            Observer { randomDishResponse ->
                randomDishResponse?.let {
                    Log.i("Random Dish Response", "$randomDishResponse.recipes[0]")
                }
            })

        mRandomDishViewModel.randomDishLoadingError.observe(
            viewLifecycleOwner,
            Observer { dataError ->
                dataError?.let {
                    Log.i("Random Dish API Error", "$dataError")
                }
            })

        mRandomDishViewModel.loadRandomDish.observe(viewLifecycleOwner, Observer { loadRandomDish ->
            loadRandomDish?.let {
                Log.i("Random Dish Loading", "$loadRandomDish")
            }
        })
    }

    // Override the onDestroy method and make the ViewBinding null when it is called.
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}