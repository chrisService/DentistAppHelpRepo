package com.dentify.dentify.api

import com.dentify.dentify.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.security.cert.CertificateException
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


object TrustCertificates{


    private var instance: OkHttpClient? = null


    fun getHttpClient(): OkHttpClient {
        if (instance == null) {
            synchronized(TrustCertificates::class.java) {
                if (instance == null) {
                    instance = createOkHttpClient()
                }
            }
        }
        return instance!!
    }


    fun createOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        // Create a trust manager that does not validate certificate chains
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
            }

            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                return arrayOf()
            }
        })

        // Install the all-trusting trust manager
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())
        // Create an ssl socket factory with our all-trusting manager
        val sslSocketFactory = sslContext.socketFactory


        builder.readTimeout(60, TimeUnit.SECONDS)
        builder.connectTimeout(30, TimeUnit.SECONDS)
        builder.writeTimeout(60, TimeUnit.SECONDS)
        builder.addInterceptor(RefreshInterceptor())
        builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
        builder.retryOnConnectionFailure(true)
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.apply { logging.level = HttpLoggingInterceptor.Level.BODY }
            builder.addInterceptor(logging)
        }
        return builder.build()
    }
}