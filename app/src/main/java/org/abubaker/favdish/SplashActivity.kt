package org.abubaker.favdish

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import org.abubaker.favdish.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Setup ViewBinding for our activity
        val splashBinding: ActivitySplashBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(splashBinding.root)

        // Full screen for newer versions SDK >= API 30
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {

            // Ignore depreciated functions
            // Useful for older versions
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )

        }

        /**
         * Animation
         */
        val splashAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_splash)
        splashBinding.tvAppName.animation = splashAnimation

        /**
         * Navigate to the MainActivity once the animation will be completed
         */
        splashAnimation.setAnimationListener(object : Animation.AnimationListener {

            // We have 3 EVENTS when we can execute the code:
            // 1. onAnimationStart()
            // 2. onAnimationEnd()
            // 3. onAnimationRepeat()

            // Start
            override fun onAnimationStart(animation: Animation?) {}

            // End
            override fun onAnimationEnd(animation: Animation?) {

                // Once the animation completes we will navigate it to the Main Activity with delay 1 second using Handler.
                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                }, 1000)
            }

            // Repeat
            override fun onAnimationRepeat(animation: Animation?) {}

        })

    }
}