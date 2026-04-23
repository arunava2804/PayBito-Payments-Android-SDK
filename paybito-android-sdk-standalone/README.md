# PayBito Cart Android SDK

A fully functional shopping cart and payment checkout SDK for Android, written in Kotlin. This SDK is the native equivalent of the `PayBitoCart.js` web library.

## Features

- **One-call Initialization:** Simple setup in your `Application` class.
- **Server-Synced Cart:** Automatically persists and syncs cart state with the PayBito backend.
- **Flexible Product Model:** Support for variants and custom attributes (color, size, etc.).
- **Drop-in UI:** Includes a Floating Action Button (FAB) and a BottomSheet Cart Drawer.
- **Secure Checkout:** Integrates with Chrome Custom Tabs for a seamless and secure payment flow.

## Installation

Add the JitPack repository to your root `build.gradle`:

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Add the dependency to your app's `build.gradle`:

```gradle
dependencies {
    implementation 'com.github.PayBito:paybito-android-sdk:1.0.0'
}
```

## Quick Start

### 1. Initialize the SDK

Initialize the SDK in your `Application` class:

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val config = PayBitoConfig(
            merchantId = "YOUR_MERCHANT_ID",
            publicKey  = "pk_YOUR_PUBLIC_KEY",
            brokerId   = "YOUR_BROKER_ID",
            origin     = "https://yourdomain.com"
        )

        PayBitoSdk.init(this, config)
    }
}
```

### 2. Add to Cart

Use the `PayBitoSdk.addToCart` method to add products:

```kotlin
val product = PayBitoProduct(
    productId = "P001",
    name      = "Wireless Headphones",
    price     = 89.99,
    imageUrl  = "https://example.com/image.jpg"
)

lifecycleScope.launch {
    PayBitoSdk.addToCart(product)
}
```

### 3. Show the Cart Drawer

Open the built-in cart UI from any `FragmentActivity`:

```kotlin
binding.btnShowCart.setOnClickListener {
    PayBitoSdk.openCart(this)
}
```

## Documentation

For full documentation, including advanced configuration and UI customization, please see the [Integration Guide](PayBito_Android_SDK_Guide.md).

## License

MIT License. See [LICENSE](LICENSE) for details.
