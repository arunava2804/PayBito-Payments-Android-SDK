# PayBito Android SDK Distribution Guide

This guide explains how to host and share the PayBito SDK and TechStore Demo App so that external developers can easily access and integrate them.

---

## 1. Recommended Platform: GitHub + JitPack
We recommend using **JitPack** because it is the easiest way to turn a GitHub repository into an Android library without needing to manage complex Maven central requirements.

### Step 1: Prepare the SDK Repository
1.  Create a new **Public** repository on GitHub (e.g., `PayBito-Android-SDK`).
2.  Upload the contents of the `paybito-android-sdk-standalone` folder to this repository.
3.  Ensure your `library/build.gradle` includes the publishing plugin:
    ```gradle
    plugins {
        id 'com.android.library'
        id 'org.jetbrains.kotlin.android'
        id 'maven-publish'
    }
    ```

### Step 2: Create a Release
1.  On your GitHub repository page, click **Releases** -> **Create a new release**.
2.  Tag it as `v1.0.0`.
3.  Upload the `TechStoreDemo.apk` (built from the Demo App) to this release so developers can test it immediately.

### Step 3: Link to JitPack
1.  Go to [JitPack.io](https://jitpack.io).
2.  Paste your GitHub repository URL (e.g., `github.com/YourUsername/PayBito-Android-SDK`).
3.  Click **Get it**. JitPack will build your library and provide the implementation line.

---

## 2. How Developers Will Use Your SDK
Once hosted, other developers only need to do two things:

### 1. Add the Repository
In their `settings.gradle` file:
```gradle
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

### 2. Add the Dependency
In their `app/build.gradle` file:
```gradle
dependencies {
    implementation 'com.github.YourUsername:PayBito-Android-SDK:v1.0.0'
}
```

---

## 3. Deployment Checklist
*   [ ] **README.md:** Copy the content of `PAYBITO_SDK_ANDROID_GUIDE.md` into the `README.md` of your GitHub repository.
*   [ ] **License:** Include a `LICENSE` file (MIT or Apache 2.0 recommended).
*   [ ] **Demo App:** Ensure the `PayBitoDemoApp` code is also in a sub-folder of the repo so developers can see the reference implementation.
*   [ ] **Proguard:** Ensure `consumer-rules.pro` in the SDK library is configured to keep necessary classes.

---

## 4. Summary of Project Assets
*   **The SDK:** Located in `paybito-android-sdk-standalone/library`
*   **The Demo App:** Located in `PayBitoDemoApp`
*   **The Docs:** `PAYBITO_SDK_ANDROID_GUIDE.md` and `PAYBITO_DEMO_APP_GUIDE.md`

*Documentation Version: 1.0.0 | April 2026*
