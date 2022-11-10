package com.dentify.dentify.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dentify.dentify.R

object LoadingScreen {

    fun loadingScreen(context: Context, container: ViewGroup): View{
        val inflater: LayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return  inflater.inflate(R.layout.loading_screen, container, false)
    }
}