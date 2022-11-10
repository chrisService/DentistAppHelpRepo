package com.dentify.dentify.ui.fragments.login

import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.dentify.dentify.`interface`.ViewModelApiListener
import com.dentify.dentify.apiModel.request.PostAuthTokenRequest
import com.dentify.dentify.apiModel.response.PostAuthTokenResponse
import com.dentify.dentify.mainViewModel.MainViewModel
import com.dentify.dentify.util.ErrorHandler
import com.dentify.dentify.util.StorageWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LoginRepository
) : MainViewModel(repository) {

    fun postToken(requestBody: PostAuthTokenRequest, apiListener: ViewModelApiListener) {
        apiListener.onStarted()
        viewModelScope.launch {
            val response = repository.postToken(requestBody)
            if (response.code() == 200) {
                val jSonObject = JSONObject(Gson().toJson(response.body()))
                tokenResponse =
                    Gson().fromJson(jSonObject.toString(), PostAuthTokenResponse::class.java)
                StorageWrapper.accessToken = tokenResponse.accessToken
                StorageWrapper.refreshToken = tokenResponse.refreshToken
                apiListener.onSuccess()
            } else {
                ErrorHandler.init(response, apiListener)
            }
        }
    }

}