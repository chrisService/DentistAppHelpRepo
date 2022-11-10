package com.dentify.dentify.reciever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dentify.dentify.`interface`.SuccessListener

class DentifyRequestReceiver(val listener: SuccessListener) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        listener.onSuccess()
    }
}