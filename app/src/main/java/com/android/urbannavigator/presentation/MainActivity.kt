package com.android.urbannavigator.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.android.urbannavigator.R
import com.android.urbannavigator.data.model.User
import com.android.urbannavigator.databinding.ActivityMainBinding
import com.android.urbannavigator.presentation.profile.ProfileViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val mainViewModel: MainViewModel by viewModels()

    var currentUser: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment, R.id.parkFragment, R.id.eventFragment, R.id.communityFragment, R.id.profileFragment
            )
        )
        navView.itemIconTintList = null
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        initViewModelObserver()
    }

    private fun initViewModelObserver() {
        val loadingDialog = LoadingDialog(this)

        mainViewModel.currentUser.observe(this, Observer { user ->
            currentUser = user
        })

        mainViewModel.loading.observe(this, Observer { isLoading ->
            if (isLoading) {
                loadingDialog.startLoadingDialog()
            } else {
                loadingDialog.dismissDialog()
            }
        })

        mainViewModel.errorMsg.observe(this, Observer { errorMsg ->
            Toast.makeText(this@MainActivity, errorMsg, Toast.LENGTH_SHORT).show()
        })

        mainViewModel.errorResetApp.observe(this){
            if(it){
               restartResetAuthApp()
            }
        }
    }

    fun restartResetAuthApp(){
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}