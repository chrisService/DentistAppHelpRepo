package com.dentify.dentify.ui

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.messaging.FirebaseMessaging
import com.dentify.dentify.R
import com.dentify.dentify.databinding.ActivityMainBinding
import com.dentify.dentify.ui.fragments.login.LoginWithEmailFragment
import com.dentify.dentify.ui.fragments.profile.LanguageAdapter
import com.dentify.dentify.util.CheckNetworkConnection
import com.dentify.dentify.util.Constants
import com.dentify.dentify.util.Constants.ENGLISH
import com.dentify.dentify.util.Constants.SWEDISH
import com.dentify.dentify.util.Constants.getSupportedLanguages
import com.dentify.dentify.util.LocaleManager
import com.dentify.dentify.util.StorageWrapper
import com.dentify.dentify.util.StorageWrapper.selectedLocale
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var checkNetworkConnection: CheckNetworkConnection
    private lateinit var manager: ReviewManager
    private lateinit var reviewInfo: ReviewInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        StorageWrapper.initialize(this)
        super.onCreate(savedInstanceState)
        saveFCMtoken()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initNavHostFragment()
        binding.bottomNavigationView.setupWithNavController(navController)
        setSpinner()
    }


    private fun saveFCMtoken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if(StorageWrapper.fcmTokken.isNullOrEmpty()){
                if (it.isComplete){
                    val fcmToken = it.result
                    StorageWrapper.fcmTokken = fcmToken
                }
            }
        }
    }

    fun appReview(){
        manager = ReviewManagerFactory.create(this)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                reviewInfo = task.result
            }else if (task.isCanceled){
                reviewInfo = task.result
            }else if (task.isComplete){
                reviewInfo = task.result
            }
        }
    }

    fun startReviewFlow(){
        val flow = manager.launchReviewFlow(this, reviewInfo)
        flow.addOnCompleteListener { _ ->
        }
        flow.addOnCanceledListener {
        }
    }

    private fun findCurrentVisibleFragment(): Fragment? {
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        return navHost?.childFragmentManager?.primaryNavigationFragment as? Fragment?
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        (findCurrentVisibleFragment() as? LoginWithEmailFragment)?.setHintsProperLanguage()
    }

    private fun setSpinner() {
        binding.spinnerLanguage.adapter = LanguageAdapter(this, getSupportedLanguages())

        when (selectedLocale) {
            ENGLISH -> {
                binding.spinnerLanguage.setSelection(1)
                selectedLocale = ENGLISH
            }
            SWEDISH -> {
                binding.spinnerLanguage.setSelection(0)
                selectedLocale = SWEDISH
            }
        }
        binding.spinnerLanguage.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position == 1 && resources.configuration.locales.get(0) != Locale(ENGLISH)) {
                        LocaleManager.setNewLocale(this@MainActivity, ENGLISH)
                        selectedLocale = ENGLISH
                        recreate()
                    } else if (position == 0  && resources.configuration.locales.get(0) != Locale(SWEDISH)) {
                        LocaleManager.setNewLocale(this@MainActivity, SWEDISH)
                        selectedLocale = SWEDISH
                        recreate()
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(LocaleManager.setLocale(newBase!!))
    }

    private fun initNavHostFragment() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.homeFragment ||
                destination.id == R.id.appointmentsFragment ||
                destination.id == R.id.supportFragment ||
                destination.id == R.id.profileFragment
            ) {
                binding.bottomNavigationView.visibility = View.VISIBLE
            } else {
                binding.bottomNavigationView.visibility = View.GONE
            }
        }
    }

    fun showLanguageSpinner(){
        binding.spinnerLanguage.visibility = View.VISIBLE
    }

    fun hideLanguageSpinner(){
        binding.spinnerLanguage.visibility = View.GONE
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setNavGraph(intent)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        overridePendingTransition(0, 0)
        binding = ActivityMainBinding.inflate(layoutInflater)
    }

    private fun setNavGraph(intent: Intent?) {
        if (intent?.data != null) {
            if (intent.data?.getQueryParameter(Constants.SET_NEW_PASSWORD_QUERY) != null) {
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.loginFragment, true)
                    .build()
                val bundle = Bundle()
                bundle.putString(Constants.SET_NEW_PASSWORD_QUERY, intent.data!!.getQueryParameter(Constants.SET_NEW_PASSWORD_QUERY))
                bundle.putString(Constants.ID, intent.data!!.getQueryParameter(Constants.ID))
                navController.navigate(R.id.setNewPasswordFragment, bundle, navOptions)
            } else if (intent.data?.getQueryParameter(Constants.ID) != null) {
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.loginFragment, true)
                    .build()
                val bundle = Bundle()
                bundle.putString(Constants.ID, intent.data!!.getQueryParameter(Constants.ID))
                navController.navigate(R.id.verifyPersonalNumberFragment, bundle, navOptions)
            } else if (intent.data?.getQueryParameter(Constants.OPEN) != null) {
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.loginFragment, true)
                    .build()
                navController.navigate(R.id.signUpFragment, null, navOptions)
            } else if (intent.data?.getQueryParameter(Constants.ROOM_ID) != null) {
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.appointmentDetailsFragment, false)
                    .build()
                val bundle = Bundle()
                bundle.putString(
                    Constants.ROOM_ID,
                    intent.data!!.getQueryParameter(Constants.ROOM_ID)
                )
                navController.navigate(R.id.videoCallFragment, bundle, navOptions)
            } else if(intent.data?.getQueryParameter(Constants.APPOINTMENT_ID) != null){
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.homeFragment, false)
                    .build()
                val bundle = Bundle()
                bundle.putString(
                    Constants.APPOINTMENT_ID,
                    intent.data!!.getQueryParameter(Constants.APPOINTMENT_ID)
                )
                navController.navigate(R.id.appointmentDetailsFragment, bundle, navOptions)
            }else if(intent.data?.getQueryParameter(Constants.REQUEST_ID) != null){
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.homeFragment, false)
                    .build()
                val bundle = Bundle()
                bundle.putString(
                    Constants.REQUEST_ID,
                    intent.data!!.getQueryParameter(Constants.REQUEST_ID)
                )
                navController.navigate(R.id.requestDetailsFragment, bundle, navOptions)
            } else if (intent.data?.getQueryParameter(Constants.BANK_ID) != null) {
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.loginFragment, false)
                    .build()
                navController.navigate(R.id.loginFragment, null, navOptions)
            }
        }
    }

    //TODO VJ: Do we need this function?
    private fun callNetworkConnection() {
        checkNetworkConnection = CheckNetworkConnection(application)
        checkNetworkConnection.observe(this) { isConnected ->
            if (isConnected) {
                Snackbar.make(binding.root, "Connection Restored", Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(binding.root, "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                    .show()
            }
        }
    }
}
