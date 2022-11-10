package com.dentify.dentify.apiModel.model


import com.google.gson.annotations.SerializedName

data class Appointment(
    @SerializedName("id")
    var id: String?,
    @SerializedName("patientId")
    var patientId: String?,
    @SerializedName("patientFullName")
    var patientFullName: String?,
    @SerializedName("clinicianId")
    var clinicianId: String?,
    @SerializedName("clinicianName")
    var clinicianName: String?,
    @SerializedName("clinicId")
    var clinicId: String?,
    @SerializedName("clinicianProfileImageUri")
    var clinicianProfileImageUri: String?,
    @SerializedName("clinicName")
    var clinicName: String?,
    @SerializedName("dateFrom")
    var dateFrom: String?,
    @SerializedName("dateTo")
    var dateTo: String?,
    @SerializedName("requestNote")
    var requestNote: String?,
    @SerializedName("appointmentType")
    var appointmentType: String?,
    @SerializedName("reason")
    var reason: String?,
    @SerializedName("status")
    var status: String?
)