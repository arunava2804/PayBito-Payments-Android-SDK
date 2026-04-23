package com.paybito.sdk

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.paybito.sdk.api.*
import com.paybito.sdk.models.*
import com.paybito.sdk.persistence.CartPreferences
import com.paybito.sdk.ui.CartDrawerSheet
import com.paybito.sdk.ui.PayBitoCheckoutActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

object PayBitoSdk {
    private lateinit var config: PayBitoConfig
    private lateinit var appContext: Context
    private lateinit var api: PayBitoApiService
    private lateinit var accountsApi: PayBitoAccountsApiService
    
    private var clientId: String? = null
    private var clientSecret: String? = null
    private var resolvedCheckoutDomain: String = "https://portal.paybito.com"
    private var brokerName: String = "Broker"
    private var exchangeName: String = "Broker"
    private var currentOrigin: String? = null

    fun getAuth(): Pair<String?, String?> = clientId to clientSecret
    fun getBrokerName(): String = brokerName
    fun getExchangeName(): String = exchangeName
    
    fun setOrigin(origin: String) {
        currentOrigin = origin
    }

    private fun getCleanOrigin(): String {
        return (currentOrigin ?: config.origin).trim().removeSuffix("/")
    }

    suspend fun refreshAuth() {
        clientId = null
        clientSecret = null
        ensureAuthenticated()
    }
    
    private val _cartState = MutableStateFlow(CartState(0, 0.0, emptyList(), null, 0L))
    val cartState: StateFlow<CartState> = _cartState.asStateFlow()

    fun init(application: Application, config: PayBitoConfig) {
        this.config = config
        this.appContext = application.applicationContext
        this.api = PayBitoApiClient.create(config.enableDebugLogs)
        this.accountsApi = PayBitoApiClient.createAccounts(config.enableDebugLogs)
        
        CartPreferences.init(application)
        _cartState.value = CartPreferences.loadCartState()

        // Sync broker info and cart state in background
        (application as? Application)?.let {
            // We use a global scope or just fire and forget for init sync
            kotlinx.coroutines.GlobalScope.launch(Dispatchers.IO) {
                resolveBrokerInfo()
                syncCartFromServer()
            }
        }
    }

    private suspend fun resolveBrokerInfo() {
        try {
            val res = accountsApi.getBrokerWiseExchangeInfo(config.brokerId, getCleanOrigin())
            if (res.code == 0 && res.domain != null) {
                resolvedCheckoutDomain = res.domain
                val brokerValue = res.value?.firstOrNull()
                if (brokerValue != null) {
                    brokerName = brokerValue.company_name
                    exchangeName = brokerValue.exchange
                }
                if (config.enableDebugLogs) Log.d("PayBitoSdk", "Resolved checkout domain: $resolvedCheckoutDomain, Broker: $brokerName, Exchange: $exchangeName")
            }
        } catch (e: Exception) {
            Log.w("PayBitoSdk", "Failed to resolve broker info, using fallback: ${e.message}")
        }
    }

    private suspend fun syncCartFromServer() {
        val currentToken = _cartState.value.token ?: return
        ensureAuthenticated()
        val cleanOrigin = getCleanOrigin()

        try {
            val json = api.getProducts(cleanOrigin, currentToken)
            if (json.has("status") && json.get("status").asBoolean) {
                val data = json.getAsJsonObject("data")
                val cartToken = data.get("cartToken").asString
                val cartTotal = data.get("cartTotal").asDouble
                val cartCount = data.get("cartCount").asInt
                
                // For a full sync, we'd map the products array back to CartItem list
                // For now, let's trust the server totals and keep existing local items 
                // OR ideally refresh the whole list.
                if (config.enableDebugLogs) Log.d("PayBitoSdk", "Cart synced from server. Total: $cartTotal")
            }
        } catch (e: Exception) {
            Log.e("PayBitoSdk", "Sync failed", e)
        }
    }

    private suspend fun ensureAuthenticated(): Boolean {
        if (clientId != null && clientSecret != null) return true
        val cleanOrigin = getCleanOrigin()
        return try {
            val res = api.validatePublicKey(config.publicKey, cleanOrigin)
            clientId = res.clientId ?: config.publicKey
            clientSecret = res.clientSecret ?: ""
            if (res.domain != null) {
                currentOrigin = res.domain
            }
            true
        } catch (e: Exception) {
            Log.e("PayBitoSdk", "Validation call error: ${e.message}")
            if (clientId == null) { clientId = config.publicKey; clientSecret = "" }
            true
        }
    }

    suspend fun addToCart(product: PayBitoProduct, button: Any? = null) = withContext(Dispatchers.IO) {
        ensureAuthenticated()
        val cleanOrigin = getCleanOrigin()
        
        val currentState = _cartState.value
        val existingItem = currentState.items.find { it.productId == product.productId }
        
        if (config.enableDebugLogs) {
            Log.d("PayBitoSdk", "addToCart: productId=${product.productId}, qty=${product.quantity}, existingItemFound=${existingItem != null}")
        }

        if (existingItem != null && currentState.token != null) {
            // Update to the exact quantity shown on screen (non-additive)
            updateQuantity(product.productId, product.quantity)
            return@withContext
        }

        val targetQuantity = product.quantity
        val attributesArrayed = if (product.attributes.isNotEmpty()) product.attributes.mapValues { listOf(it.value) } else null
        val variant = if (product.attributes.isNotEmpty()) product.attributes else null
        val sku = product.name + (if (variant != null && variant.isNotEmpty()) " - " + variant.values.joinToString(" ") else "")
        
        val priceItem = PriceItem(
            variant = variant,
            sku = sku,
            inventory = Inventory(quantity = targetQuantity),
            currencies = listOf(CurrencyItem(currency = product.currency, amount = product.price))
        )

        val registerItem = RegisterProductItem(
            productId = product.productId,
            name = product.name,
            description = product.description,
            imageUrl = product.imageUrl ?: "",
            quantity = targetQuantity,
            attributes = attributesArrayed,
            prices = listOf(priceItem)
        )

        val request = RegisterProductsRequest(
            merchantId = config.merchantId,
            catalogId = if (currentState.catalogId != 0L) currentState.catalogId else null,
            cartToken = if (!currentState.token.isNullOrEmpty()) currentState.token else null,
            products = listOf(registerItem)
        )

        try {
            if (config.enableDebugLogs) {
                val gson = com.google.gson.GsonBuilder().serializeNulls().setPrettyPrinting().create()
                Log.d("PayBitoSdk", "Register Request Body for ${product.productId}: ${gson.toJson(request)}")
            }
            
            val res = api.registerProducts(cleanOrigin, request, product.productId)
            
            if (res.status && res.data != null) {
                val data = res.data
                val newToken = data.cartToken ?: currentState.token
                val newCatalogId = data.catalogId ?: currentState.catalogId
                
                val currentItems = currentState.items.toMutableList()
                val serverProduct = data.products?.firstOrNull { it.productId == product.productId }
                val serverPriceId = serverProduct?.priceId ?: 0

                // If somehow it was already in currentItems (race condition), update it
                val idx = currentItems.indexOfFirst { it.productId == product.productId }
                if (idx != -1) {
                    currentItems[idx] = currentItems[idx].copy(quantity = targetQuantity, priceId = serverPriceId)
                } else {
                    currentItems.add(CartItem(
                        productId = product.productId,
                        name = product.name,
                        unitPrice = product.price,
                        quantity = targetQuantity,
                        currency = product.currency,
                        imageUrl = product.imageUrl,
                        attributes = attributesArrayed,
                        priceId = serverPriceId,
                        cartKey = "${product.productId}_${System.currentTimeMillis()}"
                    ))
                }

                updateLocalState(currentItems, newToken, newCatalogId)
            } else {
                handleApiError("Register", Exception(res.message ?: "Sync Rejected"))
            }
        } catch (e: Exception) {
            handleApiError("Register", e)
        }
    }

    suspend fun updateQuantity(productId: String, newQty: Int) = withContext(Dispatchers.IO) {
        val currentState = _cartState.value
        val item = currentState.items.find { it.productId == productId } ?: run {
            if (config.enableDebugLogs) Log.w("PayBitoSdk", "updateQuantity: Item $productId not found in cart")
            return@withContext
        }

        if (newQty <= 0) {
            removeProduct(productId)
            return@withContext
        }

        if (config.enableDebugLogs) {
            Log.d("PayBitoSdk", "updateQuantity: productId=$productId, currentQty=${item.quantity}, newQty=$newQty")
        }

        ensureAuthenticated()
        val cleanOrigin = getCleanOrigin()

        val variant = item.attributes?.mapValues { it.value.firstOrNull() ?: "" }
        val sku = item.name + (if (variant != null && variant.isNotEmpty()) " - " + variant.values.joinToString(" ") else "")

        val priceItem = PriceItem(
            priceId = item.priceId,
            variant = variant,
            sku = sku,
            inventory = Inventory(quantity = newQty),
            currencies = listOf(CurrencyItem(currency = item.currency, amount = item.unitPrice))
        )

        val request = UpdateProductRequest(
            merchantId = config.merchantId,
            cartToken = if (!currentState.token.isNullOrEmpty()) currentState.token else null,
            priceId = item.priceId,
            quantity = newQty,
            name = item.name,
            description = "",
            imageUrl = item.imageUrl ?: "",
            attributes = if (item.attributes?.isNotEmpty() == true) item.attributes else null,
            metadata = null,
            addPrices = listOf(priceItem)
        )

        try {
            if (config.enableDebugLogs) {
                val gson = com.google.gson.GsonBuilder().serializeNulls().setPrettyPrinting().create()
                Log.d("PayBitoSdk", "Update Request Body: ${gson.toJson(request)}")
            }

            val res = api.updateProduct(cleanOrigin, productId, request)
            if (res.status) {
                val currentItems = currentState.items.toMutableList()
                val idx = currentItems.indexOfFirst { it.productId == productId }
                if (idx != -1) {
                    currentItems[idx] = currentItems[idx].copy(quantity = newQty)
                    updateLocalState(currentItems, currentState.token, currentState.catalogId)
                }
            } else {
                handleApiError("Update", Exception(res.message ?: "Update Rejected"))
            }
        } catch (e: Exception) {
            handleApiError("Update", e)
        }
    }

    private suspend fun removeProduct(productId: String) = withContext(Dispatchers.IO) {
        val currentState = _cartState.value
        val item = currentState.items.find { it.productId == productId } ?: return@withContext
        val token = currentState.token ?: return@withContext

        ensureAuthenticated()
        val cleanOrigin = getCleanOrigin()

        try {
            val res = api.removeProduct(cleanOrigin, productId, token, currentState.catalogId, item.priceId)
            if (res.status) {
                val newList = currentState.items.filter { it.productId != productId }
                updateLocalState(newList, currentState.token, currentState.catalogId)
            }
        } catch (e: Exception) {
            val newList = currentState.items.filter { it.productId != productId }
            updateLocalState(newList, currentState.token, currentState.catalogId)
        }
    }

    suspend fun clearCart() = withContext(Dispatchers.IO) {
        updateLocalState(emptyList(), null, 0L)
        CartPreferences.clear()
    }

    private suspend fun handleApiError(tag: String, e: Exception) {
        withContext(Dispatchers.Main) {
            Log.e("PayBitoSdk", "$tag Exception", e)
            if (e is HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("PayBitoSdk", "HTTP Error ${e.code()}: $errorBody")
                Toast.makeText(appContext, "Error ${e.code()}: $errorBody", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(appContext, "Network Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun updateLocalState(items: List<CartItem>, token: String?, catalogId: Long) {
        val newCount = items.sumOf { it.quantity }
        val newTotal = items.sumOf { it.unitPrice * it.quantity }
        val newState = CartState(newCount, newTotal, items, token, catalogId)

        withContext(Dispatchers.Main) {
            _cartState.value = newState
            CartPreferences.saveCartState(newState)
            config.listener?.onCartUpdate(newState)
        }
    }

    fun openCart(activity: FragmentActivity) {
        if (_cartState.value.count == 0) {
            Toast.makeText(activity, "Your cart is empty", Toast.LENGTH_SHORT).show()
            return
        }
        CartDrawerSheet.newInstance().show(activity.supportFragmentManager, "pb_cart_drawer")
    }

    fun checkout(activity: Activity) {
        val state = _cartState.value
        if (state.count == 0) {
            Toast.makeText(activity, "Your cart is empty", Toast.LENGTH_SHORT).show()
            return
        }
        if (state.token == null) {
            Toast.makeText(activity, "Cart not synced. Try adding items again.", Toast.LENGTH_LONG).show()
            return
        }

        (activity as? FragmentActivity)?.lifecycleScope?.launch(Dispatchers.IO) {
            ensureAuthenticated()
            val cleanOrigin = getCleanOrigin()
            
            withContext(Dispatchers.Main) { Toast.makeText(activity, "Securing checkout...", Toast.LENGTH_SHORT).show() }

            val request = CreatePaymentRequest(
                merchantId = config.merchantId,
                cartToken = state.token,
                paymentName = "$exchangeName Order",
                catalogId = state.catalogId
            )

            try {
                val res = api.createPayment(cleanOrigin, request)
                if (res.error == "0" && res.id != null) {
                    val paymentId = res.id
                    val checkoutPath = "/payments/merchant/checkout/"
                    val finalUrl = "${resolvedCheckoutDomain.removeSuffix("/")}$checkoutPath$paymentId"

                    withContext(Dispatchers.Main) {
                        val intent = Intent(activity, PayBitoCheckoutActivity::class.java).apply {
                            putExtra("EXTRA_URL", finalUrl)
                        }
                        activity.startActivity(intent)
                    }
                } else {
                    withContext(Dispatchers.Main) { Toast.makeText(activity, "Checkout Error: ${res.message}", Toast.LENGTH_LONG).show() }
                }
            } catch (e: Exception) {
                handleApiError("Checkout", e)
            }
        }
    }

    fun getCount(): Int = _cartState.value.count
    fun getTotal(): Double = _cartState.value.total
}
