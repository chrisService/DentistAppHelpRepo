package com.dentify.dentify.ui.fragments.videoCall

import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.dentify.dentify.`interface`.ViewModelApiListener
import com.dentify.dentify.apiModel.response.TwilioTokenResponse
import com.dentify.dentify.mainViewModel.MainViewModel
import com.dentify.dentify.repository.MainRepository
import com.dentify.dentify.util.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class VideoCallViewModel @Inject constructor(
    private val repository: MainRepository
) : MainViewModel(repository) {

    lateinit var twilioTokenResponse: String

    fun getTwilioToken(apiListener: ViewModelApiListener) {

        apiListener.onStarted()
        viewModelScope.launch {
            val response = repository.getTwilioToken()
            if (response.code() == 200) {
                val jSonObject = JSONObject(Gson().toJson(response.body()))
                twilioTokenResponse = Gson().fromJson(
                    jSonObject.toString(),
                    TwilioTokenResponse::class.java
                ).twilioToken!!
                apiListener.onSuccess()
            } else {
                ErrorHandler.init(response, apiListener)
            }
        }
    }

}