package com.paybito.sdk.models

import com.google.gson.annotations.SerializedName

data class CartState(
    val count: Int,
    val total: Double,
    val items: List<CartItem>,
    val token: String?,
    val catalogId: Long = 0
)

data class CartItem(
    val productId: String,
    val name: String,
    val unitPrice: Double,
    val quantity: Int,
    val currency: String,
    val imageUrl: String?,
    val attributes: Map<String, List<String>>?,
    var priceId: Int = 0,
    val cartKey: String
)

data class PayBitoProduct(
    val productId: String,
    val name: String,
    val price: Double,
    val quantity: Int = 1,
    val currency: String = "USD",
    val imageUrl: String? = null,
    val description: String = "",
    val attributes: Map<String, String> = emptyMap(),
    val priceId: Int = 0
)

// --- API Request Models ---

data class RegisterProductsRequest(
    val merchantId: Long,
    val catalogId: Long? = null,
    val cartToken: String? = null,
    val products: List<RegisterProductItem>
)

data class UpdateProductRequest(
    val merchantId: Long,
    val cartToken: String?,
    val priceId: Int,
    val quantity: Int,
    val name: String,
    val description: String,
    val imageUrl: String,
    val attributes: Map<String, List<String>>?,
    val metadata: Map<String, Any>? = null,
    val addPrices: List<PriceItem>,
    val deactivatePriceIds: List<Int> = emptyList()
)

data class RegisterProductItem(
    val productId: String,
    val productType: String = "CART",
    val quantity: Int,
    val name: String,
    val description: String,
    val imageUrl: String,
    val attributes: Map<String, List<String>>?,
    val metadata: Map<String, Any>? = null,
    val prices: List<PriceItem>
)

data class PriceItem(
    val priceId: Int = 0,
    val isDefault: Boolean = true,
    val priceType: String = "one-time",
    val intervalType: String = "",
    val intervalCount: Int = 0,
    val trialDays: Int = 0,
    val retryAttempts: Int = 0,
    val totalCycles: Int = 0,
    val retryInterval: String = "",
    val variant: Map<String, String>?,
    val sku: String,
    val inventory: Inventory,
    val currencies: List<CurrencyItem>
)

data class Inventory(
    val track: Boolean = true,
    val quantity: Int
)

data class CurrencyItem(
    val currency: String,
    val amount: Double,
    val isDefault: Boolean = true
)

data class CreatePaymentRequest(
    val merchantId: Long,
    val cartToken: String,
    val paymentName: String = "Cart Payment",
    val catalogId: Long
)

// --- API Response Models ---

data class ValidateResponse(
    val clientId: String?,
    val clientSecret: String?,
    val domain: String?
)

data class RegisterResponse(
    val status: Boolean,
    val message: String?,
    val data: RegisterData?
)

data class RegisterData(
    val cartToken: String?,
    val catalogId: Long?,
    val products: List<RegisterProductResponse>?
)

data class RegisterProductResponse(
    val productId: String,
    val priceId: Int,
    val status: String
)

data class CreatePaymentResponse(
    val error: String,
    val message: String?,
    val id: String?,
    val brokerId: String? = null
)

// --- Broker Info Models ---

data class BrokerInfoResponse(
    val code: Int,
    val domain: String?,
    val message: String?,
    val value: List<BrokerValue>?
)

data class BrokerValue(
    val broker_id: String,
    val exchange: String,
    val exchange_logo: String,
    val company_name: String
)
