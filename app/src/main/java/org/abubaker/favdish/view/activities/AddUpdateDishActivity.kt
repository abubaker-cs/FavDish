package org.abubaker.favdish.view.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
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

                    // Initialize our custom Dialog so the user can pick image from Gallery/Camera
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

            // Ask for the permission while selecting the image from camera using Dexter Library.
            // And Remove the toast message.
            Dexter.withContext(this@AddUpdateDishActivity)

                // Required permissions: Read / Write External Storage + Camera
                .withPermissions(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    // Newer device do not require following code ( API > 30 )
                    // Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )

                // Multiple Permissions Listener
                .withListener(object : MultiplePermissionsListener {

                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {

                        // Safeguard: Execute only if the report is not EMPTY
                        report?.let {

                            // Verification: Here after all the permission are granted launch the CAMERA to capture an image.
                            if (report.areAllPermissionsGranted()) {

                                // Open the CAMERA: Start camera using the Image capture action.
                                // Get the result in the onActivityResult method as we are using startActivityForResult.
                                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                startForResultToLoadImage.launch(intent)
                                // startActivityForResult(intent, CAMERA)

                            }

                        }

                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        // Show the alert dialog
                        showRationalDialogForPermissions()

                    }

                }).onSameThread().check()

            // Close the Dialog after displaying the Toast Message
            dialog.dismiss()
        }

        // onClick: Gallery
        binding.tvGallery.setOnClickListener {

            // Ask for the permission while selecting the image from Gallery using Dexter Library. And Remove the toast message.
            Dexter.withContext(this@AddUpdateDishActivity)

                // Required permissions: Read External Storage
                .withPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                )

                // Single Permission Listener
                .withListener(object : PermissionListener {

                    // If all permissions were granted
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {

                        // Here after all the permission are granted launch the gallery to select and image.
                        val galleryIntent = Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )

                        //  startActivityForResult(galleryIntent, GALLERY)
                    }

                    // If the permission was denied
                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {

                        Toast.makeText(
                            this@AddUpdateDishActivity,
                            "You have denied the storage permission to select image.",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                    // Inform the user that he did not activated the required permission
                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest?,
                        token: PermissionToken?
                    ) {
                        showRationalDialogForPermissions()
                    }

                }).onSameThread().check()

            // Close the Dialog after displaying the Toast Message
            dialog.dismiss()
        }

        //Start the dialog and display it on screen.
        dialog.show()
    }

    private val startForResultToLoadImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->

            if (result.resultCode == Activity.RESULT_OK) {

                try {

                    val selectedImage: Uri? = result.data?.data
                    if (selectedImage != null) {
                        mBinding.ivDishImage.setImageURI(selectedImage)

                    } else {

                        // From Camera code goes here.
                        // Get the bitmap directly from camera
                        result.data?.extras?.let {
                            val bitmap: Bitmap = result.data?.extras?.get("data") as Bitmap
                            mBinding.ivDishImage.setImageBitmap(bitmap)

                            mBinding.ivAddDishImage.setImageDrawable(
                                ContextCompat.getDrawable(
                                    this,
                                    R.drawable.ic_vector_edit
                                )
                            )
                        }

                    }
                } catch (error: Exception) {

                    Log.d("log==>>", "Error : ${error.localizedMessage}")

                }
            }
        }

//    /**
//     * Receive the result from a previous call to
//     * {@link #startActivityForResult(Intent, int)}.  This follows the
//     * related Activity API as described there in
//     * {@link Activity#onActivityResult(int, int, Intent)}.
//     *
//     * @param requestCode The integer request code originally supplied to
//     *                    startActivityForResult(), allowing you to identify who this
//     *                    result came from.
//     * @param resultCode The integer result code returned by the child activity
//     *                   through its setResult().
//     * @param data An Intent, which can return result data to the caller
//     *               (various data can be attached to Intent "extras").
//     */
//    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK) {
//
//            // CAMERA
//            if (requestCode == CAMERA) {
//
//                data?.extras?.let {
//
//                    // Bitmap from camera
//                    val thumbnail: Bitmap = data.extras!!.get("data") as Bitmap
//
//                    // Here we will replace the setImageBitmap using glide as below.
//                    // mBinding.ivDishImage.setImageBitmap(thumbnail) // Set to the imageView.
//
//                    // Set Capture Image bitmap to the imageView using Glide
//                    Glide.with(this@AddUpdateDishActivity)
//                        .load(thumbnail)
//                        .centerCrop()
//                        .into(mBinding.ivDishImage)
//
//                    // Replace the add icon with edit icon once the image is loaded.
//                    mBinding.ivAddDishImage.setImageDrawable(
//                        ContextCompat.getDrawable(
//                            this@AddUpdateDishActivity,
//                            R.drawable.ic_vector_edit
//                        )
//                    )
//                }
//            }
//
//            // GALLERY: Get the selected image from gallery. The selected will be in form of URI so set it to the Dish ImageView.
//            else if (requestCode == GALLERY) {
//
//                data?.let {
//                    // Here we will get the select image URI.
//                    val selectedPhotoUri = data.data
//
//                    // Here we will replace the setImageURI using Glide as below.
//                    // mBinding.ivDishImage.setImageURI(selectedPhotoUri) // Set the selected image from GALLERY to imageView.
//
//                    // Set Selected Image bitmap to the imageView using Glide
//                    Glide.with(this@AddUpdateDishActivity)
//                        .load(selectedPhotoUri)
//                        .centerCrop()
//                        .into(mBinding.ivDishImage)
//
//                    // Replace the add icon with edit icon once the image is selected.
//                    mBinding.ivAddDishImage.setImageDrawable(
//                        ContextCompat.getDrawable(
//                            this@AddUpdateDishActivity,
//                            R.drawable.ic_vector_edit
//                        )
//                    )
//                }
//            }
//
//
//        } else if (resultCode == Activity.RESULT_CANCELED) {
//
//            // If Cancelled
//            Log.e("Cancelled", "Cancelled")
//
//        }
//    }


    private fun setupActionBar() {

        // Enable Support for the ActionBar
        setSupportActionBar(mBinding.toolbarAddDishActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Assign required action on the click event
        mBinding.toolbarAddDishActivity.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    /**
     * A function used to show the alert dialog when the permissions are denied and need to allow it from settings app info.
     */
    private fun showRationalDialogForPermissions() {

        // Dialog Parameters
        AlertDialog.Builder(this)

            // Content of the Alert Message
            .setMessage("It Looks like you have turned off permissions required for this feature. It can be enabled under Application Settings")

            // Define Button's Label + Action
            .setPositiveButton(
                "GO TO SETTINGS"
            ) { _, _ ->

                // Try | Catch
                try {

                    // Defined the parameters for the required intent
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)

                    // Fetch the Uri: Go to the settings of our package
                    val uri = Uri.fromParts("package", packageName, null)

                    // Add the DATA to the intent
                    intent.data = uri

                    // Initialize the intent
                    startActivity(intent)

                } catch (e: ActivityNotFoundException) {

                    // Catch and print Error
                    e.printStackTrace()

                }

            }

            .setNegativeButton("Cancel") { dialog, _ ->

                // Close the Dialog
                dialog.dismiss()

            }.show()

    }

    // Companion Objects for CAMERA + GALLERY
    companion object {
        private const val CAMERA = 1
        private const val GALLERY = 2
    }

}