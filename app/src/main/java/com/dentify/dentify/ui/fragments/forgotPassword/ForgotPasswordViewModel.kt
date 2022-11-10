package com.dentify.dentify.ui.fragments.forgotPassword

import androidx.lifecycle.viewModelScope
import com.dentify.dentify.`interface`.ViewModelApiListener
import com.dentify.dentify.apiModel.request.PostPasswordResetBeginRequest
import com.dentify.dentify.apiModel.request.PostPasswordResetCompleteRequest
import com.dentify.dentify.mainViewModel.MainViewModel
import com.dentify.dentify.util.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val repository: ForgotPasswordRepository
): MainViewModel(repository) {

    fun postResetPassword(requestBody: PostPasswordResetBeginRequest, apiListener: ViewModelApiListener){
        apiListener.onStarted()
        viewModelScope.launch {
            val response = repository.postPasswordResetBegin(requestBody)
            if (response.code() == 200) {
                apiListener.onSuccess()
            }else{
                ErrorHandler.init(response, apiListener)
            }
        }
    }

    fun postResetPasswordComplete(requestBody: PostPasswordResetCompleteRequest, apiListener: ViewModelApiListener){
        apiListener.onStarted()
        viewModelScope.launch {
            val response = repository.postPasswordResetComplete(requestBody)
            if (response.code() == 200) {
                apiListener.onSuccess()
            }else{
                ErrorHandler.init(response, apiListener)
            }
        }
    }
}