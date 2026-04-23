# PayBito Android SDK Integration Guide (v1.1.0)

This guide provides accurate technical instructions for integrating the PayBito SDK based on the latest implementation.

## 1. Initialization
The SDK is a singleton. Initialize it in your `Application` class:

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        val config = PayBitoConfig(
            merchantId = 26660L,
            publicKey  = 'pk_your_public_key',
            brokerId   = 'your_broker_id',
            origin     = 'https://your-domain.com/',
            enableDebugLogs = true
        )

        PayBitoSdk.init(this, config)
    }
}
```

## 2. Core API Usage

### A. Add to Cart
The SDK handles all network calls and local persistence automatically.
```kotlin
val product = PayBitoProduct(
    productId = 'P001',
    name = 'Studio Headphones',
    price = 89.99,
    imageUrl = 'https://example.com/image.jpg'
)

lifecycleScope.launch {
    PayBitoSdk.addToCart(product)
}
```

### B. Observe Cart State (Reactive)
Observe the `cartState` Flow to update your UI automatically when items are added or removed.
```kotlin
lifecycleScope.launch {
    PayBitoSdk.cartState.collect { state ->
        binding.tvCartCount.text = state.count.toString()
        binding.tvTotalAmount.text = '$' + state.total
        adapter.updateItems(state.items)
    }
}
```

### C. Open Cart UI
The SDK provides a built-in BottomSheet cart drawer.
```kotlin
PayBitoSdk.openCart(this) // Requires FragmentActivity
```

## 3. Launching Checkout
Initiate the secure payment process with a single call:
```kotlin
PayBitoSdk.checkout(this)
```

## 4. ProGuard Rules
If using obfuscation, add these to your `proguard-rules.pro`:
```proguard
-keep class com.paybito.sdk.models.** { *; }
-dontwarn com.paybito.sdk.**
```

*Documentation Version: 1.1.0 | April 2026*