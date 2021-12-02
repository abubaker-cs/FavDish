package org.abubaker.favdish.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import org.abubaker.favdish.R
import org.abubaker.favdish.databinding.FragmentDishDetailsBinding
import java.io.IOException
import java.util.*

class DishDetailsFragment : Fragment() {

    // It will be used for ViewBinding
    private var binding: FragmentDishDetailsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentDishDetailsBinding.inflate(inflater, container, false)
        return binding!!.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        /**
         * navArgs() - Returns a Lazy delegate to access the Fragment's arguments as an Args instance.
         *
         * Get the required arguments that we have passed through action and print few details in the Log for now.
         */
        val args: DishDetailsFragmentArgs by navArgs()
        // Log.i("Dish Title", args.dishDetails.title)
        // Log.i("Dish Type", args.dishDetails.type)

        // If arguments has data, then populate filed in the UI
        args.let {

            // Thumbnail
            try {
                // Load the dish image in the ImageView.
                Glide.with(requireActivity())
                    .load(it.dishDetails.image)
                    .centerCrop()
                    .into(binding!!.ivDishImage)

            } catch (e: IOException) {

                // Print Traced Error
                e.printStackTrace()

            }

            // Title
            binding!!.tvTitle.text = it.dishDetails.title

            // Type
            // Deprecated: type.capitalize(Local.ROOT)
            binding!!.tvType.text =
                it.dishDetails.type.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() } // Used to make first letter capital

            // Category
            binding!!.tvCategory.text = it.dishDetails.category

            // Ingredients
            binding!!.tvIngredients.text = it.dishDetails.ingredients

            // Cooking Direction
            binding!!.tvCookingDirection.text = it.dishDetails.directionToCook

            // Cooking Duration
            binding!!.tvCookingTime.text =
                resources.getString(R.string.lbl_estimate_cooking_time, it.dishDetails.cookingTime)
        }

    }

    /**
     * Override the onDestroy function to make the mBinding null that is avoid the memory leaks.
     * This we have not done before because the AllDishesFragment because when in it the onDestroy
     * function is called the app is killed. But this is the good practice to do it.
     */
    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    /**
     * Important:
     * The binding should set to null in onDestroyView instead of onDestroy, because its lifecycle
     * is attach to the view which is different from fragment, it can cause memory leak if not.
     */

}