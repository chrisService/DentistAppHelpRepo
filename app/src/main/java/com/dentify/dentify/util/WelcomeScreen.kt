package com.dentify.dentify.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import com.dentify.dentify.R
import com.dentify.dentify.databinding.PopupWelcomeBinding
import com.dentify.dentify.enum.WelcomeCondition
import com.dentify.dentify.ui.fragments.BaseFragment

object WelcomeScreen {

    fun showWelcomeScreen(context: Context, base: BaseFragment, parent: View){
        val popupView = PopupWelcomeBinding.inflate(base.layoutInflater)
        popupView.tvName.text = StorageWrapper.getPatientsProfileResponse(context)?.patient?.fullName + ","
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            base.dismissPopup() },3000)
        popupView.root.setOnClickListener {
            base.dismissPopup()
        }
        if (StorageWrapper.welcomeCondition == WelcomeCondition.Back.value){
            base.showPopup(popupView.root, parent)
            StorageWrapper.welcomeCondition = WelcomeCondition.None.value
        }else if(StorageWrapper.welcomeCondition == WelcomeCondition.Welcome.value){
            popupView.tvWelcomeText.text = context.getString(R.string.welcome)
            base.showPopup(popupView.root, parent)
            StorageWrapper.welcomeCondition = WelcomeCondition.None.value
        }
    }
}