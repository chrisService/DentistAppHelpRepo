package com.dentify.dentify.api

import com.google.gson.JsonObject
import com.dentify.dentify.BuildConfig
import com.dentify.dentify.util.Constants
import com.dentify.dentify.util.StorageWrapper
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject


class RefreshInterceptor : Interceptor {

    lateinit var originalRequest: Request
    lateinit var builder: Request.Builder

    override fun intercept(chain: Interceptor.Chain): Response {
        originalRequest = chain.request()
        builder = originalRequest.newBuilder()
        if (StorageWrapper.selectedLocale == Constants.SWEDISH) {
            builder.header("Accept-Language", Constants.SWEDISH_LOCALE)
        } else {
            builder.header("Accept-Language", Constants.ENGLISH_LOCALE)
        }
        if (StorageWrapper.accessToken.isNullOrEmpty()) {
            return chain.proceed(originalRequest)
        } else {
            setAuthHeader(builder, StorageWrapper.accessToken)
            originalRequest = builder.build()
            val response = chain.proceed(originalRequest)
            if (response.code == 200) {
                return response
            } else if (response.code == 401) {
                synchronized(this) {
                    refreshToken()
                    setAuthHeader(builder, StorageWrapper.accessToken)
                    originalRequest = builder.build()
                    val newResponse = chain.proceed(originalRequest)
                    return newResponse
                }
            }
            return response
        }
    }

    private fun setAuthHeader(builder: Request.Builder, token: String?) {
        if (token != null) //Add Auth token to each request if authorized
            builder.header("Authorization", String.format("Bearer %s", token))
    }

    private fun refreshToken() {

        val client: OkHttpClient = OkHttpClient.Builder().build()

        val jsonType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val json = JsonObject()
        json.addProperty("grant_type", "refresh_token")
        json.addProperty("refresh_token", StorageWrapper.refreshToken)
        json.addProperty("action", "None")

        val body = json.toString().toRequestBody(jsonType)

        val request = Request.Builder()
            .url(BuildConfig.API_BASE_URL + "api/auth/refresh-token")
            .post(body)
            .build()
        var response: Response? = null
        var code = 0

        try {
            response = client.newCall(request).execute()
            if (response != null) {
                code = response.code
                when (code) {
                    200 ->
                        try {
                            var jsonBody: JSONObject? = null
                            jsonBody = JSONObject(response.body!!.string())
                            StorageWrapper.accessToken = jsonBody.getString("access_token")
                            StorageWrapper.refreshToken = jsonBody.getString("refresh_token")
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                }
                response.body!!.close()
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
}

