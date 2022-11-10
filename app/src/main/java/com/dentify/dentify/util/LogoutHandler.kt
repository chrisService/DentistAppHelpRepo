package com.dentify.dentify.util

import android.content.Context
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.dentify.dentify.R
import com.dentify.dentify.`interface`.ViewModelApiListener
import com.dentify.dentify.mainViewModel.MainViewModel

object LogoutHandler {

    fun unsubscribeAndLogout(context: Context, viewModel: MainViewModel, navigationController: NavController){
        viewModel.deleteDevice(object: ViewModelApiListener {
            override fun onStarted(message: String?) {
            }
            override fun onSuccess(message: String?) {
                StorageWrapper.clearData(context)
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.homeFragment, true)
                    .build()
                navigationController.navigate(R.id.loginFragment, null, navOptions)
            }
            override fun onFailure(message: String?) {
                Log.e("ApiFailed", "deleteDevice: " + message)
                StorageWrapper.clearData(context)
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.homeFragment, true)
                    .build()
                navigationController.navigate(R.id.loginFragment, null, navOptions)
            }
        })
    }
}