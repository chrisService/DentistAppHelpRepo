package com.dentify.dentify.apiModel.response


import com.google.gson.annotations.SerializedName

data class GetRoomCreatedResponse(
    @SerializedName("status")
    var status: String?
)