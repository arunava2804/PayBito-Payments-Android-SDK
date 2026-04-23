# Add project-specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\HP ALL IN ONE\AppData\Local\Android\Sdk\tools\proguard\proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.

-keep class com.paybito.sdk.models.** { *; }
-keep class com.paybito.sdk.api.** { *; }
-keep interface com.paybito.sdk.** { *; }

# Keep GSON models
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**
-keepattributes EnclosingMethod
