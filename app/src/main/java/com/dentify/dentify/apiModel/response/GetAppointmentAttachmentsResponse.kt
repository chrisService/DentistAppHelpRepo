package com.dentify.dentify.apiModel.response


import com.google.gson.annotations.SerializedName
import com.dentify.dentify.apiModel.model.Attachment

data class GetAppointmentAttachmentsResponse(
    @SerializedName("attachments")
    var attachments: List<Attachment>?
)