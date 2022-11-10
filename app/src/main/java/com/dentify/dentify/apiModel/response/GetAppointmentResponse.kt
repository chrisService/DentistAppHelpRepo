package com.dentify.dentify.apiModel.response


import com.google.gson.annotations.SerializedName

data class GetAppointmentResponse(
    @SerializedName("id")
    var id: String?,
    @SerializedName("patientId")
    var patientId: String?,
    @SerializedName("patientFullName")
    var patientFullName: String?,
    @SerializedName("patientEmailAddress")
    var patientEmailAddress: String?,
    @SerializedName("clinicName")
    var clinicName: String?,
    @SerializedName("dateFrom")
    var dateFrom: String?,
    @SerializedName("dateTo")
    var dateTo: String?,
    @SerializedName("appointmentType")
    var appointmentType: String?,
    @SerializedName("reason")
    var reason: String?,
    @SerializedName("status")
    var status: String?
)