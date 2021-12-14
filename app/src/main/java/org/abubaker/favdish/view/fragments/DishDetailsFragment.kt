package org.abubaker.favdish.view.fragments

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import org.abubaker.favdish.R
import org.abubaker.favdish.application.FavDishApplication
import org.abubaker.favdish.databinding.FragmentDishDetailsBinding
import org.abubaker.favdish.model.entities.FavDish
import org.abubaker.favdish.utils.Constants
import org.abubaker.favdish.viewModel.FavDishViewModel
import org.abubaker.favdish.viewModel.FavDishViewModelFactory
import org.jetbrains.annotations.Nullable
import java.io.IOException
import java.util.*

class DishDetailsFragment : Fragment() {

    // It will be used for ViewBinding
    private var binding: FragmentDishDetailsBinding? = null

    /**
     * We are creating an ViewModel instance to access the methods.
     * To create the ViewModel we used the viewModels delegate, passing in an instance of our FavDishViewModelFactory.
     * This is constructed based on the repository retrieved from the FavDishApplication.
     */
    private val mFavDishViewModel: FavDishViewModel by viewModels {

        // Setup: We want to use the ViewModel in our DishDetailsFragment
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)

    }

    // Create a global variable  for Dish Details and assign the args to it.
    private var mFavDishDetails: FavDish? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // Enable support for menu
        setHasOptionsMenu(true)

    }

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

        // We will use the args passed to us, so they can be used in the SHARE feature
        mFavDishDetails = args.dishDetails

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

            // --------------------------
            // This can store HTML Markup
            // binding!!.tvCookingDirection.text = it.dishDetails.directionToCook

            // So, we are using the filtered option to remove HTML Markup using Html.fromHtml().toString()
            // The instruction or you can say the Cooking direction text is in the HTML format so we will you the fromHtml to populate it in the TextView.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                // SDK > 24
                binding!!.tvCookingDirection.text = Html.fromHtml(
                    it.dishDetails.directionToCook,
                    Html.FROM_HTML_MODE_COMPACT
                ).toString()

            } else {

                // SDK < 24
                @Suppress("DEPRECATION")
                binding!!.tvCookingDirection.text =
                    Html.fromHtml(it.dishDetails.directionToCook).toString()

            }

            // Cooking Duration
            binding!!.tvCookingTime.text =
                resources.getString(R.string.lbl_estimate_cooking_time, it.dishDetails.cookingTime)

            // Important: Without adding following code, our Favorite icon will remain unchanged if
            // we will return back to the details page from the list. This code will ensure that
            // correct updated state will be feteched from the database.
            if (args.dishDetails.favoriteDish) {

                // State: Favorite
                binding!!.ivFavoriteDish.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_favorite_selected
                    )
                )
            } else {

                // State: Unfavorite
                binding!!.ivFavoriteDish.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_favorite_unselected
                    )
                )
            }
        }


        /**
         * Assign the event to the favorite icon.
         */
        binding!!.ivFavoriteDish.setOnClickListener {

            // Update the favorite dish variable based on the current selection. i.e If it true then make it false vice-versa.
            // ! = Since our value is Boolean, so if it was FALSE then the value will be saved as True, and vice-versa.
            // False > True | True > False
            args.dishDetails.favoriteDish = !args.dishDetails.favoriteDish

            // Pass the updated values to ViewModel
            mFavDishViewModel.update(args.dishDetails)

            // Favorite Icon: Update the icons and display the toast message accordingly.
            if (args.dishDetails.favoriteDish) {

                // Favorite Icon: Selected Mode
                binding!!.ivFavoriteDish.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_favorite_selected
                    )
                )

                // Message: You have added the dish to your favorites.
                Toast.makeText(
                    requireActivity(),
                    resources.getString(R.string.msg_added_to_favorites),
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                // Favorite Icon: Unselected Mode
                binding!!.ivFavoriteDish.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_favorite_unselected
                    )
                )

                // Message: You have removed the dish from your favorites
                Toast.makeText(
                    requireActivity(),
                    resources.getString(R.string.msg_removed_from_favorite),
                    Toast.LENGTH_SHORT
                ).show()

            }

        }
    }

    // Override the onCreateOptionsMenu and onOptionsItemSelected. Inflate the menu file that we have created.
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        // XML: Select menu_share.xml
        inflater.inflate(R.menu.menu_share, menu)

        // Inflate our XML file
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            // Handle Item click action and share the dish recipe details with others.
            R.id.action_share_dish -> {

                // Base configuration
                val type = "text/plain"
                val subject = "Checkout this dish recipe"
                var extraText = ""
                val shareWith = "Share with"

                // As defined in the onViewCreate(), if received args through mFavDishDetails
                // are NOT EMPTY, then pass then prepare data and initialize the Intent
                mFavDishDetails?.let {

                    // Thumbnail: Default value, and checking if the "ImageSource = Online"
                    var image = ""
                    if (it.imageSource == Constants.DISH_IMAGE_SOURCE_ONLINE) {
                        image = it.image
                    }

                    // Cooking Instructions: Default value
                    var cookingInstructions = ""

                    // The instruction or you can say the Cooking direction text is in the HTML format so we will you the fromHtml to populate it in the TextView.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                        // SDK > 24
                        cookingInstructions = Html.fromHtml(
                            it.directionToCook,
                            Html.FROM_HTML_MODE_COMPACT
                        ).toString()

                    } else {

                        // SDK < 24
                        @Suppress("DEPRECATION")
                        cookingInstructions = Html.fromHtml(it.directionToCook).toString()

                    }

                    // extraText = It will be passed through bundle()
                    extraText =
                        "$image \n" +
                                "\n Title:  ${it.title} \n\n Type: ${it.type} \n\n Category: ${it.category}" +
                                "\n\n Ingredients: \n ${it.ingredients} \n\n Instructions To Cook: \n $cookingInstructions" +
                                "\n\n Time required to cook the dish approx ${it.cookingTime} minutes."

                }

                // Define Intent, that will carry on our extraText
                val intent = Intent(Intent.ACTION_SEND)

                // Intent: Type
                intent.type = type

                // Intent: Subject
                intent.putExtra(Intent.EXTRA_SUBJECT, subject)

                // Intent: extraText
                intent.putExtra(Intent.EXTRA_TEXT, extraText)

                // Intent: Initialize with ShareWith (Allow user to chose sender method)
                startActivity(Intent.createChooser(intent, shareWith))

                return true
            }

        }

        //
        return super.onOptionsItemSelected(item)
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