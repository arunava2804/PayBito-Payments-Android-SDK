# PayBito Demo App — Implementation Walkthrough

This guide explains how the **TechStore Demo App** uses the PayBito Android SDK to create a premium, high-performance e-commerce experience. Use this as a reference for your own application architecture.

---

## 1. App Architecture
The demo app is built using **MVVM-lite principles** with a heavy focus on **Reactive Programming**. It uses the following components:
*   **ViewBinding:** For type-safe UI interactions.
*   **StateFlow:** To observe cart changes from the SDK in real-time.
*   **Material 3:** For the modern dark-theme aesthetic.

---

## 2. Core Implementation Steps

### Step 1: Global Configuration (`MyApplication.kt`)
The demo app initializes the SDK once at the application level. This ensures that even if the app is killed and restarted, the SDK can restore the user's cart token from `SharedPreferences`.

```kotlin
val config = PayBitoConfig(
    merchantId = 26660L,
    publicKey  = "pk_5A2C...",
    brokerId   = "ARNA...",
    origin     = "https://coulombworld.com/",
    enableDebugLogs = true
)
PayBitoSdk.init(this, config)
```

### Step 2: Reactive UI Sync (`MainActivity.kt`)
Instead of manually updating the UI every time a user clicks "Add", the Demo App **observes** the SDK. This is the most efficient way to keep badges and totals synced across different screens.

```kotlin
lifecycleScope.launch {
    PayBitoSdk.cartState.collect { state ->
        // This runs automatically whenever the cart changes!
        binding.tvCartBadge.text = state.count.toString()
        binding.tvCartTotal.text = "Total: $${state.total}"
        adapter.updateCart(state.items) // Syncs the "Add/Qty" buttons in the grid
    }
}
```

### Step 3: Product Grid & Adapter (`ProductAdapter.kt`)
The demo app uses a `GridLayoutManager` (2 columns). The adapter handles two distinct actions:
1.  **New Item:** Calls `PayBitoSdk.addToCart(product)` if quantity is 0.
2.  **Existing Item:** Calls `PayBitoSdk.updateQuantity(id, newQty)` for the +/- buttons.

**Image Loading:** We use **Glide** to handle the high-resolution Unsplash product images efficiently with placeholders.

### Step 4: The Checkout WebView
When `PayBitoSdk.checkout(activity)` is called, the SDK opens a custom `PayBitoCheckoutActivity`. 
*   **Mobile View:** The demo app forces a mobile-optimized view.
*   **3D Secure:** The WebView is configured to handle the popups required by modern banks for payment verification.

### Step 5: Success Handling (`PaymentResultActivity.kt`)
This is a critical "Deep Link" activity. When the web-based payment completes, it redirects to `paybito://checkout/success`.
*   The demo app captures this via an `<intent-filter>`.
*   It displays the **redesigned Success Card**.
*   It calls `PayBitoSdk.clearCart()` to ensure the user starts fresh for their next purchase.

---

## 3. UI & Visual Identity
The demo app is designed with a modern, high-conversion fintech aesthetic:
*   **Typography:** Optimized for readability using clear, professional font families.
*   **Scaling:** Uses `sdp` for dimensions and `ssp` for text size to ensure the layout remains consistent across all Android screen densities.
*   **Theme:** Implements a premium Dark Mode palette to reduce eye strain and highlight product visuals.

---

## 4. Key Files to Study
1.  `com.example.paybitodemo.MainActivity`: The central hub for UI logic.
2.  `com.example.paybitodemo.ProductAdapter`: Handles complex recycler logic.
3.  `com.example.paybitodemo.payment.PaymentResultActivity`: Handles the post-checkout flow.

---

## 5. Troubleshooting the Demo
*   **Images not showing?** Check internet connectivity; Unsplash URLs require an active connection.
*   **500 Errors?** Ensure your `origin` is whitelisted in the PayBito Merchant portal.
*   **Deep link doesn't work?** Verify that the `scheme` and `host` in `AndroidManifest.xml` match the redirect URL configured on your server.

*HashCash Consultants · PayBito Payment Solutions*
