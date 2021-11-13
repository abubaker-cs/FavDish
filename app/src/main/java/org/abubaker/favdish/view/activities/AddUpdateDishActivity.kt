package org.abubaker.favdish.view.activities

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.abubaker.favdish.R
import org.abubaker.favdish.databinding.ActivityAddUpdateDishBinding
import org.abubaker.favdish.databinding.DialogCustomImageSelectionBinding

class AddUpdateDishActivity : AppCompatActivity(),
    View.OnClickListener {

    private lateinit var mBinding: ActivityAddUpdateDishBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // Inflate Layout: activity_add_update_dish.xml
        mBinding = ActivityAddUpdateDishBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        // Initialize the ActionBar with required actions for the onClick Event
        setupActionBar()

        // Important: Assign the click event to the image button, otherwise Toast will not be displayed
        mBinding.ivAddDishImage.setOnClickListener(
            this@AddUpdateDishActivity
        )

    }


    // Action for: Add Image (icon)
    override fun onClick(v: View?) {

        // If the view is not NULL
        if (v != null) {


            when (v.id) {

                // When id = Add image icon: #iv_add_dish_image
                R.id.iv_add_dish_image -> {

                    // Display the Toast Message
                    // Toast.makeText(this@AddUpdateDishActivity, "You have clicked on the ImageView.", Toast.LENGTH_SHORT).show()

                    customImageSelectionDialog()

                    //Get back
                    return
                }

            }

        }
    }

    /**
     * A function to launch the custom image selection dialog.
     */
    private fun customImageSelectionDialog() {
        val dialog = Dialog(this@AddUpdateDishActivity)

        // Inflate the layout: dialog_custom_image_selection_binding.xml
        val binding: DialogCustomImageSelectionBinding =
            DialogCustomImageSelectionBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        // onClick: Camera
        binding.tvCamera.setOnClickListener {
            Toast.makeText(
                this@AddUpdateDishActivity,
                "You have clicked on the Camera.",
                Toast.LENGTH_SHORT
            ).show()

            // Close the Dialog after displaying the Toast Message
            dialog.dismiss()
        }

        // onClick: Gallery
        binding.tvGallery.setOnClickListener {
            Toast.makeText(
                this@AddUpdateDishActivity,
                "You have clicked on the Gallery.",
                Toast.LENGTH_SHORT
            ).show()

            // Close the Dialog after displaying the Toast Message
            dialog.dismiss()
        }

        //Start the dialog and display it on screen.
        dialog.show()
    }


    private fun setupActionBar() {

        // Enable Support for the ActionBar
        setSupportActionBar(mBinding.toolbarAddDishActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Assign required action on the click event
        mBinding.toolbarAddDishActivity.setNavigationOnClickListener {
            onBackPressed()
        }

    }


}