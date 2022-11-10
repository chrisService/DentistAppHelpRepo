package com.dentify.dentify.repository


import com.dentify.dentify.api.ApiService
import com.dentify.dentify.apiModel.model.Appointment
import com.dentify.dentify.apiModel.query.GetBankIdCollectQuery
import com.dentify.dentify.apiModel.request.*
import com.dentify.dentify.apiModel.response.*
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject


open class MainRepository @Inject constructor(private val apiService: ApiService){


    suspend fun getProfile(): Response<GetPatientsProfileResponse> {
        return apiService.getFromApi("api/patients/profile")
    }

    suspend fun putProfile(body: PutPatientProfileRequest): Response<GetPatientsProfileResponse> {
        return apiService.putInApi("api/patients/profile", body)
    }

    suspend fun cancelAppointment(id: String, body: PutCancelAppointmentRequest): Response<GenericEmptyResponse> {
        return apiService.putInApi("api/appointments/$id/cancel", body)
    }

    suspend fun getPatientAppointments(): Response<GetPatientsProfileResponse> {
        return apiService.getFromApi("api/patients/appointments")
    }

    suspend fun getAppointment(appointmentId: String): Response<Appointment> {
        return  apiService.getFromApi("api/appointments/$appointmentId")
    }

    suspend fun getAppointmentAttachments(appointmentId: String): Response<GetAppointmentAttachmentsResponse> {
        return  apiService.getFromApi("api/appointments/$appointmentId/attachments")
    }

    suspend fun putAcceptRequest(appointmentId: String): Response<GenericEmptyResponse> {
        return  apiService.putInApi("api/appointments/$appointmentId/accept")
    }

    suspend fun putDeclineRequest(appointmentId: String, body: PutCancelAppointmentRequest): Response<GenericEmptyResponse> {
        return  apiService.putInApi("api/appointments/$appointmentId/decline", body)
    }

    suspend fun getTwilioToken(): Response<TwilioTokenResponse>{
        return apiService.getFromApi("api/video/token")
    }

    suspend fun getBankAuth(): Response<GetBankIdAuthResponse>{
        return  apiService.getFromApi("api/auth/bankid")
    }

    suspend fun getBankIdCollect(query: GetBankIdCollectQuery): Response<GetBankIdCollectResponse>{
        val queryData =  hashMapOf<String, Any>()
        queryData["orderRef"] = query.orderRef

        return  apiService.getFromApi("api/auth/bankid/collect", queryData)
    }

    suspend fun postUploadImage( attachmentType: String, part: MultipartBody.Part): Response<PostUploadImageResponse> {
        return  apiService.postUploadToApi("api/attachments/$attachmentType", part)
    }

    suspend fun deleteUploadedImage(imageId: String): Response<GenericEmptyResponse> {
        return  apiService.deleteFromApi("api/attachments/$imageId")
    }

    suspend fun checkRoomCreated(id: String): Response<GetRoomCreatedResponse> {
        return  apiService.getFromApi("/api/video/room/$id")
    }

    suspend fun postTokenBankId(body: PostTokenBankIdRequest): Response<PostAuthTokenResponse>{
        return  apiService.postToApi("api/auth/bankid/token", body)
    }

    suspend fun postDevice(body: PostDeviceRequest): Response<DeviceResponse>{
        return  apiService.postToApi("api/devices", body)
    }

    suspend fun deleteDevice(): Response<DeviceResponse>{
        return  apiService.deleteFromApi("api/devices")
    }

    suspend fun getSupportReasons(): Response<GetSupportReasonResponse> {
        return  apiService.getFromApi("api/shared/support/reasons")
    }

}