package com.paybito.sdk

data class PayBitoConfig(
    val merchantId: Long,
    val publicKey: String,      // Begins with pk_
    val brokerId: String,
    val origin: String,         // Used for domain whitelisting
    val checkoutUrl: String? = null,
    val enableDebugLogs: Boolean = false,
    val listener: PayBitoEventListener? = null
)

interface PayBitoEventListener {
    fun onCartUpdate(cart: com.paybito.sdk.models.CartState)
    fun onItemAdded(item: com.paybito.sdk.models.CartItem)
    fun onError(code: Int, message: String)
}
