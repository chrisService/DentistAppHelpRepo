package com.dentify.dentify.apiModel.request

class PostAuthRefreshTokenRequest {
    var grant_type: String? = "refresh_token"
    var refresh_token: String? = null
    var action: String? = "None"
}