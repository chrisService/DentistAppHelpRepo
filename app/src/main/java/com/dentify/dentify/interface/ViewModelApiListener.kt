package com.dentify.dentify.`interface`

interface ViewModelApiListener {
    fun onStarted(message : String? = null)
    fun onSuccess(message : String? = null)
    fun onFailure(message : String? = null)
}