package org.abubaker.favdish

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.abubaker.favdish.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Setup ViewBinding for our activity
        val splashBinding: ActivitySplashBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(splashBinding.root)

        splashBinding.tvAppName.text = "Hello World"

    }
}