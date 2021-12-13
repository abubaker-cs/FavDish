package org.abubaker.favdish.view.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import org.abubaker.favdish.R
import org.abubaker.favdish.databinding.ActivityMainBinding

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

        // navView = Invisible
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

}