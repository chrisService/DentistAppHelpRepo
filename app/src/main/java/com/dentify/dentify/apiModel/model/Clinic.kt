package com.dentify.dentify.apiModel.model


import com.google.gson.annotations.SerializedName

data class Clinic(
    @SerializedName("id")
    var id: String?,
    @SerializedName("clinicName")
    var clinicName: String?,
    @SerializedName("location")
    var location: Location?,
    @SerializedName("logoUri")
    var logoUri: String?
)