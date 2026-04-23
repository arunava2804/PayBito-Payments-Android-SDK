package com.paybito.sdk.api

import com.paybito.sdk.models.*
import retrofit2.http.*

interface PayBitoApiService {

    @Headers("Content-Type: application/json")
    @GET("api/v1/validate")
    suspend fun validatePublicKey(
        @Header("publicKey") publicKey: String,
        @Header("Origin") origin: String
    ): ValidateResponse

    @Headers("Content-Type: application/json")
    @POST("shopping/products/register")
    suspend fun registerProducts(
        @Header("Origin") origin: String,
        @Body request: RegisterProductsRequest,
        @Query("productId") productId: String? = null
    ): RegisterResponse

    @Headers("Content-Type: application/json")
    @GET("shopping/products")
    suspend fun getProducts(
        @Header("Origin") origin: String,
        @Query("cartToken") cartToken: String
    ): com.google.gson.JsonObject

    @Headers("Content-Type: application/json")
    @PUT("shopping/products")
    suspend fun updateProduct(
        @Header("Origin") origin: String,
        @Query("productId") productId: String,
        @Body request: UpdateProductRequest
    ): RegisterResponse

    @Headers("Content-Type: application/json")
    @DELETE("shopping/products")
    suspend fun removeProduct(
        @Header("Origin") origin: String,
        @Query("productId") productId: String,
        @Query("cartToken") cartToken: String,
        @Query("catalogId") catalogId: Long,
        @Query("priceId") priceId: Int
    ): RegisterResponse

    @Headers("Content-Type: application/json")
    @POST("shopping/payment/create")
    suspend fun createPayment(
        @Header("Origin") origin: String,
        @Body request: CreatePaymentRequest
    ): CreatePaymentResponse
}
