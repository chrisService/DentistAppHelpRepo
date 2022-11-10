package com.dentify.dentify.apiModel.model


import com.google.gson.annotations.SerializedName

data class Attachment(
    @SerializedName("isPatientAttachment")
    var isPatientAttachment: Boolean?,
    @SerializedName("file")
    var `file`: File?
)