package com.dentify.dentify.apiModel.model


import com.google.gson.annotations.SerializedName

data class Clinician(
    @SerializedName("id")
    var id: String?,
    @SerializedName("fullName")
    var fullName: String?,
    @SerializedName("profileImageUri")
    var profileImageUri: String?
)