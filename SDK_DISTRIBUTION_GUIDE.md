# PayBito Android SDK Distribution & JitPack Guide (v1.1.2)

This guide explains how to properly distribute the PayBito SDK using JitPack.

---

## 1. JitPack Configuration

For JitPack to correctly build your library, your `library/build.gradle` must include the `maven-publish` plugin and a defined publication block.

### Complete Library build.gradle Snippet
```gradle
plugins {
    id 'com.android.library'
    id 'org.jetbrains.kotlin.android'
    id 'maven-publish'
}

android {
    // ... other config ...

    publishing {
        singleVariant('release') {
            withSourcesJar()
        }
    }
}

afterEvaluate {
    publishing {
        publications {
            release(MavenPublication) {
                from components.release
                groupId = 'com.github.arunava2804.PayBito-Payments-Android-SDK'
                artifactId = 'library'
                version = project.findProperty('version') ?: '1.1.2'
            }
        }
    }
}
```

## 2. Root Configuration

JitPack requires a `build.gradle` and `settings.gradle` in the **root** of your repository to coordinate the build. Ensure `android.useAndroidX=true` is set in your root `gradle.properties`.

---

## 3. Creating a Release
1. Push all changes to the `main` branch.
2. Go to **Releases** on GitHub and create a new tag (e.g., `v1.1.2`).
3. Visit [JitPack.io](https://jitpack.io) and look up `arunava2804/PayBito-Payments-Android-SDK`.

## 4. How Developers Integrate the SDK

### 1. Add Repository (settings.gradle)
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

### 2. Add Dependency (app/build.gradle)
```gradle
dependencies {
    implementation 'com.github.arunava2804.PayBito-Payments-Android-SDK:library:v1.1.2'
}
```

---
*Documentation Version: 1.1.2 | April 2026*