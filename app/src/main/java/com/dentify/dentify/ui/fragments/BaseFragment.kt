package com.dentify.dentify.ui.fragments

import android.view.Gravity
import android.view.View
import android.widget.PopupWindow
import android.widget.Spinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.dentify.dentify.R
import com.dentify.dentify.ui.MainActivity
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent

open class BaseFragment: Fragment() {

    lateinit var languageSpinner: Spinner

    fun ifKeyboardIsShown(){
        languageSpinner = requireActivity().findViewById<Spinner>(R.id.spinner_language)
        KeyboardVisibilityEvent.setEventListener(requireActivity()) {
            if (it) {
                languageSpinner.visibility = View.GONE
            } else {
                languageSpinner.visibility = View.VISIBLE
            }
        }
    }

    fun showLanguageSpinner(show: Boolean){
        if (show){
            (activity as MainActivity).showLanguageSpinner()
        }else{
            (activity as MainActivity).hideLanguageSpinner()
        }
    }

    var popup: PopupWindow? = null

    fun showPopup(pView: View, parentView: View){
        popup = PopupWindow(
            pView,
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT
        )
        parentView.post {
            popup!!.showAtLocation(
                parentView, // Location to display popup window
                Gravity.START, // Exact position of layout to display popup
                0, // X offset
                0 // Y offset
            )
            popup!!.isFocusable = true
            popup!!.update()
        }
    }

    fun dismissPopup(){
        if (this.popup != null){
            this.popup!!.dismiss()
        }
    }
}