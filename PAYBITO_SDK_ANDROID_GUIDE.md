# PayBito Android SDK — Integration Guide (v4.4)

Welcome to the **PayBito Android SDK** integration guide. This document provides a step-by-step walkthrough for developers to integrate premium crypto and fiat checkout capabilities into any Android application.

---

## 1. SDK Architecture Overview
The SDK is designed to be **reactive** and **robust**. It handles:
*   **Automatic Authentication:** Seamlessly manages client secrets and token refreshes.
*   **Dynamic Branding:** Automatically fetches Broker/Exchange names and logos from the API.
*   **In-App Checkout:** A mobile-optimized, desktop-capable WebView for a secure payment experience.
*   **State Persistence:** Automatically saves the cart state across app restarts.

---

## 2. Prerequisites
Add the following dependencies to your `app/build.gradle` to support the SDK's UI and networking:

```gradle
dependencies {
    implementation 'com.github.bumptech.glide:glide:4.16.0'      // Image loading
    implementation 'com.intuit.sdp:sdp-android:1.1.0'           // Responsive sizing
    implementation 'com.intuit.ssp:ssp-android:1.1.0'           // Responsive text
    implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.7.0' // Coroutines support
}
```

---

## 3. Step-by-Step Integration

### Step 1: Initialize the SDK
Initialize the SDK in your `Application` class. This setup allows the SDK to resolve broker information and restore previous cart sessions immediately.

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val config = PayBitoConfig(
            merchantId = 26660L,
            publicKey  = "YOUR_PUBLIC_KEY",
            brokerId   = "YOUR_BROKER_ID",
            origin     = "https://yourdomain.com", // Must be whitelisted
            enableDebugLogs = true
        )

        PayBitoSdk.init(this, config)
    }
}
```

### Step 2: Display Products & Add to Cart
The SDK uses a non-additive approach. You pass a `PayBitoProduct` object, and the SDK manages the server-side registration.

```kotlin
// Define your product
val product = PayBitoProduct(
    productId = "P001",
    name      = "Wireless Headphones",
    price     = 89.99,
    imageUrl  = "https://example.com/image.jpg"
)

// Add to Cart (Handles register vs update automatically)
lifecycleScope.launch {
    PayBitoSdk.addToCart(product)
}
```

### Step 3: Observe Cart State (UI Sync)
The SDK provides a `StateFlow` that emits updates whenever the cart changes (items added, quantities modified, or cart cleared).

```kotlin
lifecycleScope.launch {
    PayBitoSdk.cartState.collect { state ->
        binding.tvCartBadge.text = state.count.toString()
        binding.tvTotalAmount.text = "$${state.total}"
    }
}
```

### Step 4: Open the Cart Drawer
The SDK includes a built-in "Glass Morphism" cart drawer.

```kotlin
binding.btnCart.setOnClickListener {
    PayBitoSdk.openCart(requireActivity() as FragmentActivity)
}
```

### Step 5: Start Checkout
When the user clicks "Proceed to Checkout," the SDK generates a payment ID and opens the **PayBitoCheckoutActivity**.

```kotlin
binding.btnCheckout.setOnClickListener {
    PayBitoSdk.checkout(this) // 'this' is your Activity
}
```

---

## 4. Handling Payment Results (Deep Linking)
To receive success/failure callbacks from the checkout WebView, configure a Deep Link in your `AndroidManifest.xml`:

```xml
<activity android:name=".payment.PaymentResultActivity" android:exported="true">
    <intent-filter android:autoVerify="true">
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data android:host="checkout" android:scheme="paybito" />
    </intent-filter>
</activity>
```

In your `PaymentResultActivity`, clear the cart on success:

```kotlin
val uri = intent?.data
if (uri?.host == "checkout" && uri.path == "/success") {
    lifecycleScope.launch {
        PayBitoSdk.clearCart() // Match web behavior: clear cart on success
    }
}
```

---

## 5. Security Best Practices
1.  **Origin Validation:** Ensure the `origin` in your `PayBitoConfig` matches the domain whitelisted in your PayBito Merchant Dashboard.
2.  **HTTPS Only:** All image URLs and API endpoints are forced to HTTPS.
3.  **Client Secret Safety:** The SDK automatically handles the Client Secret. It is stored securely in `SharedPreferences` and never logged in production.

---

## 6. Support
For API-specific issues or merchant account setup, please visit the [PayBito Merchant Dashboard](https://portal.paybito.com).

*Documentation Version: 1.0.0 | April 2026*
