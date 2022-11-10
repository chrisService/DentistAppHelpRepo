package com.dentify.dentify.apiModel.response


import com.google.gson.annotations.SerializedName
import com.dentify.dentify.apiModel.model.File

data class PostUploadImageResponse(
    @SerializedName("file")
    var `file`: File?
)