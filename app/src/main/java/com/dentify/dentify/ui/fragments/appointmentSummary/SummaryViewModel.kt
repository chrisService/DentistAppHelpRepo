package com.dentify.dentify.ui.fragments.appointmentSummary

import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.dentify.dentify.`interface`.ViewModelApiListener
import com.dentify.dentify.apiModel.request.PostPatientsAppointmentRequest
import com.dentify.dentify.apiModel.response.PostPatientAppointmentsResponse
import com.dentify.dentify.mainViewModel.MainViewModel
import com.dentify.dentify.util.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(private val repository: SummaryRepository) :
    MainViewModel(repository) {


    lateinit var newAppointmentResponse: PostPatientAppointmentsResponse

    fun postPatientAppointment(
        requestBody: PostPatientsAppointmentRequest,
        apiListener: ViewModelApiListener
    ) {
        apiListener.onStarted()
        viewModelScope.launch {
            val response = repository.postPatientAppointment(requestBody)
            if (response.code() == 200) {
                val jSonObject = JSONObject(Gson().toJson(response.body()))
                newAppointmentResponse = Gson().fromJson(
                    jSonObject.toString(),
                    PostPatientAppointmentsResponse::class.java
                )
                apiListener.onSuccess()
            } else {
                ErrorHandler.init(response, apiListener)
            }
        }
    }
}