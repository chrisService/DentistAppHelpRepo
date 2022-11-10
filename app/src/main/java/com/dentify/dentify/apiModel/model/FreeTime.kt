package com.dentify.dentify.apiModel.model


import com.google.gson.annotations.SerializedName

data class FreeTime(
    @SerializedName("dateFrom")
    var dateFrom: String?,
    @SerializedName("dateTo")
    var dateTo: String?
)