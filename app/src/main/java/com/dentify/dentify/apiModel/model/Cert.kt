package com.dentify.dentify.apiModel.model


import com.google.gson.annotations.SerializedName

data class Cert(
    @SerializedName("notBefore")
    var notBefore: String?,
    @SerializedName("notAfter")
    var notAfter: String?
)