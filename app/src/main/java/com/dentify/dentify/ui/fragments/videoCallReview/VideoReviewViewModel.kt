package com.dentify.dentify.ui.fragments.videoCallReview

import androidx.lifecycle.viewModelScope
import com.dentify.dentify.`interface`.ViewModelApiListener
import com.dentify.dentify.apiModel.request.PostVideoReviewRequest
import com.dentify.dentify.mainViewModel.MainViewModel
import com.dentify.dentify.util.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoReviewViewModel @Inject constructor(
    private val repository: VideoReviewRepository
) : MainViewModel(repository) {

    fun postVideoReview(appointmentId: String, requestBody: PostVideoReviewRequest, apiListener: ViewModelApiListener) {
        apiListener.onStarted()
        viewModelScope.launch {
            val response = repository.postVideoReview(appointmentId, requestBody)
            if (response.code() == 200) {
                apiListener.onSuccess()
            } else {
                ErrorHandler.init(response, apiListener)
            }
        }
    }

}