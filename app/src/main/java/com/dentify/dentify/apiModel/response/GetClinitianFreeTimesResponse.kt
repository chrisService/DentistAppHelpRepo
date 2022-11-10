package com.dentify.dentify.apiModel.response


import com.google.gson.annotations.SerializedName
import com.dentify.dentify.apiModel.model.FreeTime

data class GetClinitianFreeTimesResponse(
    @SerializedName("freeTimes")
    var freeTimes: List<FreeTime>?
)