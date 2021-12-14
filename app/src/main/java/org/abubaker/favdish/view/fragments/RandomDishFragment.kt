package org.abubaker.favdish.view.fragments

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import org.abubaker.favdish.R
import org.abubaker.favdish.application.FavDishApplication
import org.abubaker.favdish.databinding.FragmentRandomDishBinding
import org.abubaker.favdish.model.entities.FavDish
import org.abubaker.favdish.model.entities.RandomDish
import org.abubaker.favdish.utils.Constants
import org.abubaker.favdish.viewModel.FavDishViewModel
import org.abubaker.favdish.viewModel.FavDishViewModelFactory
import org.abubaker.favdish.viewModel.RandomDishViewModel

class RandomDishFragment : Fragment() {

    private var binding: FragmentRandomDishBinding? = null

    // An instance of the ViewModel Class
    private lateinit var mRandomDishViewModel: RandomDishViewModel

    // A global variable for our custom Progress Dialog
    private var mProgressDialog: Dialog? = null

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

        //
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

        // Call the observer function defined below in the same RandomDishFragment.kt file, to Load a Random Dish
        randomDishViewModelObserver()

        /**
         * We are setting up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
         * performs a swipe-to-refresh gesture, it will be used for calling the getRandomDishFromAPI()
         * function from the RandomDishViewModel.kt file to get the new dish details on the same screen.
         *
         * Note: srlRandomDish or srl_random_dish is the #id assigned to the root element in the
         * fragment_random_dish.xml file
         *
         */
        binding!!.srlRandomDish.setOnRefreshListener {

            // This method performs the actual data-refresh operation.
            // Auto - The method calls setRefreshing(false) when it's finished.
            mRandomDishViewModel.getRandomDishFromAPI()

        }

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

                    // Log
                    Log.i("Random Dish Response", "$randomDishResponse.recipes[0]")


                    if (binding!!.srlRandomDish.isRefreshing) {

                        // Hide the Loading ProgressBar of SwipeRefreshLayout once the response is success.
                        binding!!.srlRandomDish.isRefreshing = false

                    }

                    // Call the function to populate the response in the UI.
                    // Note: Since the API's returned result will be a LIST of Recipes,
                    // so we need to only Pick the 1st value at 0-index, using recipes[0]
                    setRandomDishResponseInUI(randomDishResponse.recipes[0])

                }
            })

        // 02 ERROR
        mRandomDishViewModel.randomDishLoadingError.observe(
            viewLifecycleOwner,
            Observer { dataError ->
                dataError?.let {
                    Log.i("Random Dish API Error", "$dataError")

                    // It will ensure that the "refreshing" icon is only appearing when needed.
                    if (binding!!.srlRandomDish.isRefreshing) {

                        // Hide the Loading ProgressBar of SwipeRefreshLayout when there is an error from API.
                        binding!!.srlRandomDish.isRefreshing = false

                    }
                }
            })

        // 03 Load Result
        mRandomDishViewModel.loadRandomDish.observe(viewLifecycleOwner, Observer { loadRandomDish ->
            loadRandomDish?.let {

                // Log message
                Log.i("Random Dish Loading", "$loadRandomDish")

                // Show the progress dialog if the SwipeRefreshLayout is not visible and hide when the usage is completed.
                if (loadRandomDish && !binding!!.srlRandomDish.isRefreshing) {

                    // Used to show the progress dialog.
                    showCustomProgressDialog()

                } else {

                    // Hide the progress dialog.
                    hideProgressDialog()

                }

            }
        })
    }

    /**
     * A method to populate the API response in the UI.
     *
     * @param recipe - Data model class of the API response with filled data.
     */
    private fun setRandomDishResponseInUI(recipe: RandomDish.Recipe) {

        // 01 Thumbnail
        Glide.with(requireActivity())
            .load(recipe.image)
            .centerCrop()
            .into(binding!!.ivDishImage)

        // 02 Title
        binding!!.tvTitle.text = recipe.title

        // 03 Dish Type
        // Set the Default Dish Type = other, and then check if any "Dish Type" is already assigned
        // to the received dish, otherwise re-assign our "default value"
        var dishType: String = "other"
        if (recipe.dishTypes.isNotEmpty()) {
            dishType = recipe.dishTypes[0]
            binding!!.tvType.text = dishType
        }

        // 04 Category (Fallback default value, as we do not have it in the API)
        // Issue: There is "no category params" present in the response so we will define it as Other.
        binding!!.tvCategory.text = "Other"

        // 05 Ingredients
        var ingredients = ""
        for (value in recipe.extendedIngredients) {

            if (ingredients.isEmpty()) {
                ingredients = value.original
            } else {
                ingredients = ingredients + ", \n" + value.original
            }
        }
        binding!!.tvIngredients.text = ingredients

        // 06 Cooking Direction / Instructions ( Received as Rich HTML Text)
        // The instruction fetched from the API are received in the Rich HTML text, so we need to
        // populate Rich HTML Text, in the TextView.

        // Assign the "instructions" value received from the API to our tv_cooking_direction TextView
        // Html.FROM_HTML_MODE_COMPACT / Html.fromHtml() will filter out the "HTML Tags" form the TEXT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            // SDK > v24

            binding!!.tvCookingDirection.text = Html.fromHtml(
                recipe.instructions,
                Html.FROM_HTML_MODE_COMPACT
            )

        } else {

            // SDK < v23
            @Suppress("DEPRECATION")
            binding!!.tvCookingDirection.text = Html.fromHtml(recipe.instructions)

        }

        // 07 Cooking Time (Duration)
        binding!!.tvCookingTime.text = resources.getString(
            R.string.lbl_estimate_cooking_time,
            recipe.readyInMinutes.toString()
        )


        // By default load the favorite image button as unselected.
        binding!!.ivFavoriteDish.setImageDrawable(

            // Setting the DEFAULT image = Unselected icon
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.ic_favorite_unselected
            )

        )

        /**
         * -----------------------------------------------------------------------------------------
         * 08 Favorite Icon (Click Event)
         * */

        // Avoiding Duplicating Entries for Favorite
        // *****************************************
        // Create a variable to avoid the duplication of items that is added by click on the
        // Favorite image to add the dish details to local database.
        var addedToFavorite = false

        // Assign the click event to the Favorite Button and add the dish details to the local database if user click on it.
        binding!!.ivFavoriteDish.setOnClickListener {

            // Handle the condition based on the variable that we have defined and show the Toast message.
            if (addedToFavorite) {

                // Inform the user that the entry is already saved in the Database.
                Toast.makeText(
                    requireActivity(),
                    resources.getString(R.string.msg_already_added_to_favorites),
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                // 01 Prepare DATA to be inserted in the Database
                // Create an instance of FavDish data model class and fill it with required information from the API response.
                val randomDishDetails = FavDish(

                    // 01 Thumbnail
                    recipe.image,

                    // 02 Image Source (Online)
                    Constants.DISH_IMAGE_SOURCE_ONLINE,

                    // 03 Title
                    recipe.title,

                    // 04 Dish Type
                    dishType,

                    // 05 Category
                    "Other",

                    // 06 Ingredients
                    ingredients,

                    // 07 Cooking Time
                    recipe.readyInMinutes.toString(),

                    // 08 Cooking Duration
                    recipe.instructions,

                    // 09 Favorite?
                    true
                )

                // 02 Create an instance of ViewModel
                // Create an instance of FavDishViewModel class and call insert function and pass the required details.
                val mFavDishViewModel: FavDishViewModel by viewModels {
                    FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
                }

                // 03 Tell the ViewModel to initialize the insert()
                mFavDishViewModel.insert(randomDishDetails)

                //  *******************************************************
                // IMPORTANT: Update the value of addedToFavorite variable.
                // ********************************************************
                addedToFavorite = true

                // 04 Update Fav Icon
                // Once the dish is inserted then update the favorite image by selected.
                binding!!.ivFavoriteDish.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_favorite_selected
                    )
                )

                // 05 Display Toast Message to confirm that the action was successful.
                // Once the dish is inserted you can acknowledge user by Toast message as below
                Toast.makeText(
                    requireActivity(),
                    resources.getString(R.string.msg_added_to_favorites),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

    }

    // Override the onDestroy method and make the ViewBinding null when it is called.
    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    /*********************************************************************************************
     * Custom Dialog (Show/Hide functions)
     */

    // A function is used to show the Custom Progress Dialog.
    private fun showCustomProgressDialog() {

        // Pass on the current context
        mProgressDialog = Dialog(requireActivity())

        // If progress dialog is not empty, then:
        mProgressDialog?.let {

            /*
            * Set the screen content from a layout resource (i.e. dialog_custom_progress.xml).
            * The resource will be inflated, adding all top-level views to the screen.
            * */
            it.setContentView(R.layout.dialog_custom_progress)

            // Initialize the custom dialog and display it on screen.
            it.show()

        }

    }

    // This function is used to dismiss the progress dialog if it is visible to user.
    private fun hideProgressDialog() {

        // Dismiss the custom dialog
        mProgressDialog?.dismiss()

    }

}