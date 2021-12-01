package org.abubaker.favdish.view.activities

import android.os.Bundle
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
    private lateinit var mNavController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(

            setOf(
                R.id.navigation_all_dishes,
                R.id.navigation_favorite_dishes,
                R.id.navigation_random_dish
            )

        )

        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.navView.setupWithNavController(navController)
    }

    // Override the onSupportNavigateUp method.
    override fun onSupportNavigateUp(): Boolean {

        // Add the navigate up code and pass the required params. This will navigate the user from
        // DishDetailsFragment to AllDishesFragment when user clicks on the home back button.
        return NavigationUI.navigateUp(mNavController, null)
    }

    // TODO Step 7: Create a function to hide the BottomNavigationView with animation.
    fun hideBottomNavigationView() {
        binding.navView.clearAnimation()
        binding.navView.animate().translationY(binding.navView.height.toFloat()).duration = 300
    }

    // TODO Step 8: Create a function to show the BottomNavigationView with Animation.
    fun showBottomNavigationView() {
        binding.navView.clearAnimation()
        binding.navView.animate().translationY(0f).duration = 300
    }

}