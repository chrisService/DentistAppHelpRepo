package com.dentify.dentify

import android.app.Application
import android.content.Context
import com.dentify.dentify.util.Constants.SWEDISH
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication: Application() {

    override fun onCreate() {
        super.onCreate()
       instance = this
    }

    fun setLanguagePref(localeKey: String){
        val appPrefs = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE).edit()
        appPrefs.putString(APP_LOCALE, localeKey)
        appPrefs.apply()
    }

    fun getLanguagePref(): String?{
        val appPrefs = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        return appPrefs.getString(APP_LOCALE, SWEDISH)
    }

    companion object{
        var instance: BaseApplication? = null
        const val APP_PREFERENCES: String = "APP_PREFERENCES"
        const val APP_LOCALE: String = "APP_LOCALE"
    }
}