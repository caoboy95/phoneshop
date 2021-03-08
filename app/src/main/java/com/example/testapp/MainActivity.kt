package com.example.testapp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.testapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    val STATE = "Trạng Thái"
    private lateinit var appBarConfiguration : AppBarConfiguration
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this, R.layout.activity_main)
        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration= AppBarConfiguration(setOf(R.id.profileFragment,R.id.billFragment,R.id.profileFragment,R.id.settingFragment))
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        binding.bottomNavView.setupWithNavController(navController)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(false)
//        binding.bottomNavView.setOnNavigationItemSelectedListener { item->
//            NavigationUI.onNavDestinationSelected(item, navController)
//            getSupportActionBar()?.setDisplayHomeAsUpEnabled(false);
//            false
//        }
//        getSupportActionBar()?.setDisplayHomeAsUpEnabled(false);
//        val navOptions = NavOptions.Builder()
//            .setEnterAnim(R.anim.from_right)
//            .setPopExitAnim(R.anim.exit_right)
//            .setPopEnterAnim(R.anim.from_left)
//            .setExitAnim(R.anim.exit_left)
//            .setLaunchSingleTop(true)
//            .build()

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}