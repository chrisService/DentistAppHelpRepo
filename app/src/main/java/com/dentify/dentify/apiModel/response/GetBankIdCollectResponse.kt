package com.dentify.dentify.apiModel.response


import com.google.gson.annotations.SerializedName
import com.dentify.dentify.apiModel.model.CompletitionData

data class GetBankIdCollectResponse(
    @SerializedName("orderRef")
    var orderRef: String?,
    @SerializedName("status")
    var status: String?,
    @SerializedName("hintCode")
    var hintCode: String?,
    @SerializedName("completitionData")
    var completitionData: CompletitionData?
)