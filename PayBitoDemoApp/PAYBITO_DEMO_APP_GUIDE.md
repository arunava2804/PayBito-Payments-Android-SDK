# TechStore Demo App Walkthrough

The **TechStore** app is a premium reference implementation showing how to integrate the PayBito SDK into a modern e-commerce UI.

## 🏗️ Architecture
The demo follows a clean, reactive pattern:
- **UI:** `MainActivity.kt` uses ViewBinding for efficient layout management.
- **State Management:** Uses Kotlin `StateFlow` from the SDK to keep the UI in sync with the cart.
- **Adaptability:** The `ProductAdapter` handles both grid display and quantity controls.

## 📱 Key Features

### 1. Splash Screen (`SplashActivity.kt`)
A professional dark-themed entry point that sets the tone for the premium "TechStore" experience.

### 2. Reactive Grid (`MainActivity.kt`)
The product grid observes `PayBitoSdk.cartState`. When a user clicks "Add to Cart":
1. The SDK registers the product with the backend.
2. The `cartState` Flow emits a new state.
3. The UI (badge, total, and adapter) updates instantly.

### 3. Integrated Checkout
The "Proceed to Checkout" button calls `PayBitoSdk.checkout()`, which:
1. Validates the cart.
2. Creates a payment session via the PayBito API.
3. Launches the mobile-optimized `PayBitoCheckoutActivity` WebView.

## 🛠️ Running the Demo
1. Ensure your `merchantId` and `publicKey` are set in `MyApplication.kt`.
2. Run the `app` module on a device with API 24+.
3. Observe the logs in Logcat filtered by `PayBitoSdk` to see real-time API interactions.
