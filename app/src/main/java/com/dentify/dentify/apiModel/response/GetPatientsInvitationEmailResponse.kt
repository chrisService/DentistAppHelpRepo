package com.dentify.dentify.apiModel.response

import com.google.gson.annotations.SerializedName

data class GetPatientsInvitationEmailResponse(
    @SerializedName("emailAddress")
    var emailAddress: String?,
    @SerializedName("phoneNumber")
    var phoneNumber: String?
)