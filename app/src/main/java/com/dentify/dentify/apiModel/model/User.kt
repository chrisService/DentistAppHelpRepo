package com.dentify.dentify.apiModel.model


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("personalNumber")
    var personalNumber: String?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("givenName")
    var givenName: String?,
    @SerializedName("surname")
    var surname: String?
)