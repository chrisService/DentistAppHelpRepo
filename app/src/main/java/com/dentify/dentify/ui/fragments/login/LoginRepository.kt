package com.dentify.dentify.ui.fragments.login

import com.dentify.dentify.api.ApiService
import com.dentify.dentify.apiModel.request.PostAuthTokenRequest
import com.dentify.dentify.apiModel.response.PostAuthTokenResponse
import com.dentify.dentify.repository.MainRepository
import retrofit2.Response
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val apiService: ApiService
) : MainRepository(apiService) {

    suspend fun postToken(body: PostAuthTokenRequest): Response<PostAuthTokenResponse> {
        return  apiService.postToApi("api/auth/token", body)
    }
}