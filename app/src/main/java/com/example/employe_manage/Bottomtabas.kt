package com.example.employe_manage

import android.os.Bundle
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.employe_manage.databinding.ActivityBottomtabasBinding
import com.example.employe_manage.fragments.HomeFragment
import com.example.employe_manage.fragments.UploadFragment
import com.example.employe_manage.fragments.UsersFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class Bottomtabas : AppCompatActivity() {


    lateinit var bottomNav : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottomtabas)
        loadFragment(HomeFragment())
        bottomNav = findViewById(R.id.bottomNav) as BottomNavigationView
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.message -> {
                    loadFragment(UsersFragment())
                    true
                }
                R.id.upload -> {
                    loadFragment(UploadFragment())
                    true
                }

                else ->false
            }
        }
    }
    private  fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.commit()
    }

}