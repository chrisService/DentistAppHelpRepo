package com.dentify.dentify.ui.fragments.forgotPassword

import com.dentify.dentify.api.ApiService
import com.dentify.dentify.apiModel.request.PostPasswordResetBeginRequest
import com.dentify.dentify.apiModel.request.PostPasswordResetCompleteRequest
import com.dentify.dentify.repository.MainRepository
import retrofit2.Response
import javax.inject.Inject

class ForgotPasswordRepository@Inject constructor(
    private val apiService: ApiService
) : MainRepository(apiService) {

    suspend fun postPasswordResetBegin(body: PostPasswordResetBeginRequest): Response<Any> {
        return  apiService.postToApi("api/accounts/password-reset/begin", body)
    }

    suspend fun postPasswordResetComplete(body: PostPasswordResetCompleteRequest): Response<Any> {
        return  apiService.postToApi("api/accounts/password-reset/complete", body)
    }
}