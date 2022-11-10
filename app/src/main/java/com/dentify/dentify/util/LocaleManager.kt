package com.dentify.dentify.util

import android.content.Context
import android.content.res.Configuration
import com.dentify.dentify.BaseApplication
import java.util.*

object LocaleManager {

    fun setLocale(mContext: Context): Context{
        return if(BaseApplication.instance!!.getLanguagePref() != null){
            updateResources(mContext, BaseApplication.instance!!.getLanguagePref()!!)
        }else{
            mContext
        }
    }

    fun setNewLocale(mContext: Context, language: String): Context{
        BaseApplication.instance!!.setLanguagePref(language)
        return updateResources(mContext, language)
    }

    private fun updateResources(context: Context, language: String): Context{
        var localContext: Context = context
        val locale = Locale(language)
        Locale.setDefault(locale)
        val res = context.resources
        val config = Configuration(res.configuration)
        config.setLocale(locale)
        localContext = context.createConfigurationContext(config)
        return localContext
    }
}