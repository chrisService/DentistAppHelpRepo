package com.dentify.dentify.api

import retrofit2.Response
import retrofit2.http.*

interface RefreshTokenApiService {

    @Headers("Content-Type: application/json", "accept: text/plain")
    @POST("{endpoint}")
    suspend fun <T: Any> postToApi(
        @Path("endpoint", encoded = true) endpoint: String,
        @Body body: Any?
    ): Response<T>
}