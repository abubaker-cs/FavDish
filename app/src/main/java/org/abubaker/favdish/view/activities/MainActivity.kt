package org.abubaker.favdish.view.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.*
import org.abubaker.favdish.R
import org.abubaker.favdish.databinding.ActivityMainBinding
import org.abubaker.favdish.model.notification.NotifyWorker
import org.abubaker.favdish.utils.Constants
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Make the navController variable as global variable.
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.nav_host_fragment)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(

            // Prepare items for the bottom menu
            setOf(
                R.id.navigation_all_dishes,
                R.id.navigation_favorite_dishes,
                R.id.navigation_random_dish
            )

        )

        // Setup ActionBar with the above defined appBarConfiguration
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Setup NavController
        binding.navView.setupWithNavController(navController)

        // Handle the Notification when user clicks on it.
        if (intent.hasExtra(Constants.NOTIFICATION_ID)) {
            val notificationId = intent.getIntExtra(Constants.NOTIFICATION_ID, 0)
            Log.i("Notification Id", "$notificationId")

            binding.navView.selectedItemId = R.id.navigation_random_dish
        }

        // Call the function to initialize the work request.
        startWork()
    }

    /**
     * onSupportNavigateUp()
     */
    override fun onSupportNavigateUp(): Boolean {

        // Add the navigate up code and pass the required params. This will navigate the user from
        // DishDetailsFragment to AllDishesFragment when user clicks on the home back button.
        return NavigationUI.navigateUp(navController, null)

    }

    /**
     * hideBottomNavigationView() - It will hide the BottomNavigationView with animation.
     */
    fun hideBottomNavigationView() {

        // If there are any animations running, then get rid of them.
        binding.navView.clearAnimation()

        // Y-Axis: The animation will move the object vertically
        binding.navView.animate().translationY(

            // We will be animating the height of the bottom menu
            binding.navView.height.toFloat()

        ).duration = 300

        // navView = GONE (removed from the UI) because Invisible will just change the transparency.
        binding.navView.visibility = View.GONE

    }

    /**
     * showBottomNavigationView() - Show the BottomNavigationView with Animation.
     */
    fun showBottomNavigationView() {

        // If there are any animations running, then get rid of them.
        binding.navView.clearAnimation()

        // Y-Axis: The animation will move the object vertically
        binding.navView.animate().translationY(

            //
            0f

        ).duration = 300

        // navView = Visible
        binding.navView.visibility = View.VISIBLE
    }

    /**
     * Constraints ensure that work is deferred until optimal conditions are met.
     *
     * A specification of the requirements that need to be met before a WorkRequest can run.
     * By default, WorkRequests do not have any requirements and can run immediately.
     * By adding requirements, you can make sure that work only runs in certain situations
     * - for example, when you have an unmetered network and are charging.
     */
    // For more details visit the link https://medium.com/androiddevelopers/introducing-workmanager-2083bcfc4712
    private fun createConstraints() = Constraints.Builder()

        // Can work on any network type, i.e. Cellular/WiFi Network
        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)

        // Can execute without the device being charged
        .setRequiresCharging(false)

        // Battery Level must be > 20+
        .setRequiresBatteryNotLow(true)

        // Build constraints, so they can be reused
        .build()

    /**
     * Periodic Work Request Builder (With 15min of delay)
     *
     * You can use any of the work request builder that are available to use.
     * We will you the PeriodicWorkRequestBuilder as we want to execute the code periodically.
     *
     * The minimum time you can set is 15 minutes. You can check the same on the below link.
     * https://developer.android.com/reference/androidx/work/PeriodicWorkRequest
     *
     * You can also set the TimeUnit as per your requirement. for example SECONDS, MINUTES, or HOURS.
     */
    private fun createWorkRequest() = PeriodicWorkRequestBuilder<NotifyWorker>(15, TimeUnit.MINUTES)
        .setConstraints(createConstraints())
        .build()

    // Create a function to startWork and pass the required params as below.
    private fun startWork() {

        /* enqueue a work, ExistingPeriodicWorkPolicy.KEEP means that if this work already exists, it will be kept
        if the value is ExistingPeriodicWorkPolicy.REPLACE, then the work will be replaced */
        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "FavDish Notify Work",
                ExistingPeriodicWorkPolicy.KEEP,
                createWorkRequest() // Pass the WorkRequest
            )
    }

}