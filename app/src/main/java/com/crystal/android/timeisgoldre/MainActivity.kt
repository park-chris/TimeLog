package com.crystal.android.timeisgoldre

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.crystal.android.timeisgoldre.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        _binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setNavigation()

    }

    // navigation settings
    private fun setNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val navigator =
            KeepStateFragment(this, navHostFragment.childFragmentManager, R.id.nav_host_fragment)

        navController.navigatorProvider.addNavigator(navigator)
        navController.setGraph(R.navigation.main_nav_graph)

        binding.mainNavi.setupWithNavController(navController)


/*
        binding.mainNavi.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.timer_fragment -> {
                    navController.navigate(R.id.timer_fragment)
                    true
                }

                R.id.history_fragment -> {
                    navController.navigate(R.id.history_fragment)
                    true
                }

                R.id.monitoring_fragment -> {
                    navController.navigate(R.id.monitoring_fragment)
                    true
                }

                R.id.settings_fragment -> {
                    navController.navigate(R.id.settings_fragment)
                    true
                }

                else -> false
            }
        }
*/

    }
}