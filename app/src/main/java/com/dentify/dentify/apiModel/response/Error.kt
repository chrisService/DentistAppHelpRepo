package com.dentify.dentify.apiModel.response


import com.google.gson.annotations.SerializedName

data class Error(
    @SerializedName("propertyName")
    var propertyName: String?,
    @SerializedName("errorMessage")
    var errorMessage: String?,
    @SerializedName("attemptedValue")
    var attemptedValue: String?
)