package com.dentify.dentify.apiModel.model


import com.google.gson.annotations.SerializedName

data class CompletitionData(
    @SerializedName("user")
    var user: User?,
    @SerializedName("device")
    var device: Device?,
    @SerializedName("cert")
    var cert: Cert?,
    @SerializedName("signature")
    var signature: String?,
    @SerializedName("ocspResponse")
    var ocspResponse: String?
)