package com.dentify.dentify.ui.fragments.settings

import androidx.lifecycle.viewModelScope
import com.dentify.dentify.`interface`.ViewModelApiListener
import com.dentify.dentify.apiModel.request.PostDeactivationRequest
import com.dentify.dentify.mainViewModel.MainViewModel
import com.dentify.dentify.util.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository
) : MainViewModel(repository) {

    fun postDeactivation(requestBody: PostDeactivationRequest, apiListener: ViewModelApiListener) {
        apiListener.onStarted()
        viewModelScope.launch {
            val response = repository.postDeactivation(requestBody)
            if (response.code() == 200) {
                apiListener.onSuccess()
            } else {
                ErrorHandler.init(response, apiListener)
            }
        }
    }
}