package com.dentify.dentify.ui.fragments.videoCallReview

import com.dentify.dentify.api.ApiService
import com.dentify.dentify.apiModel.request.PostAuthTokenRequest
import com.dentify.dentify.apiModel.request.PostVideoReviewRequest
import com.dentify.dentify.apiModel.response.GenericEmptyResponse
import com.dentify.dentify.apiModel.response.PostAuthTokenResponse
import com.dentify.dentify.repository.MainRepository
import retrofit2.Response
import javax.inject.Inject

class VideoReviewRepository @Inject constructor(
    private val apiService: ApiService
) : MainRepository(apiService) {

    suspend fun postVideoReview(appointemtnId: String, body: PostVideoReviewRequest): Response<GenericEmptyResponse> {
        return  apiService.postToApi("api/appointments/$appointemtnId/review", body)
    }
}