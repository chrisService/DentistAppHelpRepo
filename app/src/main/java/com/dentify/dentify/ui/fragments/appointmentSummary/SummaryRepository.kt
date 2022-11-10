package com.dentify.dentify.ui.fragments.appointmentSummary

import com.dentify.dentify.api.ApiService
import com.dentify.dentify.apiModel.request.PostPatientsAppointmentRequest
import com.dentify.dentify.apiModel.response.PostPatientAppointmentsResponse
import com.dentify.dentify.repository.MainRepository
import retrofit2.Response
import javax.inject.Inject

class SummaryRepository@Inject constructor(
    private val apiService: ApiService
) : MainRepository(apiService) {

    suspend fun postPatientAppointment(body: PostPatientsAppointmentRequest): Response<PostPatientAppointmentsResponse> {
        return  apiService.postToApi("api/patients/appointments", body)
    }
}