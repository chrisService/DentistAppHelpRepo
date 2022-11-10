package com.dentify.dentify.apiModel.response


import com.google.gson.annotations.SerializedName

data class ErrorResponseModel(
    @SerializedName("exceptionType")
    var exceptionType: String?,
    @SerializedName("stackTrace")
    var stackTrace: String?,
    @SerializedName("errorMessage")
    var errorMessage: String?,
    @SerializedName("errors")
    var errors: List<Error>?,
    @SerializedName("errorCode")
    var errorCode: String?
)