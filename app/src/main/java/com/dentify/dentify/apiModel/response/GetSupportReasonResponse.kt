package com.dentify.dentify.apiModel.response


import com.google.gson.annotations.SerializedName
import com.dentify.dentify.apiModel.model.Reason

data class GetSupportReasonResponse(
    @SerializedName("reasons")
    var reasons: List<Reason>?
)