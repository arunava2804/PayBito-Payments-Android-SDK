# PayBito Payments Android SDK & TechStore Demo (v1.0.0)

## 📝 Description
The **PayBito Payments Android SDK** is a professional-grade fintech solution for seamless crypto and fiat payment integration. It is context-aware, dynamically resolving broker branding and exchange configurations in real-time.

## 🚀 Quick Start (Installation)

### 1. Add JitPack to `settings.gradle`
```gradle
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

### 2. Add Dependency to `app/build.gradle`
```gradle
dependencies {
    implementation 'com.github.arunava2804:PayBito-Android-SDK:v1.0.0'
}
```

## ✨ Key Features
- **Reactive Cart Engine:** Real-time synchronization using Kotlin `StateFlow`.
- **Dynamic Branding:** Automatically fetches broker-specific names and configurations.
- **Secure Checkout:** Mobile-optimized WebView with 3D Secure support.
- **Easy Integration:** Single-line checkout and automated cart management.

## 📖 Documentation
- [SDK Integration Guide](PAYBITO_SDK_ANDROID_GUIDE.md) — Technical setup and API usage.
- [Demo App Walkthrough](PAYBITO_DEMO_APP_GUIDE.md) — Architecture and UI implementation.
- [Distribution Guide](SDK_DISTRIBUTION_GUIDE.md) — Deployment and versioning.

---
*Developed by HashCash Consultants · Powered by PayBito*
