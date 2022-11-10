package com.dentify.dentify.apiModel.model


import com.google.gson.annotations.SerializedName

data class Reason(
    @SerializedName("id")
    var id: String?,
    @SerializedName("reasonType")
    var reasonType: Int?
)