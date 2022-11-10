package com.dentify.dentify.apiModel.model


import com.google.gson.annotations.SerializedName

data class Location(
    @SerializedName("countryId")
    var countryId: String?,
    @SerializedName("countryName")
    var countryName: String?,
    @SerializedName("city")
    var city: String?,
    @SerializedName("state")
    var state: String?,
    @SerializedName("address")
    var address: String?,
    @SerializedName("postalCode")
    var postalCode: String?
)