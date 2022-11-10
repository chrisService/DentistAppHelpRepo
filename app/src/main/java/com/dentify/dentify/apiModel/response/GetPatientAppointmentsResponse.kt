package com.dentify.dentify.apiModel.response


import com.google.gson.annotations.SerializedName
import com.dentify.dentify.apiModel.model.Appointment

data class GetPatientAppointmentsResponse(
    @SerializedName("appointments")
    var appointments: List<Appointment>?
)