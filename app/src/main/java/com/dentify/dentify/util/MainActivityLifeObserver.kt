package com.dentify.dentify.util

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

class MainActivityLifeObserver : LifecycleEventObserver {

    private val TAG = this.javaClass.simpleName

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event){
            Lifecycle.Event.ON_ANY -> {Log.d(TAG, "observer test onStateChanged: ON_ANY")}
            Lifecycle.Event.ON_CREATE -> Log.d(TAG, "observer test onStateChanged: ON_CREATE")
            Lifecycle.Event.ON_START -> Log.d(TAG, "observer test onStateChanged: ON_START")
            Lifecycle.Event.ON_RESUME -> Log.d(TAG, "observer test onStateChanged: ON_RESUME")
            Lifecycle.Event.ON_PAUSE -> Log.d(TAG, "observer test onStateChanged: ON_PAUSE")
            Lifecycle.Event.ON_STOP -> Log.d(TAG, "observer test onStateChanged: ON_STOP")
            Lifecycle.Event.ON_DESTROY -> Log.d(TAG, "observer test onStateChanged: ON_DESTROY")
        }
    }

}