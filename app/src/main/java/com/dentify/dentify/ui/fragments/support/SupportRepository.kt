package com.dentify.dentify.ui.fragments.support

import com.dentify.dentify.api.ApiService
import com.dentify.dentify.apiModel.request.PostSupportRequest
import com.dentify.dentify.apiModel.response.GenericEmptyResponse
import com.dentify.dentify.repository.MainRepository
import retrofit2.Response
import javax.inject.Inject

class SupportRepository @Inject constructor(
    private val apiService: ApiService
) : MainRepository(apiService) {

    suspend fun postSupport(body: PostSupportRequest): Response<GenericEmptyResponse> {
        return  apiService.postToApi("api/shared/support", body)
    }
}