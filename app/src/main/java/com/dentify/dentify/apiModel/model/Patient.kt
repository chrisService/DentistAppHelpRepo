package com.dentify.dentify.apiModel.model


import com.google.gson.annotations.SerializedName

data class Patient(
    @SerializedName("profileImageFileDto")
    var profileImageFileDto: ProfileImageFileDto?,
    @SerializedName("fullName")
    var fullName: String?,
    @SerializedName("phoneNumber")
    var phoneNumber: String?,
    @SerializedName("emailAddress")
    var emailAddress: String?
)