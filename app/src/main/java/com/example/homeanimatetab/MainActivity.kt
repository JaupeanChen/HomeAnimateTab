package com.example.homeanimatetab

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.homeanimatetab.databinding.ActivityMainBinding
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        binding.fab.setOnClickListener {
            val animator = ObjectAnimator.ofFloat(
                binding.fab,
                "translationY",
                0f,
                -binding.fab.height.toFloat() + 30f,
                0f
            ).apply {
                duration = 3000
                repeatMode = ValueAnimator.REVERSE
                addUpdateListener {
                    binding.navView.updateDistance(abs(it.animatedValue as Float))
                }
                addListener(doOnEnd {

                })
            }
            animator.start()
        }
    }
}