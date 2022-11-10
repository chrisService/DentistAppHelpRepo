package com.dentify.dentify.apiModel.response

import com.google.gson.annotations.SerializedName

data class PostAuthTokenResponse(
    @SerializedName("access_token")
    var accessToken: String?,
    @SerializedName("refresh_token")
    var refreshToken: String?,
    @SerializedName("expires_in")
    var expiresIn: String?
)