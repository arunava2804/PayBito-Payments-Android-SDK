package com.paybito.sdk.persistence

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.paybito.sdk.models.CartItem
import com.paybito.sdk.models.CartState

object CartPreferences {
    private const val PREF_NAME = "paybito_cart_prefs"
    private lateinit var prefs: SharedPreferences
    private val gson = Gson()

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveCartState(state: CartState) {
        prefs.edit().apply {
            putString("cart_token", state.token)
            putString("catalog_id", state.catalogId.toString())
            putString("cart_items", gson.toJson(state.items))
            apply()
        }
    }

    fun loadCartState(): CartState {
        val token = prefs.getString("cart_token", null)
        val catalogIdStr = prefs.getString("catalog_id", "0")
        val catalogId = catalogIdStr?.toLongOrNull() ?: 0L
        val itemsJson = prefs.getString("cart_items", null)
        val items: List<CartItem> = if (itemsJson != null) {
            gson.fromJson(itemsJson, object : TypeToken<List<CartItem>>() {}.type)
        } else {
            emptyList()
        }
        val total = items.sumOf { it.unitPrice * it.quantity }
        val count = items.sumOf { it.quantity }
        return CartState(count, total, items, token, catalogId)
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
