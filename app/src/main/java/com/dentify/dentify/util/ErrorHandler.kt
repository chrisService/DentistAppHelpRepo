package com.dentify.dentify.util

import com.google.gson.GsonBuilder
import com.dentify.dentify.`interface`.ViewModelApiListener
import com.dentify.dentify.apiModel.response.ErrorResponseModel
import retrofit2.Response

object ErrorHandler {
    fun <T> init(response: Response<T>, apiListener: ViewModelApiListener) {
        //TODO handle different type of errors 400, 401, 500,....
        try {
            val errorResponseModel = GsonBuilder().create()
                .fromJson(response.errorBody()!!.string(), ErrorResponseModel::class.java)
            if (!errorResponseModel.errors.isNullOrEmpty()){
                apiListener.onFailure(errorResponseModel.errorMessage + " ${
                    errorResponseModel.errors?.get(0)?.errorMessage
                }")
            }else{
                apiListener.onFailure(errorResponseModel.errorMessage)
            }
        } catch (e: Exception) {
            apiListener.onFailure(Constants.GENERIC_ERROR)
        }
    }
}