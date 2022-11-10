package com.dentify.dentify.apiModel.request

import com.google.gson.annotations.SerializedName

class PostAuthTokenRequest {
    @SerializedName("grant_type")
    var grantType: String? = null
    var email: String? = null
    var password: String? = null
}