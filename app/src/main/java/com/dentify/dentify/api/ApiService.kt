package com.dentify.dentify.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @Headers("Content-Type: application/json", "accept: text/plain")
    @GET("{endpoint}")
    suspend fun <T: Any> getFromApi(
        @Path("endpoint", encoded = true) endpoint: String
    ): Response<T>

    @GET("{endpoint}")
    suspend fun <T: Any> getFromApi(
        @Path("endpoint", encoded = true) endpoint: String,
        @QueryMap queryParams: HashMap<String, Any>
    ): Response<T>

    @Headers("Content-Type: application/json", "accept: text/plain")
    @POST("{endpoint}")
    suspend fun <T: Any> postToApi(
        @Path("endpoint", encoded = true) endpoint: String,
        @Body body: Any?
    ): Response<T>

    @Multipart
    @POST("{endpoint}")
    suspend fun <T: Any> postUploadToApi(
        @Path("endpoint", encoded = true) endpoint: String,
        @Part file: MultipartBody.Part
    ): Response<T>

    @Headers("Content-Type: application/json", "accept: text/plain")
    @PUT("{endpoint}")
    suspend fun <T: Any> putInApi(
        @Path("endpoint", encoded = true) endpoint: String,
        @Body body: Any?
    ): Response<T>

    @Headers("Content-Type: application/json", "accept: text/plain")
    @PUT("{endpoint}")
    suspend fun <T: Any> putInApi(
        @Path("endpoint", encoded = true) endpoint: String
    ): Response<T>

    @Headers("Content-Type: application/json", "accept: text/plain")
    @DELETE("{endpoint}")
    suspend fun <T: Any> deleteFromApi(
        @Path("endpoint", encoded = true) endpoint: String
    ): Response<T>

}