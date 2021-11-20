package org.abubaker.favdish.view.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
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
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class AddUpdateDishActivity : AppCompatActivity(),
    View.OnClickListener {

    private lateinit var mBinding: ActivityAddUpdateDishBinding

    // A global variable for stored image path.
    private var mImagePath: String = ""

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

                                // Note: Since startActivityForResult() is depreciated, thus we will use the new approach
                                startForResultLoadFromCamera.launch(intent)
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
                        startForResultLoadFromGallery.launch(galleryIntent)
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

    private val startForResultLoadFromCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->

            // If the RESULT_OK
            if (result.resultCode == Activity.RESULT_OK) {

                try {

                    // Get Selected image's Uri
                    val selectedImage: Uri? = result.data?.data

                    // If Selected image is not NULL
                    if (selectedImage != null) {

                        // Get the bitmap directly from camera
                        result.data?.extras?.let {

                            // Store the reference of captured Bitmap from camera into a thumbnail variable
                            val thumbnail: Bitmap = result.data?.extras?.get("data") as Bitmap

                            // Set Capture Image bitmap to the imageView using Glide
                            Glide.with(this@AddUpdateDishActivity)
                                .load(thumbnail)
                                .centerCrop()
                                .into(mBinding.ivDishImage)

                            // Save the captured image via Camera to the FavDishImages directory and get back the image path.
                            mImagePath = saveImageToInternalStorage(thumbnail)

                            // Print the URI (Path of the file)
                            Log.i("ImagePath", mImagePath)

                            // Replace the add icon with edit icon once the image is loaded.
                            mBinding.ivAddDishImage.setImageDrawable(
                                ContextCompat.getDrawable(this, R.drawable.ic_vector_edit)
                            )
                        }

                    }

                } catch (error: Exception) {

                    // Log Error
                    Log.d("log==>>", "Error : ${error.localizedMessage}")

                }
            }
        }

    private val startForResultLoadFromGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->

            // If the RESULT_OK
            if (result.resultCode == Activity.RESULT_OK) {

                try {

                    // Get Selected image's Uri
                    val selectedImage = result.data?.data

                    // If Selected image is not NULL
                    if (selectedImage != null) {

                        // Get the bitmap directly from GALLERY
                        result.data?.let {

                            // Here we will get the select image URI.
                            val selectedPhotoUri = result.data?.data

                            // Here we will replace the image using Glide as below.
                            // Glide.with(this@AddUpdateDishActivity).load(selectedPhotoUri).centerCrop().into(mBinding.ivDishImage)

                            // Implement the listener to get the bitmap of Image using Glide.
                            // Set Selected Image URI to the imageView using Glide
                            Glide.with(this@AddUpdateDishActivity)
                                .load(selectedPhotoUri)
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)

                                // It will listen for Drawable
                                .listener(object : RequestListener<Drawable> {

                                    /**
                                     * The large amount of parameters inside onLoadFailed() and onResourceReady()
                                     * are auto generated as we are simply overriding the required methods using
                                     * custom code, i.e. to print the File's PATH
                                     */

                                    // Fail:
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

                                    // Success:
                                    override fun onResourceReady(
                                        resource: Drawable,
                                        model: Any?,
                                        target: Target<Drawable>?,
                                        dataSource: DataSource?,
                                        isFirstResource: Boolean
                                    ): Boolean {

                                        // Get the Bitmap and save it to the local storage and get the Image Path.
                                        val bitmap: Bitmap = resource.toBitmap()

                                        mImagePath = saveImageToInternalStorage(bitmap)

                                        // Print the URI (Path of the file)
                                        Log.i("ImagePath", mImagePath)
                                        return false
                                    }


                                })
                                .into(mBinding.ivDishImage)

                            // Replace the add icon with edit icon once the image is loaded.
                            mBinding.ivAddDishImage.setImageDrawable(
                                ContextCompat.getDrawable(this, R.drawable.ic_vector_edit)
                            )
                        }

                    }

                } catch (error: Exception) {

                    // Log Error
                    Log.d("log==>>", "Error : ${error.localizedMessage}")

                }
            }
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

    // START
    /**
     * A function to save a copy of an image to internal storage for FavDishApp to use.
     * It will get a bitmap but a String will be returned.
     * @param bitmap
     */
    private fun saveImageToInternalStorage(bitmap: Bitmap): String {

        // Get the context wrapper instance
        val wrapper = ContextWrapper(applicationContext)

        // Initializing a new file
        // The bellow line return a directory in internal storage

        /**
         * The Mode Private here is
         * File creation mode: the default mode, where the created file can only
         * be accessed by the calling application (or all applications sharing the
         * same user ID).
         *
         * IMAGE_DIRECTORY = FavDishImages defined in the "companion object" block
         *
         */
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)

        // Store using a random file name to save the image in .jpg format
        file = File(file, "${UUID.randomUUID()}.jpg")

        // Precaution
        try {

            // Get the file output stream
            val stream: OutputStream = FileOutputStream(file)

            // Compress bitmap while maintaining 100% image Quality
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            // Flush the stream (because we are done with the compression method,
            // otherwise stream will remain open all the time
            stream.flush()

            // Close stream
            stream.close()

        } catch (e: IOException) {

            // Print the caught IOException
            e.printStackTrace()

        }

        // Return the saved image's absolute path
        return file.absolutePath

    }

    // Companion Objects
    companion object {
        private const val CAMERA = 1
        private const val GALLERY = 2
        private const val IMAGE_DIRECTORY = "FavDishImages"
    }

}