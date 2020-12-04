package com.emrekalkan.bitcointicker.ui

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.emrekalkan.bitcointicker.R
import com.emrekalkan.bitcointicker.core.BaseActivity
import com.emrekalkan.bitcointicker.databinding.ActivityMainBinding
import com.emrekalkan.bitcointicker.utils.ConnectivityReceiver
import com.emrekalkan.bitcointicker.utils.views.LoadingDialog
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>(), ConnectivityReceiver.ConnectivityListener {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var loadingDialog: LoadingDialog

    private var connectivityReceiver = ConnectivityReceiver()
    private var mSnackbar: Snackbar? = null

    var isNetworkAvailable: Boolean = false

    override val viewModel: MainViewModel by viewModels()

    override fun getLayoutRes(): Int = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupNavigation()
        handleDisplayHomeAsUp()
        registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        loadingDialog = LoadingDialog()
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityListener = this
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(connectivityReceiver)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    override fun onNetworkStateChanged(isConnected: Boolean) {
        isNetworkAvailable = if (!isConnected) {
            showSnackBar("Limited or unavailable internet connection!", Snackbar.LENGTH_INDEFINITE)
            false
        } else {
            mSnackbar?.dismiss()
            true
        }
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun handleDisplayHomeAsUp() {
        findNavController(R.id.main_nav_host_fragment).addOnDestinationChangedListener { controller, _, _ ->
            supportActionBar?.setDisplayHomeAsUpEnabled(controller.previousBackStackEntry != null)
        }
    }

    fun updateLoadingDialog(loading: Boolean) {
        when (loading) {
            true -> {
                if (loadingDialog.isAdded.not()) {
                    loadingDialog.show(supportFragmentManager, LoadingDialog.TAG)
                }
            }
            false -> loadingDialog.dismiss()
        }
    }

    fun showSnackBar(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
        if (mSnackbar?.isShown == true && mSnackbar?.duration == Snackbar.LENGTH_INDEFINITE) {
            return
        }
        mSnackbar?.dismiss()
        mSnackbar = Snackbar.make(binding.root, message, duration)
        mSnackbar?.show()
    }
}