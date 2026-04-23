package com.paybito.sdk.api

import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.paybito.sdk.PayBitoSdk
import kotlinx.coroutines.runBlocking

object PayBitoApiClient {
    private const val BASE_URL = "https://service.hashcashconsultants.com/Payments-Apikey-V2/"
    private const val ACCOUNTS_BASE_URL = "https://accounts.paybito.com/"

    private fun createClient(enableDebug: Boolean): OkHttpClient {
        val logger = HttpLoggingInterceptor().apply {
            level = if (enableDebug) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }

        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val (cid, secret) = PayBitoSdk.getAuth()
                
                if (cid != null) {
                    val newRequest = originalRequest.newBuilder()
                        .header("X-MBX-PUBLIC-KEY", cid)
                        .header("CLINET-SECRET-KEY", secret ?: "")
                        .build()
                    chain.proceed(newRequest)
                } else {
                    chain.proceed(originalRequest)
                }
            }
            .authenticator(object : Authenticator {
                override fun authenticate(route: Route?, response: Response): Request? {
                    if (response.code == 401 || (response.code == 200 && isAuthError(response))) {
                        // Check if we have already retried
                        if (response.request.header("X-Retry") != null) return null

                        // Force refresh credentials
                        runBlocking {
                            PayBitoSdk.refreshAuth()
                        }

                        val (cid, secret) = PayBitoSdk.getAuth()
                        if (cid != null) {
                            return response.request.newBuilder()
                                .header("X-MBX-PUBLIC-KEY", cid)
                                .header("CLINET-SECRET-KEY", secret ?: "")
                                .header("X-Retry", "true")
                                .build()
                        }
                    }
                    return null
                }
            })
            .addInterceptor(logger)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private fun isAuthError(response: Response): Boolean {
        // Some APIs might return 200 with an error message in body
        // But usually it's 401 or 400. 
        // For now, let's rely on 401/403 or specific message if we can peek (hard with OkHttp)
        return false 
    }

    fun create(enableDebug: Boolean): PayBitoApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createClient(enableDebug))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PayBitoApiService::class.java)
    }

    fun createAccounts(enableDebug: Boolean): PayBitoAccountsApiService {
        return Retrofit.Builder()
            .baseUrl(ACCOUNTS_BASE_URL)
            .client(createClient(enableDebug))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PayBitoAccountsApiService::class.java)
    }
}
