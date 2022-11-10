package com.dentify.dentify.apiModel.response

import com.google.gson.annotations.SerializedName

class TwilioTokenResponse (
    @SerializedName("accessToken")
    var twilioToken: String?
)