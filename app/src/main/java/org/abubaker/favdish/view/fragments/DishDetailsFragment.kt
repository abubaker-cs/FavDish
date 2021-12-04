package org.abubaker.favdish.view.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import org.abubaker.favdish.R
import org.abubaker.favdish.databinding.FragmentDishDetailsBinding
import org.jetbrains.annotations.Nullable
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

                    // This listener (function) will be used to generate Palette and update background color.
                    .listener(object : RequestListener<Drawable> {

                        // Failure: Log the Error message if image will be failed to download.
                        override fun onLoadFailed(
                            @Nullable e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {

                            // log exception
                            Log.e("TAG", "Error loading image", e)

                            // important to return false so the error placeholder can be placed
                            return false
                        }

                        // Success: Generate Color from our reference image and apply it as a BackgroundColor to the target Object.
                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {

                            // Generate the Palette and set the vibrantSwatch as the background of the view.
                            // Once the resource (Drawable Image) is ready generate the Palette from the bitmap of image.
                            Palette.from(resource.toBitmap())

                                // We are GENERATING colors from the bitmap
                                .generate { palette ->

                                    /**
                                     * Type of Swatches:
                                     * If it is a value then use it, otherwise use 0
                                     * vibrantSwatch = Returns the most vibrant swatch in the palette.
                                     * Might be null, that's why we need to check if it is NULL using: it.vibrantSwatch?.rgb ?: 0
                                     */

                                    /**
                                     * 6 Color Modes:
                                     * Reference: https://developer.android.com/training/material/palette-colors
                                     * ==============
                                     * The palette library attempts to extract the following six color profiles:
                                     * 1. Light Vibrant : Palette.getLightVibrantSwatch()
                                     * 2. Dark Vibrant: Palette.getDarkVibrantSwatch()
                                     * 3. Vibrant: Palette.getVibrantSwatch()
                                     * 4. Light Muted: Palette.getLightMutedSwatch()
                                     * 5. Dark Muted: Palette.getDarkMutedSwatch()
                                     * 6. Muted: Palette.getMutedSwatch()
                                     */
                                    val intColor = palette?.vibrantSwatch?.rgb ?: 0

                                    // Change background color based on the fetched RGB value
                                    // setBackgroundColor requires an Int value, and our rgb() is already returning result as an integer value
                                    binding!!.rlDishDetailMain.setBackgroundColor(intColor)

                                }
                            return false
                        }
                    })

                    // Load image into the ImageView
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