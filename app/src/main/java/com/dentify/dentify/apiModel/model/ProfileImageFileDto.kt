package com.dentify.dentify.apiModel.model


import com.google.gson.annotations.SerializedName

data class ProfileImageFileDto(
    @SerializedName("id")
    var id: String?,
    @SerializedName("fileName")
    var fileName: String?,
    @SerializedName("uri")
    var uri: String?
)