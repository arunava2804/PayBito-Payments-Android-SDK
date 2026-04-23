package com.paybito.sdk.api

import com.paybito.sdk.models.BrokerInfoResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface PayBitoAccountsApiService {

    @GET("api/home/getBrokerWiseExchangeInfo")
    suspend fun getBrokerWiseExchangeInfo(
        @Query("brokerId") brokerId: String,
        @Header("Origin") origin: String
    ): BrokerInfoResponse
}
