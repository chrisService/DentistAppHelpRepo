package com.dentify.dentify.mainViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.dentify.dentify.`interface`.ViewModelApiListener
import com.dentify.dentify.apiModel.request.PostDeviceRequest
import com.dentify.dentify.apiModel.request.PostTokenBankIdRequest
import com.dentify.dentify.apiModel.request.PutPatientProfileRequest
import com.dentify.dentify.apiModel.response.*
import com.dentify.dentify.enum.WelcomeCondition
import com.dentify.dentify.repository.MainRepository
import com.dentify.dentify.util.ErrorHandler
import com.dentify.dentify.util.StorageWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
open class MainViewModel @Inject constructor(private val repository: MainRepository) : ViewModel() {

    lateinit var patietnsProfileResponse: GetPatientsProfileResponse
    fun getProfile(context: Context, apiListener: ViewModelApiListener) {
        apiListener.onStarted()
        viewModelScope.launch {
            val response = repository.getProfile()
            if (response.code() == 200) {
                val jSonObject = JSONObject(Gson().toJson(response.body()))
                patietnsProfileResponse =
                    Gson().fromJson(jSonObject.toString(), GetPatientsProfileResponse::class.java)
                StorageWrapper.savePatientsProfileResponse(patietnsProfileResponse, context)
                StorageWrapper.profilePictureUri = patietnsProfileResponse.patient?.profileImageFileDto?.uri
                StorageWrapper.profilePictureID = patietnsProfileResponse.patient?.profileImageFileDto?.id
                StorageWrapper.profileEmail = patietnsProfileResponse.patient?.emailAddress
                StorageWrapper.clearAppointmentImages(context)
                if (StorageWrapper.welcomeCondition != WelcomeCondition.Welcome.value){
                    StorageWrapper.welcomeCondition = WelcomeCondition.Back.value
                }
                StorageWrapper.reviewCondition++
                apiListener.onSuccess()
            } else {
                ErrorHandler.init(response, apiListener)
            }
        }
    }

    fun updateProfile(body: PutPatientProfileRequest, apiListener: ViewModelApiListener) {
        apiListener.onStarted()
        viewModelScope.launch {
            val response = repository.putProfile(body)
            if (response.code() == 200) {
                apiListener.onSuccess()
            } else {
                ErrorHandler.init(response, apiListener)
            }
        }
    }

    lateinit var patientsAppointmentsResponse: GetPatientAppointmentsResponse
    fun getAppointments(context: Context, apiListener: ViewModelApiListener) {
        apiListener.onStarted()
        viewModelScope.launch {
            val response = repository.getPatientAppointments()
            if (response.code() == 200) {
                val jSonObject = JSONObject(Gson().toJson(response.body()))
                patientsAppointmentsResponse = Gson().fromJson(jSonObject.toString(), GetPatientAppointmentsResponse::class.java)
                if (!patientsAppointmentsResponse.appointments.isNullOrEmpty()) {
                    StorageWrapper.savePatientAppointments(patientsAppointmentsResponse.appointments, context)
                }
                apiListener.onSuccess()
            } else {
                ErrorHandler.init(response, apiListener)
            }
        }
    }

    lateinit var uploadResponse: PostUploadImageResponse
    fun postUploadImage(
        attachmentType: String,
        part: MultipartBody.Part,
        apiListener: ViewModelApiListener
    ) {
        apiListener.onStarted()
        viewModelScope.launch {

            val response = repository.postUploadImage(attachmentType, part)
            if (response.code() == 200) {
                val jSonObject = JSONObject(Gson().toJson(response.body()))
                uploadResponse =
                    Gson().fromJson(jSonObject.toString(), PostUploadImageResponse::class.java)
                apiListener.onSuccess()
            } else {
                ErrorHandler.init(response, apiListener)
            }
        }
    }

    fun deleteUploadedImage(imageId: String, apiListener: ViewModelApiListener) {
        apiListener.onStarted()
        viewModelScope.launch {
            val response = repository.deleteUploadedImage(imageId)
            if (response.code() == 200) {
                apiListener.onSuccess()
            } else {
                ErrorHandler.init(response, apiListener)
            }
        }
    }

    lateinit var authResponse: GetBankIdAuthResponse
    fun getBankIdAuth(apiListener: ViewModelApiListener) {
        apiListener.onStarted()
        viewModelScope.launch {
            val response = repository.getBankAuth()
            if (response.code() == 200) {
                val jSonObject = JSONObject(Gson().toJson(response.body()))
                authResponse =
                    Gson().fromJson(jSonObject.toString(), GetBankIdAuthResponse::class.java)
                apiListener.onSuccess()
            } else {
                ErrorHandler.init(response, apiListener)
            }
        }
    }

    lateinit var tokenResponse: PostAuthTokenResponse
    fun postTokenBankId(requestBody: PostTokenBankIdRequest, apiListener: ViewModelApiListener) {
        apiListener.onStarted()
        viewModelScope.launch {
            val response = repository.postTokenBankId(requestBody)
            if (response.code() == 200) {
                val jSonObject = JSONObject(Gson().toJson(response.body()))
                tokenResponse =
                    Gson().fromJson(jSonObject.toString(), PostAuthTokenResponse::class.java)
                StorageWrapper.accessToken = tokenResponse.accessToken
                StorageWrapper.refreshToken = tokenResponse.refreshToken
                apiListener.onSuccess()
            } else {
                ErrorHandler.init(response, apiListener)
            }
        }
    }

    lateinit var supportReasonsResponse: GetSupportReasonResponse
    fun getSupportReasons(apiListener: ViewModelApiListener) {
        apiListener.onStarted()
        viewModelScope.launch {
            val response = repository.getSupportReasons()
            if (response.code() == 200) {
                val jSonObject = JSONObject(Gson().toJson(response.body()))
                supportReasonsResponse =
                    Gson().fromJson(jSonObject.toString(), GetSupportReasonResponse::class.java)
                apiListener.onSuccess()
            } else {
                ErrorHandler.init(response, apiListener)
            }
        }
    }

    fun postDevice(apiListener: ViewModelApiListener) {
        apiListener.onStarted()
        viewModelScope.launch {
            val body = PostDeviceRequest()
            body.notificationToken = StorageWrapper.fcmTokken
            val response = repository.postDevice(body)
            if (response.code() == 200) {
                apiListener.onSuccess()
            } else {
                ErrorHandler.init(response, apiListener)
            }
        }
    }

    fun deleteDevice(apiListener: ViewModelApiListener) {
        apiListener.onStarted()
        viewModelScope.launch {
            val response = repository.deleteDevice()
            if (response.code() == 200) {
                apiListener.onSuccess()
            } else {
                ErrorHandler.init(response, apiListener)
            }
        }
    }
}