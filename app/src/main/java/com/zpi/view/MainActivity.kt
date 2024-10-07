package com.zpi.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.zpi.viewmodel.MainViewModel
import com.zpi.R
import com.zpi.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        mainViewModel.setLoggedIn((intent.extras?.get("userREF") as Int))
        mainViewModel.setFirstLogin((intent.extras?.get("firstLogin") as Int))

        val navView: BottomNavigationView = binding.navView

        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    findNavController(R.id.nav_host_fragment_activity_main).navigate(R.id.action_global_navigation_home)
                    true
                }
                R.id.navigation_community -> {
                    findNavController(R.id.nav_host_fragment_activity_main).navigate(R.id.action_global_navigation_community)
                    true
                }
                R.id.navigation_game -> {
                    findNavController(R.id.nav_host_fragment_activity_main).navigate(R.id.action_global_navigation_game)
                    true
                }
                R.id.navigation_profile -> {
                    findNavController(R.id.nav_host_fragment_activity_main).navigate(R.id.action_global_navigation_profile)
                    true
                }
                else -> false
            }
        }
    }

}
