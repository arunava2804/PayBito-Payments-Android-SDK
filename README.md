# PayBito Payments Android SDK & TechStore Demo (v1.1.2)

## 📝 Description
The **PayBito Payments Android SDK** is a professional-grade fintech solution designed to empower developers with seamless crypto and fiat payment integration. Built on a reactive architecture, the SDK provides a robust bridge between native Android environments and the PayBito payment gateway.

---

## 🚀 Quick Start (Installation)

Add the JitPack repository to your settings.gradle:
```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

Add the dependency to your pp/build.gradle:
```gradle
dependencies {
    implementation 'com.github.arunava2804.PayBito-Payments-Android-SDK:library:v1.1.2'
}
```

## ✨ Key Features
*   **Reactive Cart Engine:** Real-time state synchronization using Kotlin StateFlow.
*   **Dynamic Branding:** Automatically fetches broker-specific names and configurations.
*   **Secure In-App Checkout:** Mobile-optimized WebView with 3D Secure support.
*   **Easy Integration:** Single-line checkout and automated cart management.

## 📖 Documentation
*   [SDK Integration Guide](PAYBITO_SDK_ANDROID_GUIDE.md) — Technical setup and API usage.
*   [Demo App Walkthrough](PAYBITO_DEMO_APP_GUIDE.md) — Architecture and UI implementation.
*   [Distribution Guide](SDK_DISTRIBUTION_GUIDE.md) — Deployment and versioning.

---
*Developed by HashCash Consultants · Powered by PayBito*