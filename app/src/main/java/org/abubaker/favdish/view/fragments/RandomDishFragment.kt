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

    // Override the onViewCreated method and Initialize the ViewModel variable (mRandomDishViewModel)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModelProvider will be used to initialize/create our ViewModel
        //
        // For the following code to work our:
        // class RandomDishViewModel{} should be
        // class RandomDishViewModel : ViewModel() {} in RandomDishViewModel.kt file
        mRandomDishViewModel =
            ViewModelProvider(this)[RandomDishViewModel::class.java]

        // Execute the getRandomDishFromAPI inside the RandomDishViewModel.kt to get the RESPONSE from the API
        mRandomDishViewModel.getRandomDishFromAPI()

        // Call the observer function defined below in the same RandomDishFragment.kt file
        randomDishViewModelObserver()
    }

    /**
     * A function to get the data in the observer after the API is triggered.
     * It will observe the response from the API.
     */
    private fun randomDishViewModelObserver() {

        /**
         * These are 3 mutable properties, that were defined in the RandomDishViewModel.kt file:
         * 1. val loadRandomDish = MutableLiveData<Boolean>()
         * 2. val randomDishResponse = MutableLiveData<RandomDish.Recipes>()
         * 3. val randomDishLoadingError = MutableLiveData<Boolean>()
         */

        // 01 Get the Response
        mRandomDishViewModel.randomDishResponse.observe(
            viewLifecycleOwner,
            Observer { randomDishResponse ->
                randomDishResponse?.let {
                    Log.i("Random Dish Response", "$randomDishResponse.recipes[0]")
                }
            })

        // 02 ERROR
        mRandomDishViewModel.randomDishLoadingError.observe(
            viewLifecycleOwner,
            Observer { dataError ->
                dataError?.let {
                    Log.i("Random Dish API Error", "$dataError")
                }
            })

        // 03 Load Result
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