# SDK Distribution & Deployment Guide

## 1. Versioning
We use **Semantic Versioning (SemVer)**: `v1.0.0`
- **Major:** Breaking changes.
- **Minor:** New features.
- **Patch:** Bug fixes.

## 2. Distribution via JitPack
The SDK is built as an Android Library (`.aar`) and distributed via JitPack.

### Publishing Configuration
In the library `build.gradle`:
```gradle
afterEvaluate {
    publishing {
        publications {
            release(MavenPublication) {
                from components.release
                groupId = 'com.github.arunava2804'
                artifactId = 'PayBito-Android-SDK'
                version = '1.0.0'
            }
        }
    }
}
```

## 3. Artifact Details
The generated AAR includes:
- **PayBitoSdk:** Main entry point.
- **UI Components:** `PayBitoCheckoutActivity` and `CartDrawerSheet`.
- **API Client:** Retrofit implementation for PayBito gateway.
- **Persistence:** Encrypted SharedPreferences for cart tokens.

## 4. Support
For integration support, visit the [PayBito Developer Portal](https://paybito.com/developers).
