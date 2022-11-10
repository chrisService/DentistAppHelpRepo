package com.dentify.dentify.apiModel.request

class PostPasswordResetCompleteRequest {
    var id: String? = null
    var password: String? = null
    var confirmPassword: String? = null
    var passwordRecoveryCode: String? = null
}