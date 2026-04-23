# PayBito Payments Android SDK & TechStore Demo (v1.0.0)

## 📝 Description
The **PayBito Payments Android SDK** is a professional-grade fintech solution designed to empower developers with seamless crypto and fiat payment integration. Built on a reactive architecture, the SDK provides a robust bridge between native Android environments and the PayBito payment gateway.

Unlike traditional static libraries, this SDK is **context-aware**—it dynamically resolves broker branding, exchange configurations, and whitelisted origins in real-time. Whether you are building a boutique store or a high-volume trading platform, the PayBito SDK ensures a secure, branded, and high-conversion checkout experience with minimal boilerplate code.

---

## 🚀 Quick Start (Installation)

Add the JitPack repository to your `settings.gradle`:
```gradle
dependencyResolutionManagement {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

Add the dependency to your `app/build.gradle`:
```gradle
dependencies {
    implementation 'com.github.arunava2804:PayBito-Android-SDK:v1.0.0'
}
```

## ✨ Key Features
*   **Reactive Cart Engine:** Real-time state synchronization using Kotlin `StateFlow`.
*   **Dynamic Branding:** Automatically fetches and injects broker-specific names, logos, and security badges.
*   **Secure In-App Checkout:** A mobile-optimized WebView with full support for 3D Secure authentication and modern payment gateways.
*   **Auth Resilience:** Seamlessly handles token lifecycle management and automatic credential refresh.
*   **Premium Reference App:** Includes "TechStore"—a complete, dark-themed e-commerce implementation with a professional splash screen and product grid.

## 📦 What's Inside?
1.  **PayBito PayBito SDK Library:** The core logic for transaction management and security.
2.  **TechStore Demo App:** A high-performance reference application showcasing best practices.

## 📖 Documentation
*   [SDK Integration Guide](PAYBITO_SDK_ANDROID_GUIDE.md) — Technical setup and API usage.
*   [Demo App Walkthrough](PAYBITO_DEMO_APP_GUIDE.md) — Architecture and design deep dive.
*   [Distribution Guide](SDK_DISTRIBUTION_GUIDE.md) — GitHub & JitPack deployment details.

---
*Developed by HashCash Consultants · Powered by PayBito*
