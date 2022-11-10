package com.dentify.dentify.apiModel.response


import com.google.gson.annotations.SerializedName

data class PostPatientAppointmentsResponse(
    @SerializedName("id")
    var id: String?
)