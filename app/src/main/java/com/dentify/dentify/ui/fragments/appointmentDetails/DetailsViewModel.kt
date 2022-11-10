package com.dentify.dentify.ui.fragments.appointmentDetails

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.dentify.dentify.`interface`.ViewModelApiListener
import com.dentify.dentify.apiModel.model.Appointment
import com.dentify.dentify.apiModel.request.PutCancelAppointmentRequest
import com.dentify.dentify.apiModel.response.GetAppointmentAttachmentsResponse
import com.dentify.dentify.apiModel.response.GetRoomCreatedResponse
import com.dentify.dentify.enum.TwilioRoomStatus
import com.dentify.dentify.mainViewModel.MainViewModel
import com.dentify.dentify.repository.MainRepository
import com.dentify.dentify.util.ErrorHandler
import com.dentify.dentify.util.StorageWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: MainRepository
) : MainViewModel(repository) {

    lateinit var appointmentResponse: Appointment

    fun getAppointment(appointmentId: String, apiListener: ViewModelApiListener) {
        apiListener.onStarted()
        viewModelScope.launch {
            val response = repository.getAppointment(appointmentId)
            if (response.code() == 200) {
                val jSonObject = JSONObject(Gson().toJson(response.body()))
                appointmentResponse =
                    Gson().fromJson(jSonObject.toString(), Appointment::class.java)
                apiListener.onSuccess()
            } else {
                ErrorHandler.init(response, apiListener)
            }
        }
    }

    lateinit var appointmentAttachmentsResponse: GetAppointmentAttachmentsResponse

    fun getAppointmentAttachments(context: Context, appointmentId: String, apiListener: ViewModelApiListener) {
        apiListener.onStarted()
        viewModelScope.launch {
            val response = repository.getAppointmentAttachments(appointmentId)
            if (response.code() == 200) {
                val jSonObject = JSONObject(Gson().toJson(response.body()))
                appointmentAttachmentsResponse =
                    Gson().fromJson(jSonObject.toString(), GetAppointmentAttachmentsResponse::class.java)
                StorageWrapper.saveAppointmentAttachments(appointmentAttachmentsResponse.attachments, context)
                apiListener.onSuccess()
            } else {
                ErrorHandler.init(response, apiListener)
            }
        }
    }

    fun putAcceptRequest(appointmentId: String, apiListener: ViewModelApiListener) {
        apiListener.onStarted()
        viewModelScope.launch {
            val response = repository.putAcceptRequest(appointmentId)
            if (response.code() == 200) {

                apiListener.onSuccess()
            } else {
                ErrorHandler.init(response, apiListener)
            }
        }
    }

    fun putDeclineRequest(appointmentId: String, body: PutCancelAppointmentRequest, apiListener: ViewModelApiListener) {
        apiListener.onStarted()
        viewModelScope.launch {
            val response = repository.putDeclineRequest(appointmentId, body)
            if (response.code() == 200) {
                apiListener.onSuccess()
            } else {
                ErrorHandler.init(response, apiListener)
            }
        }
    }

    lateinit var roomCreatedResponse: GetRoomCreatedResponse

    fun getRoomCreatedResponse(appointmentId: String, apiListener: ViewModelApiListener) {
        apiListener.onStarted()
        viewModelScope.launch {
            val response = repository.checkRoomCreated(appointmentId)
            if (response.code() == 200) {
                val jSonObject = JSONObject(Gson().toJson(response.body()))
                roomCreatedResponse =
                    Gson().fromJson(jSonObject.toString(), GetRoomCreatedResponse::class.java)
                if (roomCreatedResponse.status!! == TwilioRoomStatus.Created.valueString) {
                    apiListener.onSuccess()
                } else {
                    apiListener.onFailure(roomCreatedResponse.status)
                }
            } else {
                ErrorHandler.init(response, apiListener)
            }
        }
    }

    fun cancelAppointment(id: String, body: PutCancelAppointmentRequest, apiListener: ViewModelApiListener) {
        apiListener.onStarted()
        viewModelScope.launch {
            val response = repository.cancelAppointment(id, body)
            if (response.code() == 200) {
                apiListener.onSuccess()
            } else {
                ErrorHandler.init(response, apiListener)
            }
        }
    }
}