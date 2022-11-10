package com.dentify.dentify.apiModel.response


import com.google.gson.annotations.SerializedName

data class GetBankIdAuthResponse(
    @SerializedName("autoStartToken")
    var autoStartToken: String?,
    @SerializedName("orderRef")
    var orderRef: String?,
    @SerializedName("endUserIp")
    var endUserIp: String?
)