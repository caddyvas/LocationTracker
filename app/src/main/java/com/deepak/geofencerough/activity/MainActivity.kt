package com.deepak.geofencerough.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.fragment.app.Fragment
import com.deepak.geofencerough.R
import com.deepak.geofencerough.ShoppingList
import com.deepak.geofencerough.databinding.ActivityMainBinding
import com.deepak.geofencerough.fragments.ItemsListFragment
import com.deepak.geofencerough.getMyShoppingListTheme
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        activityMainBinding.bottomNavigationView.background = null


        activityMainBinding.bottomNavigationView.setOnItemSelectedListener { item ->

            when(item.itemId) {
                R.id.ic_items -> {
                    replaceFragment(ItemsListFragment())
                }

                R.id.ic_map -> {

                }

                R.id.ic_places -> {

                }

                
            }

            return@setOnItemSelectedListener true
        }

        /*// function to set the content of a compose-based screen in an app. Used to declare the UI of
        // a compose screen and it takes a lambda expression that contains the Compose UI code.
        setContent {
            getMyShoppingListTheme {
                // Surface is a basic building block for displaying content and can be used to wrap other
                // composable to provide a background color, elevation, padding, and other layout properties
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ShoppingList(this@MainActivity)
                }
        }*/
    }
    /**
     * Method that replaces fragment based on user's selection in the bottom navigation
     */
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}