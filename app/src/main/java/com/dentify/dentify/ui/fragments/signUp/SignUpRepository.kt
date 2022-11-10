package com.dentify.dentify.ui.fragments.signUp

import com.dentify.dentify.api.ApiService
import com.dentify.dentify.apiModel.request.PostPatientsPatientsRequest
import com.dentify.dentify.apiModel.response.GetPatientsInvitationEmailResponse
import com.dentify.dentify.apiModel.response.PostPatientsPatientsResponse
import com.dentify.dentify.repository.MainRepository
import retrofit2.Response
import javax.inject.Inject

class SignUpRepository @Inject constructor(
    private val apiService: ApiService
) : MainRepository(apiService) {

    suspend fun postPatients(body: PostPatientsPatientsRequest): Response<PostPatientsPatientsResponse> {
        return  apiService.postToApi("/api/patients", body)
    }

    suspend fun getInvitationEmail(initationId: String): Response<GetPatientsInvitationEmailResponse> {
        return  apiService.getFromApi("api/auth/patient-invitation/$initationId")
    }

}