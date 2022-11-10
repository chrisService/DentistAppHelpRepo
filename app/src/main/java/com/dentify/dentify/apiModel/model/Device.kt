package com.dentify.dentify.apiModel.model


import com.google.gson.annotations.SerializedName

data class Device(
    @SerializedName("ipAddress")
    var ipAddress: String?
)