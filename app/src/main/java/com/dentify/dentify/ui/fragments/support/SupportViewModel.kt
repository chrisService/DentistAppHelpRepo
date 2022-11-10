package com.dentify.dentify.ui.fragments.support

import androidx.lifecycle.viewModelScope
import com.dentify.dentify.`interface`.ViewModelApiListener
import com.dentify.dentify.apiModel.request.PostSupportRequest
import com.dentify.dentify.mainViewModel.MainViewModel
import com.dentify.dentify.util.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SupportViewModel @Inject constructor(
    private val repository: SupportRepository
) : MainViewModel(repository) {

    fun postSupport(body: PostSupportRequest, apiListener: ViewModelApiListener) {
        apiListener.onStarted()
        viewModelScope.launch {
            val response = repository.postSupport(body)
            if (response.code() == 200) {
                apiListener.onSuccess()
            } else {
                ErrorHandler.init(response, apiListener)
            }
        }
    }

}