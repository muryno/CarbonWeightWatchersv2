package com.carbonWeightwatchers.cww.presenter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.carbonWeightwatchers.cww.R
import com.carbonWeightwatchers.cww.core.observe
import com.carbonWeightwatchers.cww.presenter.di.ToastHelper
import com.carbonWeightwatchers.cww.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var toastHelper: ToastHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupActionBarWithNavController(findNavController(R.id.nav_host_fragment))

        toastHelper.toastMessages.observe(this) {
            showToast(it)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}