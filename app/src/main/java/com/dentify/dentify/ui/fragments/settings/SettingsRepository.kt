package com.dentify.dentify.ui.fragments.settings

import com.dentify.dentify.api.ApiService
import com.dentify.dentify.apiModel.request.PostDeactivationRequest
import com.dentify.dentify.apiModel.response.GenericEmptyResponse
import com.dentify.dentify.repository.MainRepository
import retrofit2.Response
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val apiService: ApiService
) : MainRepository(apiService) {

    suspend fun postDeactivation(body: PostDeactivationRequest): Response<GenericEmptyResponse> {
        return  apiService.postToApi("api/patients/profile/deactivate", body)
    }
}