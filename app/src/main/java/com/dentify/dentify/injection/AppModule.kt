package com.dentify.dentify.injection

import com.dentify.dentify.BuildConfig
import com.dentify.dentify.api.ApiService
import com.dentify.dentify.api.TrustCertificates
import com.dentify.dentify.repository.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideApi(): ApiService = Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(TrustCertificates.getHttpClient())
        .build()
        .create(ApiService::class.java)

    @Singleton
    @Provides
    fun provideMainRepository(): MainRepository {
        return MainRepository(
            provideApi()
        )
    }



}